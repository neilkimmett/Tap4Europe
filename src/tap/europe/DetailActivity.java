package tap.europe;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.ListActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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

		EuropeanaQuery query = new EuropeanaQuery("Sint-Pieterskerk");

		AsyncAPIQuery asyncquery = new AsyncAPIQuery();
		try {
			EuropeanaResults results = asyncquery.execute(query).get();
			
			//setListAdapter(new ArrayAdapter<String>(this,R.layout.detail_list_row, R.id.label, COUNTRIES));
			setListAdapter(new DetailListAdapter(results.getAllItems()));

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        ListView list = getListView();

		// Creating an item click listener, to open/close our toolbar for each item
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

                View toolbar = view.findViewById(R.id.extra_content);

                // Creating the expand animation for the item
                ExpandAnimation expandAni = new ExpandAnimation(toolbar, 500);

                // Start the animation on the toolbar
                toolbar.startAnimation(expandAni);
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
		    
		    EuropeanaItem item = items.get(pos);
		    
		    //get the reference to the textview of your row. find the item with row.findViewById()
		    TextView label = (TextView)row.findViewById(R.id.label);
		    label.setText(item.getTitle());
		    
		    TextView description = (TextView)row.findViewById(R.id.description);
		    description.setText(item.getDescription());
		    
		 // Resets the extra content to be closed
            View extraContent = row.findViewById(R.id.extra_content);
            ((LinearLayout.LayoutParams) extraContent.getLayoutParams()).bottomMargin = -50;
            extraContent.setVisibility(View.GONE);

		    if (item.isImage())
		    {
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
		    }
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
