package com.euroitlabs.broadcastapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class NotificationView extends Activity implements View.OnClickListener {

    TextView notificationtxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_view);

        notificationtxt = (TextView) findViewById(R.id.text_notification);
        processIntent(getIntent());
        //    Intent intent = new Intent();


    }

    @Override
    protected void onNewIntent(Intent intent) {
        processIntent(intent);
    }

    ;

    private void processIntent(Intent intent) {
        //get your extras
        String message = intent.getStringExtra("message");
        notificationtxt.setText(message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notification_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_notification:

                break;
        }
    }
}
