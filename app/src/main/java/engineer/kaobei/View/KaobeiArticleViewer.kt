package engineer.kaobei.View

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import engineer.kaobei.Model.Themes.Theme
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

    val limitedIndex = 14

    var header: Int = 0
    var footer: Int = 0
    var contentBackgroundImage: Int = 0
    var contentBackgroundColor: Int = 0
    var content: String = ""

    init {
        initWidget()
    }

    fun initWidget() {
        View.inflate(context, R.layout.widget_kaobei_article_viewer, this)
        mHeaderView = findViewById(R.id.header)
        mFooter1View = findViewById(R.id.footer1)
        mTv1Footer1 = findViewById(R.id.tv1_footer1)
        mTv2Footer1 = findViewById(R.id.tv2_footer1)
        mFooter2View = findViewById(R.id.footer2)
        mContentView = findViewById(R.id.content_view)
        mContentTv = findViewById(R.id.content_text)
        mContentBg = findViewById(R.id.content_background)
    }

    fun setTheme(context: Context, theme: Theme) {
        when (theme.additional.header) {
            "0" -> {
                mHeaderView.visibility = View.GONE
            }
            else -> {
                mHeaderView.visibility = View.VISIBLE
                mHeaderView.setImageResource(
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
                mFooter1View.visibility = View.GONE
                mFooter2View.visibility = View.GONE
            }
            "1" -> {
                mFooter1View.visibility = View.VISIBLE
                mFooter2View.visibility = View.GONE
                mFooter1View.setBackgroundColor(Color.parseColor(theme.backgroundColor))
                mTv1Footer1.setTextColor(Color.parseColor(theme.textColor))
                mTv2Footer1.setTextColor(Color.parseColor(theme.textColor))
            }
            else -> {
                mFooter1View.visibility = View.GONE
                mFooter2View.visibility = View.VISIBLE
                mFooter2View.setImageResource(
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
                mContentBg.setImageResource(0)
            }
            else -> {
                mContentBg.setImageResource(
                    this.resources.getIdentifier(
                        theme.additional.backgroundImage.substringAfter('.'),
                        theme.additional.backgroundImage.substringBefore('.'),
                        context.packageName
                    )
                )
            }
        }
        mContentTv.setTextColor(Color.parseColor(theme.textColor))
        mContentView.setBackgroundColor(Color.parseColor(theme.backgroundColor))
    }

    fun setTextContent(content: String) {
        mContentTv.text = limitedText(content)
    }

    fun limitedText(text: String): String {
        var copy = text
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