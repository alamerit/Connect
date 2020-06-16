package com.example.connect;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AndroidExample";

    private static final int MY_REQUEST_CODE = 123;

    private WifiManager wifiManager;
    private Button buttonScan;

    private EditText editTextPassword;
    private LinearLayout linearLayoutScanResults;
    private TextView textViewScanResults;

    private WifiBroadcastReceiver wifiReceiver;

    private Connect connect = new Connect();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonScan = findViewById(R.id.button_scan);
        editTextPassword = findViewById(R.id.editText_password);
        textViewScanResults = findViewById(R.id.textView_scanResults);
        linearLayoutScanResults = findViewById(R.id.linearLayout_scanResults);

        goIo();

        this.buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askAndStartScanWifi();
            }
        });
    }

    private void goIo() {
        this.wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        this.wifiReceiver = new WifiBroadcastReceiver();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        askAndStartScanWifi();
    }


    private void askAndStartScanWifi() {


        // for permission to Call.
        // if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) { // 23
        //     int permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
//
        //     // Check for permissions
        //     if (permission1 != PackageManager.PERMISSION_GRANTED) {
//
        //         Log.d(LOG_TAG, "Requesting Permissions");
//
        //         // Request permissions
        //       //  ActivityCompat.requestPermissions(this,
        //       //          new String[] {
        //       //                  Manifest.permission.ACCESS_COARSE_LOCATION,
        //       //                  Manifest.permission.ACCESS_FINE_LOCATION,
        //       //                  Manifest.permission.ACCESS_WIFI_STATE,
        //       //                  Manifest.permission.ACCESS_NETWORK_STATE
        //       //          }, MY_REQUEST_CODE);
        //         return;
        //     }
        //     Log.d(LOG_TAG, "Permissions Already Granted");
        // }
        this.doStartScanWifi();
    }

    private void doStartScanWifi() {
        this.wifiManager.startScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        Log.d(LOG_TAG, "onRequestPermissionsResult");

        if (requestCode == MY_REQUEST_CODE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted
                Log.d(LOG_TAG, "Permission Granted: " + permissions[0]);
                this.doStartScanWifi();
            } else {
                Log.d(LOG_TAG, "Permission Denied: " + permissions[0]);
            }
        }
    }

    private void showWifiState() {
        int state = this.wifiManager.getWifiState();
        String statusInfo = "Unknown";

        switch (state) {
            case WifiManager.WIFI_STATE_DISABLING:
                statusInfo = "Disabling";
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                statusInfo = "Disabled";
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                statusInfo = "Enabling";
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                statusInfo = "Enabled";
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
                statusInfo = "Unknown";
                break;
            default:
                statusInfo = "Unknown";
                break;
        }
        Toast.makeText(this, "Wifi Status: " + statusInfo, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        this.unregisterReceiver(this.wifiReceiver);
        super.onStop();
    }

    private void showNetworks(List<ScanResult> results) {
        this.linearLayoutScanResults.removeAllViews();

        for (final ScanResult result : results) {
            final String networkCapabilities = result.capabilities;
            final String networkSSID = result.SSID; // Network Name.
            //
            Button button = new Button(this);

            button.setText(networkSSID + " (" + networkCapabilities + ")");
            this.linearLayoutScanResults.addView(button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String networkCapabilities = result.capabilities;
                    connect.connectToNetwork(networkCapabilities, networkSSID, editTextPassword.getText().toString(), wifiManager);
                }
            });
        }
    }

    private void showNetworksDetails(List<ScanResult> results) {

        this.textViewScanResults.setText("");
        StringBuilder sb = new StringBuilder();
        sb.append("Result Count: " + results.size());
        for (int i = 0; i < results.size(); i++) {
            ScanResult result = results.get(i);
            sb.append("\n\n  --------- Network " + i + "/" + results.size() + " ---------");
            sb.append("\n result.capabilities: " + result.capabilities);
            sb.append("\n result.SSID: " + result.SSID); // Network Name.
            sb.append("\n result.BSSID: " + result.BSSID);
            sb.append("\n result.frequency: " + result.frequency);
            sb.append("\n result.level: " + result.level);
            sb.append("\n result.describeContents(): " + result.describeContents());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) { // Level 17, Android 4.2
                sb.append("\n result.timestamp: " + result.timestamp);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Level 23, Android 6.0
                sb.append("\n result.centerFreq0: " + result.centerFreq0);
                sb.append("\n result.centerFreq1: " + result.centerFreq1);
                sb.append("\n result.venueName: " + result.venueName);
                sb.append("\n result.operatorFriendlyName: " + result.operatorFriendlyName);
                sb.append("\n result.channelWidth: " + result.channelWidth);
                sb.append("\n result.is80211mcResponder(): " + result.is80211mcResponder());
                sb.append("\n result.isPasspointNetwork(): " + result.isPasspointNetwork());
            }
        }
        this.textViewScanResults.setText(sb.toString());
    }

    // Define class to listen to broadcasts
    class WifiBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "onReceive()");

            Toast.makeText(MainActivity.this, "Scan Complete!", Toast.LENGTH_SHORT).show();

            boolean ok = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);

            if (ok) {
                Log.d(LOG_TAG, "Scan OK");

                List<ScanResult> list = wifiManager.getScanResults();

                MainActivity.this.showNetworks(list);
                MainActivity.this.showNetworksDetails(list);
            } else {
                Log.d(LOG_TAG, "Scan not OK");
            }

        }
    }


}