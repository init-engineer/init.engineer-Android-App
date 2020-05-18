package engineer.kaobei

interface RecyclerViewAdapterListener<T> {
    fun onTheFirstInit(list : List<T>)
    fun onReceiveData()
    fun onFailedToReceiveData()
    fun onNoMoreData()
}