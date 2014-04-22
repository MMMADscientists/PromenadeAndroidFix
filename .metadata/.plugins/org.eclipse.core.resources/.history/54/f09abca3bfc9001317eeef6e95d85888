package com.promenadevt.library;
 
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
 
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
 
import android.content.Context;
import android.util.Log;
 
public class UserFunctions {
     
    private JSONParser jsonParser;
     
    // Testing in localhost using wamp or xampp 
    // use http://10.0.2.2/ to connect to your localhost ie http://localhost/
    private static String loginURL = "http://54.186.153.0/API/index.php";
    private static String registerURL = "http://54.186.153.0/API/index.php";
     
    private static String login_tag = "login";
    private static String register_tag = "register";
    private static String houses_tag = "houses";
    private static String rooms_tag = "rooms";
    private static String connections_tag = "connections";
    private static String property_rename_tag = "renameProperty";
    private static String room_rename_tag = "renameRoom";
    private static String house_create_tag = "createHouse";
    private static String room_create_tag = "createRoom";
    private static String room_delete_tag = "deleteRoom";
    private static String house_delete_tag = "deleteProperty";
    private static String room_url_tag = "changeRoomURL";
    
    
    // constructor
    public UserFunctions(){
        jsonParser = new JSONParser();
    }
     
    /**
     * function make Login Request
     * @param username
     * @param password
     * */
    public JSONObject loginUser(String username, String password){
    	//offload to new thread so this thing stops blowing up
        DatabaseAccessTask dbAccess = new DatabaseAccessTask();
        dbAccess.execute(login_tag , username , password);
        JSONObject json = null;
		try {
			json = dbAccess.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return json;
    }
     
    /**
     * function make Login Request
     * @param name
     * @param email
     * @param password
     * */
    public JSONObject registerUser(String name, String email, String password){
        // Building Parameters
        /*List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));*/
         
        // getting JSON Object
        DatabaseAccessTask dbAccess = new DatabaseAccessTask();
        dbAccess.execute(register_tag, name , email , password);
        JSONObject json = null;
		try {
			json = dbAccess.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return json;
    }
     
    /**
     * Function get Login status
     * */
    public boolean isUserLoggedIn(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        int count = db.getRowCount();
        if(count > 0){
            // user logged in
            return true;
        }
        return false;
    }
     
    /**
     * Function to logout user
     * Reset Database
     * */
    public boolean logoutUser(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        db.resetTables();
        return true;
    }
    
    /*
     * Function to get homes based on user's name
     */
     public JSONObject getHomes(String username){
	DatabaseAccessTask dbAccess = new DatabaseAccessTask();
        dbAccess.execute(houses_tag, username);
        JSONObject json = null;
		try {
			json = dbAccess.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return json;
    }
    
    public JSONObject getRooms(String propertyID){
	DatabaseAccessTask dbAccess = new DatabaseAccessTask();
        dbAccess.execute(rooms_tag, propertyID);
        JSONObject json = null;
		try {
			json = dbAccess.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return json;
    }
    
    public JSONObject renameHome(String propertyID, String newName){
	DatabaseAccessTask dbAccess = new DatabaseAccessTask();
        dbAccess.execute(property_rename_tag, propertyID, newName);
        JSONObject json = null;
		try {
			json = dbAccess.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return json;
    }
    
    public JSONObject renameRoom(String roomID, String newName){
	DatabaseAccessTask dbAccess = new DatabaseAccessTask();
        dbAccess.execute(room_rename_tag, roomID, newName);
        JSONObject json = null;
		try {
			json = dbAccess.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return json;
    }
    
     public JSONObject addProperty(String address, String username, String houseURL, String defaultRoom){
    	 DatabaseAccessTask dbAccess = new DatabaseAccessTask();
         dbAccess.execute(house_create_tag, address, username, houseURL, defaultRoom);
         JSONObject json = null;
 		try {
 			json = dbAccess.get();
 		} catch (InterruptedException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		} catch (ExecutionException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
         return json;
     	
     }
     
     public JSONObject addRoom(String name, String propertyID, String roomURL){
    	 DatabaseAccessTask dbAccess = new DatabaseAccessTask();
         dbAccess.execute(room_create_tag, name, propertyID, roomURL);
         JSONObject json = null;
 		try {
 			json = dbAccess.get();
 		} catch (InterruptedException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		} catch (ExecutionException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
         return json;
     	
     	
     }
     
     public JSONObject deleteRoom(String roomID){
    	 DatabaseAccessTask dbAccess = new DatabaseAccessTask();
         dbAccess.execute(room_delete_tag, roomID);
         JSONObject json = null;
 		try {
 			json = dbAccess.get();
 		} catch (InterruptedException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		} catch (ExecutionException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
         return json;
    	 
     }
     
     public JSONObject deleteProperty(String propertyID){
    	 DatabaseAccessTask dbAccess = new DatabaseAccessTask();
         dbAccess.execute(house_delete_tag, propertyID);
         JSONObject json = null;
 		try {
 			json = dbAccess.get();
 		} catch (InterruptedException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		} catch (ExecutionException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
         return json;
    	 
     }
     
     public JSONObject changeURL(String roomID, String URL){
    	 DatabaseAccessTask dbAccess = new DatabaseAccessTask();
         dbAccess.execute(room_url_tag, roomID, URL);
         JSONObject json = null;
  		try {
  			json = dbAccess.get();
  		} catch (InterruptedException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		} catch (ExecutionException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
        return json;
     }
     
}
