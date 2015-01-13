package edu.wm.werewolfmobile;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.werewolfmobile.R;

import edu.wm.werewolfmobile.exceptions.AccountCreationErrorException;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewUserActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newuser);
		
		Button btnCreateAccount = (Button) findViewById(R.id.newUserSubmit);
		
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
        	 
            public void onClick(View arg0) {
            	
            	String usernameText = ((EditText)findViewById(R.id.usernameInput)).getText().toString();
            	String lastnameText = ((EditText)findViewById(R.id.lastnameInput)).getText().toString();
            	String firstnameText = ((EditText)findViewById(R.id.firstnameInput)).getText().toString();
            	String passwordText = ((EditText)findViewById(R.id.passwordInput)).getText().toString();
            	String verifyPasswordText = ((EditText)findViewById(R.id.verifyPasswordInput)).getText().toString();
         
            	TextView error = (TextView)findViewById(R.id.errorLabel);
            	
            	if (!passwordText.equals(verifyPasswordText))  {
            		Toast.makeText(getApplicationContext(), "Error: passwords do not match!", Toast.LENGTH_SHORT).show();
            		//error.setTextColor(Color.RED);
            		//error.setText("Error: passwords do not match");
            	}
            	
            	else {
            		
            		String response;
            		
            		HttpRequestTask h = new HttpRequestTask(NewUserActivity.this, "Creating new user...");
            		try {
						response = h.execute("http://werewolf-jpblair.herokuapp.com/newuser", "POST", null, null, "username", usernameText, "password", passwordText, "firstname", firstnameText, "lastname", lastnameText).get();
						JSONObject json = new JSONObject(response);
						if (json.get("success").equals("false"))
							throw new AccountCreationErrorException();
						HttpRequestTask loginTask = new HttpRequestTask(NewUserActivity.this, "Verifying log in...");
						String loginResponse = loginTask.execute("http://werewolf-jpblair.herokuapp.com/", "GET", usernameText, passwordText).get();
						if (!loginResponse.contains("Send a POST request embedded with a username and password"))
							throw new AccountCreationErrorException();
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
                   		
            		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(NewUserActivity.this.getBaseContext());
            		SharedPreferences.Editor editor = settings.edit();
            	    editor.putString("edu.wm.werewolfmobile.username", usernameText);
            	    editor.putString("edu.wm.werewolfmobile.password", passwordText);
            	    editor.commit();
            		
	                Intent homeScreen = new Intent(getApplicationContext(), HomeActivity.class); 
	                startActivity(homeScreen);
	              
            	}
 
            }
        });
		
	}

}
