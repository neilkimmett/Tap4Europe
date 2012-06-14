package tap.europe;

import java.io.IOException;

import com.digibis.europeana4j.EuropeanaConnection;
import com.digibis.europeana4j.EuropeanaQuery;
import com.digibis.europeana4j.EuropeanaResults;

import android.os.AsyncTask;
import android.util.Log;

public class AsyncAPIQuery extends AsyncTask<EuropeanaQuery, Void, EuropeanaResults> {
    private Exception exception;
	
    protected void onPostExecute(EuropeanaResults results) {
        // TODO: check this.exception
    	Log.d("APIResults","Results finished downloading");
		Log.d("APIResults",results.toJSON());
    }

	@Override
	protected EuropeanaResults doInBackground(EuropeanaQuery... queries) {
		EuropeanaConnection conn = new EuropeanaConnection(EuropeanaAPIKey.EUROPEANA_API_KEY);
	    try {
			EuropeanaResults results = conn.search(queries[0],10);
			return results;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.exception = e;
			return null;
		}
	}

}
