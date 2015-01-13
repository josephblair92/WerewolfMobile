package edu.wm.werewolfmobile;

import org.json.JSONObject;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.werewolfmobile.R;

import edu.wm.werewolfmobile.exceptions.AccountCreationErrorException;
import edu.wm.werewolfmobile.exceptions.LoginErrorException;

public class LoginActivity extends Activity {
	
	@Override
	protected void onResume() {
		super.onResume();
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this.getBaseContext());
		String username = settings.getString("username", "");
		String password = settings.getString("password", "");
		
		if (username.equals("") || password.equals(""))
			return;
		
		try {
			HttpRequestTask loginTask = new HttpRequestTask(LoginActivity.this, "Verifying log in...");
			String loginResponse = loginTask.execute("http://werewolf-jpblair.herokuapp.com/", "GET", username, password).get();
			if (!loginResponse.contains("Send a POST request embedded with a username and password"))
				throw new LoginErrorException();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
        Intent homeScreen = new Intent(getApplicationContext(), HomeActivity.class); 
        startActivity(homeScreen);
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		Button btnLogin = (Button) findViewById(R.id.loginButton);
		Button btnRegister = (Button) findViewById(R.id.registerButton);
		
		btnLogin.setOnClickListener(new View.OnClickListener() {
        	 	
	            public void onClick(View arg0) {
	            	
	        		String usernameText = ((EditText)findViewById(R.id.usernameInput)).getText().toString();
	            	String passwordText = ((EditText)findViewById(R.id.passwordInput)).getText().toString();
	            	
	            	try {
						HttpRequestTask loginTask = new HttpRequestTask(LoginActivity.this, "Verifying log in...");
						String loginResponse = loginTask.execute("http://werewolf-jpblair.herokuapp.com/", "GET", usernameText, passwordText).get();
						if (!loginResponse.contains("Send a POST request embedded with a username and password"))
							throw new AccountCreationErrorException();
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
                   		
            		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this.getBaseContext());
            		SharedPreferences.Editor editor = settings.edit();
            	    editor.putString("edu.wm.werewolfmobile.username", usernameText);
            	    editor.putString("edu.wm.werewolfmobile.password", passwordText);
            	    editor.commit();
            		
	                Intent homeScreen = new Intent(getApplicationContext(), HomeActivity.class); 
	                startActivity(homeScreen);
	              
	            }
		});
		
		btnRegister.setOnClickListener(new View.OnClickListener() {
       	 
            public void onClick(View arg0) {
            	
            	Intent newUserScreen = new Intent(getApplicationContext(), NewUserActivity.class); 
                startActivity(newUserScreen);
                
            }
		});
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}
