package engineer.kaobei.Viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class ListViewModel<T>  : ViewModel() {

    protected var mOnReceiveDataListener: OnReceiveDataListener<T>? = null
    private val mList = ArrayList<T>()
    private val mLiveData: MutableLiveData<ArrayList<T>> by lazy {
        MutableLiveData<ArrayList<T>>().also {

        }
    }

    fun getLiveData(): LiveData<ArrayList<T>> {
        return mLiveData
    }

    fun add(list: List<T>) {
        mList.addAll(list)
        mLiveData.postValue(mList)
    }

    fun add(t: T) {
        mList.add(t)
        mLiveData.postValue(mList)
    }

    fun add(index: Int, t: T) {
        mList.add(index, t)
        mLiveData.postValue(mList)
    }

    fun remove(index: Int) {
        mList.removeAt(index)
        mLiveData.postValue(mList)
    }

    fun addOnReceiveDataListener(mOnReceiveDataListener: OnReceiveDataListener<T>) {
        this.mOnReceiveDataListener = mOnReceiveDataListener
    }

    interface OnReceiveDataListener<T> {
        fun onReceiveData(list: List<T>)
        fun onFailureToReceiveData()
        fun onNoMoreData()
    }

}