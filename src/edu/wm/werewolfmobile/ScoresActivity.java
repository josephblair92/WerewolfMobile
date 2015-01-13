package edu.wm.werewolfmobile;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.werewolfmobile.R;
import com.example.werewolfmobile.R.layout;
import com.example.werewolfmobile.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.Menu;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

public class ScoresActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scores);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		TableLayout table = (TableLayout)findViewById(R.id.scoresTable);
		//ShapeDrawable bg = new ShapeDrawable(new RectShape());
		//bg.getPaint().setColor(Color.argb(180,140,160,196));
		table.setBackgroundResource(R.drawable.rectangle_background);
		
		try {
			HttpRequestTask scoresTask = new HttpRequestTask(ScoresActivity.this, "Getting scores...");
			String scoresResponse = scoresTask.execute("http://werewolf-jpblair.herokuapp.com/scores", "GET", null, null).get();
			if (scoresResponse.equals(""))  {
		        return;
			}
			JSONArray scores = new JSONArray(scoresResponse);
			table.removeAllViews();
			
			for (int i = 0; i < scores.length(); i++) {
				JSONObject entry = scores.getJSONObject(i);
				TableRow row = new TableRow(this);
				
				TextView position = new TextView(this);
				position.setText("" + (i+1) + ".");
				position.setTextSize(20);
				row.addView(position);
				
				TextView username = new TextView(this);
				username.setText(entry.getString("username") + " (" + entry.getString("firstname") + " " + entry.getString("lastname") + ")");
				username.setTextSize(20);
				TableRow.LayoutParams lpUser = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
				lpUser.setMargins(15, 0, 50, 0);
				row.addView(username, lpUser);
				
				TextView score = new TextView(this);
				score.setTextSize(20);
				score.setText(entry.getString("score"));
				row.addView(score);
				
				table.addView(row);
				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scores_main, menu);
		return true;
	}

}
