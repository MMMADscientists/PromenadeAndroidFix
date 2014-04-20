package com.promenadevt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.promenadevt.android.R;
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
}
