package tap.europe;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncBitmapFetcher extends AsyncTask<URL, Void, Bitmap> {

	@Override
	protected Bitmap doInBackground(URL... urls) {
		 try {
		        final URLConnection conn = urls[0].openConnection();
		        conn.connect();
		        final BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
		        final Bitmap bm = BitmapFactory.decodeStream(bis);
		        bis.close();
		        return bm;
		    } catch (IOException e) {
		    	Log.d("BitmapFail", "failed to download");
		    }
		    return null;
	}
}

