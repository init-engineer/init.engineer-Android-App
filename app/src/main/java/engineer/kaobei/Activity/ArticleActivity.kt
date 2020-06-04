package engineer.kaobei.Activity

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import engineer.kaobei.Model.Articles.Article
import engineer.kaobei.Model.Comments.Comment
import engineer.kaobei.Model.Link.KaobeiLink
import engineer.kaobei.OnLoadMoreListener
import engineer.kaobei.R
import engineer.kaobei.RecyclerViewAdapterListener
import engineer.kaobei.RecyclerViewLoadMoreScroll
import engineer.kaobei.Util.ClipBoardUtil
import engineer.kaobei.Util.CustomTabUtil
import engineer.kaobei.Util.ViewUtil
import engineer.kaobei.Util.ext.viewLoading
import engineer.kaobei.Util.ext.viewLoadingWithTransition
import engineer.kaobei.Viewmodel.*
import kotlinx.android.synthetic.main.activity_article.*

/**
 * Class ArticleActivity.
 */
class ArticleActivity : AppCompatActivity() {

    companion object {
        const val ARTICLE_KEY: String = "ARTICLE_KEY"
        const val ID_KEY: String = "ID_KEY"
        const val loadingDelayTime: Long = 300
        const val visibleThreshold: Int = 15
    }

    private lateinit var adapter: ArticleRecyclerViewAdapter
    private var article: Article? = null
    private var id: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)
        
        article = intent.getParcelableExtra(ARTICLE_KEY)
        id = intent.getIntExtra(ID_KEY, 0)

        if (article == null) {
            if (id != null) {
                val articleInfoViewModel =
                    ViewModelProvider(this).get(ArticleInfoViewModel::class.java)
                articleInfoViewModel.getLiveData().observe(this, Observer { info ->
                    article = Article(
                        content = info.articleInfo.content,
                        createdAt = info.articleInfo.createdAt,
                        updatedAt = info.articleInfo.updatedAt,
                        createdDiff = info.articleInfo.createdDiff,
                        updatedDiff = info.articleInfo.updatedDiff,
                        id = info.articleInfo.id,
                        image = info.articleInfo.image
                    )
                    loadArticle(article)
                })
                articleInfoViewModel.addOnReceiveDataListener(object :
                    ObjectViewModel.OnReceiveDataListener {
                    override fun onReceiveData() {

                    }

                    override fun onFailureReceiveData() {
                        val tv_article_not_found = findViewById<TextView>(R.id.tv_article_not_found)
                        tv_article_not_found.visibility = View.VISIBLE
                        Snackbar.make(
                            findViewById(android.R.id.content),
                            "文章不存在",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                })
                articleInfoViewModel.loadArticleInfo(id!!)
            }
        } else {
            loadArticle(article)
        }

        //BackPress button
        tv_backpress.setOnClickListener {
            onBackPressed()
        }

    }

    private fun loadArticle(article: Article?) {
        val mLayoutManager = LinearLayoutManager(this)
        val scrollListener = RecyclerViewLoadMoreScroll(mLayoutManager, visibleThreshold)
        scrollListener.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                if (adapter.isInit()) {
                    Handler().postDelayed({
                        adapter.loadMoreComment()
                    }, loadingDelayTime)
                }
            }
        })
        comments_recyclerView.layoutManager = mLayoutManager
        ViewUtil.addGapController(comments_recyclerView, gap)
        adapter = ArticleRecyclerViewAdapter(this, article!!, KaobeiLink(), listOf())
        adapter.setHasStableIds(true)
        adapter.setListener(object : RecyclerViewAdapterListener<Comment> {
            override fun onTheFirstInit(list: List<Comment>) {
                // Do something ...
            }

            override fun onReceiveData() {
                scrollListener.setLoaded()
            }

            override fun onFailedToReceiveData() {
                scrollListener.setLoaded()
            }

            override fun onNoMoreData() {
                scrollListener.setIsScrolledToEnd()
            }

        })

        comments_recyclerView.adapter = adapter
        comments_recyclerView.addOnScrollListener(scrollListener)
    }

}

class ArticleRecyclerViewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var animalAvatar: AnimalAvatar = animalList.random()
    private var mListener: RecyclerViewAdapterListener<Comment>? = null
    private var loadingIndex: Int = 0
    private var comments: List<Comment> = listOf()
    private var linksIsLoaded: Boolean = false
    private var init: Boolean = false
    public lateinit var mViewModel: CommentsViewModel
    private lateinit var mLinksViewModel: LinkViewModel
    private lateinit var mArticle: Article
    private lateinit var mLinks: KaobeiLink
    private lateinit var mContext: FragmentActivity

    companion object {
        const val VIEW_TYPE_HEADER: Int = 0
        const val VIEW_TYPE_LOADING: Int = 1
        const val VIEW_TYPE_ITEM: Int = 2
        const val VIEW_TYPE_LINK: Int = 3
        const val VIEW_TYPE_LASTONE: Int = 4
    }

    constructor(
        context: FragmentActivity,
        article: Article,
        links: KaobeiLink,
        comments: List<Comment>
    ) : this() {
        this.mContext = context
        this.comments = comments
        this.mArticle = article
        this.mLinks = links
        mViewModel = ViewModelProvider(context).get(CommentsViewModel::class.java)
        mLinksViewModel = ViewModelProvider(context).get(LinkViewModel::class.java)
        mViewModel.addOnReceiveDataListener(object : ListViewModel.OnReceiveDataListener<Comment> {
            override fun onReceiveData(list: List<Comment>) {
                if (!init) {
                    init = true
                    mListener?.onTheFirstInit(list)
                }else{
                    removeLoadingView()
                }
                mListener?.onReceiveData()
            }

            override fun onFailureToReceiveData() {
                removeLoadingView()
                mListener?.onFailedToReceiveData()
            }

            override fun onNoMoreData() {
                mListener?.onNoMoreData()
                addLastOne()
            }
        })
        this.comments = mViewModel.getLivaDataValue(article.id).value!!
        mViewModel.getLiveData().observe(context, Observer<List<Comment>> { comments ->
            this.comments = comments
            notifyDataSetChanged()
        })
        mLinksViewModel.getLiveData().observe(mContext, Observer { links ->
            linksIsLoaded = true
            this.mLinks = links
            notifyItemChanged(1)
        })
        mLinksViewModel.addOnReceiveDataListener(object :
            ObjectViewModel.OnReceiveDataListener {
            override fun onReceiveData() {
                // Do something ...
            }

            override fun onFailureReceiveData() {
                mLinksViewModel.loadLink(article.id)
            }
        }
        )
        //mViewModel.add(0, Comment())
        //mViewModel.add(1, Comment())
        //mViewModel.loadComments(article.id, 1)
        mLinksViewModel.loadLink(article.id)
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
                "platform" -> {
                    media?.text = content.resources.getText(R.string.media_platform)
                    media?.background = ColorDrawable(content.resources.getColor(R.color.FXPF))
                }
            }
            /**
             * Hint:可能會Lag
             */

            avatar_background.viewLoading(
                ContextCompat.getDrawable(
                    mContext,
                    R.drawable.img_animated_rainbow
                )
            )

            if (TextUtils.equals(comment.avatar, "/img/frontend/user/nopic_192.gif")) {
                avatar.viewLoading(ContextCompat.getDrawable(mContext, R.drawable.img_nopic_192))
            } else {
                avatar.viewLoading(comment.avatar)
            }

            itemView.setOnClickListener {
                // Do something ...
            }
        }
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class LastOneViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
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

            avatar_background.viewLoadingWithTransition(ContextCompat.getDrawable(mContext, R.drawable.img_animated_rainbow))
            avatar.viewLoadingWithTransition(ContextCompat.getDrawable(mContext, animalAvatar.id))
            image.viewLoading(article.image)


            share.setOnClickListener {
                setDialog(it, "https://kaobei.engineer/cards/show/" + article.id)
            }

            itemView.setOnClickListener {
                // Do something ...
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
                sendIntent.putExtra(Intent.EXTRA_TEXT, url)
                sendIntent.type = "text/plain"
                it.context.startActivity(sendIntent)
            }

            cardview2.setOnClickListener {
                ClipBoardUtil.copy(it.context, url)
                bt_sheet.cancel()
            }

            cardview3.setOnClickListener {
                CustomTabUtil.createCustomTab(it.context, url)
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


        private var layout_fb1: LinearLayout = itemView.findViewById(R.id.layout_fb1)
        private var layout_fb2: LinearLayout = itemView.findViewById(R.id.layout_fb2)
        private var layout_tw: LinearLayout = itemView.findViewById(R.id.layout_tw)
        private var layout_pl: LinearLayout = itemView.findViewById(R.id.layout_pl)

        private var shimmer =
            itemView.findViewById<ShimmerFrameLayout>(R.id.shimmer_view_container1)
        private var view_links = itemView.findViewById<View>(R.id.view_links)

        fun bind(links: KaobeiLink) {

            var fb1 = false
            var fb2 = false
            var tw = false
            var pl = false

            if (!linksIsLoaded) {
                shimmer.visibility = View.VISIBLE
                view_links.visibility = View.GONE
                return
            } else {
                view_links.visibility = View.VISIBLE
                shimmer.visibility = View.GONE
            }

            for (i in links.data.indices) {
                if (links.data[i].type == "facebook") {
                    if (links.data[i].connections == "primary") {
                        fb1 = true
                        tvFb1Fv.text = links.data[i].like.toString()
                        tvFb1Ry.text = links.data[i].share.toString()
                        btnFb1.setOnClickListener { view ->
                            setDialog(view, links.data[i].url)
                        }
                    } else {
                        fb2 = true
                        tvFb2Fv.text = links.data[i].like.toString()
                        tvFb2Ry.text = links.data[i].share.toString()
                        btnFb2.setOnClickListener { view ->
                            setDialog(view, links.data[i].url)
                        }

                    }
                }
                if (links.data[i].type == "twitter") {
                    tw = true
                    tvTwFv.text = links.data[i].like.toString()
                    tvTwRy.text = links.data[i].share.toString()
                    btnTW.setOnClickListener { view ->
                        setDialog(view, links.data[i].url)
                    }
                }
                if (links.data[i].type == "plurk") {
                    pl = true
                    tvPlFv.text = links.data[i].like.toString()
                    tvPlRy.text = links.data[i].share.toString()
                    btnPL.setOnClickListener { view ->
                        setDialog(view, links.data[i].url)
                    }
                }
            }

            if (!fb1) {
                layout_fb1.visibility = View.GONE
            }
            if (!fb2) {
                layout_fb2.visibility = View.GONE
            }
            if (!tw) {
                layout_tw.visibility = View.GONE
            }
            if (!pl) {
                layout_pl.visibility = View.GONE
            }

        }

        fun setDialog(view: View, url: String) {
            val bt_sheet = BottomSheetDialog(view.context)
            val mView = LayoutInflater.from(view.context).inflate(R.layout.bottom_sheet_links, null)
            val textView: TextView = mView.findViewById(R.id.tv_bs_link)
            val cardview1: CardView = mView.findViewById(R.id.cardview_chrome_intent)
            val cardview2: CardView = mView.findViewById(R.id.cardview_intent)

            cardview1.setOnClickListener {
                CustomTabUtil.createCustomTab(it.context, url)
            }

            cardview2.setOnClickListener {
                ClipBoardUtil.copy(it.context, url)
                bt_sheet.cancel()
            }

            textView.text = url
            bt_sheet.setContentView(mView)
            bt_sheet.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview_style_comment, parent, false)
            return ItemViewHolder(view)
        } else if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview_header2, parent, false)
            return HeaderViewHolder(view)
        } else if (viewType == VIEW_TYPE_LINK) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview_style_link, parent, false)
            return LinkViewHolder(view)
        } else if (viewType == VIEW_TYPE_LASTONE) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.view_last_one, parent, false)
            return LastOneViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.progress_loading, parent, false)
            return LoadingViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return this.comments.count()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIEW_TYPE_HEADER
        } else if (position == 1) {
            VIEW_TYPE_LINK
        } else if (comments[position].created == "") {
            VIEW_TYPE_LOADING
        } else if (comments[position].created == "1") {
            VIEW_TYPE_LASTONE
        } else {
            VIEW_TYPE_ITEM
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.bind(comments[position])
        }
        if (holder is HeaderViewHolder) {
            holder.bind(mArticle)
        }
        if (holder is LinkViewHolder) {
            holder.bind(mLinks)
        }
    }

    fun addLastOne() {
        mViewModel.add(Comment(created = "1"))
    }

    fun addLoadingView() {
        mViewModel.add(Comment())
        loadingIndex = comments.size - 1
    }

    fun removeLoadingView() {
        if (comments.isNotEmpty()) {
            if (loadingIndex >= 1) {
                mViewModel.remove(loadingIndex)
                loadingIndex = 0
            }
        }
    }

    fun loadMoreComment() {
        addLoadingView()
        mViewModel.addPage()
        val index : Int = mViewModel.getPage().value!!
        mViewModel.loadComments(mArticle.id, index)
    }

    fun isInit(): Boolean {
        return init
    }

    fun setListener(mListener: RecyclerViewAdapterListener<Comment>) {
        this.mListener = mListener
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