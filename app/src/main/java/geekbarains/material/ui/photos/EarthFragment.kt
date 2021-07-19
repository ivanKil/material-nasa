package geekbarains.material.ui.photos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import coil.load
import com.lessons.nasa.model.Epic
import geekbarains.nasa.R
import geekbarains.nasa.ui.picture.EpicListData
import geekbarains.nasa.ui.picture.PhotosViewModel
import geekbarains.nasa.ui.picture.snackBarError
import kotlinx.android.synthetic.main.fragment_earth.*

open class EarthFragment : Fragment() {
    val viewModel: PhotosViewModel by lazy {
        ViewModelProvider(this).get(PhotosViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_earth, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    open fun init() {
        viewModel.getData().observe(viewLifecycleOwner, { renderData(it) })
        but_more.setOnClickListener { setImageUrl(viewModel.getNextImage(), this) }
    }

    protected fun setImageUrl(url: String, earthFragment: Fragment) {
        img_photo.load(url) {
            lifecycle(earthFragment)
            error(R.drawable.ic_load_error_vector)
            placeholder(R.drawable.ic_no_photo_vector)
        }
    }

    open fun getUrl(epicListData: EpicListData.Success<*>) =
        viewModel.getUrlForImage(epicListData.serverResponseData[0] as Epic)


    protected fun renderData(data: EpicListData) {
        when (data) {
            is EpicListData.Success<*> -> {
                progressCircleDeterminate.visibility = View.GONE
                setImageUrl(getUrl(data), this)
            }
            is EpicListData.Loading -> {
                progressCircleDeterminate.visibility = View.VISIBLE
            }
            is EpicListData.Error -> {
                progressCircleDeterminate.visibility = View.GONE
                view?.snackBarError(
                    if (data.error != null) data.error.message!! else resources.getString(
                        R.string.error
                    )
                )
                img_photo.load("")
            }
        }
    }
}
