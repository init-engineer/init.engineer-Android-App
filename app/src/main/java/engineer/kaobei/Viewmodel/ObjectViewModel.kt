package engineer.kaobei.Viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import engineer.kaobei.Model.KaobelUser.KaobeiUser
import okhttp3.*
import java.io.IOException

/**
 * Class ObjectViewModel.
 */
open class ObjectViewModel<T> : ViewModel() {

    protected var mOnReceiveDataListener: OnReceiveDataListener? = null
    private val mLiveData: MutableLiveData<T> by lazy {
        MutableLiveData<T>().also {
            // Do something ...
        }
    }

    fun getLiveData(): LiveData<T> {
        return mLiveData
    }

    fun change(t: T) {
        mLiveData.postValue(t)
    }

    fun addOnReceiveDataListener(mOnReceiveDataListener: OnReceiveDataListener) {
        this.mOnReceiveDataListener = mOnReceiveDataListener
    }

    interface OnReceiveDataListener {
        fun onReceiveData()
        fun onFailureReceiveData()
    }

}