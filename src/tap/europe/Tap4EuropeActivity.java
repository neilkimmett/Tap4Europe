package tap.europe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class Tap4EuropeActivity extends Activity {
	
	Facebook facebook = new Facebook("405791339459155");
    String[] permissions = new String[] {"email", "publish_checkins"};
    Button signinButton;
    TextView skipLoginView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        signinButton = (Button)findViewById(R.id.facebook_button);
        signinButton.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) 
			{
				authorizeFacebook();
			}
        	
        });
        
        skipLoginView = (TextView)findViewById(R.id.skip_login_text_view);
        skipLoginView.setOnClickListener(new Button.OnClickListener(){
        	public void onClick(View v) 
			{
				authorizeFacebook();
			}
        });
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