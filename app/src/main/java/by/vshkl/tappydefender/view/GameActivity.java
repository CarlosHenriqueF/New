package by.vshkl.tappydefender.view;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;

public class GameActivity extends Activity
{

	 private TDView gameView;

	 @Override
	 protected void onCreate(Bundle savedInstanceState)
	 {
			super.onCreate(savedInstanceState);

			/* Get a Display object to access screen details */
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);

			/* Create an instance of our Tappy Defender View (TDView)
			 Also passing in "this" which is the Context of our app */
			gameView = new TDView(this, size.x, size.y);

			/* Make our gameView the view for the Activity */
			setContentView(gameView);
	 }

	 @Override
	 protected void onResume()
	 {
			super.onResume();

			/*Hide navigation bar. Show system UI on swipe from screen border. Hide in a second after.*/
			View decorView = getWindow().getDecorView();
			decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
																			| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
																			| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
																			| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

			gameView.resume();
	 }

	 @Override
	 protected void onPause()
	 {
			super.onPause();
			gameView.pause();
	 }
}
