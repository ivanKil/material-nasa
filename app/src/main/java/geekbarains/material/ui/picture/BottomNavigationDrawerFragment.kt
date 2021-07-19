package geekbarains.nasa.ui.picture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import geekbarains.material.ui.settings.SettingsFragment
import geekbarains.nasa.R
import geekbarains.nasa.ui.chips.MRFotosFragment
import kotlinx.android.synthetic.main.bottom_navigation_layout.*

class BottomNavigationDrawerFragment : BottomSheetDialogFragment() {
    private val TAG_APOD = "APOD"
    private val TAG_MARS_ROVER = "TAG_MARS_ROVER"
    private val TAG_SETTINGS = "TAG_SETTINGS"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_navigation_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navigation_view.setNavigationItemSelectedListener { menuItem ->
            val frMars = activity?.supportFragmentManager?.findFragmentByTag(TAG_MARS_ROVER)
            val frApod = activity?.supportFragmentManager?.findFragmentByTag(TAG_APOD)
            val frSettings = activity?.supportFragmentManager?.findFragmentByTag(TAG_SETTINGS)
            when (menuItem.itemId) {
                R.id.navigation_mrfoto -> {
                    if (frMars == null)
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(
                                R.id.container,
                                MRFotosFragment(),
                                TAG_MARS_ROVER
                            )?.addToBackStack(null)?.commit()
                    else
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.container, frMars)

                }
                R.id.navigation_apod -> {
                    if (frApod == null)
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.container, ApodFragment(), TAG_APOD)
                            ?.addToBackStack(null)?.commit()
                    else
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.container, frApod)
                }
                R.id.navigation_settings ->
                    if (frSettings == null)
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.container, SettingsFragment.newInstance(), TAG_SETTINGS)
                            ?.addToBackStack(null)?.commit()
                    else
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.container, frSettings)
            }
            dismiss()
            true
        }
    }
}
