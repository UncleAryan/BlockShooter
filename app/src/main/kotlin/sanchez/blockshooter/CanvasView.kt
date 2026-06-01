package sanchez.blockshooter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class CanvasView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val playerPaint = Paint()
    private val padPaint = Paint()
    private val activeBullets: MutableList<Bullet> = ArrayList<Bullet>()

    private var padOrigin: PointF? = null
    private var padCurrent: PointF? = null
    private var isTouching = false

    private val animationRunnable: Runnable = object : Runnable {
        override fun run() {
            updateGame()
            invalidate()
            postDelayed(this, FRAME_DELAY_MILLISECONDS)
        }
    }

    init {
        initialize()
    }

    private fun initialize() {
        playerPaint.color = Color.DKGRAY
        playerPaint.style = Paint.Style.FILL
        playerPaint.isAntiAlias = true

        padPaint.color = Color.GRAY
        padPaint.alpha = 100
        padPaint.style = Paint.Style.STROKE
        padPaint.strokeWidth = 5f
        padPaint.isAntiAlias = true

        post(animationRunnable)
    }

    private fun updateGame() {
        val iterator = activeBullets.iterator()
        while (iterator.hasNext()) {
            val bullet = iterator.next()
            bullet.update()
            if (bullet.isOutOfBounds(width, height)) {
                iterator.remove()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.LTGRAY)

        val centerX = width / 2f
        val centerY = height / 2f

        canvas.drawRect(
            centerX - PLAYER_SIZE / 2,
            centerY - PLAYER_SIZE / 2,
            centerX + PLAYER_SIZE / 2,
            centerY + PLAYER_SIZE / 2,
            playerPaint
        )

        for (bullet in activeBullets) {
            bullet.draw(canvas)
        }

        // visual feedback
        if (isTouching && padOrigin != null && padCurrent != null) {
            canvas.drawCircle(padOrigin!!.x, padOrigin!!.y, 50f, padPaint)
            canvas.drawLine(padOrigin!!.x, padOrigin!!.y, padCurrent!!.x, padCurrent!!.y, padPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isTouching = true
                padOrigin = PointF(touchX, touchY)
                padCurrent = PointF(touchX, touchY)
            }

            MotionEvent.ACTION_MOVE -> if (padCurrent != null) {
                padCurrent!!.set(touchX, touchY)
            }

            MotionEvent.ACTION_UP -> {
                isTouching = false
                if (padOrigin != null && padCurrent != null) {
                    processShot(padOrigin!!, padCurrent!!)
                }
                padOrigin = null
                padCurrent = null
                performClick()
            }
        }
        return true
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    private fun processShot(origin: PointF, current: PointF) {
        val deltaX = current.x - origin.x
        val deltaY = current.y - origin.y

        val angleRadians = atan2(deltaY.toDouble(), deltaX.toDouble())


        // Snap to nearest 45 degrees
        val snappedAngle = (angleRadians / (Math.PI / 4)).roundToInt() * (Math.PI / 4)

        val velocityX = (BULLET_SPEED * cos(snappedAngle)).toFloat()
        val velocityY = (BULLET_SPEED * sin(snappedAngle)).toFloat()

        activeBullets.add(Bullet(width / 2f, height / 2f, velocityX, velocityY))
    }

    companion object {
        private const val BULLET_SPEED = 20f
        private const val FRAME_DELAY_MILLISECONDS: Long = 16
        private const val PLAYER_SIZE = 80f
    }
}
