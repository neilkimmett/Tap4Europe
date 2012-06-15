package tap.europe;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.digibis.europeana4j.EuropeanaItem;
import com.digibis.europeana4j.EuropeanaQuery;
import com.digibis.europeana4j.EuropeanaResults;

public class DetailActivity extends ListActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Open database
		DatabaseHelper dbHelper = new DatabaseHelper(this);
		try {
			dbHelper.createDataBase();
		} catch (IOException e) {
			throw new Error("Unable to create database");
		}
		dbHelper.openDataBase();

		// Get info from database given ID
		Bundle extras = getIntent().getExtras(); 
		Integer id;

		if (extras != null) {
		    id = extras.getInt("id");
		}
		else
		{
			id = new Integer(2);
		}
		
		Cursor cursor = dbHelper.getPlaceById(id);
		cursor.moveToFirst();

		// Get name of object
		String name = cursor.getString(1);

		// Tidy up
		cursor.close();
		dbHelper.close();

		// Construct query for Europeana API
		EuropeanaQuery query = new EuropeanaQuery(name);
		ListView list = getListView();

		// Add header view
		View header1 =  getLayoutInflater().inflate(R.layout.list_header, null, false);
		TextView location = (TextView)header1.findViewById(R.id.location);
		location.setText(name);

		list.addHeaderView(header1, null, false);

		AsyncAPIQuery asyncquery = new AsyncAPIQuery();
		try {
			EuropeanaResults results = asyncquery.execute(query).get();

			setListAdapter(new DetailListAdapter(results.getAllItems()));

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		// Creating an item click listener, to open/close our toolbar for each item
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

				View extraContent = view.findViewById(R.id.extra_content);

				// Creating the expand animation for the item
				ExpandAnimation expandAni = new ExpandAnimation(extraContent, 500);

				// Start the animation on the toolbar
				extraContent.startAnimation(expandAni);
				
				/*
				ImageView imageView = (ImageView)view.findViewById(R.id.image);
			
				
				ScaleAnimation scale = new ScaleAnimation((float)1.0, (float)1.5, (float)1.0, (float)1.5);
			    scale.setFillAfter(true);
			    scale.setDuration(500);
			    imageView.startAnimation(scale);
			    
			    
				ViewGroup.LayoutParams params = imageView.getLayoutParams();
				params.height = 200;
				params.width = 200;
				imageView.setLayoutParams(params);
				*/
			}
		});
	}

	public Bitmap getRemoteImage(final URL aURL) {
		try {
			final URLConnection conn = aURL.openConnection();
			conn.connect();
			final BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
			final Bitmap bm = BitmapFactory.decodeStream(bis);
			bis.close();
			return bm;
		} catch (IOException e) {}
		return null;
	}

	class DetailListAdapter extends BaseAdapter {
		List<EuropeanaItem> items;
		DetailListAdapter(List<EuropeanaItem> newItems){
			items = newItems;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			final int pos = position;
			if(row == null){
				//getting custom layout to the row
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.detail_list_row, parent, false);
			}

			final EuropeanaItem item = items.get(pos);

			//get the reference to the textview of your row. find the item with row.findViewById()
			TextView label = (TextView)row.findViewById(R.id.label);
			label.setText(item.getTitle());

			TextView description = (TextView)row.findViewById(R.id.description);
			description.setText(item.getDescription());

			
			// Resets the extra content to be closed
			View extraContent = row.findViewById(R.id.extra_content);
			((LinearLayout.LayoutParams) extraContent.getLayoutParams()).bottomMargin = -50;
			extraContent.setVisibility(View.GONE);

			ImageView imageView = (ImageView)row.findViewById(R.id.image);
			URL imageURL;
			try {
				imageURL = new URL(item.getBestThumbnail());
				AsyncBitmapFetcher fetcher = new AsyncBitmapFetcher();
				Bitmap bitmap = fetcher.execute(imageURL).get();
				imageView.setImageBitmap(bitmap);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			ImageButton shareButton = (ImageButton)row.findViewById(R.id.share_button);
			shareButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
					shareIntent.setType("text/plain");
					shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, item.getTitle());
					shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, item.getDescription());

					startActivity(Intent.createChooser(shareIntent, "Share Europeana item"));
					}
					});

			
			
			return row; //the row that ListView draws
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return items.size();
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return items.get(arg0);
		}

		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}
	}
}
