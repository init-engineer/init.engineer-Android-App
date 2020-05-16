package engineer.kaobei.Viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import engineer.kaobei.Model.UserArticles.UserArticle
import engineer.kaobei.Model.UserArticles.UserArticles
import okhttp3.*
import java.io.IOException

class DashBoardViewModel : ViewModel() {

    private var init = false
    private lateinit var accessToken:String
    private lateinit var mOnReceiveDataListener: OnReceiveDataListener;
    private val mArticles = ArrayList<UserArticle>()
    private val mArticlesLiveData: MutableLiveData<ArrayList<UserArticle>> by lazy {
        MutableLiveData<ArrayList<UserArticle>>().also {
            loadArticles(accessToken,1)
        }
    }

    fun getArticles(accessToken: String): LiveData<ArrayList<UserArticle>> {
        this.accessToken = accessToken
        return mArticlesLiveData
    }

    fun loadArticles(accessToken:String,page: Int) {
        // Do an asynchronous operation to fetch articles.
        loadDashBoard(accessToken,page)
    }

    fun loadDashBoard(accessToken:String,page: Int) {
        // Do an asynchronous operation .
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://kaobei.engineer/api/frontend/social/cards/api/dashboard?page="+ page)
            .addHeader("Authorization","Bearer "+accessToken)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mOnReceiveDataListener.onFailure()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response?.body?.string()
                if(response?.code !=200){
                    return
                }
                val bean = Gson().fromJson(responseData, UserArticles::class.javaObjectType)
                mOnReceiveDataListener.onReceiveData(bean.data)
                addArticles(bean.data)
            }

        })
    }

    fun addArticles(articles: List<UserArticle>) {
        mArticles.addAll(articles)
        mArticlesLiveData.postValue(mArticles)
    }


    fun addArticle(article: UserArticle) {
        mArticles.add(article)
        mArticlesLiveData.postValue(mArticles)
    }

    fun addArticleAt(index: Int, article: UserArticle) {
        mArticles.add(index, article)
        mArticlesLiveData.postValue(mArticles)
    }

    fun removeAt(index: Int) {
        mArticles.removeAt(index)
        mArticlesLiveData.postValue(mArticles)
    }

    fun addOnReceiveDataListener(mOnReceiveDataListener: OnReceiveDataListener) {
        this.mOnReceiveDataListener = mOnReceiveDataListener
    }

    interface OnReceiveDataListener {
        fun onReceiveData(list: List<UserArticle>)
        fun onFailure()
    }

}
