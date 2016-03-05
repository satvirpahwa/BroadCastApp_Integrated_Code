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

public class MainActivity extends Activity implements View.OnClickListener {

    private Button configbroadcastersbtn, startreceivebroadcastsbtn, stopreceivebroadcastsbtn;
    ConfigureBroadcastersActivity configure;
    String abc;
    WifiManager mainWifi;

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
        //    abc = ConfigureBroadcastersActivity.readReceiverName();
//        Intent intent = new Intent();
//        abc = intent.getStringExtra("value");
        configbroadcastersbtn.setOnClickListener(this);
        startreceivebroadcastsbtn.setOnClickListener(this);
        stopreceivebroadcastsbtn.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                startActivity(new Intent(getApplicationContext(), ConfigureBroadcastersActivity.class));
                break;
            case R.id.button2:

//                Log.i("MainActivity", "names value = " + ConfigureBroadcastersActivity.check(configure));


                //      Toast.makeText(getApplicationContext(), String.valueOf(ReadWriteFile.getInstance().getString().length()), Toast.LENGTH_SHORT).show();
                if (SingletonFile.getInstance().getString().isEmpty()) {
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
                        Utils.customToast(this, "Receive broadcast messages enabled");
                    } else {
                      //  Utils.customToast(this, "Already enabled");
                    }
                }
                break;
            case R.id.button3:
                if (isMyServiceRunning(MyService.class)) {
                    if (mainWifi.isWifiEnabled()) {
                        stopService(new Intent(this, MyService.class));
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