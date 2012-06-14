package tap.europe;

import java.io.IOException;
import java.util.List;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class HomeActivity extends MapActivity {
	
	private Location currentLoc;
	private double longitude, latitude;
	private GeoPoint gPoint;
	private MapView mapView;
	
	private static final double leuvenLong = 50.877621;
	private static final double leuvenLat = 4.704321;
	
	private int idtoWrite = 1;
	private boolean mWriteMode;
	private IntentFilter[] mWriteTagFilters;
	private PendingIntent mNfcPendingIntent;
	private NfcAdapter mNfcAdapter;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.home);

	    
	    //Set current Map location view to Leuven area
	    setLocation(); 
	    
	    setMapView();
	}
	
	
	
	@Override
	protected void onResume() {
		
		super.onResume();
		
		enableTagWriteMode();
	}
	
	
	/*
	 * Sets the currentLoc to point to the Leuven area
	 */


	private void setLocation() {
		
		Location leuven = new Location("Leuven");
		leuven.setLongitude(leuvenLong);
		leuven.setLatitude(leuvenLat);
		
		currentLoc = leuven;	
	}

	private void setMapView() {
		
		longitude = currentLoc.getLongitude() * 1E6;
		latitude = currentLoc.getLatitude() * 1E6;
		gPoint = new GeoPoint((int)longitude, (int)latitude);
		
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(false);
		mapView.setSatellite(false);
		mapView.setActivated(false);
		mapView.setClickable(true);
		
		addMapOverlays();
		
		MapController mapController = mapView.getController();
		mapController.setZoom(16);
		mapController.animateTo(gPoint);
	}

	private void addMapOverlays() {
		
		List<Overlay> mapOverlays = mapView.getOverlays();
		
		Drawable drawable_visited = this.getResources().getDrawable(R.drawable.point);
		Drawable drawable_notvisited = this.getResources().getDrawable(R.drawable.point);
		
		EuropeanaOverlay notvisitedOverlay = new EuropeanaOverlay(drawable_notvisited, this);
		EuropeanaOverlay visitedOverlay = new EuropeanaOverlay(drawable_visited, this);
		
		DatabaseHelper dbHelper = new DatabaseHelper(this);
		
		try {
			dbHelper.createDataBase();
		} catch (IOException e) {
			throw new Error("Unable to create database");
		}
		
		dbHelper.openDataBase();
		
		Cursor cursor = dbHelper.getPlaces();
		cursor.moveToFirst();
		
		while(cursor.isAfterLast() == false)
		{
			String name = cursor.getString(1);
			int longitude = (int) (cursor.getDouble(2) * 1E6);
			int latitude = (int) (cursor.getDouble(3) * 1E6);
			String description = (cursor.getString(4));
			int visited = cursor.getInt(5);
			
			GeoPoint point = new GeoPoint(longitude, latitude);
			OverlayItem item = new OverlayItem(point, name, description);
			
			if(visited == 1)
			{
				visitedOverlay.addOverlay(item);
			}
			else
			{
				notvisitedOverlay.addOverlay(item);
			}
			
			cursor.moveToNext();
		}
		
		if(notvisitedOverlay.size() >= 1)
		{
			mapOverlays.add(notvisitedOverlay);
		}
		if(visitedOverlay.size() >= 1)
		{
			mapOverlays.add(visitedOverlay);
		}
		
		cursor.close();
		dbHelper.close();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
private void enableTagWriteMode() {
		
		mWriteMode = true;
	    IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
	    mNfcAdapter = NfcAdapter.getDefaultAdapter();
	    mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 0);
	    mWriteTagFilters = new IntentFilter[] { tagDetected };
	    mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mWriteTagFilters, null);
		
	}
	
	@Override
	public void onNewIntent(Intent intent) {
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
