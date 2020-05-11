package engineer.kaobei.Activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import engineer.kaobei.Model.Article.Article
import engineer.kaobei.Model.Comments.Comment
import engineer.kaobei.Model.Link.KaobeiLink
import engineer.kaobei.OnLoadMoreListener
import engineer.kaobei.R
import engineer.kaobei.RecyclerViewLoadMoreScroll
import engineer.kaobei.Util.ViewUtil
import engineer.kaobei.View.AnimatedGap
import engineer.kaobei.Viewmodel.CommentsViewModel
import engineer.kaobei.Viewmodel.LinkViewModel
import kotlinx.android.synthetic.main.activity_article.*
import kotlin.jvm.internal.Ref


class ArticleActivity : AppCompatActivity() {

    companion object {
        const val ARTICLE_KEY: String = "ARTICLE_KEY"
        const val loadingDelayTime: Long = 500
        const val visbleThreshold = 15
    }

    private lateinit var commentsViewModel: CommentsViewModel
    private lateinit var linkViewModel: LinkViewModel
    private lateinit var adapter: LoadMoreRecyclerView


    private var init = false
    private var reInTop: Ref.BooleanRef = Ref.BooleanRef()
    private var page: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        val article = intent.extras?.get(ARTICLE_KEY) as Article
        reInTop.element = false

        //Backpress button
        tv_backpress.setOnClickListener {
            onBackPressed()
        }

        val mLayoutManager = LinearLayoutManager(this)
        val scrollListener = RecyclerViewLoadMoreScroll(mLayoutManager, visbleThreshold)
        val gap = findViewById<AnimatedGap>(R.id.gap)
        comments_recyclerView.layoutManager = LinearLayoutManager(this)
        scrollListener.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                if (init) {
                    adapter.addLoadingView()
                    Handler().postDelayed({
                        commentsViewModel.loadComments(article.id, ++page)
                    }, loadingDelayTime)
                }
            }
        })

        ViewUtil.addGapController(comments_recyclerView, gap, reInTop)
        commentsViewModel = ViewModelProviders.of(this).get(CommentsViewModel::class.java)
        commentsViewModel.addOnReceiveDataListener(object :
            CommentsViewModel.OnReceiveDataListener {
            override fun onReceiveData(list: List<Comment>) {
                if (init) {
                    adapter.removeLoadingView()
                    scrollListener.setLoaded()
                }
            }

            override fun onFailure() {
                if (!init) {
                    page--
                    adapter.removeLoadingView()
                    scrollListener.setLoaded()
                }
                Looper.prepare()
                Toast.makeText(
                    this@ArticleActivity,
                    this@ArticleActivity.resources.getText(R.string.loading_failed),
                    Toast.LENGTH_SHORT
                ).show()
                Looper.loop()
            }

            override fun onNoMoreComments() {
                scrollListener.setIsScrolledToEnd()
            }

        })
        commentsViewModel.getComments(article.id)
            .observe(this, Observer { comments ->
                if (!init) {
                    adapter.setComments(comments)
                    init = true
                }
                adapter.notifyDataSetChanged()
            })
        linkViewModel = ViewModelProviders.of(this).get(LinkViewModel::class.java)
        linkViewModel.getLink(article.id).observe(this, Observer { links ->
            adapter.setLinks(links)
            adapter.notifyItemChanged(1)
        })
        linkViewModel.addOnReceiveDataListener(object :
            LinkViewModel.OnReceiveDataListener {
            override fun onReceiveData(kaobeiLink: KaobeiLink) {

            }

            override fun onFailure() {
                linkViewModel.loadLink(article.id)
                adapter.notifyDataSetChanged()
            }
        })
        adapter =
            LoadMoreRecyclerView(
                this,
                article,
                commentsViewModel
            )
        adapter.setHasStableIds(true)
        comments_recyclerView.adapter = adapter
        comments_recyclerView.addOnScrollListener(scrollListener)

    }
}

class LoadMoreRecyclerView(
    private val Context: Context,
    private val article: Article,
    private val viewModel: CommentsViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var loadingIndex = 0

    private var comments: MutableList<Comment> = mutableListOf()
    private var linksIsLoaded = false
    private var links: KaobeiLink = KaobeiLink()
    private var animalAvatar = animalList.random()

    companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_LOADING = 1
        const val VIEW_TYPE_ITEM = 2
        const val VIEW_TYPE_LINK = 3
    }

    fun setComments(comments: MutableList<Comment>) {
        this.comments = comments
    }

    fun setLinks(links: KaobeiLink) {
        linksIsLoaded = true
        this.links = links
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var avatar = itemView.findViewById<ImageView>(R.id.comment_avatar)
        private var avatar_background =
            itemView.findViewById<ImageView>(R.id.comment_avatar_background)
        private var name = itemView.findViewById<TextView>(R.id.comment_name)
        private var content = itemView.findViewById<TextView>(R.id.comment_content)
        private var created_at = itemView.findViewById<TextView>(R.id.comment_created_at)
        private var media = itemView.findViewById<TextView>(R.id.comment_media)
        fun bind(comment: Comment) {
            name?.text = comment.name
            content?.text = comment.content
            created_at?.text = comment.created
            when (comment.media.type) {
                "facebook" -> {
                    when (comment.media.connections) {
                        "primary" -> {
                            media?.text = content.resources.getText(R.string.media_fb1)
                        }
                        "secondary" -> {
                            media?.text = content.resources.getText(R.string.media_fb2)
                        }
                    }
                    media?.background = ColorDrawable(content.resources.getColor(R.color.FxFB))
                }
                "plurk" -> {
                    media?.text = content.resources.getText(R.string.media_plurk)
                    media?.background = ColorDrawable(content.resources.getColor(R.color.FxPL))
                }
                "twitter" -> {
                    media?.text = content.resources.getText(R.string.media_twitter)
                    media?.background = ColorDrawable(content.resources.getColor(R.color.FXTW))
                }
            }
            /**
             *   Hint:可能會Lag
             * */
            /*   Glide
                   .with(context)
                   .load(content.resources.getDrawable(R.drawable.img_animated_rainbow))
                   .into(avatar_background)*/
            if (comment.avatar == "/img/frontend/user/nopic_192.gif") {
                Glide
                    .with(Context)
                    .load(content.resources.getDrawable(R.drawable.img_nopic_192))
                    .into(avatar)
            } else {
                Glide
                    .with(Context)
                    .load(comment.avatar)
                    .into(avatar)
            }
            itemView.setOnClickListener {
            }
        }
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var avatar = itemView.findViewById<ImageView>(R.id.header2_avatar)
        private var avatar_background =
            itemView.findViewById<ImageView>(R.id.header2_avatar_background)
        private var name = itemView.findViewById<TextView>(R.id.header2_name)
        private var content = itemView.findViewById<TextView>(R.id.header2_content)
        private var created_at = itemView.findViewById<TextView>(R.id.header2_created_at)
        private var image = itemView.findViewById<ImageView>(R.id.header2_image)
        private var share = itemView.findViewById<CardView>(R.id.cardview_share)
        fun bind(article: Article) {
            name?.text = animalAvatar.name
            content?.text = article.content
            created_at?.text = article.createdDiff
            Glide
                .with(Context)
                .load(content.resources.getDrawable(R.drawable.img_animated_rainbow))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(avatar_background)
            Glide
                .with(Context)
                .load(content.resources.getDrawable(animalAvatar.id))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(avatar)
            Glide
                .with(Context)
                .load(article.image)
                .into(image)
            share.setOnClickListener {
                setDialog(it, "https://kaobei.engineer/cards/show/" + article.id)
            }
            itemView.setOnClickListener {

            }
        }

        fun setDialog(view: View, url: String) {
            val bt_sheet = BottomSheetDialog(view.context)
            val mView = LayoutInflater.from(view.context).inflate(R.layout.bottom_sheet_share, null)
            val textView: TextView = mView.findViewById(R.id.tv_bs_link)
            val cardview1: CardView = mView.findViewById(R.id.cardview_share)
            val cardview2: CardView = mView.findViewById(R.id.cardview_intent)
            val cardview3: CardView = mView.findViewById(R.id.cardview_open)
            cardview1.setOnClickListener {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    url
                )
                sendIntent.type = "text/plain"
                it.context.startActivity(sendIntent)
            }
            cardview2.setOnClickListener {
                val myClipboard: ClipboardManager? =
                    Context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
                val myClip: ClipData? = ClipData.newPlainText("text", url)
                myClipboard?.primaryClip = myClip;
                bt_sheet.cancel()
            }
            cardview3.setOnClickListener {
                val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
                builder.setToolbarColor(Context.resources.getColor(R.color.colorPrimary))
                builder.setShowTitle(true)
                val customTabsIntent: CustomTabsIntent = builder.build()
                customTabsIntent.launchUrl(
                    view.context,
                    Uri.parse(url)
                )
            }
            textView.text = url
            bt_sheet.setContentView(mView)
            bt_sheet.show()
        }
    }

    inner class LinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var btnFb1 = itemView.findViewById<Button>(R.id.btn_link_fb1)
        private var tvFb1Fv = itemView.findViewById<TextView>(R.id.tv_fb1_fv)
        private var tvFb1Ry = itemView.findViewById<TextView>(R.id.tv_fb1_ry)

        private var btnFb2 = itemView.findViewById<Button>(R.id.btn_link_fb2)
        private var tvFb2Fv = itemView.findViewById<TextView>(R.id.tv_fb2_fv)
        private var tvFb2Ry = itemView.findViewById<TextView>(R.id.tv_fb2_ry)

        private var btnPL = itemView.findViewById<Button>(R.id.btn_link_pl)
        private var tvPlFv = itemView.findViewById<TextView>(R.id.tv_pl_fv)
        private var tvPlRy = itemView.findViewById<TextView>(R.id.tv_pl_ry)

        private var btnTW = itemView.findViewById<Button>(R.id.btn_link_tw)
        private var tvTwFv = itemView.findViewById<TextView>(R.id.tv_tw_fv)
        private var tvTwRy = itemView.findViewById<TextView>(R.id.tv_tw_ry)

        private var shimmer =
            itemView.findViewById<ShimmerFrameLayout>(R.id.shimmer_view_container1)
        private var view_links = itemView.findViewById<View>(R.id.view_links)

        fun bind(links: KaobeiLink) {
            if (links.data.size == 0) {
                return
            }
            if (!linksIsLoaded) {
                shimmer.visibility = View.VISIBLE
                view_links.visibility = View.GONE
            } else {
                view_links.visibility = View.VISIBLE
                shimmer.visibility = View.GONE
            }

            tvFb1Fv.text = links.data[0].like.toString()
            tvFb1Ry.text = links.data[0].share.toString()

            tvFb2Fv.text = links.data[1].like.toString()
            tvFb2Ry.text = links.data[1].share.toString()

            tvPlFv.text = links.data[3].like.toString()
            tvPlRy.text = links.data[3].share.toString()

            tvTwFv.text = links.data[2].like.toString()
            tvTwRy.text = links.data[2].share.toString()

            btnFb1.setOnClickListener { view ->
                setDialog(view, links.data[0].url)
            }
            btnFb2.setOnClickListener { view ->
                setDialog(view, links.data[1].url)
            }
            btnPL.setOnClickListener { view ->
                setDialog(view, links.data[3].url)
            }
            btnTW.setOnClickListener { view ->
                setDialog(view, links.data[2].url)
            }
        }

        fun setDialog(view: View, url: String) {
            val bt_sheet = BottomSheetDialog(view.context)
            val mView = LayoutInflater.from(view.context).inflate(R.layout.bottom_sheet_links, null)
            val textView: TextView = mView.findViewById(R.id.tv_bs_link)
            val cardview1: CardView = mView.findViewById(R.id.cardview_chrome_intent)
            val cardview2: CardView = mView.findViewById(R.id.cardview_intent)
            cardview1.setOnClickListener {
                val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
                builder.setToolbarColor(Context.resources.getColor(R.color.colorPrimary))
                builder.setShowTitle(true)
                val customTabsIntent: CustomTabsIntent = builder.build()
                customTabsIntent.launchUrl(
                    view.context,
                    Uri.parse(url)
                )
            }
            cardview2.setOnClickListener {
                var myClipboard: ClipboardManager? =
                    Context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
                var myClip: ClipData? = ClipData.newPlainText("text", url)
                myClipboard?.primaryClip = myClip;
                bt_sheet.cancel()
            }
            textView.text = url
            bt_sheet.setContentView(mView)
            bt_sheet.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ITEM) {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.cardview_style_comment, parent, false)
            return ItemViewHolder(view)
        } else if (viewType == VIEW_TYPE_HEADER) {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.cardview_header2, parent, false)
            return HeaderViewHolder(view)
        } else if (viewType == VIEW_TYPE_LINK) {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.cardview_style_link, parent, false)
            return LinkViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.progress_loading, parent, false)
            return LoadingViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return this.comments.count() + 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIEW_TYPE_HEADER
        } else if (position == 1) {
            VIEW_TYPE_LINK
        } else if (comments[position - 2].created == "") {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.bind(comments[position - 2])
        }
        if (holder is HeaderViewHolder) {
            holder.bind(article)
        }
        if (holder is LinkViewHolder) {
            holder.bind(links)
        }
    }

    fun addLoadingView() {
        viewModel.addComment(Comment())
        loadingIndex = comments.size - 1
    }

    fun removeLoadingView() {
        if (comments.isNotEmpty()) {
            if (loadingIndex >= 1) {
                viewModel.removeCommentAt(loadingIndex)
                loadingIndex = 0
            }
        }
    }

}

data class AnimalAvatar(val id: Int, val name: String)

val animalList: ArrayList<AnimalAvatar> = arrayListOf<AnimalAvatar>(
    AnimalAvatar(R.drawable.img_bat, "蝙蝠"),
    AnimalAvatar(R.drawable.img_bird, "青鳥"),
    AnimalAvatar(R.drawable.img_bull, "鬥牛"),
    AnimalAvatar(R.drawable.img_camel, "駱駝"),
    AnimalAvatar(R.drawable.img_cat, "貓咪")
)