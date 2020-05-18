package engineer.kaobei.Viewmodel

import com.google.gson.Gson
import engineer.kaobei.BASE_URL
import engineer.kaobei.KaobeiEngineerService
import engineer.kaobei.Model.Comments.Comment
import engineer.kaobei.Model.Comments.KaobeiComments
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

/**
 * A ViewModel used for the {@link ArticleListFragment}.
 */
class CommentsViewModel() : ListViewModel<Comment>() {

    fun loadComments(id:Int,page:Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(KaobeiEngineerService::class.java)
        service.comments(id.toString(),page.toString()).enqueue(object : retrofit2.Callback<KaobeiComments> {
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