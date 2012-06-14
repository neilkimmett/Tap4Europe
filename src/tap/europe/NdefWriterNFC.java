package tap.europe;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.widget.Toast;

public class NdefWriterNFC extends Activity {

	private int idtoWrite = 1;
	private boolean mWriteMode;
	private IntentFilter[] mWriteTagFilters;
	private PendingIntent mNfcPendingIntent;
	private NfcAdapter mNfcAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    enableTagWriteMode();
	}

	private void enableTagWriteMode() {
		
		mWriteMode = true;
	    IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
	    mWriteTagFilters = new IntentFilter[] { tagDetected };
	    mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mWriteTagFilters, null);
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
	    // Tag writing mode
	    if (mWriteMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
	        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	        if (NfcUtils.writeTag(NfcUtils.getPlaceidAsNdef(idtoWrite), detectedTag)) {
	            Toast.makeText(this, "Success: Wrote placeid to nfc tag", Toast.LENGTH_LONG)
	                .show();
	        } 
	        else 
	        {
	            Toast.makeText(this, "Write failed", Toast.LENGTH_LONG).show();
	        }
	    }
	}

}
