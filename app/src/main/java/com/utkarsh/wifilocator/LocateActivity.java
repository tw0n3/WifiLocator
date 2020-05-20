package com.utkarsh.wifilocator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class LocateActivity extends AppCompatActivity {
    public static final String DB_Name = "WIFI_INFO.db";
    public static final String TB_Name = "FINGERPRINT";

    public static final String AP_1 = "Girls";
    public static final String AP_2 = "Girls";
    public static final String AP_3 = "Girls";
    public static int K = 5;

    TextView textView;
    Button buttonLocate, buttonTable;

    WifiManager wifiManager;
    SQLiteDatabase db;

    List<ScanResult> results;
    int[] currentRssi;
    int[][] fingerprint;
    byte[][] kNNarray;
    int rowCount;
    int[] finalCoords = new int[2];

    //rpid, r1, r2, r3, x, y

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate);

        results = new ArrayList<>();
        db = openOrCreateDatabase(MainActivity.DB_Name, MODE_PRIVATE, null);
        textView = findViewById(R.id.textView);
        buttonLocate = findViewById(R.id.btn_locate);
        buttonTable = findViewById(R.id.btn_table);


        long x = getRowCount(db);
        int y = (int)x;
        fingerprint = new int[y][6];
        fingerprint = getFingerprint(db, TB_Name, fingerprint);


        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "WiFi is currently disabled.. enabling WiFi...", Toast.LENGTH_SHORT).show();
            wifiManager.setWifiEnabled(true);
        }

        buttonLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanWifi();
            }
        });

        buttonTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText(getTableAsString(db,TB_Name));
            }
        });
    }

    private void scanWifi() {
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] currentRssi = new byte[3];
            results = wifiManager.getScanResults();
            unregisterReceiver(this);
            for (ScanResult scanResult : results) {
                if(scanResult.SSID.equals(AP_1)){
                    currentRssi[0]= currentRssi[1] = currentRssi[2] = (byte) -scanResult.level;
                } else if(scanResult.SSID.equals(AP_2)){
                    currentRssi[1] = (byte)scanResult.level;
                } else if(scanResult.SSID.equals(AP_3)){
                    currentRssi[2] = (byte)scanResult.level;
                }
            }
//          textView.setText("\nExpected Location is : \n x =" + finalCoords[0] + "\t y = " + finalCoords[1] + "\n");
            long rowCount1 = getRowCount(db);
            String string = " : 1 : " + currentRssi[0] +  "\t: 2 :" + currentRssi[1] + "\t: 3 :" + currentRssi[2] +
                    "\n" + (kNN(currentRssi, fingerprint, rowCount1, kNNarray, K));
            textView.setText(string);
        }
    };

    public int[][] getFingerprint(SQLiteDatabase db, String tableName, int[][] fingerprint) {
        int count = 0;
        Cursor allRows  = db.rawQuery("SELECT * FROM " + TB_Name, null);
        if (allRows.moveToFirst()){
            do {
                for (int i = 0; i < 6; i++) {
                    fingerprint[count][i] = allRows.getInt(i);
                }
                count++;
            } while (allRows.moveToNext());
        }
        return fingerprint;
    }

    public String kNN(byte[] currentRssi, int[][] fingerprint, long rowCount1, byte[][] kNNarray, int k){

        int rowCount = (int) rowCount1;
        kNNarray = new byte[rowCount][4];
        int[] finalCoords = new int[2];
        byte[][] slicedArray = new byte[k][4];
        byte byteDistance;
        int SGSum = 0, D = 0, Es = 0;
        int count = 0;
        finalCoords[0] = finalCoords[1] = 0;
        String resultString = new String();
        String sliceArrayString = new String();
        String kNNArrayString1 = new String();
        String kNNArrayString2 = new String();
        String fingerprintString = new String();
        String finalCoordsString = new String();


        fingerprintString += "rpid" + " \t " + "rssi1" + " \t " + "rssi2" + " \t " +"rssi3" + " \t " +"x" + " \t " +"y" + "\n" ;
        kNNArrayString1 += "rpid" + " \t " + "distance" + " \t " +"x" + " \t " +"y" + "\n" ;
        kNNArrayString2 += "rpid" + " \t " + "distance" + " \t " +"x" + " \t " +"y" + "\n" ;
        sliceArrayString += "rpid" + " \t " + "distance" + " \t " +"x" + " \t " +"y" + "\n" ;

        for(int i = 0; i < rowCount; i++) {

            double distance = ((fingerprint[i][1] - currentRssi[0]) * (fingerprint[i][1] - currentRssi[0])) +
                              ((fingerprint[i][1] - currentRssi[1]) * (fingerprint[i][1] - currentRssi[1])) +
                              ((fingerprint[i][1] - currentRssi[2]) * (fingerprint[i][1] - currentRssi[2]));
            distance = Math.round(Math.sqrt(distance));
            distance = Math.abs(distance);
            byteDistance = (byte) distance;
            kNNarray[i][0] = (byte) fingerprint[i][0];
            kNNarray[i][1] = byteDistance;
            kNNarray[i][2] = (byte) fingerprint[i][4];
            kNNarray[i][3] = (byte) fingerprint[i][5];

            kNNArrayString1 +=  " " + kNNarray[i][0] + " \t\t\t " + kNNarray[i][1]
                    + " \t\t\t\t\t "+ kNNarray[i][2] + " \t\t " + kNNarray[i][3]
                    + "\n";

            fingerprintString +=  " " + fingerprint[i][0] + " \t\t\t " + fingerprint[i][1]
                    + " \t\t\t "+ fingerprint[i][2] + " \t\t " + fingerprint[i][3] + " \t\t\t " + fingerprint[i][4]
                    + " \t\t " + fingerprint[i][5] + "\n";
        }
            kNNarray = sortArrayKtimes(rowCount, kNNarray, k);

            for (int i = 0; i < k; i++) {
                slicedArray[i][0] = kNNarray[i][0];
                slicedArray[i][1] = kNNarray[i][1];
                slicedArray[i][2] = kNNarray[i][2];
                slicedArray[i][3] = kNNarray[i][3];

                kNNArrayString2 +=  " " + kNNarray[i][0] + " \t\t\t " + kNNarray[i][1]
                                       + " \t\t\t\t\t "+ kNNarray[i][2] + " \t\t " + kNNarray[i][3]
                                       + "\n";



                sliceArrayString +=  " " + slicedArray[i][0] + " \t\t\t " + slicedArray[i][1]
                                         + " \t\t\t\t\t "+ slicedArray[i][2] + " \t\t " + slicedArray[i][3]
                                         + "\n";

                D = D + slicedArray[i][1];
            }

            SGSum = slicedArray[k-1][2];
            SGSum = Math.abs((k * SGSum) - D);
            Es = SGSum / (k - 1);

            for ( int i = 0; i < k; i++) {
//               if (slicedArray[i][1] < Es) {
//                   slicedArray[i][0] = -1;
//                   slicedArray[i][1] = -1;
//               } else {
//                  finalCoords[0] = finalCoords[0] + slicedArray[i][2];
//                  finalCoords[1] = finalCoords[1] + slicedArray[i][3];
                    count++;
//               }

                finalCoords[0] = finalCoords[0] + slicedArray[i][2];
                finalCoords[1] = finalCoords[1] + slicedArray[i][3];

            }
        finalCoordsString += "[" + finalCoords[0] + "] , [" + finalCoords[1] + "]";


        finalCoords[0] = finalCoords[0] / count;
                finalCoords[1] = finalCoords[1] / count;

        resultString += "count = " + count + "\nEstimated Position (X,Y) : (" + finalCoords[0] + "," + finalCoords[1] + ") "+
                        "\n current rssi values received = " + currentRssi[0] + ", " + currentRssi[1] + ", " + currentRssi[2];
        resultString += "\nfingerprintString = \n" + fingerprintString;
        resultString += "\nkNNArrayPreSort = \n" + kNNArrayString1;
        resultString += "kNNArrayPostSort = \n" + kNNArrayString2;

        resultString += "slicedArray = \n" + sliceArrayString;
        resultString += "finalCoords before dividing by count = \n" + finalCoordsString;

        return resultString;
    }

    public byte[][] sortArrayKtimes(int rowCount, byte[][] kNNarray, int k) {
        byte temp; int count = 0;
        for (int i = 0; i < rowCount; i++){
            for(int j = 0; j < k; j++){
                if (kNNarray[i][1] > kNNarray[j][1]) {

                    temp = kNNarray[i][1];
                    kNNarray[i][1] = kNNarray[j][1];
                    kNNarray[j][1] = temp;

                }
            }
        }
        return kNNarray;
    }

    public void clearTable(SQLiteDatabase db, String tableName){
        db.execSQL("delete from "+ TB_Name);
    }

    public long getRowCount(SQLiteDatabase db) {
        long count = DatabaseUtils.queryNumEntries(db, TB_Name);
        return count;
    }

    public String getTableAsString(SQLiteDatabase db, String tableName) {
        int count = 0;
        String tableString = String.format("Table %s:\n", TB_Name);
        Cursor allRows  = db.rawQuery("SELECT * FROM " + TB_Name, null);
        if (allRows.moveToFirst() ){
            String[] columnNames = allRows.getColumnNames();
            tableString += columnNames[0] + "\t\t\t\t" + columnNames[1] + "\t\t\t\t" + columnNames[2]
                    + "\t\t\t\t"+ columnNames[3] + "\t\t\t\t"+ columnNames[4] + "\t\t\t\t"+ columnNames[5] + "\t\n";
            do {
                for (int i = 0; i < 6; i++) {
                    tableString += allRows.getInt(i) + "\t\t\t\t\t\t";
                }
                tableString += "\n";
                count++;
            } while (allRows.moveToNext());
            tableString += "rowCount = " + getRowCount(db) + "\n";
        }
        return tableString;
    }

}

