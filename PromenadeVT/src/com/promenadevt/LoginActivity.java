package com.promenadevt;
 
import java.util.HashMap;
 
import org.json.JSONException;
import org.json.JSONObject;
 
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
 
import com.promenadevt.android.R;
import com.promenadevt.library.DatabaseHandler;
import com.promenadevt.library.UserFunctions;
 
public class LoginActivity extends Activity {
    Button btnLogin;
    Button btnLinkToRegister;
    EditText inputUser;
    EditText inputPassword;
    TextView loginErrorMsg;
 
    // JSON Response node names
    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";
    private static String KEY_ERROR_MSG = "error_msg";
    private static String KEY_PASS = "pass";
    private static String KEY_NAME = "name";
    private static String KEY_EMAIL = "email";
    //private static String KEY_CREATED_AT = "created_at";
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        if(db.getRowCount() > 0){
        	Intent homes = new Intent(getApplicationContext(), HomesActivity.class);
        	HashMap<String, String> loginInfo = db.getUserDetails();
        	homes.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            homes.putExtra("name",loginInfo.get("username"));
            startActivity(homes);
            finish();
        }
        
 
        // Importing all assets like buttons, text fields
        inputUser = (EditText) findViewById(R.id.loginUser);
        inputPassword = (EditText) findViewById(R.id.loginPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        loginErrorMsg = (TextView) findViewById(R.id.login_error);
 
        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
                String username = inputUser.getText().toString();
                String password = inputPassword.getText().toString();
                UserFunctions userFunction = new UserFunctions();
                JSONObject json = userFunction.loginUser(username, password);
 
                // check for login response
                try {
                    if (json.getString(KEY_SUCCESS) != null) {
                        loginErrorMsg.setText("User Found");
                        String res = json.getString(KEY_SUCCESS); 
                        if(Integer.parseInt(res) == 1){
                            // user successfully logged in
                            // Store user details in SQLite Database
                            DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                            JSONObject json_user = json.getJSONObject("username");
                             
                            // Clear all previous data in database
                            userFunction.logoutUser(getApplicationContext());
                            db.addUser(json_user.getString(KEY_NAME), password, json_user.getString(KEY_EMAIL));                        
                             
                            // Launch homes Screen
                            Intent homes = new Intent(getApplicationContext(), HomesActivity.class);
                             
                            // Close all views before launching Dashboard
                            homes.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            homes.putExtra("name",username);
                            startActivity(homes);
                             
                            // Close Login Screen
                            finish();
                        }else{
                            // Error in login
                            loginErrorMsg.setText("Incorrect username/password");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
 
        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}