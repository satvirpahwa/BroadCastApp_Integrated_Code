package com.euroitlabs.broadcastapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button configbroadcastersbtn, startreceivebroadcastsbtn, stopreceivebroadcastsbtn;
    ConfigureBroadcastersActivity configure ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configbroadcastersbtn = (Button) findViewById(R.id.button1);
        startreceivebroadcastsbtn = (Button) findViewById(R.id.button2);
        stopreceivebroadcastsbtn = (Button) findViewById(R.id.button3);

        configure = new ConfigureBroadcastersActivity();
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
                if(!configure.readReceiverPin().isEmpty())
                startService(new Intent(this, MyService.class));
                else
                    Toast.makeText(this, "Please configure Broadcasters for receiving messages.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button3:
                stopService(new Intent(this, MyService.class));
                break;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_I_am_broadcaster:
                Toast.makeText(this, "Option I am Broadcaster", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(getApplicationContext(), BroadcasterActivity.class);
                startActivity(in);
                return true;
            case R.id.action_help:
                Toast.makeText(this, "Option help", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}