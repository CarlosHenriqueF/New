package by.vshkl.tappydefender.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import by.vshkl.tappydefender.R;

public class PlayerShip
{
	 private final int GRAVITY = -12;

	 // Stop ship leaving the screen
	 private int maxY;
	 private int minY;

	 //Limit the bounds of the ship's speed
	 private final int MIN_SPEED = 1;
	 private final int MAX_SPEED = 20;

	 private Bitmap bitmap;
	 private int x;
	 private int y;
	 private int speed = 0;
	 private boolean boosting;
	 private Rect hitBox;
	 private int shieldStrength;

	 public PlayerShip(Context context, int screenX, int screenY)
	 {
			x = 50;
			y = 50;
			speed = 1;
			bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ship);
			boosting = false;
			minY = 0;
			maxY = screenY - bitmap.getHeight();
			hitBox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
			shieldStrength = 2;
	 }

	 public void update()
	 {
			// Are we boosting?
			if (boosting)
			{
				 speed += 2;
			}
			else
			{
				 speed -= 5;
			}

			// Constrain top speed
			if (speed > MAX_SPEED)
			{
				 speed = MAX_SPEED;
			}

			// Never stop completely
			if (speed < MIN_SPEED)
			{
				 speed = MIN_SPEED;
			}

			// Move the ship up or down
			y -= speed + GRAVITY;

			// But don't let ship stray off screen
			if (y < minY)
			{
				 y = minY;
			}

			if (y > maxY)
			{
				 y = maxY;
			}

			// Refresh hit box location
			hitBox.left = x;
			hitBox.top = y;
			hitBox.right = x + bitmap.getWidth();
			hitBox.bottom = y + bitmap.getHeight();
	 }

	 public Bitmap getBitmap()
	 {
			return bitmap;
	 }

	 public int getX()
	 {
			return x;
	 }

	 public int getY()
	 {
			return y;
	 }

	 public int getSpeed()
	 {
			return speed;
	 }

	 public Rect getHitBox()
	 {
			return hitBox;
	 }

	 public int getShieldStrength()
	 {
			return shieldStrength;
	 }

	 /* Boosting enable */
	 public void setBoosting()
	 {
			boosting = true;
	 }

	 /* Boosting disable */
	 public void stopBoosting()
	 {
			boosting = false;
	 }

	 /* Reduce ship's shield strength */
	 public void reduceShieldStrength()
	 {
			shieldStrength --;
	 }
}
