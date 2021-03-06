package engineer.kaobei.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Class ListViewModel.
 */
open class ListViewModel<T> : ViewModel() {

    protected var mListViewModelController: ListViewModelController? = null
    protected var mOnReceiveDataListener: OnReceiveDataListener<T>? = null
    protected var mReviewOnReceiveDataListener: OnReviewOnReceiveDataListener<T>? = null
    protected var mList = ArrayList<T>()
    protected  var mLiveData: MutableLiveData<ArrayList<T>> = MutableLiveData<ArrayList<T>>()

    open fun getLiveData(): LiveData<ArrayList<T>> {
        return mLiveData
    }

    fun add(list: List<T>) {
        mList.addAll(list)
        mLiveData.value = mList
    }

    fun add(t: T) {
        mList.add(t)
        mLiveData.value = mList
    }

    fun add(index: Int, t: T) {
        mList.add(index, t)
        mLiveData.value = mList
    }

    fun change(index: Int, t: T){
        mList[index] = t
        mLiveData.value = mList
    }

    fun remove(index: Int) {
        mList.removeAt(index)
        mLiveData.value = mList
    }

    fun addOnReceiveDataListener(mOnReceiveDataListener: OnReceiveDataListener<T>) {
        this.mOnReceiveDataListener = mOnReceiveDataListener
    }

    fun addOnReviewReceiveDataListener(mOnReceiveDataListener: OnReviewOnReceiveDataListener<T>) {
        this.mReviewOnReceiveDataListener = mOnReceiveDataListener
    }

    fun addListViewModelController(mListViewModelController:ListViewModelController){
        this.mListViewModelController = mListViewModelController
    }

    interface OnReceiveDataListener<T> {
        fun onReceiveData(list: List<T>)
        fun onFailureToReceiveData()
        fun onNoMoreData()
    }

    interface OnReviewOnReceiveDataListener<T> {
        fun onReceiveData(list: List<T>,totalPage : Int)
        fun onFailureToReceiveData()
        fun onNoMoreData()
    }

    interface ListViewModelController {
        fun initLiveData()
    }


}