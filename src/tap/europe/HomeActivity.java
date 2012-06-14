package tap.europe;

import java.io.IOException;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import tap.europe.*;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class HomeActivity extends MapActivity {
	
	private Location currentLoc;
	private double longitude, latitude;
	private GeoPoint gPoint;
	private MapView mapView;
	
	private static final double leuvenLong = 50.877621;
	private static final double leuvenLat = 4.704321;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.home);
	    
	    /** Debug stuff for Neil 
	    Intent myIntent = new Intent(HomeActivity.this, DetailActivity.class);
	    HomeActivity.this.startActivity(myIntent);
	    ***/
	    
	    //Set current Map location view to Leuven area
	    setLocation();
	    
	    setMapView();
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
		mapView.setClickable(false);
		
		addMapOverlays();
		
		MapController mapController = mapView.getController();
		mapController.setZoom(16);
		mapController.animateTo(gPoint);
	}

	private void addMapOverlays() {
		
		DatabaseHelper dbHelper = new DatabaseHelper(this);
		
		try {
			dbHelper.createDataBase();
		} catch (IOException e) {
			throw new Error("Unable to create database");
		}
		
		dbHelper.openDataBase();
		
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
}
