package engineer.kaobei.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.transition.MaterialSharedAxis
import engineer.kaobei.Activity.ArticleActivity
import engineer.kaobei.Model.Articles.Article
import engineer.kaobei.OnLoadMoreListener
import engineer.kaobei.R
import engineer.kaobei.RecyclerViewAdapterListener
import engineer.kaobei.RecyclerViewLoadMoreScroll
import engineer.kaobei.Util.SnackbarUtil
import engineer.kaobei.Viewmodel.ArticleListViewModel
import engineer.kaobei.Viewmodel.ListViewModel

class ArticleListFragment : Fragment() {

    companion object {
        const val visibleThreshold = 10
        const val recyclerviewDelayLoadingTime: Long = 300    //limited recyclerview loading time
        var page: Int = 1    //Paging
        fun newInstance() = ArticleListFragment()
    }

    private lateinit var mCoorView: CoordinatorLayout //Mainactivity view
    private lateinit var mHeaderView: TextView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mShimmer1: ShimmerFrameLayout
    private lateinit var mShimmer2: ShimmerFrameLayout
    private lateinit var mShimmer3: ShimmerFrameLayout
    private lateinit var mScrollListener: RecyclerViewLoadMoreScroll

    private lateinit var mViewModel: ArticleListViewModel
    private lateinit var adapter: ArticleListRecyclerViewAdapter
    private var articles : List<Article> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_article_list, container,
            false
        )
        val forward: MaterialSharedAxis = MaterialSharedAxis.create(MaterialSharedAxis.X, true)
        val backward: MaterialSharedAxis = MaterialSharedAxis.create(MaterialSharedAxis.X, false)
        enterTransition = forward
        exitTransition = backward
        mCoorView = activity?.findViewById(R.id.main_coordinator)!!
        mHeaderView = view.findViewById(R.id.header_view)
        mShimmer1 = view.findViewById(R.id.shimmer_view_container1)
        mShimmer2 = view.findViewById(R.id.shimmer_view_container2)
        mShimmer3 = view.findViewById(R.id.shimmer_view_container3)
        mRecyclerView = view.findViewById(R.id.articleList_recyclerView)
        initRecyclerview(view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    private fun initRecyclerview(view : View) {
        mRecyclerView.visibility = View.GONE
        mRecyclerView.isNestedScrollingEnabled = false
        val mLayoutManager = LinearLayoutManager(context)
        adapter = ArticleListRecyclerViewAdapter(view.context, articles)
        adapter.setListener(object : RecyclerViewAdapterListener<Article>{
            override fun onTheFirstInit(list: List<Article>) {
                activity?.runOnUiThread {
                    mHeaderView.visibility = View.GONE
                    mShimmer1.visibility = View.GONE
                    mShimmer2.visibility = View.GONE
                    mShimmer3.visibility = View.GONE
                    mRecyclerView.visibility = View.VISIBLE
                }
            }

            override fun onReceiveData() {
                mScrollListener.setLoaded()
            }

            override fun onFailedToReceiveData() {
                mScrollListener.setLoaded()
                page--
                SnackbarUtil.makeAnchorSnackbar(mCoorView, "讀取資料失敗，請稍後再試", R.id.gap)
            }

            override fun onNoMoreData() {
                mScrollListener.setIsScrolledToEnd()
            }

        })
        mScrollListener = RecyclerViewLoadMoreScroll(mLayoutManager, visibleThreshold)
        mScrollListener.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                if(adapter.isInit()){
                    Handler().postDelayed({
                        adapter.loadMoreArticle(++page)
                    }, recyclerviewDelayLoadingTime)
                }
            }
        })
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.addOnScrollListener(mScrollListener)
        mRecyclerView.adapter = adapter
    }
}

class ArticleListRecyclerViewAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mListener : RecyclerViewAdapterListener<Article>? = null
    private lateinit var mContext : FragmentActivity
    private lateinit var mViewModel : ArticleListViewModel

    private var articleList : List<Article> = listOf()
    private var loadingIndex = 0 //for deleting loading view
    private var init = false

    companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_LOADING = 1
        const val VIEW_TYPE_ITEM = 2
    }

    constructor( context: Context, articleList : List<Article>) : this() {
        mContext = context as FragmentActivity
        this.articleList = articleList
        mViewModel = ViewModelProviders.of(context).get(ArticleListViewModel::class.java)
        mViewModel.addOnReceiveDataListener(object :ListViewModel.OnReceiveDataListener<Article>{
            override fun onReceiveData(list: List<Article>) {
                removeLoadingView()
                mListener?.onReceiveData()
                if(!init){
                    init = true
                    mListener?.onTheFirstInit(list)
                }
            }
            override fun onFailureToReceiveData() {
                removeLoadingView()
                mListener?.onFailedToReceiveData()
            }

            override fun onNoMoreData() {

            }
        })
        mViewModel.getLiveData().observe(mContext , Observer<List<Article>> { articles ->
            this.articleList = articles
            notifyDataSetChanged()
        })
        // add header
        mViewModel.add(0, Article())
        mViewModel.loadMoreArticles(1)
    }

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val id = itemView.findViewById<TextView>(R.id.style1_id)
        private val date = itemView.findViewById<TextView>(R.id.style1_date)
        private var thumbnail = itemView.findViewById<ImageView>(R.id.style1_thumbnail)

        @SuppressLint("SetTextI18n")
        fun bind(article: Article) {
            id?.text =
                "#" + mContext.resources.getString(R.string.app_name_ch) + article.id.toString(36)
            date?.text = article.createdDiff
            Glide
                .with(mContext)
                .load(article.image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(thumbnail)
            itemView.setOnClickListener {
                val intent = Intent(mContext, ArticleActivity::class.java)
                intent.putExtra(ArticleActivity.ARTICLE_KEY, article)
                mContext.startActivity(intent)
            }
        }
    }
    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ITEM) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.cardview_style1, parent, false)
            return ArticleViewHolder(view)
        } else if (viewType == VIEW_TYPE_HEADER) {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.cardview_header1, parent, false)
            return HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.progress_loading, parent, false)
            return LoadingViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return this.articleList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> {
                VIEW_TYPE_HEADER
            }
            articleList[position].id == 0 -> {
                VIEW_TYPE_LOADING
            }
            else -> {
                VIEW_TYPE_ITEM
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ArticleViewHolder) {
            holder.bind(articleList[position])
        }
    }

    fun addLoadingView() {
        //Add loading item
        mViewModel.add(Article())
        loadingIndex = articleList.size - 1
    }

    fun removeLoadingView() {
        //Remove loading item
        if (articleList.isNotEmpty()) {
            if (loadingIndex >= 0) {
                mViewModel.remove(loadingIndex)
                loadingIndex = 0
            }
        }
    }

    fun loadMoreArticle(page : Int){
        addLoadingView()
        mViewModel.loadMoreArticles(page)
    }

    fun isInit() : Boolean{
        return init
    }

    fun setListener(mArticleListRecyclerViewAdapterListener : RecyclerViewAdapterListener<Article>){
        this.mListener = mArticleListRecyclerViewAdapterListener
    }
}
