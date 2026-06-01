package sanchez.blockshooter

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class Bullet(
    private var positionX: Float,
    private var positionY: Float,
    private val velocityX: Float,
    private val velocityY: Float
) {
    private val radius = 15f
    private val paint: Paint = Paint()

    init {
        this.paint.color = Color.BLACK
        this.paint.style = Paint.Style.FILL
        this.paint.isAntiAlias = true
    }

    fun update() {
        positionX += velocityX
        positionY += velocityY
    }

    fun draw(canvas: Canvas) {
        canvas.drawCircle(positionX, positionY, radius, paint)
    }

    fun isOutOfBounds(screenWidth: Int, screenHeight: Int): Boolean {
        return positionX < -radius || positionX > screenWidth + radius || positionY < -radius || positionY > screenHeight + radius
    }
}
