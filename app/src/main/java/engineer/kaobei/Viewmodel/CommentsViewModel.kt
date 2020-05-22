package engineer.kaobei.Viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import engineer.kaobei.BASE_URL
import engineer.kaobei.KaobeiEngineerService
import engineer.kaobei.Model.Comments.Comment
import engineer.kaobei.Model.Comments.KaobeiComments
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Class CommentsViewModel.
 *
 * A ViewModel used for the {@link ArticleListFragment}.
 */
class CommentsViewModel : ListViewModel<Comment>() {

    protected var mPage : MutableLiveData<Int> = MutableLiveData<Int>(1)
    protected var id :Int = 0
    protected var mInit : MutableLiveData<Int> = MutableLiveData<Int>(0)

    init {
        mList = arrayListOf(Comment(),Comment())
        mLiveData = MutableLiveData<ArrayList<Comment>>(arrayListOf(Comment(),Comment())).also {
        }
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

    fun changePage(page:Int){
        mPage.postValue(page)
    }

    fun getLivaDataValue(id:Int):LiveData<ArrayList<Comment>>{
        this.id = id
        val value:Int = mInit.value!!
        if(value==0){
            mInit.postValue(1)
            mPage.value?.let { loadComments(id, it) }
        }
        return mLiveData
    }

    fun loadComments(id: Int, page: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(KaobeiEngineerService::class.java)
        service.comments(id.toString(), page.toString())
            .enqueue(object : retrofit2.Callback<KaobeiComments> {
                override fun onFailure(call: retrofit2.Call<KaobeiComments>, t: Throwable) {
                    mOnReceiveDataListener?.onFailureToReceiveData()
                }

                override fun onResponse(
                    call: retrofit2.Call<KaobeiComments>,
                    response: retrofit2.Response<KaobeiComments>
                ) {
                    if (response.isSuccessful) {
                        mOnReceiveDataListener?.onReceiveData(response.body()?.data!!)
                        add(response.body()?.data!!)
                        if (response.body()?.meta?.pagination?.currentPage == response.body()?.meta?.pagination?.totalPages) {
                            mOnReceiveDataListener?.onNoMoreData()
                        }
                    } else {
                        mOnReceiveDataListener?.onFailureToReceiveData()
                    }
                }
            })
    }

}