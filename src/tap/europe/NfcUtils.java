package tap.europe;

import java.io.IOException;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;

public class NfcUtils {
	
	/*
	* Converts a Long into a NdefMessage in application/vnd.facebook.places MIMEtype.
	*
	* for writing Places
	*/
	public static NdefMessage getPlaceidAsNdef(int idtoWrite) {
	    String msg = Integer.toString(idtoWrite);
	    byte[] textBytes = msg.getBytes();
	    NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
	        "application/tap.europe".getBytes(), new byte[] {}, textBytes);
	    return new NdefMessage(new NdefRecord[] { textRecord });
	}
	
	/*
	* Writes an NdefMessage to a NFC tag
	*/
	public static boolean writeTag(NdefMessage message, Tag tag) {
	    int size = message.toByteArray().length;
	    try {
	        Ndef ndef = Ndef.get(tag);
	        if (ndef != null) {
	            ndef.connect();
	            if (!ndef.isWritable()) {
	                return false;
	            }
	            if (ndef.getMaxSize() < size) {
	                return false;
	            }
	            ndef.writeNdefMessage(message);
	            return true;
	        } else {
	            NdefFormatable format = NdefFormatable.get(tag);
	            if (format != null) {
	                try {
	                    format.connect();
	                    format.format(message);
	                    return true;
	                } catch (IOException e) {
	                    return false;
	                }
	            } else {
	                return false;
	            }
	        }
	    } catch (Exception e) {
	        return false;
	    }
	}

}
