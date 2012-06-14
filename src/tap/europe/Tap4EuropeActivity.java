package tap.europe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class Tap4EuropeActivity extends Activity {
	
	private Facebook facebook = new Facebook("405791339459155");
    private String[] permissions = new String[] {"email", "publish_checkins"};
    private Button signinButton;
    private SharedPreferences mPrefs;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        mPrefs = getPreferences(MODE_PRIVATE);
        
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
        	signinButton = (Button)findViewById(R.id.button1);
            signinButton.setOnClickListener(new Button.OnClickListener(){

    			public void onClick(View v) 
    			{
    				authorizeFacebook();
    			}
            	
            });
        }
                
        Intent i = new Intent();
		i.setClass(this, HomeActivity.class);
		startActivity(i);	
    }
    
    private void authorizeFacebook()
    {
    	facebook.authorize(this, permissions, new DialogListener(){
        	
            public void onComplete(Bundle values) {}

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