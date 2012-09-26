package com.example.touchappsample;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

public class MainActivity extends Activity {
    private NfcAdapter mNfcAdapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    public void onResume() {
        super.onResume();
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // NFC非搭載端末
            finish();
            return;
        }

        if (!mNfcAdapter.isEnabled()) {
            // NFC機能オフなので設定画面へ遷移
            Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
            startActivity(intent);
            return;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);
        IntentFilter[] intentFilters = new IntentFilter[] {
                new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
        };
        String[][] techLists = {
                {
					android.nfc.tech.NfcA.class.getName(), 
					android.nfc.tech.NfcB.class.getName(),
					android.nfc.tech.IsoDep.class.getName(),
					android.nfc.tech.MifareClassic.class.getName(),
					android.nfc.tech.MifareUltralight.class.getName(),
					android.nfc.tech.NdefFormatable.class.getName(),
					android.nfc.tech.NfcV.class.getName(),
					android.nfc.tech.NfcF.class.getName(),
                }
        };
        mNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, techLists);
    }

    @Override
    public void onPause() {
        super.onPause();
        // ForegroundDispatch OFF
        mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String action = intent.getAction();
        if (action.equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            byte[] rawId = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            Toast.makeText(this, rawId.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
