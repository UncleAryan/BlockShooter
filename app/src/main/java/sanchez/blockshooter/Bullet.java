package sanchez.blockshooter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Bullet {
    private float positionX;
    private float positionY;
    private final float velocityX;
    private final float velocityY;
    private final float radius;
    private final Paint paint;

    public Bullet(float startX, float startY, float velocityX, float velocityY) {
        this.positionX = startX;
        this.positionY = startY;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.radius = 15f;

        this.paint = new Paint();
        this.paint.setColor(Color.BLACK);
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setAntiAlias(true);
    }

    public void update() {
        positionX += velocityX;
        positionY += velocityY;
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle(positionX, positionY, radius, paint);
    }

    public boolean isOutOfBounds(int screenWidth, int screenHeight) {
        return positionX < -radius || positionX > screenWidth + radius ||
               positionY < -radius || positionY > screenHeight + radius;
    }
}
