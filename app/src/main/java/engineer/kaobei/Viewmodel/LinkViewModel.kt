package engineer.kaobei.Viewmodel

import engineer.kaobei.BASE_URL
import engineer.kaobei.KaobeiEngineerService
import engineer.kaobei.Model.Link.KaobeiLink
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Class LinkViewModel.
 */
class LinkViewModel : ObjectViewModel<KaobeiLink>() {

    fun loadLink(id: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(KaobeiEngineerService::class.java)
        service.links(id.toString()).enqueue(object : retrofit2.Callback<KaobeiLink> {
            override fun onFailure(call: retrofit2.Call<KaobeiLink>, t: Throwable) {
                mOnReceiveDataListener?.onFailureReceiveData()
            }

            override fun onResponse(
                call: retrofit2.Call<KaobeiLink>,
                response: retrofit2.Response<KaobeiLink>
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