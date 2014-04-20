package com.promenadevt;

import static com.jockeyjs.NativeOS.nativeOS;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.IOUtils;

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
	UserFunctions userFunctions;
	public WebView webView;
	private Jockey jockey;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_room);
		
		Intent intent = getIntent();
		propID = intent.getStringExtra("propID");
		dbID = intent.getStringExtra("id");
		
		webView = (WebView) findViewById(R.id.webView);
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
		webView.loadUrl("file:///android_asset/index.html");
	}
	
	public void setJockeyEvents() {

		
		jockey.on("propertySelect", new JockeyHandler() {
			
			@Override
			protected void doPerform(Map<Object, Object> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
