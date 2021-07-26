package geekbarains.nasa.ui.picture

//import coil.api.load

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.WebChromeClient
import android.webkit.WebSettings.PluginState
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import coil.load
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.lessons.nasa.model.ApodData
import geekbarains.material.ui.photos.PhotosActivity
import geekbarains.material.ui.settings.SettingsFragment
import geekbarains.nasa.R
import geekbarains.nasa.databinding.MainFragmentBinding
import geekbarains.nasa.ui.MainActivity
import kotlinx.android.synthetic.main.main_fragment.*
import retrofit2.HttpException


fun View.snackBarError(string: String) {
    var sb = Snackbar.make(
        this,
        "${resources.getString(R.string.error)} : $string",
        Snackbar.LENGTH_LONG
    )
    sb.view.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
    sb.show()
}

class ApodFragment : Fragment() {
    private val TAG = "PictureOfTheDayFragment"
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private val viewModel: ApodViewModel by lazy {
        ViewModelProvider(this).get(ApodViewModel::class.java)
    }
    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!
    private val WIKI_URL = "https://en.wikipedia.org/wiki/"

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getData(1)
            .observe(viewLifecycleOwner, { renderData(it) })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBottomSheetBehavior(binding.bottomDescr.bottomSheetContainer)
        input_layout.setEndIconOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("$WIKI_URL${input_edit_text.text.toString()}")
            })
        }
        binding.chipGroupDay.setOnCheckedChangeListener { group, checkedId ->
            viewModel.getData(
                checkedId
            )
        }
        setBottomAppBar(view)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_bottom_bar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_settings -> requireActivity().supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container, SettingsFragment.newInstance())
                ?.addToBackStack(null)?.commit()
            R.id.app_bar_photos ->
                requireActivity().let { startActivity(Intent(it, PhotosActivity::class.java)) }
            android.R.id.home -> {
                activity?.let {
                    BottomNavigationDrawerFragment().show(it.supportFragmentManager, "tag")
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun renderData(data: ApodData) {
        when (data) {
            is ApodData.Success -> {
                val serverResponseData = data.serverResponseData
                val url = serverResponseData.url
                if (url.isNullOrEmpty()) {
                    toast(resources.getString(R.string.empty_link))
                } else {
                    if (url.contains("www.youtube.com")) {
                        showVideo(url, image_view.height)
                        image_view.visibility = View.INVISIBLE
                        videoView1.visibility = View.VISIBLE
                    } else {
                        image_view.visibility = View.VISIBLE
                        videoView1.visibility = View.INVISIBLE
                        image_view.load(url) {
                            lifecycle(this@ApodFragment)
                            error(R.drawable.ic_load_error_vector)
                            placeholder(R.drawable.ic_no_photo_vector)
                        }
                    }
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                    binding.bottomDescr.bottomSheetDescriptionHeader.text = serverResponseData.title
                    binding.bottomDescr.bottomSheetDescription.text = serverResponseData.explanation
                }
            }
            is ApodData.Loading -> {
                Log.i(TAG, "loading")
                //showLoading()
            }
            is ApodData.Error -> {
                //showError(data.error.message)
                if (data.error is HttpException && data.error.code() == 404)
                    toast(getString(R.string.picture_not_found))
                else
                    toast(data.error.message)
                image_view.load("")
            }
        }
    }

    private fun showVideo(url: String, height: Int) {
        videoView1.getSettings().setJavaScriptEnabled(true)
        videoView1.getSettings().setPluginState(PluginState.ON)
        videoView1.loadUrl(url)
        videoView1.setWebChromeClient(WebChromeClient())
        videoView1.setLayoutParams(
            ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
        )
    }

    private fun setBottomAppBar(view: View) {
        val context = activity as MainActivity
        context.setSupportActionBar(binding.bottomAppBar)
        setHasOptionsMenu(true)
        binding.bottomAppBar.fabCradleRoundedCornerRadius = 300f
        fab.setOnClickListener {
            if (binding.inputLayout.visibility != View.GONE) {
                binding.inputLayout.visibility = View.GONE
                binding.bottomAppBar.fabCradleRoundedCornerRadius = 15f
            } else {
                binding.inputLayout.visibility = View.VISIBLE
                binding.bottomAppBar.fabCradleRoundedCornerRadius = 300f
            }
        }
    }

    private fun setBottomSheetBehavior(bottomSheet: ConstraintLayout) {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun Fragment.toast(string: String?) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).apply {
            setGravity(Gravity.BOTTOM, 0, 250)
            show()
        }
    }

    companion object {
        fun newInstance() = ApodFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
