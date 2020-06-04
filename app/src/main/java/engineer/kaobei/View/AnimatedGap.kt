package engineer.kaobei.View

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
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
    private var init = false

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
        this.paint.color = ContextCompat.getColor(context, R.color.FxWhite)
        this.paint.strokeWidth = 15F
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        this.w = w
        this.h = h
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (this.init) {
            canvas?.drawLine((this.w / 2).toFloat(), 0F, this.currentIndexLeft.toFloat(), 0F, this.paint)
            canvas?.drawLine((this.w / 2).toFloat(), 0F, this.currentIndexRight.toFloat(), 0F, this.paint)
        }
    }

    fun hide() {
        this.animator?.cancel()
        this.animator2?.cancel()
        this.animator = ValueAnimator.ofInt(0, w / 2).apply {
            this.duration = 200
            this.interpolator = DecelerateInterpolator()
            addUpdateListener { valueAnimator ->
                currentIndexLeft = valueAnimator.animatedValue as Int
                invalidate()
            }
        }
        this.animator2 = ValueAnimator.ofInt(w, w / 2).apply {
            this.duration = 200
            this.interpolator = DecelerateInterpolator()
            addUpdateListener { valueAnimator ->
                currentIndexRight = valueAnimator.animatedValue as Int
                Log.d("qijian", "curValue:" + valueAnimator.animatedValue);
                invalidate()
            }
        }
        this.animator?.start()
        this.animator2?.start()
    }

    fun show() {
        this.animator?.cancel()
        this.animator2?.cancel()
        this.animator = ValueAnimator.ofInt(this.w / 2, 0).apply {
            this.duration = 200
            this.interpolator = DecelerateInterpolator()
            addUpdateListener { valueAnimator ->
                if (!init) {
                    init = true
                } else {
                    currentIndexLeft = valueAnimator.animatedValue as Int
                    invalidate()
                }
            }
        }
        this.animator2 = ValueAnimator.ofInt(this.w / 2, this.w).apply {
            this.duration = 200
            this.interpolator = DecelerateInterpolator()
            addUpdateListener { valueAnimator ->
                currentIndexRight = valueAnimator.animatedValue as Int
                Log.d("qijian", "curValue:" + valueAnimator.animatedValue);
                invalidate()
            }
        }
        this.animator?.start()
        this.animator2?.start()
    }

}
