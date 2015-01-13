package edu.wm.werewolfmobile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

import com.example.werewolfmobile.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button btnNewUser = (Button) findViewById(R.id.login);
		
        btnNewUser.setOnClickListener(new View.OnClickListener() {
        	 
            public void onClick(View arg0) {
                //Starting a new Intent
                Intent loginScreen = new Intent(getApplicationContext(), LoginActivity.class); 
                startActivity(loginScreen);
 
            }
        });
        
		Button btnHome = (Button) findViewById(R.id.home);
		
        btnHome.setOnClickListener(new View.OnClickListener() {
        	 
            public void onClick(View arg0) {
                //Starting a new Intent
                Intent homeScreen = new Intent(getApplicationContext(), HomeActivity.class); 
                startActivity(homeScreen);
 
            }
        });
        
		Button btnUsers = (Button) findViewById(R.id.userList);
		
        btnUsers.setOnClickListener(new View.OnClickListener() {
        	 
            public void onClick(View arg0) {
                //Starting a new Intent
                Intent userScreen = new Intent(getApplicationContext(), UserListActivity.class); 
                startActivity(userScreen);
 
            }
        });
        
		Button btnScores = (Button) findViewById(R.id.scoresButton);
		
        btnScores.setOnClickListener(new View.OnClickListener() {
        	 
            public void onClick(View arg0) {
                //Starting a new Intent
                Intent scoresScreen = new Intent(getApplicationContext(), ScoresActivity.class); 
                startActivity(scoresScreen);
 
            }
        });
        
        Button btnGet = (Button)findViewById(R.id.getButton);
        btnGet.setOnClickListener(new View.OnClickListener() {
        	
        	public void onClick(View arg0) {
        		
        		HttpRequestTask h = new HttpRequestTask(MainActivity.this, "Creating new user...");
        		String result = "";
        		try {
					result = h.execute("http://werewolf-jpblair.herokuapp.com/newuser", "POST", null, null, "username", "yoloswag420", "password", "password", "firstname", "Bill", "lastname", "Jones").get();
        			//result = h.execute("http://werewolf-jpblair.herokuapp.com/scores", "GET", null, null).get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        		TextView tv1 = (TextView)findViewById(R.id.textView1);
        		tv1.setText("Request successful");
        		TextView tv2 = (TextView)findViewById(R.id.textView2);
        		tv2.setText(result);
        		
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
