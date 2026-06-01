package sanchez.blockshooter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CanvasView extends View {
    private static final float BULLET_SPEED = 20f;
    private static final long FRAME_DELAY_MILLISECONDS = 16;
    private static final float PLAYER_SIZE = 80f;

    private final Paint playerPaint = new Paint();
    private final Paint padPaint = new Paint();
    private final List<Bullet> activeBullets = new ArrayList<>();

    private PointF padOrigin = null;
    private PointF padCurrent = null;
    private boolean isTouching = false;

    private final Runnable animationRunnable = new Runnable() {
        public void run() {
            updateGame();
            invalidate();
            postDelayed(this, FRAME_DELAY_MILLISECONDS);
        }
    };

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        playerPaint.setColor(Color.DKGRAY);
        playerPaint.setStyle(Paint.Style.FILL);
        playerPaint.setAntiAlias(true);

        padPaint.setColor(Color.GRAY);
        padPaint.setAlpha(100);
        padPaint.setStyle(Paint.Style.STROKE);
        padPaint.setStrokeWidth(5f);
        padPaint.setAntiAlias(true);

        post(animationRunnable);
    }

    private void updateGame() {
        Iterator<Bullet> iterator = activeBullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.update();
            if (bullet.isOutOfBounds(getWidth(), getHeight())) {
                iterator.remove();
            }
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.LTGRAY);

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        canvas.drawRect(
                centerX - PLAYER_SIZE / 2,
                centerY - PLAYER_SIZE / 2,
                centerX + PLAYER_SIZE / 2,
                centerY + PLAYER_SIZE / 2,
                playerPaint
        );

        for (Bullet bullet : activeBullets) {
            bullet.draw(canvas);
        }

        // visual feedback
        if (isTouching && padOrigin != null && padCurrent != null) {
            canvas.drawCircle(padOrigin.x, padOrigin.y, 50f, padPaint);
            canvas.drawLine(padOrigin.x, padOrigin.y, padCurrent.x, padCurrent.y, padPaint);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouching = true;
                padOrigin = new PointF(touchX, touchY);
                padCurrent = new PointF(touchX, touchY);
                break;

            case MotionEvent.ACTION_MOVE:
                if (padCurrent != null) {
                    padCurrent.set(touchX, touchY);
                }
                break;

            case MotionEvent.ACTION_UP:
                isTouching = false;
                if (padOrigin != null && padCurrent != null) {
                    processShot(padOrigin, padCurrent);
                }
                padOrigin = null;
                padCurrent = null;
                performClick();
                break;
        }
        return true;
    }

    public boolean performClick() {
        return super.performClick();
    }

    private void processShot(PointF origin, PointF current) {
        float deltaX = current.x - origin.x;
        float deltaY = current.y - origin.y;

        double angleRadians = Math.atan2(deltaY, deltaX);
        
        // Snap to nearest 45 degrees
        double snappedAngle = Math.round(angleRadians / (Math.PI / 4)) * (Math.PI / 4);

        float velocityX = (float) (BULLET_SPEED * Math.cos(snappedAngle));
        float velocityY = (float) (BULLET_SPEED * Math.sin(snappedAngle));

        activeBullets.add(new Bullet(getWidth() / 2f, getHeight() / 2f, velocityX, velocityY));
    }
}
