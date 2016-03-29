package com.euroitlabs.broadcastapp;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

public class MyService extends Service {

    WifiManager mainWifi;
    private boolean isRunning = false;
    String decryptedMessage = "", complete_decryptedMessage = "";
    int M1 = 2, M2 = 4, M3 = 9, M4 = 5;
    int N1 = 4, N2 = 6;
    String wifiname = "";
    private WifiReceiver receiverWifi;
    int index;
    static final int READ_BLOCK_SIZE = 100;


    @Override
    public void onCreate() {
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver();
        isRunning = true;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        Runnable r = new Runnable() {

            @Override
            public void run() {
                while (isRunning) {
                    if (mainWifi.isWifiEnabled()) {
                        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                        mainWifi.startScan();
                        try {
                            Thread.sleep(60000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
        return START_STICKY;

    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void Notify(String broadcast_message, int number) {
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(this, NotificationView.class);
        intent.putExtra("message", broadcast_message);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        //     Build notification
        Notification noti = new Notification.Builder(this)
                .setContentTitle("Message Received..")
                .setContentText(broadcast_message).setSmallIcon(R.mipmap.logo)
                .setContentIntent(pIntent).setSound(soundUri).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(number, noti);
    }

    int selectedEncryptedMessageChar(int n) {
        int character_n = 0;
        if (index == 0 || index == 8 || index == 16 || index == 24) {
            character_n = n - (M1 + N1);
        } else if (index == 1 || index == 9 || index == 17 || index == 25) {
            character_n = n - (M1 + N2);
        } else if (index == 2 || index == 10 || index == 18 || index == 26) {
            character_n = n - (M2 + N1);
        } else if (index == 3 || index == 11 || index == 19 || index == 27) {
            character_n = n - (M2 + N2);
        } else if (index == 4 || index == 12 || index == 20 || index == 28) {
            character_n = n - (M3 + N1);
        } else if (index == 5 || index == 13 || index == 21 || index == 29) {
            character_n = n - (M3 + N2);
        } else if (index == 6 || index == 14 || index == 22) {
            character_n = n - (M4 + N1);
        } else if (index == 7 || index == 15 || index == 23) {
            character_n = n - (M4 + N2);
        }
        return character_n;
    }

    String decryptMessage(String encryptedHotspotName) {
        decryptedMessage = "";
        for (index = 0; index < encryptedHotspotName.length(); index++) {
            char d = encryptedHotspotName.charAt(index);
            switch (d) {
                case 'A':
                    decryptedChar(selectedEncryptedMessageChar(1));
                    break;
                case 'B':
                    decryptedChar(selectedEncryptedMessageChar(2));
                    break;
                case 'C':
                    decryptedChar(selectedEncryptedMessageChar(3));
                    break;
                case 'D':
                    decryptedChar(selectedEncryptedMessageChar(4));
                    break;
                case 'E':
                    decryptedChar(selectedEncryptedMessageChar(5));
                    break;
                case 'F':
                    decryptedChar(selectedEncryptedMessageChar(6));
                    break;
                case 'G':
                    decryptedChar(selectedEncryptedMessageChar(7));
                    break;
                case 'H':
                    decryptedChar(selectedEncryptedMessageChar(8));
                    break;
                case 'I':
                    decryptedChar(selectedEncryptedMessageChar(9));
                    break;
                case 'J':
                    decryptedChar(selectedEncryptedMessageChar(10));
                    break;
                case 'K':
                    decryptedChar(selectedEncryptedMessageChar(11));
                    break;
                case 'L':
                    decryptedChar(selectedEncryptedMessageChar(12));
                    break;
                case 'M':
                    decryptedChar(selectedEncryptedMessageChar(13));
                    break;
                case 'N':
                    decryptedChar(selectedEncryptedMessageChar(14));
                    break;
                case 'O':
                    decryptedChar(selectedEncryptedMessageChar(15));
                    break;
                case 'P':
                    decryptedChar(selectedEncryptedMessageChar(16));
                    break;
                case 'Q':
                    decryptedChar(selectedEncryptedMessageChar(17));
                    break;
                case 'R':
                    decryptedChar(selectedEncryptedMessageChar(18));
                    break;
                case 'S':
                    decryptedChar(selectedEncryptedMessageChar(19));
                    break;
                case 'T':
                    decryptedChar(selectedEncryptedMessageChar(20));
                    break;
                case 'U':
                    decryptedChar(selectedEncryptedMessageChar(21));
                    break;
                case 'V':
                    decryptedChar(selectedEncryptedMessageChar(22));
                    break;
                case 'W':
                    decryptedChar(selectedEncryptedMessageChar(23));
                    break;
                case 'X':
                    decryptedChar(selectedEncryptedMessageChar(24));
                    break;
                case 'Y':
                    decryptedChar(selectedEncryptedMessageChar(25));
                    break;
                case 'Z':
                    decryptedChar(selectedEncryptedMessageChar(26));
                    break;
                case 'a':
                    decryptedChar(selectedEncryptedMessageChar(27));
                    break;
                case 'b':
                    decryptedChar(selectedEncryptedMessageChar(28));
                    break;
                case 'c':
                    decryptedChar(selectedEncryptedMessageChar(29));
                    break;
                case 'd':
                    decryptedChar(selectedEncryptedMessageChar(30));
                    break;
                case 'e':
                    decryptedChar(selectedEncryptedMessageChar(31));
                    break;
                case 'f':
                    decryptedChar(selectedEncryptedMessageChar(32));
                    break;
                case 'g':
                    decryptedChar(selectedEncryptedMessageChar(33));
                    break;
                case 'h':
                    decryptedChar(selectedEncryptedMessageChar(34));
                    break;
                case 'i':
                    decryptedChar(selectedEncryptedMessageChar(35));
                    break;
                case 'j':
                    decryptedChar(selectedEncryptedMessageChar(36));
                    break;
                case 'k':
                    decryptedChar(selectedEncryptedMessageChar(37));
                    break;
                case 'l':
                    decryptedChar(selectedEncryptedMessageChar(38));
                    break;
                case 'm':
                    decryptedChar(selectedEncryptedMessageChar(39));
                    break;
                case 'n':
                    decryptedChar(selectedEncryptedMessageChar(40));
                    break;
                case 'o':
                    decryptedChar(selectedEncryptedMessageChar(41));
                    break;
                case 'p':
                    decryptedChar(selectedEncryptedMessageChar(42));
                    break;
                case 'q':
                    decryptedChar(selectedEncryptedMessageChar(43));
                    break;
                case 'r':
                    decryptedChar(selectedEncryptedMessageChar(44));
                    break;
                case 's':
                    decryptedChar(selectedEncryptedMessageChar(45));
                    break;
                case 't':
                    decryptedChar(selectedEncryptedMessageChar(46));
                    break;
                case 'u':
                    decryptedChar(selectedEncryptedMessageChar(47));
                    break;
                case 'v':
                    decryptedChar(selectedEncryptedMessageChar(48));
                    break;
                case 'w':
                    decryptedChar(selectedEncryptedMessageChar(49));
                    break;
                case 'x':
                    decryptedChar(selectedEncryptedMessageChar(50));
                    break;
                case 'y':
                    decryptedChar(selectedEncryptedMessageChar(51));
                    break;
                case 'z':
                    decryptedChar(selectedEncryptedMessageChar(52));
                    break;
                case '0':
                    decryptedChar(selectedEncryptedMessageChar(53));
                    break;
                case '1':
                    decryptedChar(selectedEncryptedMessageChar(54));
                    break;
                case '2':
                    decryptedChar(selectedEncryptedMessageChar(55));
                    break;
                case '3':
                    decryptedChar(selectedEncryptedMessageChar(56));
                    break;
                case '4':
                    decryptedChar(selectedEncryptedMessageChar(57));
                    break;
                case '5':
                    decryptedChar(selectedEncryptedMessageChar(58));
                    break;
                case '6':
                    decryptedChar(selectedEncryptedMessageChar(59));
                    break;
                case '7':
                    decryptedChar(selectedEncryptedMessageChar(60));
                    break;
                case '8':
                    decryptedChar(selectedEncryptedMessageChar(61));
                    break;
                case '9':
                    decryptedChar(selectedEncryptedMessageChar(62));
                    break;
                case ',':
                    decryptedChar(selectedEncryptedMessageChar(63));
                    break;
                case '.':
                    decryptedChar(selectedEncryptedMessageChar(64));
                    break;
                case '?':
                    decryptedChar(selectedEncryptedMessageChar(65));
                    break;
                case '!':
                    decryptedChar(selectedEncryptedMessageChar(66));
                    break;
                case ';':
                    decryptedChar(selectedEncryptedMessageChar(67));
                    break;
                case ':':
                    decryptedChar(selectedEncryptedMessageChar(68));
                    break;
                case '/':
                    decryptedChar(selectedEncryptedMessageChar(69));
                    break;
                case '\\':
                    decryptedChar(selectedEncryptedMessageChar(70));
                    break;
                case '@':
                    decryptedChar(selectedEncryptedMessageChar(71));
                    break;
                case '#':
                    decryptedChar(selectedEncryptedMessageChar(72));
                    break;
                case '%':
                    decryptedChar(selectedEncryptedMessageChar(73));
                    break;
                case '&':
                    decryptedChar(selectedEncryptedMessageChar(74));
                    break;
                case '*':
                    decryptedChar(selectedEncryptedMessageChar(75));
                    break;
                case '(':
                    decryptedChar(selectedEncryptedMessageChar(76));
                    break;
                case ')':
                    decryptedChar(selectedEncryptedMessageChar(77));
                    break;
                case '-':
                    decryptedChar(selectedEncryptedMessageChar(78));
                    break;
                case '+':
                    decryptedChar(selectedEncryptedMessageChar(79));
                    break;
                case '=':
                    decryptedChar(selectedEncryptedMessageChar(80));
                    break;
                case '<':
                    decryptedChar(selectedEncryptedMessageChar(81));
                    break;
                case '>':
                    decryptedChar(selectedEncryptedMessageChar(82));
                    break;
                case '"':
                    decryptedChar(selectedEncryptedMessageChar(83));
                    break;
                case '\'':
                    decryptedChar(selectedEncryptedMessageChar(84));
                    break;
                case ' ':
                    decryptedChar(selectedEncryptedMessageChar(85));
                    break;
                case '[':
                    decryptedChar(selectedEncryptedMessageChar(86));
                    break;
                case ']':
                    decryptedChar(selectedEncryptedMessageChar(87));
                    break;
                case '{':
                    decryptedChar(selectedEncryptedMessageChar(88));
                    break;
                case '}':
                    decryptedChar(selectedEncryptedMessageChar(89));
                    break;
                case '_':
                    decryptedChar(selectedEncryptedMessageChar(90));
                    break;
                case '~':
                    decryptedChar(selectedEncryptedMessageChar(91));
                    break;
                case '|':
                    decryptedChar(selectedEncryptedMessageChar(92));
                    break;
                case '$':
                    decryptedChar(selectedEncryptedMessageChar(93));
                    break;
                case '`':
                    decryptedChar(selectedEncryptedMessageChar(94));
                    break;
                case '^':
                    decryptedChar(selectedEncryptedMessageChar(95));
                    break;
                default:
                    decryptedMessage += String.valueOf(d);
                    break;
            }
        }
        complete_decryptedMessage = messageafterdecryption(decryptedMessage);

        return complete_decryptedMessage;
    }

    void decryptedChar(int n) {
        switch (n) {
            case 1:
                decryptedMessage += "A";
                break;
            case 2:
                decryptedMessage += "B";
                break;
            case 3:
                decryptedMessage += "C";
                break;
            case 4:
                decryptedMessage += "D";
                break;
            case 5:
                decryptedMessage += "E";
                break;
            case 6:
                decryptedMessage += "F";
                break;
            case 7:
                decryptedMessage += "G";
                break;
            case 8:
                decryptedMessage += "H";
                break;
            case 9:
                decryptedMessage += "I";
                break;
            case 10:
                decryptedMessage += "J";
                break;
            case 11:
                decryptedMessage += "K";
                break;
            case 12:
                decryptedMessage += "L";
                break;
            case 13:
                decryptedMessage += "M";
                break;
            case 14:
                decryptedMessage += "N";
                break;
            case 15:
                decryptedMessage += "O";
                break;
            case 16:
                decryptedMessage += "P";
                break;
            case 17:
                decryptedMessage += "Q";
                break;
            case 18:
                decryptedMessage += "R";
                break;
            case 19:
                decryptedMessage += "S";
                break;
            case 20:
                decryptedMessage += "T";
                break;
            case 21:
                decryptedMessage += "U";
                break;
            case 22:
                decryptedMessage += "V";
                break;
            case 23:
                decryptedMessage += "W";
                break;
            case 24:
                decryptedMessage += "X";
                break;
            case 25:
                decryptedMessage += "Y";
                break;
            case 26:
                decryptedMessage += "Z";
                break;
            case 27:
                decryptedMessage += "a";
                break;
            case 28:
                decryptedMessage += "b";
                break;
            case 29:
                decryptedMessage += "c";
                break;
            case 30:
                decryptedMessage += "d";
                break;
            case 31:
                decryptedMessage += "e";
                break;
            case 32:
                decryptedMessage += "f";
                break;
            case 33:
                decryptedMessage += "g";
                break;
            case 34:
                decryptedMessage += "h";
                break;
            case 35:
                decryptedMessage += "i";
                break;
            case 36:
                decryptedMessage += "j";
                break;
            case 37:
                decryptedMessage += "k";
                break;
            case 38:
                decryptedMessage += "l";
                break;
            case 39:
                decryptedMessage += "m";
                break;
            case 40:
                decryptedMessage += "n";
                break;
            case 41:
                decryptedMessage += "o";
                break;
            case 42:
                decryptedMessage += "p";
                break;
            case 43:
                decryptedMessage += "q";
                break;
            case 44:
                decryptedMessage += "r";
                break;
            case 45:
                decryptedMessage += "s";
                break;
            case 46:
                decryptedMessage += "t";
                break;
            case 47:
                decryptedMessage += "u";
                break;
            case 48:
                decryptedMessage += "v";
                break;
            case 49:
                decryptedMessage += "w";
                break;
            case 50:
                decryptedMessage += "x";
                break;
            case 51:
                decryptedMessage += "y";
                break;
            case 52:
                decryptedMessage += "z";
                break;
            case 53:
                decryptedMessage += "0";
                break;
            case 54:
                decryptedMessage += "1";
                break;
            case 55:
                decryptedMessage += "2";
                break;
            case 56:
                decryptedMessage += "3";
                break;
            case 57:
                decryptedMessage += "4";
                break;
            case 58:
                decryptedMessage += "5";
                break;
            case 59:
                decryptedMessage += "6";
                break;
            case 60:
                decryptedMessage += "7";
                break;
            case 61:
                decryptedMessage += "8";
                break;
            case 62:
                decryptedMessage += "9";
                break;
            case 63:
                decryptedMessage += ",";
                break;
            case 64:
                decryptedMessage += ".";
                break;
            case 65:
                decryptedMessage += "?";
                break;
            case 66:
                decryptedMessage += "!";
                break;
            case 67:
                decryptedMessage += ";";
                break;
            case 68:
                decryptedMessage += ":";
                break;
            case 69:
                decryptedMessage += "/";
                break;
            case 70:
                decryptedMessage += "\\";
                break;
            case 71:
                decryptedMessage += "@";
                break;
            case 72:
                decryptedMessage += "#";
                break;
            case 73:
                decryptedMessage += "%";
                break;
            case 74:
                decryptedMessage += "&";
                break;
            case 75:
                decryptedMessage += "*";
                break;
            case 76:
                decryptedMessage += "(";
                break;
            case 77:
                decryptedMessage += ")";
                break;
            case 78:
                decryptedMessage += "-";
                break;
            case 79:
                decryptedMessage += "+";
                break;
            case 80:
                decryptedMessage += "=";
                break;
            case 81:
                decryptedMessage += "<";
                break;
            case 82:
                decryptedMessage += ">";
                break;
            case 83:
                decryptedMessage += "\"";
                break;
            case 84:
                decryptedMessage += "'";
                break;
            case 85:
                decryptedMessage += " ";
                break;
            case 86:
                decryptedMessage += "[";
                break;
            case 87:
                decryptedMessage += "]";
                break;
            case 88:
                decryptedMessage += "{";
                break;
            case 89:
                decryptedMessage += "}";
                break;
            case 90:
                decryptedMessage += "_";
                break;
            case 91:
                decryptedMessage += "~";
                break;
            case 92:
                decryptedMessage += "|";
                break;
            case 93:
                decryptedMessage += "$";
                break;
            case 94:
                decryptedMessage += "`";
                break;
            case 95:
                decryptedMessage += "^";
                break;
            default:
                int x = 95 - Math.abs(n);
                decryptedChar(x);
                break;
        }


    }

    String messageafterdecryption(String msg) {
        String message;
        String msg_1 = msg.substring(0, 14);
        String msg_2 = msg.substring(15);
        message = msg_1 + msg_2;
        return message;
    }

    public void savelastDecryptedMsg(String message) {
        // add-write text into file
        try {
            FileOutputStream fileout = openFileOutput("message.txt", MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(message);
            outputWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readlastDecryptedMsg() {
        //reading text from file
        String savedmessage = "";
        //reading text from file
        try {
            FileInputStream fileIn = openFileInput("message.txt");
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            int charRead;

            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                savedmessage += readstring;
            }
            InputRead.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return savedmessage;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {

        isRunning = false;
        unregisterReceiver(receiverWifi);
        stopSelf();
    }

    class WifiReceiver extends BroadcastReceiver {

        private List<ScanResult> wifiList;

        public void onReceive(Context c, Intent intent) {

            wifiList = mainWifi.getScanResults();

            for (int i = 0; i < wifiList.size(); i++) {

                wifiname = wifiList.get(i).SSID;
                if (!wifiname.isEmpty()) {
                    if (wifiname.charAt(0) == '0' || wifiname.charAt(0) == '1' || wifiname.charAt(0) == '2' || wifiname.charAt(0) == '3' || wifiname.charAt(0) == '4' || wifiname.charAt(0) == '5'
                            || wifiname.charAt(0) == '6' || wifiname.charAt(0) == '7' || wifiname.charAt(0) == '8' || wifiname.charAt(0) == '9') {
                        if (wifiname.charAt(1) == '0' || wifiname.charAt(1) == '1' || wifiname.charAt(1) == '2' || wifiname.charAt(1) == '3' || wifiname.charAt(1) == '4' || wifiname.charAt(1) == '5'
                                || wifiname.charAt(1) == '6' || wifiname.charAt(1) == '7' || wifiname.charAt(1) == '8' || wifiname.charAt(1) == '9') {
                            if (wifiname.charAt(16) == '6') {
                                String encrypt_msg = "";
                                encrypt_msg = wifiname.substring(2);
                                String notify_message = "";
                                notify_message = decryptMessage(encrypt_msg);
                                if (readlastDecryptedMsg().isEmpty()) {
                                    savelastDecryptedMsg(notify_message);
                                    Notify(notify_message, 0);
                                } else if (notify_message.equals(readlastDecryptedMsg())) {
                                    //do nothing
                                } else if (!notify_message.equals(readlastDecryptedMsg())) {
                                    savelastDecryptedMsg(notify_message);
                                    Notify(notify_message, 0);
                                }
                            }
                        }
                    } else {
                        // do nothing
                    }
                } else {
                    // do nothing
                }
            }
        }

    }

}