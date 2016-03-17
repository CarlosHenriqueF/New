package by.vshkl.tappydefender.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;

import by.vshkl.tappydefender.game.EnemyShip;
import by.vshkl.tappydefender.game.PlayerShip;
import by.vshkl.tappydefender.game.SpaceDust;

public class TDView extends SurfaceView implements Runnable
{

	 volatile boolean playing;
	 Thread gameThread = null;

	 private PlayerShip playerShip;
	 private EnemyShip enemyShip1;
	 private EnemyShip enemyShip2;
	 private EnemyShip enemyShip3;
	 private Paint paint;
	 private Canvas canvas;
	 private SurfaceHolder holder;

	 // HUD
	 private float distanceRemaining;
	 private long timeTaken;
	 private long timeStarted;
	 private long fastestTime;

	 private int screenX;
	 private int screenY;

	 private Context context;

	 private boolean gameEnded;

	 public ArrayList<SpaceDust> dustList = new ArrayList<SpaceDust>();

	 /* Sounds */
	 private SoundPool soundPool;
	 int start = -1;
	 int bump = -1;
	 int destroyed = -1;
	 int win = -1;

	 /* Game data persistence */
	 private SharedPreferences prefs;
	 private SharedPreferences.Editor editor;

	 public TDView(Context context, int x, int y)
	 {
			super(context);

			this.context = context;

			// Get a reference to a file called HiScores. If id doesn't exist one is created
			prefs = context.getSharedPreferences("HiScores", context.MODE_PRIVATE);
			editor = prefs.edit();

			fastestTime = prefs.getLong("fastestTime", 1000000);

			// This SoundPool is deprecated but don't worry
			soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
			try
			{
				 //Create objects of the 2 required classes
				 AssetManager assetManager = context.getAssets();
				 AssetFileDescriptor descriptor;

				 //create our three fx in memory ready for use
				 descriptor = assetManager.openFd("start.ogg");
				 start = soundPool.load(descriptor, 0);

				 descriptor = assetManager.openFd("win.ogg");
				 win = soundPool.load(descriptor, 0);

				 descriptor = assetManager.openFd("bump.ogg");
				 bump = soundPool.load(descriptor, 0);

				 descriptor = assetManager.openFd("destroyed.ogg");
				 destroyed = soundPool.load(descriptor, 0);
			}
			catch (IOException e)
			{
				 Log.e("error", "failed to load sound files");
			}

			screenX = x;
			screenY = y;

			holder = getHolder();
			paint = new Paint();

			startGame();
	 }

	 @Override
	 public void run()
	 {
			while (playing)
			{
				 update();
				 draw();
				 control();
			}
	 }

	 // SurfaceView allows us to handle the onTouchEvent
	 @Override
	 public boolean onTouchEvent(MotionEvent event)
	 {
			switch (event.getAction() & MotionEvent.ACTION_MASK)
			{
				 case MotionEvent.ACTION_DOWN:
						playerShip.setBoosting();
						// If we are currently on the pause screen, start a new game
						if (gameEnded)
						{
							 startGame();
						}
						break;
				 case MotionEvent.ACTION_UP:
						playerShip.stopBoosting();
						break;
			}

			return true;
	 }

	 /* Start game implementation */
	 public void startGame()
	 {
			gameEnded = false;

			/* Initialise player's ship */
			playerShip = new PlayerShip(context, screenX, screenY);

			/* Initialise enemy ships */
			enemyShip1 = new EnemyShip(context, screenX, screenY);
			enemyShip2 = new EnemyShip(context, screenX, screenY);
			enemyShip3 = new EnemyShip(context, screenX, screenY);

			/* Initialise space objects */
			int numSpecs = 40;
			for (int i = 0; i < numSpecs; i++)
			{
				 SpaceDust spec = new SpaceDust(screenX, screenY);
				 dustList.add(spec);
			}

			/* Reset time and distance */
			distanceRemaining = 10000;  // 10 km
			timeTaken = 0;

			/* Get start time */
			timeStarted = System.currentTimeMillis();

			soundPool.play(start, 1, 1, 0, 0, 1);
	 }

	 /* Clean up our thread if the game is interrupted or the player quits */
	 public void pause()
	 {
			playing = false;
			try
			{
				 gameThread.join();
			}
			catch (InterruptedException e)
			{
				 e.printStackTrace();
			}
	 }

	 /* Make a new thread and start it execution moves to our R */
	 public void resume()
	 {
			playing = true;
			gameThread = new Thread(this);
			gameThread.start();
	 }

	 private void update()
	 {
			// Collision detection on new positions
			// Before move because we are testing last frames
			// position which has just been drawn
			// If you are using images in excess of 100 pixels
			// wide then increase the -100 value accordingly
			boolean hitDetected = false;

			if (Rect.intersects(playerShip.getHitBox(), enemyShip1.getHitBox()))
			{
				 hitDetected = true;
				 enemyShip1.setX(-400);
			}

			if (Rect.intersects(playerShip.getHitBox(), enemyShip2.getHitBox()))
			{
				 hitDetected = true;
				 enemyShip2.setX(-400);
			}

			if (Rect.intersects(playerShip.getHitBox(), enemyShip3.getHitBox()))
			{
				 hitDetected = true;
				 enemyShip3.setX(-400);
			}

			if (hitDetected)
			{
				 soundPool.play(bump, 1, 1, 0, 0, 1);
				 playerShip.reduceShieldStrength();
				 if (playerShip.getShieldStrength() < 0)
				 {
						soundPool.play(destroyed, 1, 1, 0, 0, 1);
						gameEnded = true;
				 }
			}

			playerShip.update();

			enemyShip1.update(playerShip.getSpeed());
			enemyShip2.update(playerShip.getSpeed());
			enemyShip3.update(playerShip.getSpeed());

			for (SpaceDust sd : dustList)
			{
				 sd.update(playerShip.getSpeed());
			}

			if (!gameEnded)
			{
				 //subtract distance to home planet based on current speed
				 distanceRemaining -= playerShip.getSpeed();
				 //How long has the player been flying
				 timeTaken = System.currentTimeMillis() - timeStarted;
			}

			//Completed the game!
			if (distanceRemaining < 0)
			{
				 soundPool.play(win, 1, 1, 0, 0, 1);
				 //check for new fastest time
				 if (timeTaken < fastestTime)
				 {
						// Save high score
						editor.putLong("fastestTime", timeTaken);
						editor.commit();
						fastestTime = timeTaken;
				 }

				 // avoid ugly negative numbers in the HUD
				 distanceRemaining = 0;

				 // Now end the game
				 gameEnded = true;
			}
	 }

	 private void draw()
	 {
			if (holder.getSurface().isValid())
			{

				 //First we lock the area of memory we will be drawing to
				 canvas = holder.lockCanvas();

				 // Rub out the last frame
				 canvas.drawColor(Color.argb(255, 0, 0, 0));

				 // For debugging =======================================================================
//            paint.setColor(Color.argb(255, 255, 255, 255));
//
//            canvas.drawRect(playerShip.getHitBox().left,
//                    playerShip.getHitBox().top,
//                    playerShip.getHitBox().right,
//                    playerShip.getHitBox().bottom,
//                    paint);
//
//            canvas.drawRect(enemyShip1.getHitBox().left,
//                    enemyShip1.getHitBox().top,
//                    enemyShip1.getHitBox().right,
//                    enemyShip1.getHitBox().bottom,
//                    paint);
//
//            canvas.drawRect(enemyShip2.getHitBox().left,
//                    enemyShip2.getHitBox().top,
//                    enemyShip2.getHitBox().right,
//                    enemyShip2.getHitBox().bottom,
//                    paint);
//
//            canvas.drawRect(enemyShip3.getHitBox().left,
//                    enemyShip3.getHitBox().top,
//                    enemyShip3.getHitBox().right,
//                    enemyShip3.getHitBox().bottom,
//                    paint);
				 // =====================================================================================

				 // Draw the player's ship
				 canvas.drawBitmap(
						playerShip.getBitmap(),
						playerShip.getX(),
						playerShip.getY(),
						paint);

				 // Draw enemies
				 canvas.drawBitmap(
						enemyShip1.getBitmap(),
						enemyShip1.getX(),
						enemyShip1.getY(), paint);

				 canvas.drawBitmap(
						enemyShip2.getBitmap(),
						enemyShip2.getX(),
						enemyShip2.getY(), paint);

				 canvas.drawBitmap(
						enemyShip3.getBitmap(),
						enemyShip3.getX(),
						enemyShip3.getY(), paint);

				 // White specs of dust
				 paint.setColor(Color.argb(255, 255, 255, 255));
				 for (SpaceDust sd : dustList)
				 {
						canvas.drawPoint(sd.getX(), sd.getY(), paint);
				 }

				 /* HUD displaying */
				 if (!gameEnded)
				 {
						paint.setTextAlign(Paint.Align.LEFT);
						paint.setColor(Color.argb(255, 255, 255, 255));
						paint.setTextSize(25);
						canvas.drawText("Fastest:" + fastestTime + "s", 10, 20, paint);
						canvas.drawText("Time:" + formatTime(timeTaken) + "s", screenX / 2, 20, paint);
						canvas.drawText("Distance:" + distanceRemaining / 1000 +
														" KM", screenX / 3, screenY - 20, paint);
						canvas.drawText("Shield:" + playerShip.getShieldStrength(), 10, screenY - 20, paint);
						canvas.drawText("Speed:" + playerShip.getSpeed() * 60 +
														" MPS", (screenX / 3) * 2, screenY - 20, paint);
				 }
				 else
				 {
						// Show pause screen
						paint.setTextSize(80);
						paint.setTextAlign(Paint.Align.CENTER);
						canvas.drawText("Game Over", screenX / 2, 100, paint);
						paint.setTextSize(25);
						canvas.drawText("Fastest:" + formatTime(fastestTime) + "s", 10, 160, paint);
						canvas.drawText("Time:" + formatTime(timeTaken) + "s", screenX / 2, 200, paint);
						canvas.drawText("Distance remaining:" +
														distanceRemaining / 1000 + " KM", screenX / 2, 240, paint);
						paint.setTextSize(80);
						canvas.drawText("Tap to replay!", screenX / 2, 350, paint);
				 }

				 // Unlock and draw the scene
				 holder.unlockCanvasAndPost(canvas);
			}
	 }

	 /* Control framerate. Slow down the ship*/
	 private void control()
	 {
			try
			{
				 gameThread.sleep(17);
			}
			catch (InterruptedException e)
			{
				 e.printStackTrace();
			}
	 }

	 private String formatTime(long time)
	 {
			long seconds = (time) / 1000;
			long thousandths = (time) - (seconds * 1000);
			String strThousandths = "" + thousandths;
			if (thousandths < 100)
			{
				 strThousandths = "0" + thousandths;
			}
			if (thousandths < 10)
			{
				 strThousandths = "0" + strThousandths;
			}
			return "" + seconds + "." + strThousandths;
	 }
}
