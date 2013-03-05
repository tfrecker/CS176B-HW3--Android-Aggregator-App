package com.example.cs176bhw3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.OpenRequest;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.model.GraphUser;

public class MainActivity extends Activity {

    public static Facebook facebook;
    @SuppressWarnings("deprecation")
    public static AsyncFacebookRunner mAsyncRunner;
  private Button buttonFacebookLoginLogout;
	private Button buttonFacebookReload;
	private TextView userInfoTextView;
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private String notifications;
	private String graph_or_fql;
	boolean loggedOut = false;
	
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);

	    buttonFacebookLoginLogout = (Button)findViewById(R.id.FacebookLogin);
	    buttonFacebookReload = (Button)findViewById(R.id.FacebookReload);
	    //buttonFacebookLoginLogout.setBackgroundResource(R.drawable.facebook_icon);
	    //buttonFacebookReload.setBackgroundResource(R.drawable.facebook_icon);
	    
	    userInfoTextView = (TextView) this.findViewById(R.id.userInfoTextView);
	    userInfoTextView.setMovementMethod(new ScrollingMovementMethod());
	    
	    Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }
        
        buttonFacebookReload.setOnClickListener(new OnClickListener() {
            public void onClick(View view) { updateFacebook(); }
        });
        
        updateFacebookView();
	  }

	  @SuppressWarnings("deprecation")
	public void updateFacebook() {
		  final Session session = Session.getActiveSession();
		  // make request to the /me API
          Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

            // callback after Graph API response with user object
            @Override
            public void onCompleted(GraphUser user, Response response) {
              if (user != null) {
            	//Toast toast = Toast.makeText(getBaseContext(), "Welcome " + user.getName() + "!", 3);
            	//toast.setGravity(Gravity.TOP|Gravity.RIGHT, 0, 100);
            	//toast.show();
            	//userInfoTextView.setText("Welcome " + user.getName() + "!" + "\n");
            	//userInfoTextView.scrollTo(0, 0);
              }
            }
          });
          
          String token=session.getAccessToken();
          String url = "https://graph.facebook.com/me/home?access_token=" + token;
          String result = connect(url);
          Log.d("url request",url);
          userInfoTextView.setText(result);
          userInfoTextView.scrollTo(0, 0);
          //String test = connect("https://fbcdn-photos-a.akamaihd.net/hphotos-ak-prn1/525420_10151460445072969_319446972_s.jpg");
          // show The Image
    	  //new DownloadImageTask((ImageView) findViewById(R.id.imageView1)).execute("http://fbcdn-photos-a.akamaihd.net/hphotos-ak-ash3/526254_10200757875132620_397691449_s.jpg");
          //Log.d("jpg request",test);
	  }
	  
	  private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	      ImageView bmImage;

	      public DownloadImageTask(ImageView bmImage) {
	          this.bmImage = bmImage;
	      }

	      protected Bitmap doInBackground(String... urls) {
	          String urldisplay = urls[0];
	          Bitmap mIcon11 = null;
	          try {
	              InputStream in = new java.net.URL(urldisplay).openStream();
	              mIcon11 = BitmapFactory.decodeStream(in);
	          } catch (Exception e) {
	              Log.e("Error", e.getMessage());
	              e.printStackTrace();
	          }
	          return mIcon11;
	      }

	      protected void onPostExecute(Bitmap result) {
	          bmImage.setImageBitmap(result);
	      }
	  }
	  
	  public static String connect(String url)
	  {
		  String result = null;
	      HttpClient httpclient = new DefaultHttpClient();

	      // Prepare a request object
	      HttpGet httpget = new HttpGet(url); 

	      // Execute the request
	      HttpResponse response;
	      try {
	          response = httpclient.execute(httpget);
	          // Examine the response status
	          Log.i("Praeda",response.getStatusLine().toString());

	          // Get hold of the response entity
	          HttpEntity entity = response.getEntity();
	          // If the response does not enclose an entity, there is no need
	          // to worry about connection release

	          if (entity != null) {

	              // A Simple JSON Response Read
	              InputStream instream = entity.getContent();
	              result= convertStreamToString(instream);
	              // now you have the string representation of the HTML request
	              instream.close();
	              Log.d("result",result);
	          }


	      } catch (Exception e) {}
		return result;
	  }

      private static String convertStreamToString(InputStream is) {
	      /*
	       * To convert the InputStream to String we use the BufferedReader.readLine()
	       * method. We iterate until the BufferedReader return null which means
	       * there's no more data to read. Each line will appended to a StringBuilder
	       * and returned as String.
	       */
	      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	      StringBuilder sb = new StringBuilder();

	      String line = null;
	      try {
	          while ((line = reader.readLine()) != null) {
	              sb.append(line + "\n");
	          }
	      } catch (IOException e) {
	          e.printStackTrace();
	      } finally {
	          try {
	              is.close();
	          } catch (IOException e) {
	              e.printStackTrace();
	          }
	      }
	      return sb.toString();
	  }
	  
	  
      
	  public void FacebookLogin(){
	// start Facebook Login
	    Session.openActiveSession(this, true, new Session.StatusCallback() {

	      // callback when session changes state
	      @Override
	      public void call(Session session, SessionState state, Exception exception) {
	        if (session.isOpened()) {

	          // make request to the /me API
	          Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

	            // callback after Graph API response with user object
	            @Override
	            public void onCompleted(GraphUser user, Response response) {
	              if (user != null) {
	            	Toast toast = Toast.makeText(getBaseContext(), "Welcome " + user.getName() + "!", 3);
	            	toast.setGravity(Gravity.TOP|Gravity.RIGHT, 0, 100);
	            	toast.show();
	                //TextView welcome = (TextView) findViewById(R.id.welcome);
	                //welcome.setText("Hello " + user.getName() + "!");'
	            	//buttonFacebookLoginLogout.setText(R.string.Facebooklogout);
		            //buttonFacebookLoginLogout.setOnClickListener(new OnClickListener() {
		            //    public void onClick(View view) { FacebookLogin(); }
		            //});
	            	updateFacebookView();
	              }
	            }
	          });
	        }
	      }
	    });
	  }
	  
	    private void updateFacebookView() {
	        Session session = Session.getActiveSession();
	        if (session.isOpened()) {
	            buttonFacebookLoginLogout.setText(R.string.Facebooklogout);
	            userInfoTextView.setVisibility(View.VISIBLE);
	            buttonFacebookLoginLogout.setOnClickListener(new OnClickListener() {
	                public void onClick(View view) { onClickLogout(); }
	            });
	        } else {
	            buttonFacebookLoginLogout.setText(R.string.Facebooklogin);
	            userInfoTextView.setVisibility(View.INVISIBLE);
	            buttonFacebookLoginLogout.setOnClickListener(new OnClickListener() {
	                public void onClick(View view) { onClickLogin(); }
	            });
	        }
	    }
	    
	    private void onClickLogin() {
	        Session session = Session.getActiveSession();
	        if (session != null && !session.isOpened() && loggedOut == false) {
	        	Log.d("test","got here!");
	            OpenRequest openRequest = new OpenRequest(this).setCallback(statusCallback); // HERE IT IS OKAY TO EXECUTE THE CALLBACK BECAUSE WE'VE GOT THE PERMISSIONS
	            if (openRequest != null) {
	                openRequest.setDefaultAudience(SessionDefaultAudience.FRIENDS);
	                openRequest.setPermissions(Arrays.asList("read_stream"));
	                openRequest.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
	                session.openForRead(openRequest);
	            }
	        }
	        if (!session.isOpened() && !session.isClosed()) {
	            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
	        } else {
	            Session.openActiveSession(this, true, statusCallback);
	        }
	    }

	    private void onClickLogout() {
	        Session session = Session.getActiveSession();
	        if (!session.isClosed()) {
	            session.closeAndClearTokenInformation();
	            loggedOut = true;
	            //Log.d("Generated : ", session.getExpirationDate().toString());
	        }
	    }
	  
	    private class SessionStatusCallback implements Session.StatusCallback {
	        @Override
	        public void call(Session session, SessionState state, Exception exception) {
	            updateFacebookView();
	        }
	    }
	    
	  @Override
	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
	      super.onActivityResult(requestCode, resultCode, data);
	      Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	  }

	}
