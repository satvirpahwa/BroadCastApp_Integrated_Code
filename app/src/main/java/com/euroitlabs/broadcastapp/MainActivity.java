package com.euroitlabs.broadcastapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.FileInputStream;
import java.io.InputStreamReader;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button configbroadcastersbtn, startreceivebroadcastsbtn, stopreceivebroadcastsbtn;
    ConfigureBroadcastersActivity configure;
    String abc;
    WifiManager mainWifi;
    static final int READ_BLOCK_SIZE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //   configure = new ConfigureBroadcastersActivity();
        configbroadcastersbtn = (Button) findViewById(R.id.button1);
        startreceivebroadcastsbtn = (Button) findViewById(R.id.button2);
        stopreceivebroadcastsbtn = (Button) findViewById(R.id.button3);
        setTitle("Main Menu");
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        configbroadcastersbtn.setOnClickListener(this);
        startreceivebroadcastsbtn.setOnClickListener(this);
        stopreceivebroadcastsbtn.setOnClickListener(this);
        //     startreceivebroadcastsbtn.setClickable(false);
//        startreceivebroadcastsbtn.setEnabled(false);
//        startreceivebroadcastsbtn.setBackgroundColor(Color.GRAY);
        if (isMyServiceRunning(MyService.class)) {
            startreceivebroadcastsbtn.setEnabled(false);
            startreceivebroadcastsbtn.setBackgroundResource(R.drawable.custom_btn_disabled);
        } else {
               stopreceivebroadcastsbtn.setEnabled(false);
          //  stopreceivebroadcastsbtn.setVisibility(View.GONE);
              stopreceivebroadcastsbtn.setBackgroundResource(R.drawable.custom_btn_disabled);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public String checkReceiverName() {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                startActivity(new Intent(getApplicationContext(), ConfigureBroadcastersActivity.class));
                break;
            case R.id.button2:
  //                    Toast.makeText(getApplicationContext(), checkReceiverName(), Toast.LENGTH_SHORT).show();
                if (checkReceiverName().isEmpty()) {
//                    Toast toast = Toast.makeText(this, "Please configure Broadcasters for receiving messages", Toast.LENGTH_SHORT);
//                    View view = toast.getView();
//                    view.setBackgroundResource(R.drawable.custom_toast);
//                    toast.show();
                    Utils.customToast(this, "Please configure Broadcasters for receiving messages");
                } else {
                    if (!isMyServiceRunning(MyService.class)) {
                        if (mainWifi.isWifiEnabled()) {
                            startService(new Intent(getApplicationContext(), MyService.class));
                            //  Toast.makeText(this, "Receive broadcast messages enabled", Toast.LENGTH_SHORT).show();
                            //   Utils.customToast(this, "Receive broadcast messages enabled 1");
                        } else {
                            if (Utils.turnOnOffWifi(getApplicationContext(), true)) {
                                startService(new Intent(getApplicationContext(), MyService.class));
                                //  Toast.makeText(this, "Receive broadcast messages enabled", Toast.LENGTH_SHORT).show();
                                //    Utils.customToast(this, "Receive broadcast messages enabled 2");
                            }
                        }
                        startreceivebroadcastsbtn.setEnabled(false);
                        startreceivebroadcastsbtn.setBackgroundResource(R.drawable.custom_btn_disabled);
                        stopreceivebroadcastsbtn.setEnabled(true);
                            stopreceivebroadcastsbtn.setBackgroundResource(R.drawable.custom_btn_shakespeare);
                        Utils.customToast(this, "Receive broadcast messages enabled");
                    } else {
                        //  Utils.customToast(this, "Already enabled");

                        //   stopreceivebroadcastsbtn.setBackgroundColor(Color.GRAY);

                    }
                }
                break;
            case R.id.button3:
                if (isMyServiceRunning(MyService.class)) {
                    if (mainWifi.isWifiEnabled()) {
                        stopService(new Intent(this, MyService.class));
                        startreceivebroadcastsbtn.setEnabled(true);
                       startreceivebroadcastsbtn.setBackgroundResource(R.drawable.custom_btn_shakespeare);
                        stopreceivebroadcastsbtn.setEnabled(false);
                          stopreceivebroadcastsbtn.setBackgroundResource(R.drawable.custom_btn_disabled);
                        // Toast.makeText(this, "Receiving Broadcasts stopped", Toast.LENGTH_SHORT).show();
                        Utils.customToast(this, "Receiving Broadcasts stopped");
                    }
                } else {
                    //   Toast.makeText(this, "No Receive Broadcast service enabled", Toast.LENGTH_SHORT).show();
                    //  Utils.customToast(this, "No Receive Broadcast service enabled");
                }
                break;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_I_am_broadcaster:
                //   Toast.makeText(this, "Option I am Broadcaster", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(getApplicationContext(), BroadcasterActivity.class);
                startActivity(in);
                return true;
            case R.id.action_help:
                //   Toast.makeText(this, "Option help", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //  Toast.makeText(this, "Back pressed", Toast.LENGTH_SHORT).show();
        MainActivity.this.finish();
    }
}