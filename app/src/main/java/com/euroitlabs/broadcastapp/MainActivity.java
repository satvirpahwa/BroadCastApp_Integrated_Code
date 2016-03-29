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
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.InputStreamReader;

public class MainActivity extends Activity implements View.OnClickListener {

    Button configbroadcastersbtn, startreceivebroadcastsbtn, stopreceivebroadcastsbtn;
    WifiManager mainWifi;
    static final int READ_BLOCK_SIZE = 100;
    boolean wifiFlag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configbroadcastersbtn = (Button) findViewById(R.id.button1);
        startreceivebroadcastsbtn = (Button) findViewById(R.id.button2);
        stopreceivebroadcastsbtn = (Button) findViewById(R.id.button3);

        setTitle("Main Menu");
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        configbroadcastersbtn.setOnClickListener(this);
        startreceivebroadcastsbtn.setOnClickListener(this);
        stopreceivebroadcastsbtn.setOnClickListener(this);

        if (isMyServiceRunning(MyService.class)) {
            startreceivebroadcastsbtn.setEnabled(false);
            startreceivebroadcastsbtn.setBackgroundResource(R.drawable.custom_btn_disabled);
        } else {
            stopreceivebroadcastsbtn.setEnabled(false);
            stopreceivebroadcastsbtn.setBackgroundResource(R.drawable.custom_btn_disabled);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Checking recv_name.txt file
     * for any receiver
     */
    public String checkReceiverName() {
        //reading text from file
        String sender_name = "";
        //reading text from file
        try {
            FileInputStream fileIn = openFileInput("recv_name.txt");
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            int charRead;

            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                sender_name += readstring;
            }
            InputRead.close();

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
                if (checkReceiverName().isEmpty()) {
                    Toast.makeText(this, "Please configure Broadcasters for receiving messages", Toast.LENGTH_SHORT).show();
                } else {
                    if (!isMyServiceRunning(MyService.class)) {
                        if (mainWifi.isWifiEnabled()) {
                            wifiFlag = true;
                            startService(new Intent(getApplicationContext(), MyService.class));
                        } else {
                            if (Utils.turnOnOffWifi(getApplicationContext(), true)) {
                                startService(new Intent(getApplicationContext(), MyService.class));
                            }
                        }
                        startreceivebroadcastsbtn.setEnabled(false);
                        startreceivebroadcastsbtn.setBackgroundResource(R.drawable.custom_btn_disabled);
                        stopreceivebroadcastsbtn.setEnabled(true);
                        stopreceivebroadcastsbtn.setBackgroundResource(R.drawable.custom_btn_shakespeare);
                        Toast.makeText(this, "Receiving Broadcast Message started", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.button3:
                if (isMyServiceRunning(MyService.class)) {
                    if (mainWifi.isWifiEnabled() && wifiFlag) {
                        wifiFlag = false;
                        stopService(new Intent(this, MyService.class));
                        startreceivebroadcastsbtn.setEnabled(true);
                        startreceivebroadcastsbtn.setBackgroundResource(R.drawable.custom_btn_shakespeare);
                        stopreceivebroadcastsbtn.setEnabled(false);
                        stopreceivebroadcastsbtn.setBackgroundResource(R.drawable.custom_btn_disabled);
                        Toast.makeText(this, "Receiving Broadcast Message stopped", Toast.LENGTH_SHORT).show();
                    } else if (mainWifi.isWifiEnabled() && !wifiFlag) {
                        stopService(new Intent(this, MyService.class));
                        Utils.turnOnOffWifi(getApplicationContext(), false);
                        startreceivebroadcastsbtn.setEnabled(true);
                        startreceivebroadcastsbtn.setBackgroundResource(R.drawable.custom_btn_shakespeare);
                        stopreceivebroadcastsbtn.setEnabled(false);
                        stopreceivebroadcastsbtn.setBackgroundResource(R.drawable.custom_btn_disabled);
                        Toast.makeText(this, "Receiving Broadcast Message stopped", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_I_am_broadcaster:
                Intent in = new Intent(getApplicationContext(), BroadcasterActivity.class);
                startActivity(in);
                return true;
            case R.id.action_help:
                Intent i = new Intent(getApplicationContext(), ReceiverHelpActivity.class);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.this.finish();
    }
}