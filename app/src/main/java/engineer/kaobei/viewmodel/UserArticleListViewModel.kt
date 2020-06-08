package engineer.kaobei.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import engineer.kaobei.BASE_URL
import engineer.kaobei.KaobeiEngineerService
import engineer.kaobei.model.articles.Article
import engineer.kaobei.model.userarticles.UserArticle
import engineer.kaobei.model.userarticles.UserArticles
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Class UserArticleListViewModel.
 */
class UserArticleListViewModel : ListViewModel<UserArticle>() {

    protected var mPage : MutableLiveData<Int> = MutableLiveData<Int>(1)
    protected var mInit : MutableLiveData<Int> = MutableLiveData<Int>(0)

    init {
        mList = arrayListOf()
        mLiveData = MutableLiveData<ArrayList<UserArticle>>(arrayListOf()).also {

        }
    }

    fun refreshData(){
        mInit.value = 0
        mPage.value = 1
        mList = arrayListOf()
        mLiveData.value = arrayListOf()
    }

    fun setInit(value : Boolean){
        if(value){
            mInit.value = 1
        }else{
            mInit.value = 0
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
            mPage.value = index
        }
    }

    fun minusPage(){
        if(mPage.value!=null){
            var index :Int= mPage.value!!
            index--
            mPage.value = index
        }
    }

    fun loadMoreArticles(accessToken: String, page: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(KaobeiEngineerService::class.java)

        service.userArticleList("Bearer $accessToken", page.toString())
            .enqueue(object : retrofit2.Callback<UserArticles> {
                override fun onFailure(call: retrofit2.Call<UserArticles>, t: Throwable) {
                    mOnReceiveDataListener?.onFailureToReceiveData()
                }

                override fun onResponse(
                    call: retrofit2.Call<UserArticles>,
                    response: retrofit2.Response<UserArticles>
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
