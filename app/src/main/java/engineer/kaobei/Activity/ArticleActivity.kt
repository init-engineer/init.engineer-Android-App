package engineer.kaobei.Activity


import android.content.Intent
import android.graphics.drawable.ColorDrawable
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
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
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
import engineer.kaobei.Viewmodel.CommentsViewModel
import engineer.kaobei.Viewmodel.ListViewModel
import engineer.kaobei.Viewmodel.LinkViewModel
import engineer.kaobei.Viewmodel.ObjectViewModel
import kotlinx.android.synthetic.main.activity_article.*


class ArticleActivity : AppCompatActivity() {

    companion object {
        const val ARTICLE_KEY: String = "ARTICLE_KEY"
        const val loadingDelayTime: Long = 500
        const val visbleThreshold = 17
    }

    private lateinit var adapter: ArticleRecyclerViewAdapter
    private var page: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        val article = intent.extras?.get(ARTICLE_KEY) as Article

        //BackPress button
        tv_backpress.setOnClickListener {
            onBackPressed()
        }

        val mLayoutManager = LinearLayoutManager(this)
        val scrollListener = RecyclerViewLoadMoreScroll(mLayoutManager, visbleThreshold)
        scrollListener.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                if(adapter.isInit()){
                    Handler().postDelayed({
                        adapter.loadMoreComment(++page)
                    }, loadingDelayTime)
                }
            }
        })
        comments_recyclerView.layoutManager = mLayoutManager
        ViewUtil.addGapController(comments_recyclerView, gap)
        adapter = ArticleRecyclerViewAdapter(this,article, KaobeiLink(), listOf())
        adapter.setHasStableIds(true)
        adapter.setListener(object : RecyclerViewAdapterListener<Comment>{
            override fun onTheFirstInit(list: List<Comment>) {

            }

            override fun onReceiveData() {
                scrollListener.setLoaded()
            }

            override fun onFailedToReceiveData() {
                page--
                scrollListener.setLoaded()
                Looper.prepare()
                Toast.makeText(
                    this@ArticleActivity,
                    this@ArticleActivity.resources.getText(R.string.loading_failed),
                    Toast.LENGTH_SHORT
                ).show()
                Looper.loop()
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

    private var mListener: RecyclerViewAdapterListener<Comment>? = null
    private var loadingIndex = 0
    private var comments: List<Comment> = listOf()
    private var linksIsLoaded = false
    private var animalAvatar = animalList.random()
    private lateinit var mViewModel: CommentsViewModel
    private lateinit var mLinksViewModel: LinkViewModel
    private var init = false
    private lateinit var mArticle: Article
    private lateinit var mLinks: KaobeiLink
    private lateinit var mContext: FragmentActivity
    companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_LOADING = 1
        const val VIEW_TYPE_ITEM = 2
        const val VIEW_TYPE_LINK = 3
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
        mViewModel = ViewModelProviders.of(context).get(CommentsViewModel::class.java)
        mLinksViewModel = ViewModelProviders.of(context).get(LinkViewModel::class.java)
        mViewModel.addOnReceiveDataListener(object : ListViewModel.OnReceiveDataListener<Comment> {
            override fun onReceiveData(list: List<Comment>) {
                if (!init) {
                    init = true
                    mListener?.onTheFirstInit(list)
                }
                removeLoadingView()
                mListener?.onReceiveData()
            }
            override fun onFailureToReceiveData() {
                removeLoadingView()
                mListener?.onFailedToReceiveData()
            }

            override fun onNoMoreData() {
                mListener?.onNoMoreData()
            }
        })
        mViewModel.getLiveData().observe(context , Observer<List<Comment>> { comments ->
            this.comments = comments
            notifyDataSetChanged()
        })
        mLinksViewModel.getLiveData().observe(mContext, Observer { links ->
            setLinks(links)
            notifyItemChanged(1)
        })
        mLinksViewModel.addOnReceiveDataListener(object :
            ObjectViewModel.OnReceiveDataListener {
            override fun onReceiveData() {

            }

            override fun onFailureReceiveData() {
                mLinksViewModel.loadLink(article.id)
            }
        })
        mViewModel.add(0, Comment())
        mViewModel.add(0, Comment())
        mViewModel.loadComments(article.id, 1)
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
            }
            /**
             *   Hint:可能會Lag
             * */
            Glide
                .with(mContext)
                .load(content.resources.getDrawable(R.drawable.img_animated_rainbow))
                .into(avatar_background)
            if (comment.avatar == "/img/frontend/user/nopic_192.gif") {
                Glide
                    .with(mContext)
                    .load(content.resources.getDrawable(R.drawable.img_nopic_192))
                    .into(avatar)
            } else {
                Glide
                    .with(mContext)
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
                .with(mContext)
                .load(content.resources.getDrawable(R.drawable.img_animated_rainbow))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(avatar_background)
            Glide
                .with(mContext)
                .load(content.resources.getDrawable(animalAvatar.id))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(avatar)
            Glide
                .with(mContext)
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
        return this.comments.count()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIEW_TYPE_HEADER
        } else if (position == 1) {
            VIEW_TYPE_LINK
        } else if (comments[position].created == "") {
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
            holder.bind(comments[position])
        }
        if (holder is HeaderViewHolder) {
            holder.bind(mArticle)
        }
        if (holder is LinkViewHolder) {
            holder.bind(mLinks)
        }
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

    fun loadMoreComment(page : Int){
        addLoadingView()
        mViewModel.loadComments(mArticle.id,page)
    }

    fun isInit() : Boolean{
        return init
    }

    fun setLinks(links: KaobeiLink) {
        linksIsLoaded = true
        this.mLinks = links
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