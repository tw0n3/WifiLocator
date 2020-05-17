package com.utkarsh.wifilocator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public static final String DB_Name = "WIFI_INFO.db";
    public static final String TB_Name = "FINGERPRINT";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button scan_mainpage = (Button) findViewById(R.id.scan_mainpage);
        Button locate_mainpage = (Button) findViewById(R.id.locate_mainpage);


        scan_mainpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(i);

            }
        });


        locate_mainpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, LocateActivity.class);
                startActivity(i);

            }
        });
    }
}
