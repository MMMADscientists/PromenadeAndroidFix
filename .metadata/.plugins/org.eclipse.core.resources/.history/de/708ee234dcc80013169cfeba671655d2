package com.promenadevt;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
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

public class EditActivity extends Activity 
{
    EditText inputName;
	Button btnChangeName;
	Button btnTakePhoto;
	Button btnAddConnection;
	Button btnViewRoom;
	Button btnDelete;
	Button btnDeleteYes;
	Button btnDeleteNo;
    ViewSwitcher switcher;
	ImageView roomImage;
	
	UserFunctions userFunctions;
	

	private static String username;
	private static String roomName;
	private static String dbID;
	private static String propID;
	private static String addr;
	private static String roomURL;
	
	int CAMERA_PIC_REQUEST = 1337; 
	private static final int PHOTO_SELECTED = 1;
	
	Constants Constants;
	
	private AmazonS3Client s3Client = new AmazonS3Client(
			new BasicAWSCredentials(Constants.ACCESS_KEY_ID,
					Constants.SECRET_KEY));
	
	/*private void getPano(){
		Intent takePanoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(takePanoIntent,CAMERA_PIC_REQUEST);
		
		/*Intent res = new Intent();
	    String mPackage = "com.google.android.gallery3d";
	    String mClass = "com.google.android.apps.lightcycle.ProtectedPanoramaCaptureActivity";

	    res.setComponent(new ComponentName(mPackage,mClass));
	    startActivityForResult(res,CAMERA_PIC_REQUEST);
		
	}*/
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if( requestCode == CAMERA_PIC_REQUEST){
			Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
			roomImage.setImageBitmap(thumbnail);
		}
		else if(requestCode == PHOTO_SELECTED){
			if (resultCode == RESULT_OK) {

				Uri selectedImage = data.getData();
				//Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
				roomImage.setImageURI(selectedImage);
				new S3PutObjectTask().execute(selectedImage);
				userFunctions.changeURL(dbID, "https://s3-us-west-2.amazonaws.com/promenadevt-1/room"+dbID);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	 @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event)  {
	        if (keyCode == KeyEvent.KEYCODE_BACK ) {
	            // do something on back.
	        	//DatabaseHandler db = new DatabaseHandler(getApplicationContext());
	        	Intent rooms = new Intent(getApplicationContext(), RoomsActivity.class);
	        	//HashMap<String, String> loginInfo = db.getUserDetails();
	        	rooms.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        	rooms.putExtra("user", username);
	        	rooms.putExtra("id",propID);
	        	rooms.putExtra("addr", addr);
	            startActivity(rooms);
	            return true;
	        }

	        return super.onKeyDown(keyCode, event);
	    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_room);
		
		s3Client.setRegion(Region.getRegion(Regions.US_WEST_2));
		
		//new S3GeneratePresignedUrlTask().execute();
		
		
		// may need to account for newly registered user here
		Intent intent = getIntent();
		// pull info from previous page
		username = intent.getStringExtra("user");
		roomName = intent.getStringExtra("name");
		propID = intent.getStringExtra("propID");
		addr = intent.getStringExtra("addr");
		inputName = (EditText) findViewById(R.id.nameRoom);
		inputName.setText(roomName);
		dbID = intent.getStringExtra("id");
		roomURL = "https://s3-us-west-2.amazonaws.com/promenadevt-1/room"+dbID;
		Constants = new Constants(propID,dbID);
		
		btnChangeName = (Button) findViewById(R.id.btnUpdateR);
		btnTakePhoto = (Button) findViewById(R.id.btnPhoto);
		btnAddConnection = (Button) findViewById(R.id.btnConnection);
		btnViewRoom = (Button) findViewById(R.id.btnView);
		btnDelete = (Button) findViewById(R.id.btnDelete);
		btnDeleteYes = (Button) findViewById(R.id.btnDeleteRoomYes);
		btnDeleteNo = (Button) findViewById(R.id.btnDeleteRoomNo);
		switcher = (ViewSwitcher) findViewById(R.id.editRoomsSwitch);
		roomImage =(ImageView) findViewById(R.id.PhotoCaptured);
		
		userFunctions = new UserFunctions();
		
		if(roomURL != null){
			try {
				Bitmap bitmap = new S3GetObjectTask().execute().get();
				roomImage.setImageBitmap(bitmap);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
        btnChangeName.setOnClickListener(new View.OnClickListener() 
		{

			@Override
			public void onClick(View arg0) {
				// change room name in database
				UserFunctions userFunction = new UserFunctions();
				String newName = inputName.getText().toString();
				userFunction.renameRoom(dbID,newName);

			}
			 
		 });
       
        btnTakePhoto.setOnClickListener(new View.OnClickListener() 
		{

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(intent, PHOTO_SELECTED);
				
			}
			 
		 });
        
        btnAddConnection.setOnClickListener(new View.OnClickListener() 
		{

			@Override
			public void onClick(View arg0) {
				// go to screen to add connections to room
				String url = "http://54.186.153.0/API/embed_js.php?i="+propID;
				Intent browser = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
				startActivity(browser);
			}
			 
		 });
        
        btnViewRoom.setOnClickListener(new View.OnClickListener() 
		{

			@Override
			public void onClick(View arg0) {
				// view room as it is now
				//new S3GeneratePresignedUrlTask().execute();
			}
			 
		 });
        
        btnDelete.setOnClickListener(new View.OnClickListener() 
		{

			@Override
			public void onClick(View arg0) {
				switcher.showNext();
			}
			 
		 });
        
        btnDeleteYes.setOnClickListener(new View.OnClickListener() 
		 {

			@Override
			public void onClick(View arg0) {
				userFunctions.deleteRoom(dbID);
				Intent rooms = new Intent(getApplicationContext(), RoomsActivity.class);
				rooms.putExtra("user", username);
				rooms.putExtra("addr", addr);
				rooms.putExtra("id",propID);
				switcher.showPrevious();
				startActivity(rooms);
			}
			 
		 });
		 
		 btnDeleteNo.setOnClickListener(new View.OnClickListener() 
		 {

			@Override
			public void onClick(View arg0) {
               switcher.showPrevious();
			}
			 
		 });
		
		 
	}
	//AMAZON S3 STUFF HERE *********************************************************************************************************
	private class S3PutObjectTask extends AsyncTask<Uri, Void, S3TaskResult> {

		ProgressDialog dialog;

		protected void onPreExecute() {
			dialog = new ProgressDialog(EditActivity.this);
			dialog.setMessage("Uploading");
			dialog.setCancelable(false);
			dialog.show();
		}

		protected S3TaskResult doInBackground(Uri... uris) {

			if (uris == null || uris.length != 1) {
				return null;
			}

			// The file location of the image selected.
			Uri selectedImage = uris[0];


            ContentResolver resolver = getContentResolver();
            String fileSizeColumn[] = {OpenableColumns.SIZE}; 
            
			Cursor cursor = resolver.query(selectedImage,
                    fileSizeColumn, null, null, null);

            cursor.moveToFirst();

            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            // If the size is unknown, the value stored is null.  But since an int can't be
            // null in java, the behavior is implementation-specific, which is just a fancy
            // term for "unpredictable".  So as a rule, check if it's null before assigning
            // to an int.  This will happen often:  The storage API allows for remote
            // files, whose size might not be locally known.
            String size = null;
            if (!cursor.isNull(sizeIndex)) {
                // Technically the column stores an int, but cursor.getString will do the
                // conversion automatically.
                size = cursor.getString(sizeIndex);
            } 
            
			cursor.close();

			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType(resolver.getType(selectedImage));
			if(size != null){
			    metadata.setContentLength(Long.parseLong(size));
			}

			S3TaskResult result = new S3TaskResult();

			// Put the image data into S3.
			PutObjectRequest por;
			try {
				por = new PutObjectRequest(
						Constants.getPictureBucket(), Constants.ROOM_ID,
						resolver.openInputStream(selectedImage),metadata);
						s3Client.putObject(por);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

			return result;
		}

		protected void onPostExecute(S3TaskResult result) {

			dialog.dismiss();

			if (result.getErrorMessage() != null) {

				displayErrorAlert(
						"failure",
						result.getErrorMessage());
			}
		}
	}

	private class S3GetObjectTask extends
			AsyncTask<Void, Void, Bitmap> {

		protected Bitmap doInBackground(Void... voids) {

			//S3TaskResult result = new S3TaskResult();

			try {
				// Ensure that the image will be treated as such.
				ResponseHeaderOverrides override = new ResponseHeaderOverrides();
				override.setContentType("image/jpeg");

				// Generate the presigned URL.

				// Added an hour's worth of milliseconds to the current time.
				
				GetObjectRequest urlRequest = new GetObjectRequest(
						Constants.getPictureBucket(), "room" + dbID);
				//urlRequest.setExpiration(expirationDate);
				urlRequest.setResponseHeaders(override);

				S3Object url = s3Client.getObject(urlRequest);
					
				byte[] bytes = null;
				try {
					bytes = IOUtils.toByteArray(url.getObjectContent());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
				return bitmap;
				//roomImage.setImageBitmap(bitmap);
				
				//result.setUri(Uri.parse(url.toURI().toString()));

			} catch (Exception exception) {

				//result.setErrorMessage(exception.getMessage());
			}
			return null;

			//return result;
		}
	}

	private class S3TaskResult {
		String errorMessage = null;
		Uri uri = null;

		public String getErrorMessage() {
			return errorMessage;
		}

		public void setErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
		}

		public Uri getUri() {
			return uri;
		}

		public void setUri(Uri uri) {
			this.uri = uri;
		}
	}
	
	// Display an Alert message for an error or failure.
		protected void displayAlert(String title, String message) {

			AlertDialog.Builder confirm = new AlertDialog.Builder(this);
			confirm.setTitle(title);
			confirm.setMessage(message);

			confirm.setNegativeButton(
					"ok",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

							dialog.dismiss();
						}
					});

			confirm.show().show();
		}

		protected void displayErrorAlert(String title, String message) {

			AlertDialog.Builder confirm = new AlertDialog.Builder(this);
			confirm.setTitle(title);
			confirm.setMessage(message);

			confirm.setNegativeButton(
					"ok",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

							EditActivity.this.finish();
						}
					});

			confirm.show().show();
		}
}

