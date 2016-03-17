package by.vshkl.tappydefender.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import by.vshkl.tappydefender.R;

public class MainActivity extends Activity
{

	 @Override
	 protected void onCreate(Bundle savedInstanceState)
	 {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);

			SharedPreferences prefs;
			prefs = getSharedPreferences("HiScores", MODE_PRIVATE);

			final TextView textHighscore = (TextView) findViewById(R.id.textView);
			textHighscore.setText("Fastest Time:" + prefs.getLong("fastestTime", 1000000));
	 }

	 @Override
	 protected void onResume()
	 {
			super.onResume();

			/* Hide navigation bar. Show system UI on swipe from screen border.
			 Hide in a second after.*/
			View decorView = getWindow().getDecorView();
			decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
																			| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
																			| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
																			| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	 }

	 public void startGame(View view)
	 {
			startActivity(new Intent(this, GameActivity.class));
			finish();
	 }

	 public void exitGame(View view)
	 {
			finish();
	 }

	 /* If the player hits the back button, quit the app */
	 @Override
	 public boolean onKeyDown(int keyCode, KeyEvent event)
	 {
			if (keyCode == KeyEvent.KEYCODE_BACK)
			{
				 finish();
				 return true;
			}
			return false;
	 }
}
