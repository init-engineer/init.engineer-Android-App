package engineer.kaobei.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.facebook.shimmer.ShimmerFrameLayout
import engineer.kaobei.Activity.ui.ArticleActivity
import engineer.kaobei.Viewmodel.ArticleListViewModel
import engineer.kaobei.Model.Article.Article
import engineer.kaobei.OnLoadMoreListener
import engineer.kaobei.R
import engineer.kaobei.RecyclerViewLoadMoreScroll

class ArticleListFragment : Fragment() {

    lateinit var headerView: TextView
    lateinit var recyclerView: RecyclerView
    lateinit var shimmer1: ShimmerFrameLayout
    lateinit var shimmer2: ShimmerFrameLayout
    lateinit var shimmer3: ShimmerFrameLayout
    lateinit var scrollListener: RecyclerViewLoadMoreScroll

    companion object {
        //The First loading of RecyclerView
        private lateinit var adapter: LoadMoreRecyclerView
        fun newInstance() = ArticleListFragment()
    }

    private lateinit var viewModel: ArticleListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(
            R.layout.fragment_article_list, container,
            false
        )
        init = false
        headerView = view.findViewById(R.id.header_view)
        shimmer1 = view.findViewById(R.id.shimmer_view_container1)
        shimmer2 = view.findViewById(R.id.shimmer_view_container2)
        shimmer3 = view.findViewById(R.id.shimmer_view_container3)
        initRecyclerview(view)
        return view
    }

    var init = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ArticleListViewModel::class.java)
        viewModel.addOnReceiveDataListener(object :
            ArticleListViewModel.OnReceiveDataListener {
            override fun onReceiveData(list: List<Article>) {
                if (init) {
                    adapter.removeLoadingView()
                    scrollListener.setLoaded()
                }
            }

        })

        viewModel.getArticles().observe(this, Observer<List<Article>> { articles ->
            if (!init) {
                headerView.visibility = View.GONE
                shimmer1.visibility = View.GONE
                shimmer2.visibility = View.GONE
                shimmer3.visibility = View.GONE
                recyclerView.alpha = 0f
                recyclerView.visibility = View.VISIBLE
                recyclerView.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .setListener(null)
                adapter = context?.let {
                    LoadMoreRecyclerView(
                        it,
                        articles,
                        viewModel
                    )
                }!!
                recyclerView.adapter =
                    adapter
                init = true
            }
            adapter.notifyDataSetChanged()
        })
        // TODO: Use the ViewModel
    }

    fun initRecyclerview(view: View) {
        recyclerView = view.findViewById(R.id.articleList_recyclerView)
        recyclerView.visibility = View.GONE
        recyclerView.isNestedScrollingEnabled = false
        val mLayoutManager = LinearLayoutManager(context)
        scrollListener = RecyclerViewLoadMoreScroll(mLayoutManager,10)
        scrollListener.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                adapter.addLoadingView()
                Handler().postDelayed({
                    viewModel.loadArticles()
                },1000)
            }
        })
        recyclerView.layoutManager = mLayoutManager
        recyclerView.addOnScrollListener(scrollListener)
    }
}

class LoadMoreRecyclerView(
    private val context: Context,
    private val articles: List<Article>,
    private val viewModel: ArticleListViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_HEADER = 0
    private val VIEW_TYPE_LOADING = 1
    private val VIEW_TYPE_ITEM = 2

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val id = itemView.findViewById<TextView>(R.id.style1_id)
        private val date = itemView.findViewById<TextView>(R.id.style1_date)
        private var thumbnail = itemView.findViewById<ImageView>(R.id.style1_thumbnail)

        fun bind(article: Article) {
            id?.text = "#純靠北工程師" + article.id.toString(36)
            date?.text = article.createdDiff
            Glide
                .with(context)
                .load(article.image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(thumbnail)
            itemView.setOnClickListener {
                var intent = Intent(context, ArticleActivity::class.java)
                intent.putExtra("ARTICLE_KEY", article)
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
        return if(loadingIndex==0){
            this.articles.count()
        }else{
            this.articles.count()+1
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIEW_TYPE_HEADER
        } else if (articles[position-1].id == 0) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.bind(articles[position-1])
        }
    }

    var loadingIndex = 0
    fun addLoadingView() {
        //Add loading item
        viewModel.addArticle(Article())
        loadingIndex = articles.size - 1
    }

    fun removeLoadingView() {
        //Remove loading item
        if (articles.isNotEmpty()) {
            viewModel.removeAt(loadingIndex)
            loadingIndex=0
        }
    }

}
