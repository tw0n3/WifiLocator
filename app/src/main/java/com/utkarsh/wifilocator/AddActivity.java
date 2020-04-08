package com.utkarsh.wifilocator;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddActivity extends AppCompatActivity {


    EditText editrpid,editrssi1,editrssi2,editrssi3;
    Button addData;
    DbHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        dbHelper = new DbHelper(this);

        editrpid = findViewById(R.id.getrpid);
        editrssi1 = findViewById(R.id.getrssi1);
        editrssi2 = findViewById(R.id.getrssi2);
        editrssi3 = findViewById(R.id.getrssi3);

        addData = findViewById(R.id.addbtn);

        AddData();

    }

    public void AddData(){
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isInserted = dbHelper.insertData(editrpid.getText().toString(),editrssi1.getText().toString(),editrssi2.getText().toString(),editrssi3.getText().toString());

                if (isInserted = true)
                    Toast.makeText(AddActivity.this,"Inserted",Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(AddActivity.this,"Not Inserted",Toast.LENGTH_LONG).show();
            }
        });
    }
}
