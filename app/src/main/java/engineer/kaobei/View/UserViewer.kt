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
class UserViewer : LinearLayout{

    private lateinit var img_avatar : ImageView
    private lateinit var img_avatar_background : ImageView
    private lateinit var tv_name : TextView
    private lateinit var tv_email : TextView
    private lateinit var cardview_avatar : CardView
    private  var authorized: Boolean = false

    constructor(context: Context) : super(context) {
        initWidget(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initWidget(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initWidget(context, attrs)
    }

    fun initWidget(context: Context, attrs: AttributeSet?){
        View.inflate(context, R.layout.widget_user_viewer, this)
        img_avatar = findViewById(R.id.img_avatar)
        img_avatar_background = findViewById(R.id.content_background)
        tv_name = findViewById(R.id.tv_name)
        tv_email = findViewById(R.id.tv_email)
        cardview_avatar = findViewById(R.id.cardview_avatar)
    }

    fun setProfile(kaobeiUser: KaobeiUser){
        Glide.with(this)
            .load(kaobeiUser.avatar)
            .into(img_avatar)
        Glide.with(this)
            .load(resources.getDrawable(R.drawable.img_animated_rainbow))
            .into(img_avatar_background)
        tv_name.text = kaobeiUser.fullName
        tv_email.text = kaobeiUser.email
    }

    fun initView(authorized:Boolean){
        if(authorized){
            this.authorized = true
            tv_name.text = "Loading"
            tv_email.text = ""
        }else{
            this.authorized = false
            tv_name.text = "點擊登入"
            tv_email.visibility=View.GONE
            Glide.with(this)
                .load(resources.getDrawable(R.drawable.img_kb))
                .into(img_avatar)
        }
    }
}