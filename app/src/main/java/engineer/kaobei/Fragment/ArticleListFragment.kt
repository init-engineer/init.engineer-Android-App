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
import engineer.kaobei.RecyclerViewLoadMoreScroll
import engineer.kaobei.Util.SnackbarUtil
import engineer.kaobei.Viewmodel.ArticleListViewModel

class ArticleListFragment : Fragment() {

    companion object {
        const val visibleThreshold = 10
        const val recyclerviewDelayLoadingTime: Long = 500    //limited recyclerview loading time
        var init = false     //The First loading of RecyclerView
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
    private lateinit var adapter: LoadMoreRecyclerView

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
        init = false
        mCoorView = activity?.findViewById(R.id.main_coordinator)!!
        mHeaderView = view.findViewById(R.id.header_view)
        mShimmer1 = view.findViewById(R.id.shimmer_view_container1)
        mShimmer2 = view.findViewById(R.id.shimmer_view_container2)
        mShimmer3 = view.findViewById(R.id.shimmer_view_container3)
        mRecyclerView = view.findViewById(R.id.articleList_recyclerView)
        initRecyclerview()
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val forward: MaterialSharedAxis = MaterialSharedAxis.create(MaterialSharedAxis.X, true)
        val backward: MaterialSharedAxis = MaterialSharedAxis.create(MaterialSharedAxis.X, false)
        enterTransition = forward
        exitTransition = backward
        //Article ViewModel
        mViewModel = ViewModelProviders.of(this).get(ArticleListViewModel::class.java)
        mViewModel.addOnReceiveDataListener(object :
            ArticleListViewModel.OnReceiveDataListener {
            override fun onReceiveData(list: List<Article>) {
                //remove the loading view
                if (init) {
                    adapter.removeLoadingView()
                    mScrollListener.setLoaded()
                }
            }

            override fun onFailure() {
                //remove the loading view and show status
                if (init) {
                    page--
                    adapter.removeLoadingView()
                    mScrollListener.setLoaded()
                }
                SnackbarUtil.makeAnchorSnackbar(mCoorView, "讀取資料失敗，請稍後再試", R.id.gap)
            }
        })
        mViewModel.getArticles().observe(viewLifecycleOwner, Observer<List<Article>> { articles ->
            if (!init) {
                mHeaderView.visibility = View.GONE
                mShimmer1.visibility = View.GONE
                mShimmer2.visibility = View.GONE
                mShimmer3.visibility = View.GONE
                mRecyclerView.visibility = View.VISIBLE
                adapter = context?.let {
                    LoadMoreRecyclerView(
                        it,
                        articles,
                        mViewModel
                    )
                }!!
                mRecyclerView.adapter = adapter
                init = true
            }
            adapter.notifyDataSetChanged()
        })
    }

    private fun initRecyclerview() {
        mRecyclerView.visibility = View.GONE
        mRecyclerView.isNestedScrollingEnabled = false
        val mLayoutManager = LinearLayoutManager(context)
        mScrollListener = RecyclerViewLoadMoreScroll(mLayoutManager, visibleThreshold)
        mScrollListener.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                if(init){
                    adapter.addLoadingView()
                    Handler().postDelayed({
                        mViewModel.loadArticles(++page)
                    }, recyclerviewDelayLoadingTime)
                }
            }
        })
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.addOnScrollListener(mScrollListener)
    }
}

class LoadMoreRecyclerView(
    private val context: Context,
    private val articles: List<Article>,
    private val viewModel: ArticleListViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //for deleting loading view
    var loadingIndex = 0

    companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_LOADING = 1
        const val VIEW_TYPE_ITEM = 2
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val id = itemView.findViewById<TextView>(R.id.style1_id)
        private val date = itemView.findViewById<TextView>(R.id.style1_date)
        private var thumbnail = itemView.findViewById<ImageView>(R.id.style1_thumbnail)

        @SuppressLint("SetTextI18n")
        fun bind(article: Article) {
            id?.text =
                "#" + context.resources.getString(R.string.app_name_ch) + article.id.toString(36)
            date?.text = article.createdDiff
            Glide
                .with(context)
                .load(article.image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(thumbnail)
            itemView.setOnClickListener {
                val intent = Intent(context, ArticleActivity::class.java)
                intent.putExtra(ArticleActivity.ARTICLE_KEY, article)
                context.startActivity(intent)
            }
        }
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ITEM) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.cardview_style1, parent, false)
            return ItemViewHolder(view)
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
        return this.articles.count()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIEW_TYPE_HEADER
        } else if (articles[position].id == 0) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.bind(articles[position])
        }
    }

    fun addLoadingView() {
        //Add loading item
        viewModel.addArticle(Article())
        loadingIndex = articles.size - 1
    }

    fun removeLoadingView() {
        //Remove loading item
        if (articles.isNotEmpty()) {
            if (loadingIndex >= 0) {
                viewModel.removeAt(loadingIndex)
                loadingIndex = 0
            }
        }
    }

}
