package tap.europe;

import java.io.IOException;

import com.digibis.europeana4j.EuropeanaConnection;
import com.digibis.europeana4j.EuropeanaQuery;
import com.digibis.europeana4j.EuropeanaResults;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class DetailActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    EuropeanaQuery query = new EuropeanaQuery("Sint-Pieterskerk");
	    
	    new AsyncAPIQuery().execute(query);
	}

}
