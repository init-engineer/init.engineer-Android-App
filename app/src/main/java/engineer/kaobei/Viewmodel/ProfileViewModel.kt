package engineer.kaobei.Viewmodel

import engineer.kaobei.BASE_URL
import engineer.kaobei.KaobeiEngineerService
import engineer.kaobei.Model.KaobelUser.BeanKaobeiUser
import engineer.kaobei.Model.KaobelUser.KaobeiUser
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Class ProfileViewModel.
 */
class ProfileViewModel : ObjectViewModel<KaobeiUser>() {

    fun loadProfile(accessToken: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(KaobeiEngineerService::class.java)
        service.profile("Bearer " + accessToken)
            .enqueue(object : retrofit2.Callback<BeanKaobeiUser> {
                override fun onFailure(call: Call<BeanKaobeiUser>, t: Throwable) {
                    mOnReceiveDataListener?.onFailureReceiveData()
                }

                override fun onResponse(
                    call: Call<BeanKaobeiUser>,
                    response: Response<BeanKaobeiUser>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.data?.let { change(it) }
                        mOnReceiveDataListener?.onReceiveData()
                    } else {
                        mOnReceiveDataListener?.onFailureReceiveData()
                    }
                }

            })
    }
}

