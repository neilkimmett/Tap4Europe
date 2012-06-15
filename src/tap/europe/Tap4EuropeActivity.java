package tap.europe;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

public class Tap4EuropeActivity extends Activity {
	
	private Facebook facebook = new Facebook("405791339459155");
    private String[] permissions = new String[] {"email", "publish_checkins"};
    private Button signinButton;
    private SharedPreferences mPrefs;
    TextView skipLoginView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.main);

        mPrefs = getSharedPreferences("europeana", MODE_PRIVATE);
        
        String access_token = mPrefs.getString("access_token", null);
        
        long expires = mPrefs.getLong("access_expires", 0);
        
        if(access_token != null) 
        {
            facebook.setAccessToken(access_token);
        }
        if(expires != 0) 
        {
            facebook.setAccessExpires(expires);
        }
        else if(access_token == null)
        {
        	signinButton = (Button)findViewById(R.id.facebook_button);
            signinButton.setOnClickListener(new Button.OnClickListener(){

    			public void onClick(View v) 
    			{
    				authorizeFacebook();
    			}
            	
            });
        }
                
        skipLoginView = (TextView)findViewById(R.id.skip_login_text_view);
        skipLoginView.setOnClickListener(new Button.OnClickListener()
        {
        	public void onClick(View v) 
			{
				authorizeFacebook();
			}
        });
   }
    
    private void startHome()
    {
    	Intent i = new Intent();
		i.setClass(this, HomeActivity.class);
		startActivity(i);
    }
    
    private void authorizeFacebook()
    {
    	
    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    	StrictMode.setThreadPolicy(policy); 
    	
    	facebook.authorize(this, permissions, new DialogListener(){
        	
            public void onComplete(Bundle values) 
            {
            	String jsonUser = null;
				try {
					jsonUser = facebook.request("me");
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	JSONObject obj = null;
				try {
					obj = Util.parseJson(jsonUser);
				} catch (FacebookError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
            	String facebookId = obj.optString("id");
            	String name = obj.optString("name");
            	
            	SharedPreferences.Editor editor = mPrefs.edit();
            	editor.putString("fb_name", name);
            	editor.putString("fb_id", facebookId);
            	editor.commit();
            	
            	Toast.makeText(getApplicationContext(), "Signed in with " + name, Toast.LENGTH_LONG).show();
            	
            	startHome();
            }

            public void onFacebookError(FacebookError error) {}

            public void onError(DialogError e) {}

            public void onCancel() {}
       	
       });
    }


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		
		facebook.authorizeCallback(requestCode, resultCode, data);	
	}
}