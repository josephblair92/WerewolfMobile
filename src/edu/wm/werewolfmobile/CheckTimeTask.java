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
import android.widget.Toast;

public class CheckTimeTask extends AsyncTask<String, Integer, String[]> {
	
	private Context appContext;
	private HomeActivity homeActivity;
	ProgressDialog pd;
	String msg;
	
	public CheckTimeTask(HomeActivity h, String loadingMsg) {
		homeActivity = h;
		this.msg = loadingMsg;
	}
	
	public CheckTimeTask() {
		appContext = null;
	}

    protected void onProgressUpdate(Integer... progress) {
        //setProgressPercent(progress[0]);
    }
    
    @Override
    protected void onPreExecute () {
    	
    	if (appContext != null)  {
	    	pd = new ProgressDialog(appContext);
	    	pd.setMessage(msg);
	    	pd.show();
    	}
    	else
    		pd = null;
    	
    }

    protected void onPostExecute(String[] result) {
    	//Toast.makeText(getApplicationContext(), "command sent", Toast.LENGTH_LONG).show();
    	super.onPostExecute(result);
    	
    	String strTownspeople = result[0];
    	String strWerewolves = result[1];
    	
    	if (strTownspeople.equals("") || strWerewolves.equals(""))
    		return;
    	
    	int numTownspeople=Integer.parseInt(strTownspeople);
		int numWerewolves=Integer.parseInt(strWerewolves);
		
		System.out.println("Townspeople: " + numTownspeople + " Werewolves: " + numWerewolves);
		
		homeActivity.updateProgressBar(numWerewolves, numTownspeople);
    	
    	//pd.setMessage("");
    	if (pd != null)
    		pd.dismiss();
    }

	@Override
	protected String[] doInBackground(String... params) {
		
		String response1 = httpRequest("http://werewolf-jpblair.herokuapp.com/numAliveTownspeople", "GET", null, null);
		String response2 = httpRequest("http://werewolf-jpblair.herokuapp.com/numAliveWerewolves", "GET", null, null);
		String [] retVal = {response1, response2};
		return retVal;		
		
	}
	
	public String httpRequest(String... params) {
		
		if (pd != null)
			pd.setMessage(msg);
		
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
