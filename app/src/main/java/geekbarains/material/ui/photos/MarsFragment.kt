package geekbarains.material.ui.photos

import androidx.lifecycle.observe
import com.lessons.nasa.model.Mars
import geekbarains.nasa.ui.picture.EpicListData
import kotlinx.android.synthetic.main.fragment_earth.*

class MarsFragment : EarthFragment() {

    override fun init() {
        viewModel.getDataMars().observe(viewLifecycleOwner, { renderData(it) })
        but_more.setOnClickListener { setImageUrl(viewModel.getNextImageMars(), this@MarsFragment) }
    }

    override fun getUrl(epicListData: EpicListData.Success<*>) =
        (epicListData.serverResponseData[0] as Mars).img_src
}
