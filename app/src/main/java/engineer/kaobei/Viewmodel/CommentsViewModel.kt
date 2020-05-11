package engineer.kaobei.Viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import engineer.kaobei.Model.Comments.Comment
import engineer.kaobei.Model.Comments.KaobeiComments
import okhttp3.*
import java.io.IOException

/**
 * A ViewModel used for the {@link ArticleListFragment}.
 */
class CommentsViewModel() : ViewModel() {

    private lateinit var mOnReceiveDataListener : OnReceiveDataListener;
    private var id = 0
    private val  mComments = ArrayList<Comment>()
    private val mCommentsLiveData: MutableLiveData<ArrayList<Comment>> by lazy {
        MutableLiveData<ArrayList<Comment>>().also {
            loadComments(id,1)
        }
    }

    fun getComments(id:Int): LiveData<ArrayList<Comment>> {
        this.id = id
        return mCommentsLiveData
    }

    fun loadComments(id:Int,page:Int) {
        // Do an asynchronous operation to fetch articles.
        loadMoreComments(id,page)
    }

    private fun loadMoreComments(id:Int,page: Int) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://kaobei.engineer/api/frontend/social/cards/$id/comments?page=$page")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mOnReceiveDataListener.onFailure()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                if(response.code !=200){
                    mOnReceiveDataListener.onFailure()
                    return
                }
                val bean = Gson().fromJson(responseData, KaobeiComments::class.javaObjectType)
                if(bean.data.isEmpty()){
                    mOnReceiveDataListener.onNoMoreComments()
                }
                mOnReceiveDataListener.onReceiveData(bean.data)
                addComments(bean.data)
            }

        })
    }


    fun addComments(comments: List<Comment>) {
        mComments.addAll(comments)
        mCommentsLiveData.postValue(mComments)
    }

    fun addComment(comment:Comment) {
        mComments.add(comment)
        mCommentsLiveData.postValue(mComments)
    }


    fun addCommentAt(index : Int,comment:Comment) {
        mComments.add(index,comment)
        mCommentsLiveData.postValue(mComments)
    }

    fun removeCommentAt(index : Int) {
        mComments.removeAt(index)
        mCommentsLiveData.postValue(mComments)
    }

    fun addOnReceiveDataListener(mOnReceiveDataListener: OnReceiveDataListener){
        this.mOnReceiveDataListener = mOnReceiveDataListener
    }

    interface OnReceiveDataListener {
        fun onReceiveData(list:List<Comment>)
        fun onFailure()
        fun onNoMoreComments()
    }


}