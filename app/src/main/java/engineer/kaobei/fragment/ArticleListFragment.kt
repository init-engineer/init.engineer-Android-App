package engineer.kaobei.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.transition.MaterialSharedAxis
import engineer.kaobei.activity.ArticleActivity
import engineer.kaobei.model.articles.Article
import engineer.kaobei.OnLoadMoreListener
import engineer.kaobei.R
import engineer.kaobei.RecyclerViewAdapterListener
import engineer.kaobei.RecyclerViewLoadMoreScroll
import engineer.kaobei.util.ext.viewLoadingWithTransition
import engineer.kaobei.util.SnackbarUtil
import engineer.kaobei.viewmodel.ArticleListViewModel
import engineer.kaobei.viewmodel.ListViewModel
import kotlinx.android.synthetic.main.fragment_article_list.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

class ArticleListFragment : Fragment() {

    companion object {
        const val visibleThreshold = 10
        const val recyclerviewDelayLoadingTime: Long = 300    //limited recyclerview loading time
        fun newInstance() = ArticleListFragment()
    }

    private lateinit var mCoorView: CoordinatorLayout //Mainactivity view
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mScrollListener: RecyclerViewLoadMoreScroll
    private lateinit var view_loading: View
    private lateinit var view_refresh_layout : SwipeRefreshLayout

    private lateinit var adapter: ArticleListRecyclerViewAdapter

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
        view_loading = view.findViewById(R.id.view_loading)
        view_refresh_layout = view.findViewById(R.id.view_refresh_layout)
        mRecyclerView = view.findViewById(R.id.articleList_recyclerView)
        initRecyclerview(view)
        return view
    }

    private fun initRecyclerview(view: View) {
        mRecyclerView.visibility = View.GONE
        mRecyclerView.isNestedScrollingEnabled = false
        val mLayoutManager = LinearLayoutManager(context)
        adapter = ArticleListRecyclerViewAdapter(view.context)
        adapter.setListener(object : RecyclerViewAdapterListener<Article> {
            override fun onTheFirstInit(list: List<Article>) {
                activity?.runOnUiThread {
                    view_loading.visibility = View.GONE
                    mRecyclerView.visibility = View.VISIBLE
                }
            }

            override fun onReceiveData() {
                mScrollListener.setLoaded()
            }

            override fun onFailedToReceiveData() {
                mScrollListener.setLoaded()
                adapter.pageBack()
                SnackbarUtil.makeAnchorSnackbar(mCoorView, "讀取資料失敗，請稍後再試", R.id.gap)
            }

            override fun onNoMoreData() {
                mScrollListener.setIsScrolledToEnd()
            }

        })
        mScrollListener = RecyclerViewLoadMoreScroll(mLayoutManager, visibleThreshold)
        mScrollListener.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                if (adapter.isInit()) {
                    Handler().postDelayed({
                        adapter.loadMoreArticle()
                    }, recyclerviewDelayLoadingTime)
                }
            }
        })
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.addOnScrollListener(mScrollListener)
        mRecyclerView.adapter = adapter


        view_refresh_layout.setOnRefreshListener {
            view_loading.visibility = View.VISIBLE
            adapter.refresh()
            view_refresh_layout.isRefreshing = false
        }

        //讓swipeRefreshLayout偵測RecyclerView是否在頂端
        view_refresh_layout.setOnChildScrollUpCallback(object:SwipeRefreshLayout.OnChildScrollUpCallback{
            override fun canChildScrollUp(parent: SwipeRefreshLayout, child: View?): Boolean {
                return articleList_recyclerView.computeVerticalScrollOffset() >0
            }

        })
    }
}

class ArticleListRecyclerViewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mListener: RecyclerViewAdapterListener<Article>? = null
    private lateinit var mContext: FragmentActivity
    private lateinit var mViewModel: ArticleListViewModel

    private var articleList: List<Article> = listOf()

    companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_LOADING = 1
        const val VIEW_TYPE_ITEM = 2
    }

    constructor(context: Context) : this() {
        mContext = context as FragmentActivity
        mViewModel = ViewModelProvider(context).get(ArticleListViewModel::class.java)
        mViewModel.addOnReceiveDataListener(object : ListViewModel.OnReceiveDataListener<Article> {
            override fun onReceiveData(list: List<Article>) {
                removeLoadingView()
                mListener?.onReceiveData()
                if (!mViewModel.isInit()) {
                    mViewModel.setInit(true)
                    mListener?.onTheFirstInit(list)
                }
            }
            override fun onFailureToReceiveData() {
                removeLoadingView()
                mListener?.onFailedToReceiveData()
            }

            override fun onNoMoreData() {
                mListener?.onNoMoreData()
            }
        })
        mViewModel.getLiveData().observe(mContext, Observer<List<Article>> { articles ->
            this.articleList = articles
            notifyDataSetChanged()
        })
        refresh()
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
            thumbnail.viewLoadingWithTransition(article.image)
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
        mViewModel.add(Article(id = -1))
    }

    fun removeLoadingView() {
        //Remove loading item
        if (mViewModel.isInit()) {
            for(i in mViewModel.getLiveData().value?.lastIndex?.downTo(0)!!){
                if(mViewModel.getLiveData().value!![i].id==-1){
                    mViewModel.remove(i)
                }
            }
        }
    }

    fun loadMoreArticle() {
        mViewModel.addPage()
        addLoadingView()
        if(getPage()!=null){
            getPage()?.let { mViewModel.loadMoreArticles(it) }
        }
    }

    fun isInit(): Boolean {
        return mViewModel.isInit()
    }

    fun pageBack(){
        mViewModel.minusPage()
    }

    fun pageUp(){
        mViewModel.addPage()
    }

    fun getPage() : Int? {
        return mViewModel.getPage().value
    }

    fun refresh(){
        mViewModel.refreshData()
        this.articleList = mViewModel.getLiveData().value!!
        mViewModel.getPage().value?.let { mViewModel.loadMoreArticles(it) }
    }

    fun setListener(mArticleListRecyclerViewAdapterListener: RecyclerViewAdapterListener<Article>) {
        this.mListener = mArticleListRecyclerViewAdapterListener
    }
}
