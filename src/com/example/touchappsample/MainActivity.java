package com.example.touchappsample;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.touchappsample.net.JSONLoader;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements LoaderCallbacks<JSONObject> {
    private NfcAdapter mNfcAdapter;

    private static final int LOADER_ADD = 0;
    
    private static final String KEY_URL_ADD = "url_add";
    
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        
        setContentView(R.layout.main);
        setProgressBarIndeterminateVisibility(false);
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
        	/*
            byte[] rawId = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            Toast.makeText(this, rawId.toString(), Toast.LENGTH_SHORT).show();
            */
        	Bundle bundle = new Bundle();
        	bundle.putString(KEY_URL_ADD, "http://api.svartalfheim.jp/vote/add");
        	if(getLoaderManager().getLoader(LOADER_ADD) == null){
        		getLoaderManager().initLoader(LOADER_ADD, bundle, this);
        	}else{
        		getLoaderManager().restartLoader(LOADER_ADD, bundle, this);
        	}
        }
    }

	@Override
	public Loader<JSONObject> onCreateLoader(int id, Bundle args) {
		if(id == LOADER_ADD){
			setProgressBarIndeterminateVisibility(true);
			
			return new JSONLoader(getApplicationContext(), args.getString(KEY_URL_ADD));
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<JSONObject> loader, JSONObject json) {
		try {
			String count = json.getString("count");
			TextView textView = (TextView) findViewById(R.id.textView1);
			textView.setText("COUNT="+count);
			
			setProgressBarIndeterminateVisibility(false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onLoaderReset(Loader<JSONObject> loader) {
		
	}
}
