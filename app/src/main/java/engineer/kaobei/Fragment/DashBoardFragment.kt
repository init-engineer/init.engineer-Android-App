package engineer.kaobei.Fragment

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.transition.MaterialSharedAxis
import com.google.gson.Gson
import engineer.kaobei.Activity.ArticleActivity
import engineer.kaobei.Activity.LoginActivity
import engineer.kaobei.Activity.MainActivity
import engineer.kaobei.Activity.SettingsActivity
import engineer.kaobei.Database.AuthStateManager
import engineer.kaobei.Model.Articles.Article
import engineer.kaobei.Model.KaobelUser.BeanKaobeiUser
import engineer.kaobei.Model.UserArticles.UserArticle
import engineer.kaobei.Model.UserArticles.UserArticles
import engineer.kaobei.OnLoadMoreListener
import engineer.kaobei.R
import engineer.kaobei.RecyclerViewLoadMoreScroll
import engineer.kaobei.Util.SnackbarUtil
import engineer.kaobei.View.UserViewer
import engineer.kaobei.Viewmodel.DashBoardViewModel
import kotlinx.android.synthetic.main.fragment_dashboard.*
import net.openid.appauth.AuthState
import okhttp3.*
import java.io.IOException


class DashBoardFragment : Fragment() {

    companion object {
        const val recyclerviewDelayLoadingTime: Long = 500
        const val visibleThreshold = 10
        private lateinit var authStateManager : AuthStateManager
        private var isAuthorized = false
        fun newInstance() = DashBoardFragment()
    }

    var init = false
    var page: Int = 1

    private lateinit var mCoorView: CoordinatorLayout
    private lateinit var mViewModel: DashBoardViewModel
    private lateinit var adapter: HistoryLoadMoreRecyclerView
    private lateinit var mScrollListener: RecyclerViewLoadMoreScroll

    private lateinit var mShimmer1: ShimmerFrameLayout
    private lateinit var mShimmer2: ShimmerFrameLayout
    private lateinit var mShimmer3: ShimmerFrameLayout
    private lateinit var tv_no_post:TextView

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


        val view_dashboard:LinearLayout = view.findViewById(R.id.view_dashboard)
        val view_not_authorized:LinearLayout = view.findViewById(R.id.view_not_authorized)
        val login_button: Button = view.findViewById(R.id.login_button)

        if(authStateManager.getCurrent().isAuthorized){
            view_dashboard.visibility = View.VISIBLE
            view_not_authorized.visibility = View.GONE
        }else{
            view_dashboard.visibility = View.GONE
            view_not_authorized.visibility = View.VISIBLE
            login_button.setOnClickListener {
                login()
            }
        }

        if(authStateManager.getCurrent().isAuthorized){
            isAuthorized = true
            userviewer.initView(true)
            userviewer.setOnClickListener {
                val bt_sheet = BottomSheetDialog(view?.context)
                val mView = LayoutInflater.from(view?.context).inflate(R.layout.bottom_sheet_authorized, null)
                val cardview_logout : CardView = mView.findViewById(R.id.cardview_logout)
                cardview_logout.setOnClickListener{
                    logout()
                }
                val cardview_setting : CardView = mView.findViewById(R.id.cardview_setting)
                cardview_setting.setOnClickListener {
                    val intent = Intent(context, SettingsActivity::class.java)
                    activity?.startActivity(intent)
                }
                bt_sheet.setContentView(mView)
                bt_sheet.show()
            }
            authStateManager.getCurrent().accessToken?.let { loadProfile(it) }

            val rv_dashboard : RecyclerView = view.findViewById(R.id.rv_dashboard)
            val mLayoutManager = LinearLayoutManager(context)
            rv_dashboard.layoutManager = mLayoutManager
            mViewModel = ViewModelProviders.of(this).get(DashBoardViewModel::class.java)
            mScrollListener = RecyclerViewLoadMoreScroll(mLayoutManager, visibleThreshold)
            mScrollListener.setOnLoadMoreListener(object : OnLoadMoreListener {
                override fun onLoadMore() {
                    if(init){
                        adapter.addLoadingView()
                        Handler().postDelayed({
                            authStateManager.getCurrent().accessToken?.let {
                                mViewModel.loadArticles(
                                    it,++page)
                            }
                        }, recyclerviewDelayLoadingTime)
                    }
                }
            })
            mViewModel.addOnReceiveDataListener(object :
                DashBoardViewModel.OnReceiveDataListener {
                override fun onReceiveData(list: List<UserArticle>) {
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


            authStateManager.getCurrent().accessToken?.let {
                mViewModel.getArticles(it).observe(viewLifecycleOwner,
                    Observer<List<UserArticle>> { articles ->
                        if (!init) {
                            if(articles.size == 0){
                                tv_no_post.visibility = View.VISIBLE
                            }else{
                                tv_no_post.visibility = View.GONE
                            }
                            mShimmer1.visibility = View.GONE
                            mShimmer2.visibility = View.GONE
                            mShimmer3.visibility = View.GONE
                            rv_dashboard.visibility = View.VISIBLE
                            adapter = HistoryLoadMoreRecyclerView(view.context,articles,mViewModel)
                            rv_dashboard.adapter = adapter
                            init = true
                        }
                        adapter.notifyDataSetChanged()
                    })
            }


        }else{
            isAuthorized = false
            mShimmer1.visibility = View.GONE
            mShimmer2.visibility = View.GONE
            mShimmer3.visibility = View.GONE
            userviewer.initView(false)
            userviewer.setOnClickListener {
                val bt_sheet = BottomSheetDialog(view.context)
                val mView = LayoutInflater.from(view.context).inflate(R.layout.bottom_sheet_not_authorized, null)
                val cardview_login : CardView = mView.findViewById(R.id.cardview_login)
                cardview_login.setOnClickListener {
                    login()
                }
                bt_sheet.setContentView(mView)
                bt_sheet.show()
            }
        }

        return view
    }


    fun login(){
        val intent = Intent(context, LoginActivity::class.java)
        activity?.startActivityForResult(intent,LoginActivity.RC_AUTH)
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

    fun loadDashBoard(accessToken:String) {
        // Do an asynchronous operation .
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://kaobei.engineer/api/frontend/social/cards/api/dashboard")
            .addHeader("Authorization","Bearer "+accessToken)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response?.body?.string()
                if(response?.code !=200){
                    return
                }
                val bean = Gson().fromJson(responseData,UserArticles::class.javaObjectType)
            }

        })
    }

    fun loadProfile(accessToken:String) {
        // Do an asynchronous operation .
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://kaobei.engineer/api/frontend/user/profile")
            .addHeader("Authorization","Bearer "+accessToken)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response?.body?.string()
                if(response?.code !=200){
                    return
                }
                val bean = Gson().fromJson(responseData,BeanKaobeiUser::class.javaObjectType)
                activity?.runOnUiThread {
                    userviewer?.setProfile(bean.data)
                }
            }

        })
    }

}

class HistoryLoadMoreRecyclerView(
    private val context: Context,
    private val userArticles: List<UserArticle>,
    private val viewModel: DashBoardViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //for deleting loading view
    var loadingIndex = 0

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
            if(userArticle.isBanned==1){
                banned_layout.visibility = View.VISIBLE
                style2_banned_mark.text = userArticle.bannedRemarks
            }else{
                banned_layout.visibility = View.GONE
            }
            id?.text =
                "#" + context.resources.getString(R.string.app_name_ch) + userArticle.id.toString(36)
            date?.text = userArticle.createdDiff
            Glide
                .with(context)
                .load(userArticle.image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(thumbnail)
            itemView.setOnClickListener {
                val article = Article(userArticle.content,userArticle.createdAt,userArticle.createdDiff,userArticle.id,userArticle.image,userArticle.updatedAt,userArticle.updatedDiff)
                val intent = Intent(context, ArticleActivity::class.java)
                intent.putExtra(ArticleActivity.ARTICLE_KEY, article)
                context.startActivity(intent)
            }
        }
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ITEM) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.cardview_style2, parent, false)
            return ItemViewHolder(view)}
        else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.progress_loading, parent, false)
            return LoadingViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return this.userArticles.count()
    }

    override fun getItemViewType(position: Int): Int {
        return  if (userArticles[position].id == 0) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.bind(userArticles[position])
        }
    }

    fun addLoadingView() {
        //Add loading item
        viewModel.addArticle(UserArticle())
        loadingIndex = userArticles.size - 1
    }

    fun removeLoadingView() {
        //Remove loading item
        if (userArticles.isNotEmpty()) {
            if (loadingIndex >= 0) {
                viewModel.removeAt(loadingIndex)
                loadingIndex = 0
            }
        }
    }


}
