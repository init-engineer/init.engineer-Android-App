package engineer.kaobei.viewmodel

import engineer.kaobei.BASE_URL
import engineer.kaobei.KaobeiEngineerService
import engineer.kaobei.model.userarticles.UserArticle
import engineer.kaobei.model.userarticles.UserArticles
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Class UserArticleListViewModel.
 */
class UserArticleListViewModel : ListViewModel<UserArticle>() {

    fun loadArticles(accessToken: String, page: Int) {
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
