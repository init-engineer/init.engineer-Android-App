package engineer.kaobei.View

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import engineer.kaobei.Model.KaobelUser.KaobeiUser
import engineer.kaobei.R

/**
 * Class UserViewer.
 */
class UserViewer : LinearLayout {

    private lateinit var imageViewAvatar: ImageView
    private lateinit var imageViewAvatarBackground: ImageView
    private lateinit var textViewName: TextView
    private lateinit var textViewEmail: TextView
    private lateinit var cardViewAvatar: CardView
    private var authorized: Boolean = false

    constructor(context: Context) : super(context) {
        initWidget(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initWidget(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initWidget(context, attrs)
    }

    fun setProfile(kaobeiUser: KaobeiUser) {
        Glide.with(this)
            .load(kaobeiUser.avatar)
            .into(this.imageViewAvatar)
        Glide.with(this)
            .load(context.getDrawable(R.drawable.img_animated_rainbow))
            .into(this.imageViewAvatarBackground)
        this.textViewName.text = kaobeiUser.fullName
        this.textViewEmail.text = kaobeiUser.email
    }

    fun initView(authorized: Boolean) {
        if (authorized) {
            this.authorized = true
            this.textViewName.text = "Loading"
            this.textViewEmail.text = ""
        } else {
            this.authorized = false
            this.textViewName.text = "點擊登入"
            this.textViewEmail.visibility = View.GONE
            Glide.with(this)
                .load(resources.getDrawable(R.drawable.img_kb))
                .into(imageViewAvatar)
        }
    }

    private fun initWidget(context: Context, attrs: AttributeSet?) {
        View.inflate(context, R.layout.widget_user_viewer, this)
        this.imageViewAvatar = findViewById(R.id.img_avatar)
        this.imageViewAvatarBackground = findViewById(R.id.content_background)
        this.textViewName = findViewById(R.id.tv_name)
        this.textViewEmail = findViewById(R.id.tv_email)
        this.cardViewAvatar = findViewById(R.id.cardview_avatar)
    }
}