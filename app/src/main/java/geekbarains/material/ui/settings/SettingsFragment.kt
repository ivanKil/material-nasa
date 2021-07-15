package geekbarains.material.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import geekbarains.nasa.R
import geekbarains.nasa.databinding.SettingsFragmentBinding
import geekbarains.nasa.ui.picture.ApodFragment


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
        }
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}
