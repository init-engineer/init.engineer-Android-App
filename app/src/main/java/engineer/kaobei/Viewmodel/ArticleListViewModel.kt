package engineer.kaobei.Viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import engineer.kaobei.BASE_URL
import engineer.kaobei.KaobeiEngineerService
import engineer.kaobei.Model.Articles.Article
import engineer.kaobei.Model.Articles.KaobeiArticleList
import engineer.kaobei.Model.Comments.Comment
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Class ArticleListViewModel.
 *
 * A ViewModel used for the {@link ArticleListFragment}.
 */
class ArticleListViewModel : ListViewModel<Article>() {

    protected var mPage : MutableLiveData<Int> = MutableLiveData<Int>(1)
    protected var mInit : MutableLiveData<Int> = MutableLiveData<Int>(0)

    init {
        mList = arrayListOf(Article())
        mLiveData = MutableLiveData<ArrayList<Article>>(arrayListOf(Article())).also {

        }
    }

    fun isInit() : Boolean{
        val value:Int = mInit.value!!
        return value != 0
    }

    fun getPage(): LiveData<Int> {
        return mPage
    }

    fun addPage(){
        if(mPage.value!=null){
            var index :Int= mPage.value!!
            index++
            mPage.postValue(index)
            mPage.value = index
        }
    }

    fun initValue():LiveData<ArrayList<Article>>{
        val value:Int = mInit.value!!
        if(value==0){
            mInit.postValue(1)
            mPage.value?.let { loadMoreArticles(it) }
        }
        return mLiveData
    }

    fun loadMoreArticles(page: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(KaobeiEngineerService::class.java)
        service.articleList(page.toString())
            .enqueue(object : retrofit2.Callback<KaobeiArticleList> {
                override fun onFailure(call: retrofit2.Call<KaobeiArticleList>, t: Throwable) {
                    mOnReceiveDataListener?.onFailureToReceiveData()
                }

                override fun onResponse(
                    call: retrofit2.Call<KaobeiArticleList>,
                    response: retrofit2.Response<KaobeiArticleList>
                ) {
                    if (response.isSuccessful) {
                        mOnReceiveDataListener?.onReceiveData(response.body()?.data!!)
                        add(response.body()?.data!!)
                    } else {
                        mOnReceiveDataListener?.onFailureToReceiveData()
                    }
                }
            })
    }

}
