package engineer.kaobei.viewmodel

import engineer.kaobei.BASE_URL
import engineer.kaobei.KaobeiEngineerService
import engineer.kaobei.model.articleInfo.KaobeiArticleInfo
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ArticleInfoViewModel : ObjectViewModel<KaobeiArticleInfo>() {

    fun loadArticleInfo(id: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(KaobeiEngineerService::class.java)
        service.show(id.toString())
            .enqueue(object : retrofit2.Callback<KaobeiArticleInfo> {
                override fun onFailure(call: retrofit2.Call<KaobeiArticleInfo>, t: Throwable) {
                    mOnReceiveDataListener?.onFailureReceiveData()
                }

                override fun onResponse(
                    call: retrofit2.Call<KaobeiArticleInfo>,
                    response: retrofit2.Response<KaobeiArticleInfo>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { change(it) }
                        mOnReceiveDataListener?.onReceiveData()
                    } else {
                        mOnReceiveDataListener?.onFailureReceiveData()
                    }
                }
            })
    }

}