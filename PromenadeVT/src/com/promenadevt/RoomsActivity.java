package com.promenadevt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.promenadevt.android.R;
import com.promenadevt.library.Constants;
import com.promenadevt.library.DatabaseHandler;
import com.promenadevt.library.UserFunctions;

public class RoomsActivity extends Activity 
{
	List<Button> rooms;
	Button btnNewRoom;
	Button btnChangeAddr;
	Button btnLogout;
	Button btnDelete;
	Button btnDeleteYes;
	Button btnDeleteNo;
	Button btnUploadPic;
    ViewSwitcher switcher;
    EditText inputAddr;
    
    UserFunctions userFunctions;
    
    private static String username;
	private static String address;
	private static String dbID;
	
	private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";
    private static String KEY_ERROR_MSG = "error_msg";
    private static String KEY_TUPLE = "tuples";
    private static String KEY_IDROOM = "idRoom";
    private static String KEY_ROOMNAME = "name";
    private static String KEY_ROOMURL = "roomURL";
    private static String KEY_IDPROPERTY = "idProperty";
    
    int CAMERA_PIC_REQUEST = 1337; 
	private static final int PHOTO_SELECTED = 1;
	
	String mCurrentPhotoPath;
	
	private AmazonS3Client s3Client = new AmazonS3Client(
			new BasicAWSCredentials(Constants.ACCESS_KEY_ID,
					Constants.SECRET_KEY));
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            // do something on back.
        	DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        	Intent homes = new Intent(getApplicationContext(), HomesActivity.class);
        	HashMap<String, String> loginInfo = db.getUserDetails();
        	homes.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            homes.putExtra("name",loginInfo.get("username"));
            startActivity(homes);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rooms);
		
		rooms = new ArrayList<Button>();
		
		
		// may need to account for newly registered user here
		Intent intent = getIntent();
		// pull info from previous page
		username = intent.getStringExtra("user");
		address = intent.getStringExtra("addr");
		inputAddr = (EditText) findViewById(R.id.addr);
		inputAddr.setText(address);
		
		userFunctions = new UserFunctions();
		
		dbID = intent.getStringExtra("id");
		
		btnChangeAddr = (Button) findViewById(R.id.btnUpdateP);
		btnDeleteYes = (Button) findViewById(R.id.btnDeleteHomeYes);
		btnDeleteNo = (Button) findViewById(R.id.btnDeleteHomeNo);
		switcher = (ViewSwitcher) findViewById(R.id.roomsSwitch);
		// make database call
		final UserFunctions userFunction = new UserFunctions();
		JSONObject json = userFunction.getRooms(dbID);
    	
    	//lists of recieved data
    	List<Integer> idRoom = new ArrayList<Integer>();
    	List<String> roomName = new ArrayList<String>();
    	final List<String> roomURL = new ArrayList<String>();
    	
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
                    		roomURL.add(curTuple.getString(KEY_ROOMURL));
                    	}
                    }
                        
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        
		// fill xml based on db call
        LinearLayout ll = (LinearLayout) findViewById(R.id.roomsLayout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        
        
        //front door picture (upload)
        Button upload_front = new Button(this);
        upload_front.setId(idRoom.size()+4);
        upload_front.setText("Upload Front Picture");
        ll.addView(upload_front, params);
        btnUploadPic = (Button) findViewById(idRoom.size()+4);
        
        // may need separate file
        for(int i = 0; i < idRoom.size(); i++)
        {
        	
            Button btn = new Button(this);
            btn.setId(i);
            final int id_ = btn.getId();
            // set text to address
            btn.setText(roomName.get(i).toString());
            ll.addView(btn, params);
            // fill properties based on db call
            rooms.add((Button) findViewById(id_));
        }
        
        //new room button
        Button newRoom = new Button(this);
        newRoom.setId(idRoom.size());
        newRoom.setText("Add new room +");
        ll.addView(newRoom, params);
        btnNewRoom = (Button) findViewById(idRoom.size());
        
        //delete room
        Button deleteRoom = new Button(this);
        deleteRoom.setId(idRoom.size()+2);
        deleteRoom.setText("Delete Property");
        ll.addView(deleteRoom, params);
        btnDelete = (Button) findViewById(idRoom.size()+2);
        
        //logout button
        Button logout = new Button(this);
        logout.setId(idRoom.size()+1);
        logout.setText("Logout");
        ll.addView(logout, params);
        btnLogout = (Button) findViewById(idRoom.size()+1);

        
        btnUploadPic.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(intent, PHOTO_SELECTED);
				
			}
		});
        
        btnChangeAddr.setOnClickListener(new View.OnClickListener() 
		{

			@Override
			public void onClick(View arg0) {
				// change address name in database
				String newAddr = inputAddr.getText().toString();
				userFunctions.renameHome(dbID,newAddr);
			}
			 
		 });
		
		for(int i = 0; i < rooms.size(); i++)
		{
			final String name = roomName.get(i);
			final Integer id = idRoom.get(i);
			final String url = roomURL.get(i);
			 rooms.get(i).setOnClickListener(new View.OnClickListener() 
			 {

				@Override
				public void onClick(View arg0) {
					//  go to next page with given room selected
					Intent next = new Intent(getApplicationContext(),
	                        EditActivity.class);
					next.putExtra("user", username);
					next.putExtra("name", name);
					next.putExtra("id",id.toString());
					next.putExtra("propID",dbID);
					next.putExtra("addr",address);
					next.putExtra("url",url);
	                startActivity(next);
	                finish();
				}
				 
			 });
		}
		
		 btnNewRoom.setOnClickListener(new View.OnClickListener() 
		 {

			@Override
			public void onClick(View arg0) {
				// create new room and go to edit page
				JSONObject jsonID = userFunctions.addRoom("New Room",dbID,"");

				String value = "";
				try {
					value = jsonID.getJSONObject(KEY_TUPLE).getString(KEY_IDROOM);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
				// may need to update webgl here
				Intent next = new Intent(getApplicationContext(),
                        EditActivity.class);
				next.putExtra("user", username);
				next.putExtra("name", "New Room");
				next.putExtra("id",value.toString());
				next.putExtra("propID",dbID);
				next.putExtra("addr",address);
				next.putExtra("url", "");
                startActivity(next);
                finish();
				
			}
			 
		 });
		 
		 btnLogout.setOnClickListener(new View.OnClickListener() 
		 {

			@Override
			public void onClick(View arg0) {
				userFunctions.logoutUser(getApplicationContext());
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(login);
                // Closing rooms screen
                finish();
				
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
					userFunctions.deleteProperty(dbID);
	                DatabaseHandler db = new DatabaseHandler(getApplicationContext());
	            	Intent homes = new Intent(getApplicationContext(), HomesActivity.class);
	            	HashMap<String, String> loginInfo = db.getUserDetails();
	            	homes.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	                homes.putExtra("name",loginInfo.get("username"));
	                startActivity(homes);
	                finish();
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode == CAMERA_PIC_REQUEST){
			if (resultCode == RESULT_OK) {
				File caputredImage = new File(mCurrentPhotoPath);
				//Uri caputredImage = Uri.fromFile(f);
				new S2PutPictureTask().execute(caputredImage);
				userFunctions.changeHouseURL(dbID, "https:s3-us-west-2.amazonaws.com/promenadevt-1/property"+dbID);
			}
		}
		if(requestCode == PHOTO_SELECTED){
			if (resultCode == RESULT_OK) {

				Uri selectedImage = data.getData();
				//Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
				//roomImage.setImageURI(selectedImage);
				new S3PutObjectTask().execute(selectedImage);
				userFunctions.changeHouseURL(dbID, "https:s3-us-west-2.amazonaws.com/promenadevt-1/property"+dbID);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private class S2PutPictureTask extends AsyncTask<File, Void, S3TaskResult> {
		
		protected S3TaskResult doInBackground(File... files) {
			
			if (files == null || files.length != 1) {
				return null;
			}
	
			// The file location of the image selected.
			File selectedImage = files[0];
	
	
			S3TaskResult result = new S3TaskResult();
	
			// Put the image data into S3.
			PutObjectRequest por;
			por = new PutObjectRequest(
					Constants.getPictureBucket(), "property" + dbID,
					selectedImage);
					s3Client.putObject(por);
			
	
			return result;
		}
	}
	
	private class S3PutObjectTask extends AsyncTask<Uri, Void, S3TaskResult> {
	
		ProgressDialog dialog;
	
		protected void onPreExecute() {
			dialog = new ProgressDialog(RoomsActivity.this);
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
						Constants.getPictureBucket(), "property" + dbID,
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

							RoomsActivity.this.finish();
						}
					});

			confirm.show().show();
		}

}


