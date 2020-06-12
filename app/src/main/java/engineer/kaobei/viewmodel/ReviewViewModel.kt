package engineer.kaobei.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import engineer.kaobei.BASE_URL
import engineer.kaobei.KaobeiEngineerService
import engineer.kaobei.model.ReviewArticles.ReviewArticle
import engineer.kaobei.model.ReviewArticles.ReviewArticles
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ReviewViewModel : ListViewModel<ReviewArticle>() {
    protected var mPage : MutableLiveData<Int> = MutableLiveData<Int>(1)
    protected var mInit : MutableLiveData<Int> = MutableLiveData<Int>(0)

    init {
        mList = arrayListOf()
        mLiveData = MutableLiveData<ArrayList<ReviewArticle>>(arrayListOf()).also {

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

        service.reviewArticleList("Bearer $accessToken", page.toString())
            .enqueue(object : retrofit2.Callback<ReviewArticles> {
                override fun onFailure(call: retrofit2.Call<ReviewArticles>, t: Throwable) {
                    mReviewOnReceiveDataListener?.onFailureToReceiveData()
                }

                override fun onResponse(
                    call: retrofit2.Call<ReviewArticles>,
                    response: retrofit2.Response<ReviewArticles>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.meta?.pagination?.total?.let {
                            mReviewOnReceiveDataListener?.onReceiveData(response.body()?.data!!,
                                it
                            )
                        }
                        response.body()?.data
                        add(response.body()?.data!!)
                    } else {
                        mReviewOnReceiveDataListener?.onFailureToReceiveData()
                    }
                }
            })
    }
}
