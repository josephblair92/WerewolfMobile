package edu.wm.werewolfmobile;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.JSONObject;

import edu.wm.werewolfmobile.exceptions.HttpErrorException;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

public class PlayerListTask extends AsyncTask<String, Integer, String[]> {
	
	private Context appContext;
	private UserListActivity userListActivity;
	ProgressDialog pd;
	String msg;
	String username;
	String password;
	
	public PlayerListTask(UserListActivity u, String loadingMsg, String username, String password) {
		userListActivity = u;
		this.msg = loadingMsg;
		this.username=username;
		this.password=password;
	}
	
	public PlayerListTask() {
		appContext = null;
	}

    protected void onProgressUpdate(Integer... progress) {
        //setProgressPercent(progress[0]);
    }
    
    @Override
    protected void onPreExecute () {
    	
    	if (userListActivity != null)  {
	    	pd = new ProgressDialog(userListActivity);
	    	pd.setMessage(msg);
	    	pd.show();
    	}
    	else
    		pd = null;
    	
    }

    protected void onPostExecute(String... result) {
    	//Toast.makeText(getApplicationContext(), "command sent", Toast.LENGTH_LONG).show();
    	super.onPostExecute(result);
    	
		userListActivity.rg.clearCheck();
		userListActivity.rg.removeAllViews();
    	
    	String response = result[0];
    	String response2 = result[1];
    	String response3 = result[2];
    	String response4 = result[3];
    	
    	userListActivity.displayAlivePlayers(response);
    	userListActivity.displayDeadPlayers(response2);
    	
		if (response3.contains("true"))  {
			
			// at night
	        
	        if (response4.contains("true")) {
	        	// is werewolf
	        	userListActivity.displayNearbyPlayers();
	        }
	        else if (response4.contains("false")) {
	        	// is townsperson
	        	userListActivity.actionButton.setVisibility(View.INVISIBLE);
	        }
	        
		}
		else  {
			// day
			userListActivity.actionButton.setVisibility(View.VISIBLE);
			userListActivity.actionButton.setText("Vote");
		}
    	
    	//pd.setMessage("");
    	if (pd != null)
    		pd.dismiss();
    }

	@Override
	protected String[] doInBackground(String... params) {
		
		//Get alive players

		System.out.println("Getting alive players");
		String response = httpRequest("http://werewolf-jpblair.herokuapp.com/players/alive", "GET", username, password);
	
		//Get dead players

		System.out.println("Getting dead players");
		String response2 =  httpRequest("http://werewolf-jpblair.herokuapp.com/players/dead", "GET", username, password);

		//If night and werewolf, get nearby

		System.out.println("Checking time of day");
		String response3 = httpRequest("http://werewolf-jpblair.herokuapp.com/atnight", "GET", username, password);
		
		System.out.println("Checking if werewolf");
	    String response4 = httpRequest("http://werewolf-jpblair.herokuapp.com/amWerewolf", "GET", username, password);
	    
	    String [] retVal = {response, response2, response3, response4};
	    
	    return retVal;
		
	}
	
	public String httpRequest(String... params) {
		
    	String output = "";
    	
    	try	{
 	       
        	URL url = new URL(params[0]); 
        	String method = params[1];
        	String authBase64 = "";
        	
        	if (params[2] != null && params[3] != null) {
        		
            	String username = params[2];
            	String password = params[3];
        	
	        	String auth = username+":"+password;
	        	byte[] data = null;
	            try {
	                data = auth.getBytes("UTF-8");
	            } catch (UnsupportedEncodingException e1) {
	            e1.printStackTrace();
	            }
	            authBase64 = Base64.encodeToString(data, Base64.DEFAULT);
        	}
        	
        	for (int i = 3; i < params.length; i++)  {
        		
        	}
        	
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Connection", "close");
            //String encoded = Base64.encode(username+":"+password); 
            if (!authBase64.equals(""))
            	conn.setRequestProperty("Authorization", "Basic "+ authBase64);
            
            if (params[1].equals("GET"))
            	conn.setRequestMethod("GET");
            else if (params[1].equals("POST")) {
            	conn.setDoOutput(true);
            	conn.setDoInput(true);
            	conn.setRequestMethod("POST");
            	DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            	for (int i = 4; i < params.length - 1; i=i+2) {
            		
            		if (i != 4)
            			os.writeBytes("&");

            		os.writeBytes(params[i] + "=" + params[i+1]);
            	}
        		os.flush();
        		os.close();
            	/*
            	conn.setRequestProperty("Content-Type","application/json");
            	JSONObject payload = new JSONObject();
            	for (int i = 4; i < params.length - 1; i++)
            		payload.put(params[i], params[i+1]);
            	DataOutputStream os = new DataOutputStream(conn.getOutputStream ());
                os.writeUTF(URLEncoder.encode(payload.toString(),"UTF-8"));
                os.flush();
                os.close();
				*/
            	
            	
            	
            }
            
            // Get the response
            
            /*
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
            	throw new HttpErrorException();
            */
            
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            output = "";
            while ((line = rd.readLine()) != null) {
        		output += line;
            }
            
    	}
    	catch (Exception e)	{
    		e.printStackTrace();
    	}
    	
    	return output;
	}
	
}
