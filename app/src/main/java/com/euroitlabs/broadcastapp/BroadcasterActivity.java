package com.euroitlabs.broadcastapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.util.Random;

public class BroadcasterActivity extends Activity implements View.OnClickListener {

    TextView senderpintxt;
    EditText et_message;
    Button sendmsgbtn;
    static final int READ_BLOCK_SIZE = 100;
    boolean sendr_pin_flag;
    String encryptedMessage = "", complete_encryptedMessage = "";
    int M1 = 2, M2 = 4, M3 = 9, M4 = 5;
    int N1 = 4, N2 = 6;
    static WifiManager wifi;
    int index;
    static Random random;
    MenuItem register;
    NumberPicker np;
    static String oldHotspotName;
    boolean wifiFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcaster2);


        senderpintxt = (TextView) findViewById(R.id.sendrpin_txt);
        et_message = (EditText) findViewById(R.id.editText_msg);
        sendmsgbtn = (Button) findViewById(R.id.btnsendmsg);
        np = (NumberPicker) findViewById(R.id.numberPicker);

        random = new Random();
        if (readSenderPin().isEmpty()) {
            int value;
            value = generateSenderPin();
            senderpintxt.setText(String.valueOf(value));
            sendr_pin_flag = true;
        } else {
            String value;
            value = readSenderPin();
            senderpintxt.setText(value);
        }
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        InputFilter filter = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {

                for (int i = start; i < end; i++) {
                    if (source.charAt(i) < 32 || source.charAt(i) > 126) {
                        Toast.makeText(getApplication(), "Character " + source.charAt(start) + " is not allowed in the message.", Toast.LENGTH_SHORT).show();
                        return "";
                    }
                }
                return null;
            }
        };
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(29);

        et_message.setFilters(new InputFilter[]{filter, filterArray[0]});
        sendmsgbtn.setOnClickListener(this);
        et_message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    sendmsgbtn.setVisibility(View.VISIBLE);
                }
            }
        });
        np.setMinValue(1);
        np.setMaxValue(99);
        np.setValue(5);
        np.setWrapSelectorWheel(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_broadcaster, menu);
        register = menu.findItem(R.id.action_stop_ongoing_broadcast);
        if (isMyServiceRunning(BroadcastService.class)) {
            register.setVisible(true);

        } else {
            register.setVisible(false);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnsendmsg:
                String st;
                String message;
                st = et_message.getText().toString();
                if (st.isEmpty()) {
                    Toast.makeText(this, "Please enter a message to broadcast.", Toast.LENGTH_SHORT).show();
                } else {
                    register.setVisible(true);
                    message = messageToBeEncrypted(st);
                    if (wifi.isWifiEnabled()) {
                        wifiFlag = true;
                        if (setHotspotName(encryptMessage(message), getApplicationContext())) {
                            Utils.turnOnOffHotspot(getApplicationContext(), true);
                        }
                    } else if (!wifi.isWifiEnabled()) {
                        if (setHotspotName(encryptMessage(message), getApplicationContext())) {
                            Utils.turnOnOffHotspot(getApplicationContext(), true);
                        }
                    } else if (!Utils.turnOnOffHotspot(getApplicationContext(), false) && Utils.turnOnOffWifi(getApplicationContext(), true)) {
                        if (setHotspotName(encryptMessage(message), getApplicationContext())) {
                            Utils.turnOnOffHotspot(getApplicationContext(), true);
                        }
                    }
                }
                et_message.setText("");
                Toast.makeText(this, "Message Broadcasted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, BroadcastService.class);
                intent.putExtra("timer", String.valueOf(np.getValue()));
                intent.putExtra("originalhotspotname", oldHotspotName);
                intent.putExtra("wififlag", wifiFlag);
                startService(intent);
                break;
        }

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_stop_ongoing_broadcast:
                register.setVisible(false);
                stopService(new Intent(getApplicationContext(), BroadcastService.class));
                Utils.turnOnOffHotspot(getApplicationContext(), false);
                BroadcasterActivity.setHotspotName(oldHotspotName, getApplicationContext());
                if (!wifi.isWifiEnabled() && wifiFlag) {
                    wifiFlag = false;
                    Utils.turnOnOffWifi(getApplicationContext(), true);
                } else {
                    Utils.turnOnOffWifi(getApplicationContext(), false);
                }
                Toast.makeText(this, "Broadcasted Message stopped", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_receiver_setting:
                Intent in = new Intent(getApplicationContext(), MainActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);
                return true;
            case R.id.action_regenerate_new_pin:
                receiverPinAlert();
                return true;
            case R.id.action_help:
                Intent i = new Intent(getApplicationContext(), BroadcasterHelpActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Checking if any service is running
     * in background
     */

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * dialog to check pin change
     */
    public void receiverPinAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to change the pin ?");

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                int value;
                value = generateSenderPin();
                senderpintxt.setText(String.valueOf(value));
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Generate sender pin
     */
    public int generateSenderPin() {
        int sender_pin = (1 + random.nextInt(2)) * 1000 + random.nextInt(1000); //will generate a number 0000 to 9999
        saveSenderPin(String.valueOf(sender_pin));
        return sender_pin;
    }

    /**
     * Save sender pin in txt file
     */
    public void saveSenderPin(String pin) {
        // add-write text into file
        try {
            FileOutputStream fileout = openFileOutput("sendr_pin.txt", MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(String.valueOf(pin));
            outputWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Read sender pin from txt file
     */

    public String readSenderPin() {
        //reading text from file
        String sender_pin = "";
        //reading text from file
        try {
            FileInputStream fileIn = openFileInput("sendr_pin.txt");
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            int charRead;

            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                sender_pin += readstring;
            }
            InputRead.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sender_pin;
    }

    /**
     * Set Message as Hotspot name
     * in encrypted form
     */
    public static boolean setHotspotName(String newName, Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
            Method getConfigMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiManager);
            oldHotspotName = wifiConfig.SSID;
            wifiConfig.SSID = newName;
            Method setConfigMethod = wifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
            setConfigMethod.invoke(wifiManager, wifiConfig);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Adding "X" as 15th character
     * in message to be broadcasted
     */
    String messageToBeEncrypted(String msg) {
        if (msg.length() < 30) {
            if (msg.length() < 14) {
                for (int j = msg.length() + 1; j < 15; j++) {
                    msg += " ";
                }
                msg += "x";
                for (int k = 1; k <= 15; k++) {
                    msg += " ";
                }
            } else if (msg.length() > 14) {
                String msg_1 = msg.substring(0, 14);
                String msg_2 = msg.substring(14);
                msg = msg_1 + "x" + msg_2;
                for (int m = msg.length(); m < 30; m++) {
                    msg += " ";
                }
            } else if (msg.length() == 14) {
                msg += "x";
                for (int m = msg.length(); m < 30; m++) {
                    msg += " ";
                }
            }
        }
        return msg;
    }

    /**
     * Encrypt character
     */
    int selectedMessageChar(int n) {
        int character_n = 0;
        if (index == 0 || index == 8 || index == 16 || index == 24) {
            character_n = M1 + N1 + n;
        } else if (index == 1 || index == 9 || index == 17 || index == 25) {
            character_n = M1 + N2 + n;
        } else if (index == 2 || index == 10 || index == 18 || index == 26) {
            character_n = M2 + N1 + n;
        } else if (index == 3 || index == 11 || index == 19 || index == 27) {
            character_n = M2 + N2 + n;
        } else if (index == 4 || index == 12 || index == 20 || index == 28) {
            character_n = M3 + N1 + n;
        } else if (index == 5 || index == 13 || index == 21 || index == 29) {
            character_n = M3 + N2 + n;
        } else if (index == 6 || index == 14 || index == 22) {
            character_n = M4 + N1 + n;
        } else if (index == 7 || index == 15 || index == 23) {
            character_n = M4 + N2 + n;
        }
        return character_n;
    }

    /**
     * Encrypt message
     */
    String encryptMessage(String hotspotName) {
        encryptedMessage = "";
        for (index = 0; index < hotspotName.length(); index++) {
            char c = hotspotName.charAt(index);
            switch (c) {
                case 'A':
                    encryptedChar(selectedMessageChar(1));
                    break;
                case 'B':
                    encryptedChar(selectedMessageChar(2));
                    break;
                case 'C':
                    encryptedChar(selectedMessageChar(3));
                    break;
                case 'D':
                    encryptedChar(selectedMessageChar(4));
                    break;
                case 'E':
                    encryptedChar(selectedMessageChar(5));
                    break;
                case 'F':
                    encryptedChar(selectedMessageChar(6));
                    break;
                case 'G':
                    encryptedChar(selectedMessageChar(7));
                    break;
                case 'H':
                    encryptedChar(selectedMessageChar(8));
                    break;
                case 'I':
                    encryptedChar(selectedMessageChar(9));
                    break;
                case 'J':
                    encryptedChar(selectedMessageChar(10));
                    break;
                case 'K':
                    encryptedChar(selectedMessageChar(11));
                    break;
                case 'L':
                    encryptedChar(selectedMessageChar(12));
                    break;
                case 'M':
                    encryptedChar(selectedMessageChar(13));
                    break;
                case 'N':
                    encryptedChar(selectedMessageChar(14));
                    break;
                case 'O':
                    encryptedChar(selectedMessageChar(15));
                    break;
                case 'P':
                    encryptedChar(selectedMessageChar(16));
                    break;
                case 'Q':
                    encryptedChar(selectedMessageChar(17));
                    break;
                case 'R':
                    encryptedChar(selectedMessageChar(18));
                    break;
                case 'S':
                    encryptedChar(selectedMessageChar(19));
                    break;
                case 'T':
                    encryptedChar(selectedMessageChar(20));
                    break;
                case 'U':
                    encryptedChar(selectedMessageChar(21));
                    break;
                case 'V':
                    encryptedChar(selectedMessageChar(22));
                    break;
                case 'W':
                    encryptedChar(selectedMessageChar(23));
                    break;
                case 'X':
                    encryptedChar(selectedMessageChar(24));
                    break;
                case 'Y':
                    encryptedChar(selectedMessageChar(25));
                    break;
                case 'Z':
                    encryptedChar(selectedMessageChar(26));
                    break;
                case 'a':
                    encryptedChar(selectedMessageChar(27));
                    break;
                case 'b':
                    encryptedChar(selectedMessageChar(28));
                    break;
                case 'c':
                    encryptedChar(selectedMessageChar(29));
                    break;
                case 'd':
                    encryptedChar(selectedMessageChar(30));
                    break;
                case 'e':
                    encryptedChar(selectedMessageChar(31));
                    break;
                case 'f':
                    encryptedChar(selectedMessageChar(32));
                    break;
                case 'g':
                    encryptedChar(selectedMessageChar(33));
                    break;
                case 'h':
                    encryptedChar(selectedMessageChar(34));
                    break;
                case 'i':
                    encryptedChar(selectedMessageChar(35));
                    break;
                case 'j':
                    encryptedChar(selectedMessageChar(36));
                    break;
                case 'k':
                    encryptedChar(selectedMessageChar(37));
                    break;
                case 'l':
                    encryptedChar(selectedMessageChar(38));
                    break;
                case 'm':
                    encryptedChar(selectedMessageChar(39));
                    break;
                case 'n':
                    encryptedChar(selectedMessageChar(40));
                    break;
                case 'o':
                    encryptedChar(selectedMessageChar(41));
                    break;
                case 'p':
                    encryptedChar(selectedMessageChar(42));
                    break;
                case 'q':
                    encryptedChar(selectedMessageChar(43));
                    break;
                case 'r':
                    encryptedChar(selectedMessageChar(44));
                    break;
                case 's':
                    encryptedChar(selectedMessageChar(45));
                    break;
                case 't':
                    encryptedChar(selectedMessageChar(46));
                    break;
                case 'u':
                    encryptedChar(selectedMessageChar(47));
                    break;
                case 'v':
                    encryptedChar(selectedMessageChar(48));
                    break;
                case 'w':
                    encryptedChar(selectedMessageChar(49));
                    break;
                case 'x':
                    encryptedChar(selectedMessageChar(50));
                    break;
                case 'y':
                    encryptedChar(selectedMessageChar(51));
                    break;
                case 'z':
                    encryptedChar(selectedMessageChar(52));
                    break;
                case '0':
                    encryptedChar(selectedMessageChar(53));
                    break;
                case '1':
                    encryptedChar(selectedMessageChar(54));
                    break;
                case '2':
                    encryptedChar(selectedMessageChar(55));
                    break;
                case '3':
                    encryptedChar(selectedMessageChar(56));
                    break;
                case '4':
                    encryptedChar(selectedMessageChar(57));
                    break;
                case '5':
                    encryptedChar(selectedMessageChar(58));
                    break;
                case '6':
                    encryptedChar(selectedMessageChar(59));
                    break;
                case '7':
                    encryptedChar(selectedMessageChar(60));
                    break;
                case '8':
                    encryptedChar(selectedMessageChar(61));
                    break;
                case '9':
                    encryptedChar(selectedMessageChar(62));
                    break;
                case ',':
                    encryptedChar(selectedMessageChar(63));
                    break;
                case '.':
                    encryptedChar(selectedMessageChar(64));
                    break;
                case '?':
                    encryptedChar(selectedMessageChar(65));
                    break;
                case '!':
                    encryptedChar(selectedMessageChar(66));
                    break;
                case ';':
                    encryptedChar(selectedMessageChar(67));
                    break;
                case ':':
                    encryptedChar(selectedMessageChar(68));
                    break;
                case '/':
                    encryptedChar(selectedMessageChar(69));
                    break;
                case '\\':
                    encryptedChar(selectedMessageChar(70));
                    break;
                case '@':
                    encryptedChar(selectedMessageChar(71));
                    break;
                case '#':
                    encryptedChar(selectedMessageChar(72));
                    break;
                case '%':
                    encryptedChar(selectedMessageChar(73));
                    break;
                case '&':
                    encryptedChar(selectedMessageChar(74));
                    break;
                case '*':
                    encryptedChar(selectedMessageChar(75));
                    break;
                case '(':
                    encryptedChar(selectedMessageChar(76));
                    break;
                case ')':
                    encryptedChar(selectedMessageChar(77));
                    break;
                case '-':
                    encryptedChar(selectedMessageChar(78));
                    break;
                case '+':
                    encryptedChar(selectedMessageChar(79));
                    break;
                case '=':
                    encryptedChar(selectedMessageChar(80));
                    break;
                case '<':
                    encryptedChar(selectedMessageChar(81));
                    break;
                case '>':
                    encryptedChar(selectedMessageChar(82));
                    break;
                case '"':
                    encryptedChar(selectedMessageChar(83));
                    break;
                case '\'':
                    encryptedChar(selectedMessageChar(84));
                    break;
                case ' ':
                    encryptedChar(selectedMessageChar(85));
                    break;
                case '[':
                    encryptedChar(selectedMessageChar(86));
                    break;
                case ']':
                    encryptedChar(selectedMessageChar(87));
                    break;
                case '{':
                    encryptedChar(selectedMessageChar(88));
                    break;
                case '}':
                    encryptedChar(selectedMessageChar(89));
                    break;
                case '_':
                    encryptedChar(selectedMessageChar(90));
                    break;
                case '~':
                    encryptedChar(selectedMessageChar(91));
                    break;
                case '|':
                    encryptedChar(selectedMessageChar(92));
                    break;
                case '$':
                    encryptedChar(selectedMessageChar(93));
                    break;
                case '`':
                    encryptedChar(selectedMessageChar(94));
                    break;
                case '^':
                    encryptedChar(selectedMessageChar(95));
                    break;
                default:
                    encryptedMessage += String.valueOf(c);
                    break;
            }
        }
        complete_encryptedMessage = String.valueOf(generateTwoRandomDigits()) + encryptedMessage;

        return complete_encryptedMessage;
    }

    /**
     * Encrpted character corresponding
     * to original message character
     */
    void encryptedChar(int n) {

        switch (n) {
            case 1:
                encryptedMessage += "A";
                break;
            case 2:
                encryptedMessage += "B";
                break;
            case 3:
                encryptedMessage += "C";
                break;
            case 4:
                encryptedMessage += "D";
                break;
            case 5:
                encryptedMessage += "E";
                break;
            case 6:
                encryptedMessage += "F";
                break;
            case 7:
                encryptedMessage += "G";
                break;
            case 8:
                encryptedMessage += "H";
                break;
            case 9:
                encryptedMessage += "I";
                break;
            case 10:
                encryptedMessage += "J";
                break;
            case 11:
                encryptedMessage += "K";
                break;
            case 12:
                encryptedMessage += "L";
                break;
            case 13:
                encryptedMessage += "M";
                break;
            case 14:
                encryptedMessage += "N";
                break;
            case 15:
                encryptedMessage += "O";
                break;
            case 16:
                encryptedMessage += "P";
                break;
            case 17:
                encryptedMessage += "Q";
                break;
            case 18:
                encryptedMessage += "R";
                break;
            case 19:
                encryptedMessage += "S";
                break;
            case 20:
                encryptedMessage += "T";
                break;
            case 21:
                encryptedMessage += "U";
                break;
            case 22:
                encryptedMessage += "V";
                break;
            case 23:
                encryptedMessage += "W";
                break;
            case 24:
                encryptedMessage += "X";
                break;
            case 25:
                encryptedMessage += "Y";
                break;
            case 26:
                encryptedMessage += "Z";
                break;
            case 27:
                encryptedMessage += "a";
                break;
            case 28:
                encryptedMessage += "b";
                break;
            case 29:
                encryptedMessage += "c";
                break;
            case 30:
                encryptedMessage += "d";
                break;
            case 31:
                encryptedMessage += "e";
                break;
            case 32:
                encryptedMessage += "f";
                break;
            case 33:
                encryptedMessage += "g";
                break;
            case 34:
                encryptedMessage += "h";
                break;
            case 35:
                encryptedMessage += "i";
                break;
            case 36:
                encryptedMessage += "j";
                break;
            case 37:
                encryptedMessage += "k";
                break;
            case 38:
                encryptedMessage += "l";
                break;
            case 39:
                encryptedMessage += "m";
                break;
            case 40:
                encryptedMessage += "n";
                break;
            case 41:
                encryptedMessage += "o";
                break;
            case 42:
                encryptedMessage += "p";
                break;
            case 43:
                encryptedMessage += "q";
                break;
            case 44:
                encryptedMessage += "r";
                break;
            case 45:
                encryptedMessage += "s";
                break;
            case 46:
                encryptedMessage += "t";
                break;
            case 47:
                encryptedMessage += "u";
                break;
            case 48:
                encryptedMessage += "v";
                break;
            case 49:
                encryptedMessage += "w";
                break;
            case 50:
                encryptedMessage += "x";
                break;
            case 51:
                encryptedMessage += "y";
                break;
            case 52:
                encryptedMessage += "z";
                break;
            case 53:
                encryptedMessage += "0";
                break;
            case 54:
                encryptedMessage += "1";
                break;
            case 55:
                encryptedMessage += "2";
                break;
            case 56:
                encryptedMessage += "3";
                break;
            case 57:
                encryptedMessage += "4";
                break;
            case 58:
                encryptedMessage += "5";
                break;
            case 59:
                encryptedMessage += "6";
                break;
            case 60:
                encryptedMessage += "7";
                break;
            case 61:
                encryptedMessage += "8";
                break;
            case 62:
                encryptedMessage += "9";
                break;
            case 63:
                encryptedMessage += ",";
                break;
            case 64:
                encryptedMessage += ".";
                break;
            case 65:
                encryptedMessage += "?";
                break;
            case 66:
                encryptedMessage += "!";
                break;
            case 67:
                encryptedMessage += ";";
                break;
            case 68:
                encryptedMessage += ":";
                break;
            case 69:
                encryptedMessage += "/";
                break;
            case 70:
                encryptedMessage += "\\";
                break;
            case 71:
                encryptedMessage += "@";
                break;
            case 72:
                encryptedMessage += "#";
                break;
            case 73:
                encryptedMessage += "%";
                break;
            case 74:
                encryptedMessage += "&";
                break;
            case 75:
                encryptedMessage += "*";
                break;
            case 76:
                encryptedMessage += "(";
                break;
            case 77:
                encryptedMessage += ")";
                break;
            case 78:
                encryptedMessage += "-";
                break;
            case 79:
                encryptedMessage += "+";
                break;
            case 80:
                encryptedMessage += "=";
                break;
            case 81:
                encryptedMessage += "<";
                break;
            case 82:
                encryptedMessage += ">";
                break;
            case 83:
                encryptedMessage += "\"";
                break;
            case 84:
                encryptedMessage += "'";
                break;
            case 85:
                encryptedMessage += " ";
                break;
            case 86:
                encryptedMessage += "[";
                break;
            case 87:
                encryptedMessage += "]";
                break;
            case 88:
                encryptedMessage += "{";
                break;
            case 89:
                encryptedMessage += "}";
                break;
            case 90:
                encryptedMessage += "_";
                break;
            case 91:
                encryptedMessage += "~";
                break;
            case 92:
                encryptedMessage += "|";
                break;
            case 93:
                encryptedMessage += "$";
                break;
            case 94:
                encryptedMessage += "`";
                break;
            case 95:
                encryptedMessage += "^";
                break;
            default:
                int x = Math.abs(n) - 95;
                encryptedChar(x);
                break;
        }


    }

    /**
     * Two Random digits prior to
     * encrypted message
     */

    public static int generateTwoRandomDigits() {

        int sender_pin = (1 + random.nextInt(2)) * 10 + random.nextInt(10); //will generate a number 00 to 99
        return sender_pin;
    }
}
