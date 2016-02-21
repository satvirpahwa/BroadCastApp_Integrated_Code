package com.euroitlabs.broadcastapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConfigureBroadcastersActivity extends AppCompatActivity implements View.OnClickListener {

    private Button savebtn, backbtn;
    private EditText et_name1, et_pin1, et_name2, et_pin2;
  //  private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_broadcasters);

        et_name1 = (EditText) findViewById(R.id.Name1);
        et_pin1 = (EditText) findViewById(R.id.Pin1);
        et_name2 = (EditText) findViewById(R.id.Name2);
        et_pin2 = (EditText) findViewById(R.id.Pin2);

        savebtn = (Button) findViewById(R.id.buttonsave);
        backbtn = (Button) findViewById(R.id.buttonback);
//        dbManager = new DBManager(this);
//        dbManager.open();
        savebtn.setOnClickListener(this);
        backbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonsave:
                final String name1 = et_name1.getText().toString();
                final String pin1 = et_pin1.getText().toString();

           //     dbManager.insert(name1, pin1);
                Toast.makeText(getApplicationContext(),"save button clicked",Toast.LENGTH_LONG).show();
                break;
            case R.id.buttonback:
//                Cursor cursor = dbManager.fetch();
//                while(cursor.isAfterLast() == false){
//                    et_name2.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
//                    et_pin2.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PIN)));
            //        Toast.makeText(getApplicationContext(),cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)),Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(),"back button clicked",Toast.LENGTH_LONG).show();
 //               }
                break;
        }
    }

}
