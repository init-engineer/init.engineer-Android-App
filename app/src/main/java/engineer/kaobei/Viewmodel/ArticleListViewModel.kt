package engineer.kaobei.Viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import engineer.kaobei.Model.Articles.Article
import engineer.kaobei.Model.Articles.KaobeiArticleList
import okhttp3.*
import java.io.IOException

/**
 * A ViewModel used for the {@link ArticleListFragment}.
 */
class ArticleListViewModel : ViewModel() {

    private var init = false
    private lateinit var mOnReceiveDataListener: OnReceiveDataListener;
    private val mArticles = ArrayList<Article>()
    private val mArticlesLiveData: MutableLiveData<ArrayList<Article>> by lazy {
        MutableLiveData<ArrayList<Article>>().also {
            loadArticles(1)
        }
    }

    fun getArticles(): LiveData<ArrayList<Article>> {
        return mArticlesLiveData
    }

    fun loadArticles(page: Int) {
        // Do an asynchronous operation to fetch articles.
        loadMoreArticles(page)
    }

    private fun loadMoreArticles(page: Int) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://kaobei.engineer/api/frontend/social/cards?page=" + page)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mOnReceiveDataListener.onFailure()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                if (response.code != 200) {
                    mOnReceiveDataListener.onFailure()
                    return
                }
                val bean = Gson().fromJson(responseData, KaobeiArticleList::class.javaObjectType)
                mOnReceiveDataListener.onReceiveData(bean.data)
                addArticles(bean.data)
            }
        })
    }

    fun addArticles(articles: List<Article>) {
        mArticles.addAll(articles)
        if (!init) {
            mArticles.add(0, Article())
            init = true
        }
        mArticlesLiveData.postValue(mArticles)
    }

    fun addArticle(article: Article) {
        mArticles.add(article)
        mArticlesLiveData.postValue(mArticles)
    }

    fun addArticleAt(index: Int, article: Article) {
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
        fun onReceiveData(list: List<Article>)
        fun onFailure()
    }


}
