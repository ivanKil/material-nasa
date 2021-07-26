package geekbarains.nasa.ui.picture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lessons.nasa.model.Epic
import com.lessons.nasa.model.Mars
import com.lessons.nasa.network.NasaAPI
import com.lessons.nasa.network.RetrofitServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

sealed class EpicListData {
    data class Success<T>(val serverResponseData: List<T>) : EpicListData()
    data class Error(val error: Throwable) : EpicListData()
    data class Loading(val progress: Int?) : EpicListData()
}

class PhotosViewModel(
    private val liveDataEpicList: MutableLiveData<EpicListData> = MutableLiveData(),
    private val liveDataMarsList: MutableLiveData<EpicListData> = MutableLiveData(),
    private val retrofitImpl: NasaAPI = RetrofitServices.create()
) :
    ViewModel() {
    private var epicList: MutableList<Epic>? = null
    private var marsList: MutableList<Mars>? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val dateFormat2 = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    private var curImg = 0

    fun getData(): LiveData<EpicListData> {
        sendServerRequest()
        return liveDataEpicList
    }

    fun getDataMars(): LiveData<EpicListData> {
        sendServerRequestMars()
        return liveDataMarsList
    }

    private fun sendServerRequest() {
        if (liveDataEpicList.value != null) {
            liveDataEpicList.value = EpicListData.Success(epicList?.toList() ?: emptyList())
            return
        }
        liveDataEpicList.value = EpicListData.Loading(null)
        retrofitImpl.getEpicList()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io()).subscribe({
                epicList = it.toMutableList()
                liveDataEpicList.value = EpicListData.Success(it)
            }, {
                liveDataEpicList.value = EpicListData.Error(it)
            })
    }

    fun getUrlForImage(epic: Epic): String {
        val d = dateFormat2.format(dateFormat.parse(epic.date))
        return "${RetrofitServices.BASE_URL}EPIC/archive/natural/${d}/png/${epic.image}.png?api_key=${RetrofitServices.API_KEY}"
    }

    fun getNextImage() =
        if (epicList != null && epicList!!.size > curImg++) getUrlForImage(epicList!![curImg]) else ""

    private fun sendServerRequestMars() {
        if (liveDataMarsList.value != null) {
            liveDataMarsList.value = EpicListData.Success(marsList?.toList() ?: emptyList())
            return
        }
        liveDataMarsList.value = EpicListData.Loading(null)
        retrofitImpl.getMarsList()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io()).subscribe({
                marsList = it.photos.toMutableList()
                liveDataMarsList.value = EpicListData.Success(it.photos)
            }, {
                liveDataMarsList.value = EpicListData.Error(it)
            })
    }


    fun getNextImageMars() =
        if (marsList != null && marsList!!.size > curImg++) marsList!![curImg].img_src else ""
}
