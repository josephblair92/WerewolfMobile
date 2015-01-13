package edu.wm.werewolfmobile;

import com.example.werewolfmobile.R;

import edu.wm.werewolfmobile.exceptions.AccountCreationErrorException;
import edu.wm.werewolfmobile.service.LocationUpdateService;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class HomeActivity extends Activity {
	
	TextView gameStatus;
	TextView playerRatio;
	ImageView image;
	ProgressBar pb;
	boolean flag;
	String username = "";
	String password = "";
	static boolean NIGHT = false;
	static boolean DAY = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		gameStatus = (TextView)findViewById(R.id.gameStatus);
		image = (ImageView)findViewById(R.id.imageView1);
		flag = true;
		
		Button btnLogout = (Button) findViewById(R.id.logoutButton);
		Button btnPlayers = (Button)findViewById(R.id.playersButton);
		Button btnScores = (Button)findViewById(R.id.Button01);
		
		btnScores.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				Intent scoresIntent = new Intent(HomeActivity.this, ScoresActivity.class);
				startActivity(scoresIntent);
				
			}
		});
		
		btnLogout.setOnClickListener(new View.OnClickListener() {
    	 	
            public void onClick(View arg0) {
            	
            	//Clear out stored username/password
            	
            	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this.getBaseContext());
        		SharedPreferences.Editor editor = settings.edit();
        	    editor.putString("edu.wm.werewolfmobile.username", null);
        	    editor.putString("edu.wm.werewolfmobile.password", null);
        	    editor.commit();
        	    
        	    //Stop background service
        	    
        		Intent locationUpdateServiceIntent = new Intent(HomeActivity.this, LocationUpdateService.class);
        		HomeActivity.this.stopService(locationUpdateServiceIntent);
            	
            	Intent loginScreen = new Intent(getApplicationContext(), LoginActivity.class); 
                startActivity(loginScreen);            	
            	
            }
		});
		
		btnPlayers.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
		        Intent userListScreen = new Intent(getApplicationContext(), UserListActivity.class); 
		        startActivity(userListScreen);				
			}
			
		});
		
		System.out.println("End of onCreate");
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateGame();
	}
	
	public void updateGame() {
		
		boolean timeOfDay = DAY;
		boolean playerDead = false;
		int numWerewolves = 0;
		int numTownspeople = 0;
		
		//Check that the user is logged in
		
		if (validateLogin() == false)  {
			System.out.println("Login validation failed");
	        Intent loginScreen = new Intent(getApplicationContext(), LoginActivity.class); 
	        startActivity(loginScreen);
	        return;
		}
		
		//Start background service if it isn't already running
		
		if (!LocationUpdateService.timerRunning)  {
			Intent locationUpdateServiceIntent = new Intent(this, LocationUpdateService.class);
			startService(locationUpdateServiceIntent);
		}
		
		System.out.println("Validated login");
		
		//Fill in top with username, initialize layout items
		
		TextView usernameLabel = (TextView)findViewById(R.id.loggedInUsernameLabel);
		playerRatio = (TextView)findViewById(R.id.playerRatio);
		pb = (ProgressBar)findViewById(R.id.progressBar1);
		usernameLabel.setText(username);
		
		//Check if there is an active game
		
		try {
			HttpRequestTask activegameTask = new HttpRequestTask(HomeActivity.this, "Checking for active game...");
			String activeGameResponse = activegameTask.execute("http://werewolf-jpblair.herokuapp.com/isgameactive", "GET", null, null).get();
			if (activeGameResponse.contains("false"))  {
		        Intent newgameScreen = new Intent(getApplicationContext(), NewGameActivity.class); 
		        startActivity(newgameScreen);
		        return;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Validated active game");
		
		//Check if the player is alive
		
		try {
			HttpRequestTask checkDeadTask = new HttpRequestTask(HomeActivity.this, "Checking if alive...");
			String checkDeadResponse = checkDeadTask.execute("http://werewolf-jpblair.herokuapp.com/amdead", "GET", username, password).get();
			if (checkDeadResponse.contains("true"))  {
				System.out.println("dead");
				playerDead = true;
				playerDead();
				//updateUI(playerDead, timeOfDay, numWerewolves, numTownspeople);
				return;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Validated alive");
		
		//Check the time of day
		
		CheckTimeTask ctt = new CheckTimeTask(this, "Checking time of day...");
		ctt.execute();
		
		//Check number of townspeople and werewolves alive
		
		PlayerRatioTask prt = new PlayerRatioTask(this, "Checking game status...");
		prt.execute();
		
		/*
		
		try {
			HttpRequestTask statusTask = new HttpRequestTask(HomeActivity.this, "Checking game status...");
			String response = statusTask.execute("http://werewolf-jpblair.herokuapp.com/atnight", "GET", username, password).get();
			if (response.contains("false"))  {
		        timeOfDay = DAY;
			}
			else if (response.contains("true")) {
				timeOfDay = NIGHT;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Checked for time of day");
		
		*/
		
		//Check number of werewolves and townspeople, set progress bar
		
		/*
		
		try {
			
			
			HttpRequestTask aliveTownspeopleTask = new HttpRequestTask(HomeActivity.this, "Checking game status...");
			String response1 = aliveTownspeopleTask.execute("http://werewolf-jpblair.herokuapp.com/numAliveTownspeople", "GET", username, password).get();
			numTownspeople=Integer.parseInt(response1);
			
			System.out.println("Checked number of townspeople");
			
			HttpRequestTask aliveWerewolvesTask = new HttpRequestTask(HomeActivity.this, "Checking game status...");
			String response2 = aliveWerewolvesTask.execute("http://werewolf-jpblair.herokuapp.com/numAliveWerewolves", "GET", username, password).get();
			numWerewolves=Integer.parseInt(response2);
			
			System.out.println("Checked number of werewolves");
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		*/
		
	}
	
	public void updateProgressBar(int numWerewolves, int numTownspeople) {
		
		playerRatio.setText(numWerewolves + " werewolves and " + numTownspeople + " townspeople are alive.");
		double total = numTownspeople + numWerewolves;
		pb.setProgress((int)(100 * (numWerewolves/total)));
		
	}
	
	private void playerDead() {
		System.out.println("In playerDead()");
		gameStatus.setText("You are dead!");
		image.setImageResource(R.drawable.skull);
		playerRatio.setText("Please wait until the next game starts.");
		pb.setVisibility(View.INVISIBLE);
	}

	public void day() {
		gameStatus.setText("It is currently day");
		image.setImageResource(R.drawable.day);
		
	}
	
	public void night() {
		gameStatus.setText("It is currently night");
		image.setImageResource(R.drawable.night);
	}
	
	public boolean validateLogin() {
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this.getBaseContext());
		username = settings.getString("edu.wm.werewolfmobile.username", "Unavailable");
		password = settings.getString("edu.wm.werewolfmobile.password", "Unavailable");
		
		System.out.println("username - " + username);
		System.out.println("password - " + password);
		
		if (username.equals("Unavailable") || password.equals("Unavailable"))
			return false;
		
		try {
			HttpRequestTask loginTask = new HttpRequestTask(HomeActivity.this, "Verifying account information...");
			String loginResponse = loginTask.execute("http://werewolf-jpblair.herokuapp.com/", "GET", username, password).get();
			System.out.println("Tried connecting to server");
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

/*
        final Button button = (Button) findViewById(R.id.ButtonGo);
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

            }
        }); 
        
*/
