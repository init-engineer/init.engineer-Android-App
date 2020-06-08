package engineer.kaobei.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.transition.MaterialSharedAxis
import engineer.kaobei.activity.ArticleActivity
import engineer.kaobei.activity.LoginActivity
import engineer.kaobei.activity.MainActivity
import engineer.kaobei.activity.SettingsActivity
import engineer.kaobei.manager.AuthStateManager
import engineer.kaobei.model.articles.Article
import engineer.kaobei.model.kaobeluser.KaobeiUser
import engineer.kaobei.model.userarticles.UserArticle
import engineer.kaobei.OnLoadMoreListener
import engineer.kaobei.R
import engineer.kaobei.RecyclerViewAdapterListener
import engineer.kaobei.RecyclerViewLoadMoreScroll
import engineer.kaobei.util.ext.viewLoadingWithTransition
import engineer.kaobei.util.SnackbarUtil
import engineer.kaobei.view.UserViewer
import engineer.kaobei.viewmodel.ListViewModel
import engineer.kaobei.viewmodel.ProfileViewModel
import engineer.kaobei.viewmodel.UserArticleListViewModel
import net.openid.appauth.AuthState


class DashBoardFragment : Fragment() {

    companion object {
        const val recyclerviewDelayLoadingTime: Long = 300
        const val visibleThreshold = 10
        private lateinit var authStateManager: AuthStateManager
        private var isAuthorized = false
        fun newInstance() = DashBoardFragment()
    }

    var page: Int = 1

    private lateinit var mCoorView: CoordinatorLayout
    private lateinit var adapter: HistoryLoadMoreRecyclerView
    private lateinit var mScrollListener: RecyclerViewLoadMoreScroll

    private lateinit var mShimmer1: ShimmerFrameLayout
    private lateinit var mShimmer2: ShimmerFrameLayout
    private lateinit var mShimmer3: ShimmerFrameLayout
    private lateinit var tv_no_post: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(
            R.layout.fragment_dashboard, container,
            false
        )

        val userviewer = view.findViewById<UserViewer>(R.id.userviewer)
        mCoorView = activity?.findViewById(R.id.main_coordinator)!!
        mShimmer1 = view.findViewById(R.id.shimmer_view_container1)
        mShimmer2 = view.findViewById(R.id.shimmer_view_container2)
        mShimmer3 = view.findViewById(R.id.shimmer_view_container3)
        tv_no_post = view.findViewById(R.id.tv_no_post)

        authStateManager = AuthStateManager.getInstance(view.context)


        val view_dashboard: LinearLayout = view.findViewById(R.id.view_dashboard)
        val view_not_authorized: LinearLayout = view.findViewById(R.id.view_not_authorized)
        val login_button: Button = view.findViewById(R.id.login_button)

        if (authStateManager.getCurrent().isAuthorized) {
            view_dashboard.visibility = View.VISIBLE
            view_not_authorized.visibility = View.GONE
        } else {
            view_dashboard.visibility = View.GONE
            view_not_authorized.visibility = View.VISIBLE
            login_button.setOnClickListener {
                login()
            }
        }

        if (authStateManager.getCurrent().isAuthorized) {
            val accessToken: String? = authStateManager.getCurrent().accessToken
            if (accessToken != null) {
                isAuthorized = true
                userviewer.initView(true)
                userviewer.setOnClickListener {
                    val bt_sheet = BottomSheetDialog(view?.context)
                    val mView = LayoutInflater.from(view?.context)
                        .inflate(R.layout.bottom_sheet_authorized, null)
                    val cardview_logout: CardView = mView.findViewById(R.id.cardview_logout)
                    cardview_logout.setOnClickListener {
                        logout()
                    }
                    val cardview_setting: CardView = mView.findViewById(R.id.cardview_setting)
                    cardview_setting.setOnClickListener {
                        val intent = Intent(context, SettingsActivity::class.java)
                        activity?.startActivity(intent)
                    }
                    bt_sheet.setContentView(mView)
                    bt_sheet.show()
                }
                val rv_dashboard: RecyclerView = view.findViewById(R.id.rv_dashboard)
                rv_dashboard.visibility = View.GONE
                val mLayoutManager = LinearLayoutManager(context)
                rv_dashboard.layoutManager = mLayoutManager

                adapter =
                    HistoryLoadMoreRecyclerView(
                        view.context,
                        accessToken,
                        listOf()
                    )
                adapter.setListener(object : RecyclerViewAdapterListener<UserArticle> {
                    override fun onTheFirstInit(list: List<UserArticle>) {
                        activity?.runOnUiThread {
                            if (list.isEmpty()) {
                                tv_no_post.visibility = View.VISIBLE
                            } else {
                                tv_no_post.visibility = View.GONE
                            }
                            mShimmer1.visibility = View.GONE
                            mShimmer2.visibility = View.GONE
                            mShimmer3.visibility = View.GONE
                            rv_dashboard.visibility = View.VISIBLE
                        }
                    }

                    override fun onReceiveData() {
                        mScrollListener.setLoaded()
                    }

                    override fun onFailedToReceiveData() {
                        page--
                        mScrollListener.setLoaded()
                        SnackbarUtil.makeAnchorSnackbar(mCoorView, "讀取資料失敗，請稍後再試", R.id.gap)
                    }

                    override fun onNoMoreData() {
                        TODO("Not yet implemented")
                    }

                })
                rv_dashboard.adapter = adapter

                mScrollListener = RecyclerViewLoadMoreScroll(mLayoutManager,
                    visibleThreshold
                )
                mScrollListener.setOnLoadMoreListener(object : OnLoadMoreListener {
                    override fun onLoadMore() {
                        if (adapter.isInit()) {
                            Handler().postDelayed({
                                adapter.loadMoreArticle(++page)
                            },
                                recyclerviewDelayLoadingTime
                            )
                        }
                    }
                })

                val profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
                profileViewModel.getLiveData()
                    .observe(viewLifecycleOwner, Observer<KaobeiUser> { user ->
                        userviewer.setProfile(user)
                    })
                profileViewModel.loadProfile(accessToken)
            }
        } else {
            isAuthorized = false
            mShimmer1.visibility = View.GONE
            mShimmer2.visibility = View.GONE
            mShimmer3.visibility = View.GONE
            userviewer.initView(false)
            userviewer.setOnClickListener {
                val bt_sheet = BottomSheetDialog(view.context)
                val mView = LayoutInflater.from(view.context)
                    .inflate(R.layout.bottom_sheet_not_authorized, null)
                val cardview_login: CardView = mView.findViewById(R.id.cardview_login)
                cardview_login.setOnClickListener {
                    login()
                }
                bt_sheet.setContentView(mView)
                bt_sheet.show()
            }
        }

        return view
    }


    fun login() {
        val intent = Intent(context, LoginActivity::class.java)
        activity?.startActivityForResult(intent, LoginActivity.RC_AUTH)
    }

    fun logout() {
        val authState = AuthState()
        authStateManager.replace(authState)
        val intent = Intent(context, MainActivity::class.java)
        activity?.finish()
        startActivity(intent);
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val forward: MaterialSharedAxis = MaterialSharedAxis.create(MaterialSharedAxis.X, true)
        val backward: MaterialSharedAxis = MaterialSharedAxis.create(MaterialSharedAxis.X, false)
        enterTransition = forward
        exitTransition = backward
        // TODO: Use the ViewModel
    }


}

class HistoryLoadMoreRecyclerView() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //for deleting loading view
    private var mListener: RecyclerViewAdapterListener<UserArticle>? = null
    private lateinit var mContext: FragmentActivity
    private lateinit var mViewModel: UserArticleListViewModel

    var articleList: List<UserArticle> = listOf()
    private lateinit var accessToken: String
    private var loadingIndex = 0
    private var init = false

    constructor(context: Context, accessToken: String, articleList: List<UserArticle>) : this() {
        this.mContext = context as FragmentActivity
        this.articleList = articleList
        this.accessToken = accessToken
        mViewModel = ViewModelProvider(context).get(UserArticleListViewModel::class.java)
        mViewModel.addOnReceiveDataListener(object :
            ListViewModel.OnReceiveDataListener<UserArticle> {
            override fun onReceiveData(list: List<UserArticle>) {
                removeLoadingView()
                if (!init) {
                    init = true
                    mListener?.onTheFirstInit(list)
                }
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
        mViewModel.getLiveData().observe(mContext, Observer<List<UserArticle>> { articles ->
            this.articleList = articles
            notifyDataSetChanged()
        })
        mViewModel.loadArticles(accessToken, 1)
    }


    companion object {
        const val VIEW_TYPE_LOADING = 1
        const val VIEW_TYPE_ITEM = 2
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val id = itemView.findViewById<TextView>(R.id.style1_id)
        private val date = itemView.findViewById<TextView>(R.id.style1_date)
        private var thumbnail = itemView.findViewById<ImageView>(R.id.style1_thumbnail)
        private var banned_layout = itemView.findViewById<LinearLayout>(R.id.banned_layout)
        private var style2_banned_mark = itemView.findViewById<TextView>(R.id.style2_banned_mark)

        @SuppressLint("SetTextI18n")
        fun bind(userArticle: UserArticle) {
            if (userArticle.isBanned == 1) {
                banned_layout.visibility = View.VISIBLE
                style2_banned_mark.text = userArticle.bannedRemarks
            } else {
                banned_layout.visibility = View.GONE
            }
            id?.text =
                "#" + mContext.resources.getString(R.string.app_name_ch) + userArticle.id.toString(
                    36
                )
            date?.text = userArticle.createdDiff
            thumbnail.viewLoadingWithTransition(userArticle.image)

            itemView.setOnClickListener {
                val article = Article(
                    userArticle.content,
                    userArticle.createdAt,
                    userArticle.createdDiff,
                    userArticle.id,
                    userArticle.image,
                    userArticle.updatedAt,
                    userArticle.updatedDiff
                )
                val intent = Intent(mContext, ArticleActivity::class.java)
                intent.putExtra(ArticleActivity.ARTICLE_KEY, article)
                mContext.startActivity(intent)
            }
        }
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ITEM) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.cardview_style2, parent, false)
            return ItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.progress_loading, parent, false)
            return LoadingViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return this.articleList.count()
    }

    override fun getItemViewType(position: Int): Int {
        return if (articleList[position].id == 0) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.bind(articleList[position])
        }
    }

    fun addLoadingView() {
        //Add loading item
        mViewModel.add(UserArticle())
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

    fun loadMoreArticle(page: Int) {
        addLoadingView()
        mViewModel.loadArticles(accessToken, page)
    }


    fun isInit(): Boolean {
        return init
    }


    fun setListener(mArticleListRecyclerViewAdapterListener: RecyclerViewAdapterListener<UserArticle>) {
        this.mListener = mArticleListRecyclerViewAdapterListener
    }

}
