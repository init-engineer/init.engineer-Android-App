package engineer.kaobei.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import engineer.kaobei.model.themes.Theme
import engineer.kaobei.R

/**
 * Class KaobeiArticleViewer.
 */
class KaobeiArticleViewer(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private lateinit var mHeaderView: ImageView
    private lateinit var mFooter1View: RelativeLayout
    private lateinit var mTv1Footer1: TextView
    private lateinit var mTv2Footer1: TextView
    private lateinit var mFooter2View: ImageView
    private lateinit var mContentView: RelativeLayout
    private lateinit var mContentTv: TextView
    private lateinit var mContentBg: ImageView

    private val limitedIndex: Int = 14

    var header: Int = 0
    var footer: Int = 0
    var contentBackgroundImage: Int = 0
    var contentBackgroundColor: Int = 0
    var content: String = ""

    init {
        this.initWidget()
    }

    fun setTheme(context: Context, theme: Theme) {
        when (theme.additional.header) {
            "0" -> {
                this.mHeaderView.visibility = View.GONE
            }
            else -> {
                this.mHeaderView.visibility = View.VISIBLE
                this.mHeaderView.setImageResource(
                    this.resources.getIdentifier(
                        theme.additional.header.substringAfter('.'),
                        theme.additional.header.substringBefore('.'),
                        context.packageName
                    )
                )
            }
        }
        when (theme.additional.footer) {
            "0" -> {
                this.mFooter1View.visibility = View.GONE
                this.mFooter2View.visibility = View.GONE
            }
            "1" -> {
                this.mFooter1View.visibility = View.VISIBLE
                this.mFooter2View.visibility = View.GONE
                this.mFooter1View.setBackgroundColor(Color.parseColor(theme.backgroundColor))
                this.mTv1Footer1.setTextColor(Color.parseColor(theme.textColor))
                this.mTv2Footer1.setTextColor(Color.parseColor(theme.textColor))
            }
            else -> {
                this.mFooter1View.visibility = View.GONE
                this.mFooter2View.visibility = View.VISIBLE
                this.mFooter2View.setImageResource(
                    this.resources.getIdentifier(
                        theme.additional.footer.substringAfter('.'),
                        theme.additional.footer.substringBefore('.'),
                        context.packageName
                    )
                )
            }
        }
        when (theme.additional.backgroundImage) {
            "0" -> {
                this.mContentBg.setImageResource(0)
            }
            else -> {
                this.mContentBg.setImageResource(
                    this.resources.getIdentifier(
                        theme.additional.backgroundImage.substringAfter('.'),
                        theme.additional.backgroundImage.substringBefore('.'),
                        context.packageName
                    )
                )
            }
        }
        this.mContentTv.setTextColor(Color.parseColor(theme.textColor))
        this.mContentView.setBackgroundColor(Color.parseColor(theme.backgroundColor))
    }

    fun setTextContent(content: String) {
        this.mContentTv.text = this.limitedText(content)
    }

    private fun initWidget() {
        View.inflate(this.context, R.layout.widget_kaobei_article_viewer, this)
        this.mHeaderView = findViewById(R.id.header)
        this.mFooter1View = findViewById(R.id.footer1)
        this.mTv1Footer1 = findViewById(R.id.tv1_footer1)
        this.mTv2Footer1 = findViewById(R.id.tv2_footer1)
        this.mFooter2View = findViewById(R.id.footer2)
        this.mContentView = findViewById(R.id.content_view)
        this.mContentTv = findViewById(R.id.content_text)
        this.mContentBg = findViewById(R.id.content_background)
    }

    private fun limitedText(text: String): String {
        var copy: String = text
        var result = ""
        while (copy.length > limitedIndex) {
            val buffer = copy.substring(0, limitedIndex)
            result = result + buffer + "\n"
            copy = copy.substring(limitedIndex)
        }
        result += copy
        return result
    }
}