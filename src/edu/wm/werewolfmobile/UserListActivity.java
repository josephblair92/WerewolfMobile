package edu.wm.werewolfmobile;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.werewolfmobile.R;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;

public class UserListActivity extends Activity {
	
	String username, password;
	Button actionButton;
	TextView status;
	RadioGroup rg;
	
	protected void displayAlivePlayers(String JSONresponse) {
		
		try {
		
			JSONArray alivePlayers = new JSONArray(JSONresponse);
			
			for (int i = 0; i < alivePlayers.length(); i++) {
				
				JSONObject p = alivePlayers.getJSONObject(i);
				String firstname = p.getString("firstname");
				String lastname = p.getString("lastname");
				String username = p.getString("username");
				
				RadioButton rb = new RadioButton(this);
				rb.setText(firstname + " " + lastname + " (" + username + ")");
				Drawable d = getResources().getDrawable(R.drawable.ic_launcher);
		        rb.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
		        rb.setCompoundDrawablePadding(10);
		        rb.setTag(username);
				rg.addView(rb, i, new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));

			}
			
		}
			
		catch (Exception e) {
			e.printStackTrace();
		}
			
	}
	
	
	
	protected void displayDeadPlayers(String JSONResponse) {
		
		try {
	
			JSONArray deadPlayers = new JSONArray(JSONResponse);
			
			for (int i = 0; i < deadPlayers.length(); i++) {
				
				JSONObject p = deadPlayers.getJSONObject(i);
				String firstname = p.getString("firstname");
				String lastname = p.getString("lastname");
				String username = p.getString("username");
				
				if (username.equals(this.username)) {
			        Intent homeScreen = new Intent(getApplicationContext(), HomeActivity.class); 
			        startActivity(homeScreen);	
				}
					
				RadioButton rb = new RadioButton(this);
				rb.setText(firstname + " " + lastname + " (" + username + ")");
				Drawable d = getResources().getDrawable(R.drawable.ic_launcher);
			    rb.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
			    rb.setCompoundDrawablePadding(10);
			    rb.setTag(username);
			    rb.setAlpha(75);
			    rb.setBackgroundColor(Color.GRAY);
			    rb.setEnabled(false);
				rg.addView(rb, i, new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
				
			}
			
		}
		
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	protected void displayPlayersInScentRange(String JSONResponse) {
		
		try {
		
	    	JSONArray nearbyPlayers = new JSONArray(JSONResponse);
	    	
	    	ArrayList<String> nearbyUsernames = new ArrayList<String>();
	    	for (int i = 0; i < nearbyPlayers.length(); i++) {
	    		JSONObject p = nearbyPlayers.getJSONObject(i);
	    		nearbyUsernames.add(p.getString("username"));      		
	    	}
	    	
			for (int i = 0; i < rg.getChildCount(); i++) {
				View v = rg.getChildAt(i);
				if (nearbyUsernames.contains(v.getTag()))
					v.setBackgroundColor(Color.YELLOW);
			}
		
		}
		
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	protected void displayPlayersInKillRange(String JSONResponse) {
		
		try {
			
	    	JSONArray nearbyPlayers = new JSONArray(JSONResponse);
	    	
	    	ArrayList<String> nearbyUsernames = new ArrayList<String>();
	    	for (int i = 0; i < nearbyPlayers.length(); i++) {
	    		JSONObject p = nearbyPlayers.getJSONObject(i);
	    		nearbyUsernames.add(p.getString("username"));      		
	    	}
	    	
			for (int i = 0; i < rg.getChildCount(); i++) {
				View v = rg.getChildAt(i);
				if (nearbyUsernames.contains(v.getTag()))
					v.setBackgroundColor(Color.rgb(255, 100, 0));
			}
		
		}
		
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	protected void displayNearbyPlayers() {
		
		actionButton.setVisibility(View.VISIBLE);
		actionButton.setText("Kill");
		
		try {
	    	HttpRequestTask scentRangeTask = new HttpRequestTask(UserListActivity.this, "Getting nearby players...");
	    	String response = scentRangeTask.execute("http://werewolf-jpblair.herokuapp.com/players/nearby", "GET", username, password).get();
			displayPlayersInScentRange(response);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
	    	HttpRequestTask killRangeTask = new HttpRequestTask(UserListActivity.this, "Getting nearby players...");
	    	String response = killRangeTask.execute("http://werewolf-jpblair.herokuapp.com/players/nearby/kill", "GET", username, password).get();
			displayPlayersInKillRange(response);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	protected void loadPlayerList() {
		
			rg.clearCheck();
			rg.removeAllViews();
			
			//Get alive players
			
			try {
				HttpRequestTask alivePlayersTask = new HttpRequestTask(UserListActivity.this, "Getting list of players...");
				String response = alivePlayersTask.execute("http://werewolf-jpblair.herokuapp.com/players/alive", "GET", username, password).get();
				displayAlivePlayers(response);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			//Get dead players
			
			try {
				HttpRequestTask deadPlayersTask = new HttpRequestTask(UserListActivity.this, "Getting list of players...");
				String response2 = deadPlayersTask.execute("http://werewolf-jpblair.herokuapp.com/players/dead", "GET", username, password).get();
				displayDeadPlayers(response2);
			}
			catch (Exception e) {
				e.printStackTrace();
			}

				
			//If night and werewolf, get nearby
			
			try {
			
				HttpRequestTask statusTask = new HttpRequestTask(UserListActivity.this, "Checking game status...");
				String response3 = statusTask.execute("http://werewolf-jpblair.herokuapp.com/atnight", "GET", username, password).get();
				
				HttpRequestTask checkWerewolfTask = new HttpRequestTask(UserListActivity.this, "Checking game status...");
		        String response4 = checkWerewolfTask.execute("http://werewolf-jpblair.herokuapp.com/amWerewolf", "GET", username, password).get();
				
				if (response3.contains("true"))  {
			        
			        if (response4.contains("true")) {			     
			        	displayNearbyPlayers();
			        }
			        else if (response4.contains("false")) {
			        	actionButton.setVisibility(View.INVISIBLE);
			        }
			        
				}
				else {
					actionButton.setVisibility(View.VISIBLE);
					actionButton.setText("Vote");
				}
		
			}
			
			catch (Exception e) {
				e.printStackTrace();
			}			
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (validateLogin() == false)  {
	        Intent loginScreen = new Intent(getApplicationContext(), LoginActivity.class); 
	        startActivity(loginScreen);
		}
		
		TextView usernameLabel = (TextView)findViewById(R.id.loggedInUsernameLabel);
		usernameLabel.setText(username);
		
		//loadPlayerList();
		rg.clearCheck();
		rg.removeAllViews();
		PlayerListTask plt = new PlayerListTask(this, "Loading player list...", username, password);
		plt.execute();
		
		/*
		
		try {
			HttpRequestTask activegameTask = new HttpRequestTask(HomeActivity.this, "Checking for active game...");
			String activeGameResponse = activegameTask.execute("http://werewolf-jpblair.herokuapp.com/activegame", "GET", null, null).get();
			if (activeGameResponse.contains("false"))  {
		        Intent newgameScreen = new Intent(getApplicationContext(), NewGameActivity.class); 
		        startActivity(newgameScreen);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			HttpRequestTask statusTask = new HttpRequestTask(HomeActivity.this, "Checking game status...");
			String response = statusTask.execute("http://werewolf-jpblair.herokuapp.com/atnight", "GET", username, password).get();
			if (response.contains("false"))  {
		        day();
			}
			else if (response.contains("true")) {
				night();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			int werewolves=0, townspeople=0;
			
			HttpRequestTask aliveTownspeopleTask = new HttpRequestTask(HomeActivity.this, "Checking game status...");
			String response1 = aliveTownspeopleTask.execute("http://werewolf-jpblair.herokuapp.com/numAliveTownspeople", "GET", username, password).get();
			townspeople=Integer.parseInt(response1);
			
			HttpRequestTask aliveWerewolvesTask = new HttpRequestTask(HomeActivity.this, "Checking game status...");
			String response2 = aliveWerewolvesTask.execute("http://werewolf-jpblair.herokuapp.com/numAliveWerewolves", "GET", username, password).get();
			werewolves=Integer.parseInt(response2);
			
			TextView tv = (TextView)findViewById(R.id.playerRatio);
			ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar1);
			
			tv.setText(werewolves + " werewolves and " + townspeople + " townspeople are alive.");
			double total = townspeople + werewolves;
			pb.setProgress((int)(100 * (werewolves/total)));
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		*/
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userlist);

		rg = (RadioGroup)findViewById(R.id.playersRadioGroup);
		Button btnLogout = (Button) findViewById(R.id.logoutButton);
		Button btnHome = (Button)findViewById(R.id.homeButton);
		actionButton = (Button)findViewById(R.id.actionButton);	
		
		btnLogout.setOnClickListener(new View.OnClickListener() {
    	 	
            public void onClick(View arg0) {
            	
            	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(UserListActivity.this.getBaseContext());
        		SharedPreferences.Editor editor = settings.edit();
        	    editor.putString("edu.wm.werewolfmobile.username", null);
        	    editor.putString("edu.wm.werewolfmobile.password", null);
        	    editor.commit();
            	
            	Intent loginScreen = new Intent(getApplicationContext(), LoginActivity.class); 
                startActivity(loginScreen);            	
            	
            }
		});
		
		btnHome.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
		        Intent homeScreen = new Intent(getApplicationContext(), HomeActivity.class); 
		        startActivity(homeScreen);				
			}
			
		});
		
		actionButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				String action = actionButton.getText().toString();
				int id = rg.getCheckedRadioButtonId();
				if (id == -1)
					return;
				String victimUsername = findViewById(id).getTag().toString();
				
				if (action.equals("Vote")) {
					
					try {
						HttpRequestTask submitVoteTask = new HttpRequestTask(UserListActivity.this, "Submitting vote...");
				        String response = submitVoteTask.execute("http://werewolf-jpblair.herokuapp.com/players/" + victimUsername, "POST", username, password, "action", "vote").get();
				        JSONObject jsonResponse = new JSONObject(response);
				        if (jsonResponse.getString("success").equals("false"))
				        	Toast.makeText(getApplicationContext(), "Error: Unable to cast vote.", Toast.LENGTH_SHORT).show();
				        else
				        	Toast.makeText(getApplicationContext(), "Vote successfully cast!", Toast.LENGTH_SHORT).show();
					}
					catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(getApplicationContext(), "Error: Unable to cast vote.", Toast.LENGTH_SHORT).show();
						
					}
					
					rg.clearCheck();
					
				}
				else if (action.equals("Kill")) {
					
					try {
						HttpRequestTask submitVoteTask = new HttpRequestTask(UserListActivity.this, "Killing " + victimUsername + "...");
				        String response = submitVoteTask.execute("http://werewolf-jpblair.herokuapp.com/players/" + victimUsername, "POST", username, password, "action", "kill").get();
				        JSONObject jsonResponse = new JSONObject(response);
				        if (jsonResponse.getString("success").equals("false"))
				        	Toast.makeText(getApplicationContext(), "Error: Unable to kill " + victimUsername + ".", Toast.LENGTH_SHORT).show();
				        else
				        	Toast.makeText(getApplicationContext(), victimUsername + " was successfully killed.", Toast.LENGTH_SHORT).show();
					}
					catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(getApplicationContext(), "Error: Unable to kill " + victimUsername + ".", Toast.LENGTH_SHORT).show();
						
					}
					
					rg.clearCheck();
					
				}
				
			}
		});
		
	}
	
	public boolean validateLogin() {
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(UserListActivity.this.getBaseContext());
		username = settings.getString("edu.wm.werewolfmobile.username", "Unavailable");
		password = settings.getString("edu.wm.werewolfmobile.password", "Unavailable");
		
		System.out.println("username - " + username);
		System.out.println("password - " + password);
		
		if (username.equals("Unavailable") || password.equals("Unavailable"))
			return false;
		
		try {
			HttpRequestTask loginTask = new HttpRequestTask(UserListActivity.this, "Verifying account information...");
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
