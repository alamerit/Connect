package com.example.connect;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

public class Connect {

    void connectToNetwork(String networkCapabilities, String networkSSID, String pass, WifiManager wifiManager) {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes
        conf.status = WifiConfiguration.Status.ENABLED;
        conf.priority = 40;
        Log.d("Tag  -", networkSSID);
        //  Toast.makeText(this, "Connecting to network: "+ networkSSID, Toast.LENGTH_SHORT).show();
        Log.d("Tag  -", networkSSID);
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + networkSSID + "\"";
        if (networkCapabilities.toUpperCase().contains("WEP")) { // WEP Network.
            //   Toast.makeText(this, "WEP Network", Toast.LENGTH_SHORT).show();
            wifiConfig.wepKeys[0] = "\"" + pass + "\"";
            wifiConfig.wepTxKeyIndex = 0;
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        } else if (networkCapabilities.toUpperCase().contains("WPA")) { // WPA Network
            Log.v("rht", "Configuring WPA");
            conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

            conf.preSharedKey = "\"" + pass + "\"";


            // Toast.makeText(this, "WPA Network" + pass, Toast.LENGTH_SHORT).show();
        } else {
            //  Toast.makeText(this, "OPEN Network", Toast.LENGTH_SHORT).show();
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }

        int networkId = wifiManager.addNetwork(conf);

        Log.v("rht", "Add result " + networkId);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                Log.v("rht", "WifiConfiguration SSID " + i.SSID);

                boolean isDisconnected = wifiManager.disconnect();
                Log.v("rht", "isDisconnected : " + isDisconnected);

                boolean isEnabled = wifiManager.enableNetwork(i.networkId, true);
                Log.v("rht", "isEnabled : " + isEnabled);

                boolean isReconnected = wifiManager.reconnect();
                Log.v("rht", "isReconnected : " + isReconnected);

                break;
            }
        }
    }
}
