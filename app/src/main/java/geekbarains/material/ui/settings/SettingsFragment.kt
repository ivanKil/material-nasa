package geekbarains.material.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.transition.ChangeBounds
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionManager
import geekbarains.nasa.R
import geekbarains.nasa.databinding.SettingsFragmentBinding
import geekbarains.nasa.ui.picture.ApodFragment
import kotlinx.android.synthetic.main.settings_fragment.*


class SettingsFragment : Fragment() {
    private var _binding: SettingsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.chipTheme.setOnCheckedChangeListener { group, checkedId ->
            showComponents(checkedId)
        }
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private fun showComponents(checkedId: Int) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(context, R.layout.settings_fragment_end)

        val transition = ChangeBounds()
        transition.interpolator = AnticipateOvershootInterpolator(1.0f)
        transition.duration = 500
        transition.addListener(object : TransitionListenerAdapter() {
            override fun onTransitionEnd(transition: Transition) {
                when (checkedId) {
                    R.id.chip_green -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        requireActivity().setTheme(R.style.AppTheme2)
                    }
                    R.id.chip_red -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        requireActivity().setTheme(R.style.AppTheme)
                    }
                    R.id.chip_black -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        requireActivity().setTheme(R.style.AppTheme)
                    }
                }
                activity?.supportFragmentManager?.beginTransaction()
                    ?.add(R.id.container, ApodFragment.newInstance())?.commit()
                transition.removeListener(this)
            }
        })
        TransitionManager.beginDelayedTransition(constraint_container, transition)
        constraintSet.applyTo(constraint_container)

    }
}
