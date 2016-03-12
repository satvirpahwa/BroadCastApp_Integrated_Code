package com.euroitlabs.broadcastapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class ConfigureBroadcastersActivity extends Activity implements View.OnClickListener {

    private Button savebtn, backbtn;
    private EditText et_name1, et_pin1, et_name2, et_pin2, et_name3, et_pin3, et_name4, et_pin4, et_name5, et_pin5;
    //  private DBManager dbManager;
    static final int READ_BLOCK_SIZE = 100;
    static int c = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_broadcaster);

        et_name1 = (EditText) findViewById(R.id.Name1);
        et_pin1 = (EditText) findViewById(R.id.Pin1);
        et_name2 = (EditText) findViewById(R.id.Name2);
        et_pin2 = (EditText) findViewById(R.id.Pin2);
        et_name3 = (EditText) findViewById(R.id.Name3);
        et_pin3 = (EditText) findViewById(R.id.Pin3);
        et_name4 = (EditText) findViewById(R.id.Name4);
        et_pin4 = (EditText) findViewById(R.id.Pin4);
        et_name5 = (EditText) findViewById(R.id.Name5);
        et_pin5 = (EditText) findViewById(R.id.Pin5);

        savebtn = (Button) findViewById(R.id.buttonsave);
        backbtn = (Button) findViewById(R.id.buttonback);
        if (!readReceiverName().isEmpty()) {
            String get_recv_name = readReceiverName();
            String get_recv_pin = readReceiverPin();
//            Toast.makeText(getBaseContext(), get_recv_name,
//                    Toast.LENGTH_SHORT).show();
//            Toast.makeText(getBaseContext(), get_recv_pin,
//                    Toast.LENGTH_SHORT).show();
//            String[] names = new String[0];
//            String[] pins = new String[0];
            if (get_recv_name.contains(" ")) {
                Log.i("Configure", "inside contains whitespace");

                String[] names = get_recv_name.split(" ");
                String[] pins = get_recv_pin.split(" ");
                Log.i("Configure", "names array lengh = " + String.valueOf(names.length));
                //       for (int i = 0; i < names.length; i++) {
//                        Toast.makeText(getBaseContext(), String.valueOf(names.length),
//                                Toast.LENGTH_SHORT).show();
                if (names.length == 2) {
                    et_name1.setText(names[0]);
                    et_name2.setText(names[1]);
                    et_pin1.setText(pins[0]);
                    et_pin2.setText(pins[1]);
                } else if (names.length == 3) {
                    et_name1.setText(names[0]);
                    et_name2.setText(names[1]);
                    et_name3.setText(names[2]);
                    et_pin1.setText(pins[0]);
                    et_pin2.setText(pins[1]);
                    et_pin3.setText(pins[2]);
                } else if (names.length == 4) {
                    et_name1.setText(names[0]);
                    et_name2.setText(names[1]);
                    et_name3.setText(names[2]);
                    et_name4.setText(names[3]);
                    et_pin1.setText(pins[0]);
                    et_pin2.setText(pins[1]);
                    et_pin3.setText(pins[2]);
                    et_pin4.setText(pins[3]);
                } else if (names.length == 5) {
                    et_name1.setText(names[0]);
                    et_name2.setText(names[1]);
                    et_name3.setText(names[2]);
                    et_name4.setText(names[3]);
                    et_name5.setText(names[4]);
                    et_pin1.setText(pins[0]);
                    et_pin2.setText(pins[1]);
                    et_pin3.setText(pins[2]);
                    et_pin4.setText(pins[3]);
                    et_pin5.setText(pins[4]);
                } else {
                    et_name1.setText(names[0]);
                    et_pin1.setText(pins[0]);
                }
            } else {
                Log.i("Configure", "inside does not contains whitespace");
            }


        }
        //  ConfigureBroadcastersActivity c = new ConfigureBroadcastersActivity();
//        String x = readReceiverName();
//        Log.i("Configure", "names value = " + x);

//        dbManager = new DBManager(this);
//        dbManager.open();
        savebtn.setOnClickListener(this);
        backbtn.setOnClickListener(this);
    }

    public void saveReceiverName(String name) {
        // add-write text into file
        try {
            FileOutputStream fileout = openFileOutput("recv_name.txt", MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(String.valueOf(name));
            outputWriter.close();

            //display file saved message
//            Toast.makeText(getBaseContext(), "Broadcasters saved",
//                    Toast.LENGTH_SHORT).show();
            Utils.customToast(this, "Broadcasters saved");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readReceiverName() {
        //reading text from file
        String sender_name = "";
        //reading text from file
        try {
            FileInputStream fileIn = openFileInput("recv_name.txt");
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            String s = "";
            int charRead;

            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                sender_name += readstring;
            }
            InputRead.close();
            //    Toast.makeText(getBaseContext(), sender_pin, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sender_name;
    }

    public void saveReceiverPin(String pin) {
        // add-write text into file
        try {
            FileOutputStream fileout = openFileOutput("recv_pin.txt", MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(String.valueOf(pin));
            outputWriter.close();

            //display file saved message
//            Toast.makeText(getBaseContext(), "File saved successfully!",
//                    Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readReceiverPin() {
        //reading text from file
        String sender_pin = "";
        //reading text from file
        try {
            FileInputStream fileIn = openFileInput("recv_pin.txt");
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            String s = "";
            int charRead;

            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                sender_pin += readstring;
            }
            InputRead.close();
            //    Toast.makeText(getBaseContext(), sender_pin, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sender_pin;
    }

    public String check() {
        String str = "Pahwa";
        return str;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonsave:
                final String name1 = et_name1.getText().toString();
                final String pin1 = et_pin1.getText().toString();
                final String name2 = et_name2.getText().toString();
                final String pin2 = et_pin2.getText().toString();
                final String name3 = et_name3.getText().toString();
                final String pin3 = et_pin3.getText().toString();
                final String name4 = et_name4.getText().toString();
                final String pin4 = et_pin4.getText().toString();
                final String name5 = et_name5.getText().toString();
                final String pin5 = et_pin5.getText().toString();
                if ((!name1.isEmpty() && pin1.isEmpty()) || (!name2.isEmpty() && pin2.isEmpty()) || (!name3.isEmpty() && pin3.isEmpty()) || (!name4.isEmpty() && pin4.isEmpty()) || (!name5.isEmpty() && pin5.isEmpty())) {
                    //   Toast.makeText(getApplicationContext(), "Please enter pin corresponding to the name.", Toast.LENGTH_LONG).show();
                    Utils.customToast(this, "Please enter pin corresponding to the name");
                } else if ((name1.isEmpty() && !pin1.isEmpty()) || (name2.isEmpty() && !pin2.isEmpty()) || (name3.isEmpty() && !pin3.isEmpty()) || (name4.isEmpty() && !pin4.isEmpty()) || (name5.isEmpty() && !pin5.isEmpty())) {
                    //  Toast.makeText(getApplicationContext(), "Please enter name corresponding to the name.", Toast.LENGTH_LONG).show();
                    Utils.customToast(this, "Please enter name corresponding to the pin");

                } else if ((name1.isEmpty() && pin1.isEmpty()) && (name2.isEmpty() && pin2.isEmpty()) && (name3.isEmpty() && pin3.isEmpty()) && (name4.isEmpty() && pin4.isEmpty()) && (name5.isEmpty() && pin5.isEmpty())) {
                    saveReceiverPin("");
                    saveReceiverName("");
                    //   SingletonFile.getInstance().setString("");
                } else if (((!name1.isEmpty()) && (!pin1.isEmpty()) && (pin1.length()!=4)) || ((!name2.isEmpty()) && (!pin2.isEmpty()) && (pin2.length()!=4)) || ((!name3.isEmpty()) && (!pin3.isEmpty()) && (pin3.length()!=4)) || ((!name4.isEmpty()) && (!pin4.isEmpty()) && (pin4.length()!=4)) || ((!name5.isEmpty()) && (!pin5.isEmpty()) && (pin5.length()!=4))) {
                    Utils.customToast(this, "Please enter a valid 4 digit pin");
                } else {
                    //     dbManager.insert(name1, pin1);
                    String finalname = name1 + " " + name2 + " " + name3 + " " + name4 + " " + name5;
                    String finalpin = pin1 + " " + pin2 + " " + pin3 + " " + pin4 + " " + pin5;
                    saveReceiverName(finalname);
                    saveReceiverPin(finalpin);
                    //   SingletonFile.getInstance().setString(finalname);
                    //    check();
                    Log.i("Configure", "on click name :" + finalname);
                    Log.i("Configure", "on click pin :" + finalpin);
                    //     Toast.makeText(getApplicationContext(), "save button clicked", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.buttonback:
                //              Intent intent =
//                Log.i("Configure", "value of check :" + String.valueOf(check()));
//                intent.putExtra("value",String.valueOf(check()));
                Intent in = new Intent(getApplicationContext(), MainActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);
                //  Toast.makeText(getApplicationContext(), "back button clicked", Toast.LENGTH_LONG).show();
                //               }
                break;
        }
    }

}
