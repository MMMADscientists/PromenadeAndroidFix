package com.promenadevt;

import static com.jockeyjs.NativeOS.nativeOS;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.promenadevt.android.R;
import com.promenadevt.library.Constants;
import com.promenadevt.library.UserFunctions;
import android.webkit.JsResult;
import android.util.Log;
import android.widget.Toast;


import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.jockeyjs.Jockey;
import com.jockeyjs.JockeyAsyncHandler;
import com.jockeyjs.JockeyCallback;
import com.jockeyjs.JockeyHandler;
import com.jockeyjs.JockeyImpl;


public class ConnectActivity extends Activity {

	private static String dbID;
	private static String propID;
	private static String currConID;
	UserFunctions userFunctions;
	public WebView webView;
	private Jockey jockey;
	
	
	ImageButton btnUp;
	ImageButton btnDown;
	Button btnConfirm;
	TextView name;
	ViewSwitcher switcher;
	
	private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";
    private static String KEY_ERROR_MSG = "error_msg";
    private static String KEY_TUPLE = "tuples";
    private static String KEY_IDROOM = "idRoom";
    private static String KEY_ROOMNAME = "name";
    private static String KEY_ROOMURL = "roomURL";
    private static String KEY_IDPROPERTY = "idProperty";
    private static int index = 0;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_room);
		
		Intent intent = getIntent();
		propID = intent.getStringExtra("propID");
		dbID = intent.getStringExtra("id");
		
		webView = (WebView) findViewById(R.id.webView);
		
		btnUp = (ImageButton) findViewById(R.id.up);
		btnDown = (ImageButton) findViewById(R.id.down);
		btnConfirm = (Button) findViewById(R.id.confirmDest);
		switcher = (ViewSwitcher) findViewById(R.id.connectionSwitch);
		name = (TextView) findViewById(R.id.destName);
		name.setText("No Connection Selected");
		final UserFunctions userFunction = new UserFunctions();
		JSONObject json = userFunction.getRooms(dbID);
    	
    	//lists of recieved data
    	final List<Integer> idRoom = new ArrayList<Integer>();
    	final List<String> roomName = new ArrayList<String>();
    	//final List<String> roomURL = new ArrayList<String>();
    	
    	try {
                if (json.getString(KEY_SUCCESS) != null) {
                    //loginErrorMsg.setText("");
                    String res = json.getString(KEY_SUCCESS); 
                    if(Integer.parseInt(res) == 1){
                    	JSONArray tuples = json.getJSONArray(KEY_TUPLE);
                    	for(int i = 0; i< tuples.length(); i++){
                    		JSONObject curTuple = tuples.getJSONObject(i);
                    		idRoom.add(curTuple.getInt(KEY_IDROOM));
                    		roomName.add(curTuple.getString(KEY_ROOMNAME));
                    		//roomURL.add(curTuple.getString(KEY_ROOMURL));
                    	}
                    }
                        
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
    	
    	if(idRoom.size() >= 2 && idRoom.get(index).toString() == dbID)
    	{
    		index++;
    	}
    	name.setText(roomName.get(index));
    	
    	btnUp.setOnClickListener(new View.OnClickListener() 
		{

			@Override
			public void onClick(View arg0) {
				
				if(index == 0)
				{
					index = idRoom.size()-1;
				}
				else
				{
					index--;
				}
				name.setText(roomName.get(index));
			}
			 
		 });
    	
    	btnDown.setOnClickListener(new View.OnClickListener() 
		{

			@Override
			public void onClick(View arg0) {
				
				if(index == idRoom.size()-1)
				{
					index = 0;
				}
				else
				{
					index++;
				}
				name.setText(roomName.get(index));

			}
			 
		 });
    	
    	btnConfirm.setOnClickListener(new View.OnClickListener() 
		{

			@Override
			public void onClick(View arg0) {
				
				/* update connection in db with id currConId
				 	send jockeyjs message to webgl
				 	context switch
				 
				 */
				switcher.showPrevious();
			}
			 
		 });
    	
	}
	
	@Override
	protected void onStart() {
		super.onStart();

		jockey = JockeyImpl.getDefault();

		jockey.configure(webView);

		jockey.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				Log.d("webViewClient", "page finished loading!");
			}
		});


		setJockeyEvents();

		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					JsResult result) {
				Toast.makeText(ConnectActivity.this, message, Toast.LENGTH_SHORT)
						.show();
				result.confirm();
				return true;
			}
		});
		// use webgl url
		webView.loadUrl("http://54.186.153.0/API//embedjs.php?i="+propID);
	}
	
	public void setJockeyEvents() {

		
		jockey.on("propertySelect", new JockeyHandler() {
			
			@Override
			protected void doPerform(Map<Object, Object> conInfo) {
				
				/* check if is new connection
					if new
						create new connection in db
						update currConId
						context switch
					else
						update currConId
						context switch
				*/
				switcher.showNext();
			}
		});
	}
}