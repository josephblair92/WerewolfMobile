package edu.wm.werewolfmobile;

import org.json.JSONObject;

import com.example.werewolfmobile.R;

import edu.wm.werewolfmobile.exceptions.AccountCreationErrorException;
import edu.wm.werewolfmobile.exceptions.GameCreationErrorException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class NewGameActivity extends Activity {
	
	String username;
	String password;
	
	protected void onResume() {
		
		super.onResume();
		
		if (validateLogin() == false)  {
	        Intent loginScreen = new Intent(getApplicationContext(), LoginActivity.class); 
	        startActivity(loginScreen);
		}
		
		TextView usernameLabel = (TextView)findViewById(R.id.loggedInUsernameLabel);
		usernameLabel.setText(username);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newgame);
		
		Button btnLogout = (Button) findViewById(R.id.logoutButton);
		Button btnCreateGame = (Button)findViewById(R.id.createGameButton);
    	NumberPicker hoursPicker = (NumberPicker)findViewById(R.id.hoursPicker);
    	NumberPicker minutesPicker = (NumberPicker)findViewById(R.id.minsPicker);
    	
    	hoursPicker.setMinValue(0);
    	hoursPicker.setMaxValue(100);
    	minutesPicker.setMinValue(0);
    	minutesPicker.setMaxValue(59);
		
		btnLogout.setOnClickListener(new View.OnClickListener() {
    	 	
            public void onClick(View arg0) {
            	
            	Intent loginScreen = new Intent(getApplicationContext(), LoginActivity.class); 
                startActivity(loginScreen);            	
            	
            }
		});
		
		btnCreateGame.setOnClickListener(new View.OnClickListener() {
    	 	
            public void onClick(View arg0) {
            	
            	NumberPicker hoursPicker = (NumberPicker)findViewById(R.id.hoursPicker);
            	NumberPicker minutesPicker = (NumberPicker)findViewById(R.id.minsPicker);
            	int hours = hoursPicker.getValue();
            	int minutes = minutesPicker.getValue();
            	String numMinutes = "" + ((hours * 60) + minutes);
            	
        		try {
        			HttpRequestTask loginTask = new HttpRequestTask(NewGameActivity.this, "Verifying account information...");
        			String response = loginTask.execute("http://werewolf-jpblair.herokuapp.com/newgame", "POST", username, password, "numMinutes", numMinutes).get();
					JSONObject json = new JSONObject(response);
					if (json.get("success").equals("false"))
						throw new GameCreationErrorException();
        		}
        		catch (Exception e) {
        			e.printStackTrace();
        			return;
        		}
            	
            	Intent homeScreen = new Intent(getApplicationContext(), HomeActivity.class); 
                startActivity(homeScreen);            	
            	
            }
		});
		
	}
	
	public boolean validateLogin() {
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(NewGameActivity.this.getBaseContext());
		username = settings.getString("edu.wm.werewolfmobile.username", "Unavailable");
		password = settings.getString("edu.wm.werewolfmobile.password", "Unavailable");
		
		System.out.println("username - " + username);
		System.out.println("password - " + password);
		
		if (username.equals("Unavailable") || password.equals("Unavailable"))
			return false;
		
		try {
			HttpRequestTask loginTask = new HttpRequestTask(NewGameActivity.this, "Verifying account information...");
			String loginResponse = loginTask.execute("http://werewolf-jpblair.herokuapp.com/", "GET", username, password).get();
			if (!loginResponse.contains("Send a POST request embedded with a username and password"))
				return false;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}
