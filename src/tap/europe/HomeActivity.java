package tap.europe;

import java.io.IOException;
import java.util.List;

<<<<<<< HEAD
import android.content.Intent;
=======
import android.database.Cursor;
import android.graphics.drawable.Drawable;
>>>>>>> c3f680e87b04c5a69c7e91575c76a97bc5712b4d
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
<<<<<<< HEAD
import android.view.MenuItem;
import tap.europe.*;
=======
>>>>>>> c3f680e87b04c5a69c7e91575c76a97bc5712b4d

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
}
