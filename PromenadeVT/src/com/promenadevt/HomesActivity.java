package com.promenadevt;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
 
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.promenadevt.android.R;
import com.promenadevt.library.DatabaseHandler;
import com.promenadevt.library.UserFunctions;

public class HomesActivity extends Activity 
{
	List<Button> properties;// = new ArrayList<Button>();
	Button btnNewProp;
	Button btnLogout;
	private static String username;
	
	TextView title;// = (TextView) findViewById(R.id.username);
	
	UserFunctions userFunctions;
	
	// JSON Response node names
    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";
    private static String KEY_ERROR_MSG = "error_msg";
    private static String KEY_TUPLE = "tuples";
    private static String KEY_IDPROPERTY = "idProperty";
    private static String KEY_ADDRESS = "address";
    private static String KEY_HOUSEURL = "houseURL";
    private static String KEY_NAME = "name";
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            // do something on back.
        	userFunctions.logoutUser(getApplicationContext());
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(login);
            // Closing rooms screen
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }
    
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homes);
		
		
		
		properties = new ArrayList<Button>();
		
		userFunctions = new UserFunctions();
		
		// may need to account for newly registered user here
		Intent intent = getIntent();
		username = intent.getStringExtra("name");// pull from previous page
		//dbID = intent.getStringExtra("id");
		
		title = (TextView) findViewById(R.id.username);
		
		title.setText(username);
		
		// make database call
		UserFunctions userFunction = new UserFunctions();
    	JSONObject json = userFunction.getHomes(username);
    	if(json == null){
    		Log.e("com.examples.androidhive","no json received.");
    	}
    	
    	//lists of received data
    	List<Integer> idProperty = new ArrayList<Integer>();
    	List<String> address = new ArrayList<String>();
    	List<String> houseURL = new ArrayList<String>();
    	
    	try {
                if (json.getString(KEY_SUCCESS) != null) {
                    //loginErrorMsg.setText("");
                    String res = json.getString(KEY_SUCCESS); 
                    if(Integer.parseInt(res) == 1){
                    	JSONArray tuples = json.getJSONArray(KEY_TUPLE);
                    	for(int i = 0; i< tuples.length(); i++){
                    		JSONObject curTuple = tuples.getJSONObject(i);
                    		idProperty.add(curTuple.getInt(KEY_IDPROPERTY));
                    		address.add(curTuple.getString(KEY_ADDRESS));
                    		houseURL.add(curTuple.getString(KEY_HOUSEURL));
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
        for(int i = 0; i < idProperty.size(); i++)
        {
        	
            Button btn = new Button(this);
            btn.setId(i);
            final int id_ = btn.getId();
            // set text to address
            btn.setText(address.get(i).toString());
            ll.addView(btn, params);
            // fill properties based on db call
            properties.add((Button) findViewById(id_));
        }
        
        
        Button btn = new Button(this);
        btn.setId(idProperty.size());
        btn.setText("Add new property +");
        ll.addView(btn, params);
        btnNewProp = (Button) findViewById(idProperty.size());
        
      //logout button
        Button logout = new Button(this);
        logout.setId(idProperty.size()+1);
        logout.setText("Logout");
        ll.addView(logout, params);
        btnLogout = (Button) findViewById(idProperty.size()+1);
       
		
		
		for(int i = 0; i < properties.size(); i++)
		{
			final String addr = address.get(i);
			final Integer id = idProperty.get(i);
			properties.get(i).setOnClickListener(new View.OnClickListener() 
			{

				@Override
				public void onClick(View arg0) {
					// go to next page with given property selected
					
					Intent next = new Intent(getApplicationContext(),
	                        RoomsActivity.class);
					next.putExtra("user", username);
					next.putExtra("addr", addr);
					next.putExtra("id",id.toString());
	                startActivity(next);
				}
				 
			});
		}
		
		 btnNewProp.setOnClickListener(new View.OnClickListener() 
		 {

			@Override
			public void onClick(View arg0) {
				// create new entry in database and go to next page
				JSONObject jsonID = userFunctions.addProperty("New Property",username,null,"0");

				String value = "";
				try {
					value = jsonID.getJSONObject(KEY_TUPLE).getString(KEY_IDPROPERTY);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// may need to update webgl here
				Intent next = new Intent(getApplicationContext(),
                        RoomsActivity.class);
				next.putExtra("user", username);
				next.putExtra("addr", "New Property");
				next.putExtra("id",value.toString());
                startActivity(next);
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
	}
}
