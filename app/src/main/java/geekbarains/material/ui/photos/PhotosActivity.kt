package geekbarains.material.ui.photos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import geekbarains.nasa.R
import kotlinx.android.synthetic.main.photos_activity.*

class PhotosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.photos_activity)
        view_pager.adapter = ViewPagerAdapter(supportFragmentManager, applicationContext)
        tab_layout.setupWithViewPager(view_pager)
        indicator.setViewPager(view_pager)
    }
}
