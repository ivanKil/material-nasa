package geekbarains.material.ui.photos

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import geekbarains.nasa.R

private const val EARTH_FRAGMENT = 0
private const val MARS_FRAGMENT = 1

class ViewPagerAdapter(private val fragmentManager: FragmentManager, val context: Context) :
    FragmentStatePagerAdapter(fragmentManager) {

    private val fragments = arrayOf(EarthFragment(), MarsFragment())

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> fragments[EARTH_FRAGMENT]
            1 -> fragments[MARS_FRAGMENT]
            else -> fragments[EARTH_FRAGMENT]
        }
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {

        return when (position) {
            0 -> context.getResources().getString(R.string.earth)
            1 -> context.getResources().getString(R.string.mars)
            else -> context.getResources().getString(R.string.earth)
        }
    }
}
