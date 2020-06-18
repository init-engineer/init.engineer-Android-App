package engineer.kaobei.fragment

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.transition.MaterialSharedAxis
import engineer.kaobei.*
import engineer.kaobei.activity.LoginActivity
import engineer.kaobei.database.AuthStateManager
import engineer.kaobei.model.ReviewArticle.SingleReviewArticle
import engineer.kaobei.model.ReviewArticles.ReviewArticle
import engineer.kaobei.util.SnackbarUtil
import engineer.kaobei.util.ext.viewLoadingWithTransition
import engineer.kaobei.viewmodel.ListViewModel
import engineer.kaobei.viewmodel.ReviewViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ReviewFragment : Fragment() {

    companion object {
        const val recyclerviewDelayLoadingTime: Long = 300
        const val visibleThreshold = 10
        private lateinit var authStateManager: AuthStateManager
        fun newInstance() = ReviewFragment()
    }

    private lateinit var viewModel: ReviewViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(
            R.layout.fragment_review, container,
            false
        )
        val viewPager2 : ViewPager2 = view.findViewById(R.id.vp2_review)
        val tv_page: TextView = view.findViewById(R.id.tv_page)
        val tv_tothefirst: TextView = view.findViewById(R.id.tv_tothefirst)
        val tv_tothelast: TextView = view.findViewById(R.id.tv_tothelast)
        val view_not_authorized: LinearLayout = view.findViewById(R.id.view_not_authorized)
        val login_button: Button = view.findViewById(R.id.login_button)
        val view_review: NestedScrollView = view.findViewById(R.id.view_review)

        authStateManager = AuthStateManager.getInstance(view.context)
        if (authStateManager.getCurrent().isAuthorized) {
            view_not_authorized.visibility = View.GONE
            view_review.visibility = View.VISIBLE
            val accessToken: String? = authStateManager.getCurrent().accessToken
            if (accessToken != null) {
                val adapter =
                    ReviewAdapter(
                        view.context,
                        accessToken
                    )
                viewPager2.setPageTransformer(DepthPageTransformer())
                viewPager2.adapter = adapter
                val callback = object : ViewPager2.OnPageChangeCallback(){
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        val total = adapter.getTotalPage()
                        val page = viewPager2.currentItem + 1
                        tv_page.setText("$page/$total ")
                        if(position==adapter.itemCount-1){
                            adapter.loadMoreArticle()
                        }
                    }
                }
                viewPager2.registerOnPageChangeCallback(callback)
                tv_tothefirst.setOnClickListener {
                    if(viewPager2.currentItem%10==0){
                        viewPager2.setCurrentItem(viewPager2.currentItem-10,true)
                    }else{
                        viewPager2.setCurrentItem(viewPager2.currentItem-(viewPager2.currentItem%10),true)
                    }
                }
                tv_tothelast.setOnClickListener {
                    viewPager2.setCurrentItem(viewPager2.currentItem+(10-(viewPager2.currentItem%10)),true)
                }
            }else{

            }
        }else{
            view_not_authorized.visibility = View.VISIBLE
            view_review.visibility = View.GONE
            login_button.setOnClickListener {
                val intent = Intent(context, LoginActivity::class.java)
                activity?.startActivityForResult(intent, LoginActivity.RC_AUTH)
            }
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val forward: MaterialSharedAxis = MaterialSharedAxis.create(MaterialSharedAxis.X, true)
        val backward: MaterialSharedAxis = MaterialSharedAxis.create(MaterialSharedAxis.X, false)
        enterTransition = forward
        exitTransition = backward
        viewModel = ViewModelProvider(this).get(ReviewViewModel::class.java)
        // TODO: Use the ViewModel
    }

}

class ReviewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //for deleting loading view
    private var mListener: RecyclerViewAdapterListener<ReviewArticle>? = null
    private lateinit var mContext: FragmentActivity
    private lateinit var mViewModel: ReviewViewModel

    var articleList: List<ReviewArticle> = listOf()
    private lateinit var accessToken: String
    private var totalPage = 0
    private lateinit var retrofit:Retrofit
    private lateinit var service:KaobeiEngineerService
    private lateinit var mCoorView:CoordinatorLayout

    constructor(context: Context, accessToken: String) : this() {
        this.mContext = context as FragmentActivity
        this.articleList = articleList
        this.accessToken = accessToken
        mCoorView = mContext.findViewById(R.id.main_coordinator)!!
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(KaobeiEngineerService::class.java)

        mViewModel = ViewModelProvider(context).get(ReviewViewModel::class.java)
        mViewModel.addOnReviewReceiveDataListener(object :
            ListViewModel.OnReviewOnReceiveDataListener<ReviewArticle> {
            override fun onReceiveData(list: List<ReviewArticle>, page:Int) {
                totalPage = page
                if (!isInit()) {
                    mViewModel.setInit(true)
                    mListener?.onTheFirstInit(list)
                }
                mListener?.onReceiveData()
            }

            override fun onFailureToReceiveData() {
                mListener?.onFailedToReceiveData()
            }

            override fun onNoMoreData() {
                mListener?.onNoMoreData()
            }
        })
        mViewModel.getLiveData().observe(mContext, Observer<List<ReviewArticle>> { articles ->
            this.articleList = articles
            notifyDataSetChanged()
        })
        refresh()
    }


    companion object {
        const val VIEW_TYPE_LOADING = 1
        const val VIEW_TYPE_ITEM = 2
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val id = itemView.findViewById<TextView>(R.id.style1_id)
        private val date = itemView.findViewById<TextView>(R.id.style1_date)
        private val tv_text = itemView.findViewById<TextView>(R.id.tv_text)
        private var thumbnail = itemView.findViewById<ImageView>(R.id.style1_thumbnail)
        private var tv_approved_total = itemView.findViewById<TextView>(R.id.tv_approved_total)
        private var tv_deny_total = itemView.findViewById<TextView>(R.id.tv_deny_total)
        private var card = itemView.findViewById<CardView>(R.id.card)
        private var btn_approve = itemView.findViewById<Button>(R.id.btn_approve)
        private var btn_deny = itemView.findViewById<Button>(R.id.btn_deny)
        private var rippleBackground = itemView.findViewById<LinearLayout>(R.id.rippleBackground)
        @SuppressLint("SetTextI18n")
        fun bind(reviewArticle: ReviewArticle,position:Int) {
            id?.text =
                "#" + mContext.resources.getString(R.string.app_name_ch) + reviewArticle.id.toString(
                    36
                )
            date?.text = reviewArticle.createdDiff
            thumbnail.viewLoadingWithTransition(reviewArticle.image)
            tv_text?.text = reviewArticle.content
            tv_approved_total?.text = reviewArticle.succeeded.toString()
            tv_deny_total?.text = reviewArticle.failed.toString()
            if(reviewArticle.review>0){
                card.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.RVY))
                btn_approve.setBackgroundColor(ContextCompat.getColor(mContext, R.color.FxBlackT))
                btn_deny.setBackgroundColor(ContextCompat.getColor(mContext, R.color.FxBlackT))
                btn_approve.isClickable = false
                btn_deny.isClickable = false
            }else if(reviewArticle.review==0){
                card.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.FxBlackT))
                btn_approve.setBackgroundColor(ContextCompat.getColor(mContext, R.color.RVY))
                btn_deny.setBackgroundColor(ContextCompat.getColor(mContext, R.color.RVN))
                btn_approve.setOnClickListener {
                    service.approveArticle("Bearer $accessToken",reviewArticle.id.toString())
                        .enqueue(object : retrofit2.Callback<SingleReviewArticle> {
                            override fun onFailure(call: retrofit2.Call<SingleReviewArticle>, t: Throwable) {
                                SnackbarUtil.makeAnchorSnackbar(mCoorView, "失敗，請稍後再試", R.id.gap)
                            }
                            override fun onResponse(
                                call: retrofit2.Call<SingleReviewArticle>,
                                response: retrofit2.Response<SingleReviewArticle>
                            ) {
                                if (response.isSuccessful) {
                                    card.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.RVY))
                                    SnackbarUtil.makeAnchorSnackbar(mCoorView, "成功投票！", R.id.gap)
                                    btn_approve.isClickable = false
                                    btn_deny.isClickable = false
                                    btn_approve.setBackgroundColor(ContextCompat.getColor(mContext, R.color.FxBlackT))
                                    btn_deny.setBackgroundColor(ContextCompat.getColor(mContext, R.color.FxBlackT))
                                    val data = response.body()?.data
                                    if(data!=null){
                                        mViewModel.change(position, ReviewArticle(
                                            content = data.content,
                                            createdAt = data.createdAt,
                                            createdDiff = data.createdDiff,
                                            failed = data.failed,
                                            id = data.id,
                                            image = data.image,
                                            review = 1,
                                            succeeded = data.succeeded,
                                            updatedAt = data.updatedAt,
                                            updatedDiff = data.updatedDiff
                                        ))
                                    }
                                } else {
                                    SnackbarUtil.makeAnchorSnackbar(mCoorView, "失敗，請稍後再試", R.id.gap)
                                }
                            }
                        })
                }
                btn_deny.setOnClickListener {
                    service.denyArticle("Bearer $accessToken",reviewArticle.id.toString())
                        .enqueue(object : retrofit2.Callback<SingleReviewArticle> {
                            override fun onFailure(call: retrofit2.Call<SingleReviewArticle>, t: Throwable) {
                                SnackbarUtil.makeAnchorSnackbar(mCoorView, "失敗，請稍後再試", R.id.gap)
                            }
                            override fun onResponse(
                                call: retrofit2.Call<SingleReviewArticle>,
                                response: retrofit2.Response<SingleReviewArticle>
                            ) {
                                if (response.isSuccessful) {
                                    card.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.RVN))
                                    SnackbarUtil.makeAnchorSnackbar(mCoorView, "成功投票！", R.id.gap)
                                    btn_approve.isClickable = false
                                    btn_deny.isClickable = false
                                    btn_approve.setBackgroundColor(ContextCompat.getColor(mContext, R.color.FxBlackT))
                                    btn_deny.setBackgroundColor(ContextCompat.getColor(mContext, R.color.FxBlackT))
                                    val data = response.body()?.data
                                    if(data!=null){
                                        mViewModel.change(position, ReviewArticle(
                                            content = data.content,
                                            createdAt = data.createdAt,
                                            createdDiff = data.createdDiff,
                                            failed = data.failed,
                                            id = data.id,
                                            image = data.image,
                                            review = -1,
                                            succeeded = data.succeeded,
                                            updatedAt = data.updatedAt,
                                            updatedDiff = data.updatedDiff
                                        ))
                                    }
                                } else {
                                    SnackbarUtil.makeAnchorSnackbar(mCoorView, "失敗，請稍後再試", R.id.gap)
                                }
                            }
                        })
                }
            }else if(reviewArticle.review<0){
                card.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.RVN))
                btn_approve.setBackgroundColor(ContextCompat.getColor(mContext, R.color.FxBlackT))
                btn_deny.setBackgroundColor(ContextCompat.getColor(mContext, R.color.FxBlackT))
                btn_approve.isClickable = false
                btn_deny.isClickable = false
            }
            itemView.setOnClickListener {

            }
        }
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_style3, parent, false)
            val lp = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            view.setPadding(60,0,60,0)
            view.layoutParams = lp
            return ItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.progress_loading, parent, false)
            val lp = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            view.layoutParams = lp
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
            holder.bind(articleList[position],position)
        }
    }

    fun loadMoreArticle() {
        mViewModel.addPage()
        mViewModel.getPage().value?.let { mViewModel.loadMoreArticles(accessToken, it) }
    }

    fun refresh(){
        mViewModel.refreshData()
        this.articleList = mViewModel.getLiveData().value!!
        mViewModel.getPage().value?.let { mViewModel.loadMoreArticles(accessToken,it) }
    }

    fun isInit(): Boolean {
        return mViewModel.isInit()
    }

    fun getTotalPage() : Int{
        return totalPage
    }


    fun setListener(mArticleListRecyclerViewAdapterListener: RecyclerViewAdapterListener<ReviewArticle>) {
        this.mListener = mArticleListRecyclerViewAdapterListener
    }

}