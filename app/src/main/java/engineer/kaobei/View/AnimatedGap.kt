package engineer.kaobei.View

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import engineer.kaobei.R

/**
 * Class AnimatedGap.
 */
class AnimatedGap : LinearLayout {

    private var paint: Paint = Paint()

    private var animator: ValueAnimator? = null
    private var animator2: ValueAnimator? = null
    private var currentIndexLeft = 0
    private var currentIndexRight = 0

    private var w = 0
    private var h = 0
    var inito = false

    constructor(context: Context) : super(context) {
        initial(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initial(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initial(context, attrs)
    }

    private fun initial(context: Context, attrs: AttributeSet?) {
        setWillNotDraw(false)
        paint.color = context.resources.getColor(R.color.FxWhite)
        paint.strokeWidth = 15F
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        this.w = w
        this.h = h
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (inito) {
            canvas?.drawLine((w / 2).toFloat(), 0F, currentIndexLeft.toFloat(), 0F, paint)
            canvas?.drawLine((w / 2).toFloat(), 0F, currentIndexRight.toFloat(), 0F, paint)
        }

    }

    fun hide() {
        animator?.cancel()
        animator2?.cancel()
        animator = ValueAnimator.ofInt(0, w / 2).apply {
            duration = 200
            interpolator = DecelerateInterpolator()
            addUpdateListener { valueAnimator ->
                currentIndexLeft = valueAnimator.animatedValue as Int
                invalidate()
            }
        }
        animator2 = ValueAnimator.ofInt(w, w / 2).apply {
            duration = 200
            interpolator = DecelerateInterpolator()
            addUpdateListener { valueAnimator ->
                currentIndexRight = valueAnimator.animatedValue as Int
                Log.d("qijian", "curValue:" + valueAnimator.animatedValue);
                invalidate()
            }
        }
        animator?.start()
        animator2?.start()
    }

    fun show() {
        animator?.cancel()
        animator2?.cancel()
        animator = ValueAnimator.ofInt(w / 2, 0).apply {
            duration = 200
            interpolator = DecelerateInterpolator()
            addUpdateListener { valueAnimator ->
                if (!inito) {
                    inito = true
                } else {
                    currentIndexLeft = valueAnimator.animatedValue as Int
                    invalidate()
                }

            }
        }
        animator2 = ValueAnimator.ofInt(w / 2, w).apply {
            duration = 200
            interpolator = DecelerateInterpolator()
            addUpdateListener { valueAnimator ->
                currentIndexRight = valueAnimator.animatedValue as Int
                Log.d("qijian", "curValue:" + valueAnimator.animatedValue);
                invalidate()
            }
        }
        animator?.start()
        animator2?.start()
    }

}