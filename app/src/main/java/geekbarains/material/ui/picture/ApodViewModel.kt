package geekbarains.nasa.ui.picture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lessons.nasa.model.ApodData
import com.lessons.nasa.network.NasaAPI
import com.lessons.nasa.network.RetrofitServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class ApodViewModel(
    private val liveDataApod: MutableLiveData<ApodData> = MutableLiveData(),
    private val retrofitImpl: NasaAPI = RetrofitServices.create()
) :
    ViewModel() {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun getData(dayMinus: Int): LiveData<ApodData> {
        sendServerRequest(dayMinus)
        return liveDataApod
    }

    private fun sendServerRequest(dayMinus: Int) {
        liveDataApod.value = ApodData.Loading(null)
        var calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, dayMinus * (-1) + 1);
        val dateString = dateFormat.format(calendar.time)
        retrofitImpl.getPictureOfTheDay(dateString).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io()).subscribe({
                liveDataApod.value = ApodData.Success(it)
            }, {
                liveDataApod.value = ApodData.Error(it)
            })
    }
}
