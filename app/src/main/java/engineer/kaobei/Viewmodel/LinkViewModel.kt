package engineer.kaobei.Viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import engineer.kaobei.Model.Link.KaobeiLink
import okhttp3.*
import java.io.IOException

class LinkViewModel : ViewModel() {

    private lateinit var mOnReceiveDataListener : LinkViewModel.OnReceiveDataListener;
    private var id = 0
    private val mCommentsLiveData: MutableLiveData<KaobeiLink> by lazy {
        MutableLiveData<KaobeiLink>().also {
            loadLink(id)
        }
    }

    fun getLink(id : Int): LiveData<KaobeiLink> {
        this.id = id
        return mCommentsLiveData
    }

    fun loadLink(id:Int) {
        // Do an asynchronous operation to fetch articles.
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://kaobei.engineer/api/frontend/social/cards/$id/links")
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
                val bean = Gson().fromJson(responseData, KaobeiLink::class.javaObjectType)
                mOnReceiveDataListener.onReceiveData(bean)
                mCommentsLiveData.postValue(bean)
            }

        })
    }

    fun addOnReceiveDataListener(mOnReceiveDataListener: OnReceiveDataListener){
        this.mOnReceiveDataListener = mOnReceiveDataListener
    }

    interface OnReceiveDataListener {
        fun onReceiveData(kaobeiLink: KaobeiLink)
        fun onFailure()
    }

}