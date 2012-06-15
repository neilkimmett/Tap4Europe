package tap.europe;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.nfc.tech.TagTechnology;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.QuickContactBadge;
import android.widget.RatingBar;
import android.widget.TextView;
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
	private QuickContactBadge contact;
	private SharedPreferences mPrefs;
	private DatabaseHelper dbHelper;
	
	private static final double leuvenLong = 50.877621;
	private static final double leuvenLat = 4.704321;
	
	private int points;
	private String _id;
	private byte[] tagID;
	private IntentFilter[] mWriteTagFilters;
	private PendingIntent mNfcPendingIntent;
	private NfcAdapter mNfcAdapter;
	private String[][] mTechLists;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.home);

	    
	    //Set current Map location view to Leuven area
	    setLocation(); 
	    
	    dbHelper = new DatabaseHelper(this);
	    setMapView();
	    
	    setIntentFilters();
	    
	    setContactBadge();
	    
	    setPoints();
	    
	    modifyRatingBar();
	}

	private void setPoints() 
	{
		TextView pointsView = (TextView) findViewById(R.id.points_number);
		dbHelper.openDataBase();
		
		Cursor cursor = dbHelper.getPoints(Integer.parseInt(_id));
		cursor.moveToFirst();
		
		while(cursor.isAfterLast() == false)
		{
			points = cursor.getInt(1);
			pointsView.setText(points);
		}
		
		cursor.close();
		dbHelper.close();
	}

	private void setContactBadge() 
	{
		mPrefs = getSharedPreferences("europeana", MODE_PRIVATE);
	    _id = mPrefs.getString("fb_id", null);
		contact = (QuickContactBadge) findViewById(R.id.quickContactBadge1);
	    URL img_value = null;
	    try {
			img_value = new URL("http://graph.facebook.com/"+_id+"/picture?type=large");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	    Bitmap bm = null;
	    try {
			bm = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    contact.setImageBitmap(bm);
	}

	private void modifyRatingBar() 
	{
		RatingBar ratingBar = (RatingBar)findViewById(R.id.ratingBar1);
		ratingBar.setFocusable(false);
		ratingBar.setOnTouchListener(new OnTouchListener() 
		{
		        public boolean onTouch(View v, MotionEvent event) 
		        {
		            return true;
		        }
		    });
	}

	private void setIntentFilters() 
	{
		this.mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		this.mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		
		IntentFilter tag = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
	    try {
	        tag.addDataType("*/*");
	        tag.addDataScheme("http");
	    } catch (MalformedMimeTypeException e) {
	        throw new RuntimeException("fail", e);
	    }
		
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
	    try {
	        ndef.addDataType("*/*");
	        tag.addDataScheme("http");
	    } catch (MalformedMimeTypeException e) {
	        throw new RuntimeException("fail", e);
	    }

	    IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
	    try {
	        tech.addDataType("*/*");
	        tag.addDataScheme("http");
	    } catch (MalformedMimeTypeException e) {
	        throw new RuntimeException("fail", e);
	    }

	    this.mWriteTagFilters = new IntentFilter[] { tag, ndef, tech };
		
	    this.mTechLists = new String[][] { new String[] { NfcA.class.getName(),
	            NfcB.class.getName(), NfcF.class.getName(),
	            NfcV.class.getName(), IsoDep.class.getName(),
	            MifareClassic.class.getName(),
	            MifareUltralight.class.getName(), Ndef.class.getName(), TagTechnology.class.getName() } };
	}	

	@Override
	protected void onPause() {
		super.onPause();
		mNfcAdapter.disableForegroundDispatch(this);
	}

	@Override
	protected void onResume() {
		
		super.onResume();
		
		//if (mNfcAdapter != null) mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mWriteTagFilters, mTechLists);
		
		mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mWriteTagFilters, mTechLists);
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
		return false;
	}
		
	@Override
	public void onNewIntent(Intent intent) {
		
		String action = intent.getAction();

	    if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
	        // reag TagTechnology object...
	    } else if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
	        // read NDEF message...
	    } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

	    }
	    
	    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	   
	    tagID = tag.getId();
	    
	    Ndef ndefTag = Ndef.get(tag);
	    int size = ndefTag.getMaxSize();         // tag size
	    boolean writable = ndefTag.isWritable(); // is tag writable?
	    String type = ndefTag.getType();         // tag type
	    
	            
        String convertToHex = ByteArrayToHexString(tagID);
        Log.e("Home", convertToHex);
        
        startIntent(convertToHex);
	}
	
	private void startIntent(String convertToHex) 
	{
		//Send LocID according to Tag used
		int LocID = 0;
		if(convertToHex.compareTo("04249DF5") == 0)
		{
			LocID = 1;
		}
		else
		{
			LocID = 2;
		}
		
		Toast.makeText(getApplicationContext(), "Loc ID found = " + LocID, Toast.LENGTH_SHORT).show();
		
		checkPlaceVisited(LocID);
		
		Intent toContent = new Intent();
		toContent.setClass(getApplicationContext(), DetailActivity.class);
		toContent.putExtra("id", LocID);
		startActivity(toContent);
	}

	private void checkPlaceVisited(int locID) 
	{
		dbHelper.openDataBase();
		EuropeanaLocation loc = dbHelper.getLocationById(locID);
		
		if(loc.getVisited() == 1)
		{
			Toast.makeText(getApplicationContext(), "You already visited this place!", Toast.LENGTH_LONG).show();
		}
		else
		{
			Toast.makeText(getApplicationContext(), "Congratulations on visiting this new site in Leuven! You are being awarded 20 points!", Toast.LENGTH_LONG).show();
			dbHelper.updatePoints(Integer.parseInt(_id), locID);
		}
		
		dbHelper.close();
	}

	public static byte[] getBytes(Serializable obj) throws IOException 
	{
	    ByteArrayOutputStream bos   = new ByteArrayOutputStream();
	    ObjectOutputStream oos      = new ObjectOutputStream(bos);
	    oos.writeObject(obj);

	    byte[] data = bos.toByteArray();

	    oos.close();
	    return data;
	}
	
	String ByteArrayToHexString(byte [] inarray) 
	{
		int i, j, in;
		String [] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
		String out= "";

		for(j = 0 ; j < inarray.length ; ++j) 
		{
			in = (int) inarray[j] & 0xff;
			i = (in >> 4) & 0x0f;
			out += hex[i];
			i = in & 0x0f;
			out += hex[i];
		}
		return out;
	}
}
