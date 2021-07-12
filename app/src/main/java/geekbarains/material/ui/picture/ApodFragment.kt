package geekbarains.nasa.ui.picture

//import coil.api.load
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import coil.load
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lessons.nasa.model.ApodData
import geekbarains.nasa.R
import geekbarains.nasa.databinding.MainFragmentBinding
import geekbarains.nasa.ui.MainActivity

import kotlinx.android.synthetic.main.main_fragment.*
import retrofit2.HttpException

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
            R.id.app_bar_settings -> toast(getString(R.string.settings))
            // activity?.supportFragmentManager?.beginTransaction()
            //?.add(R.id.container, ChipsFragment())?.addToBackStack(null)?.commit()
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
                    //showError("Сообщение, что ссылка пустая")
                    toast("Link is empty")
                } else {
                    //showSuccess()
                    image_view.load(url) {
                        lifecycle(this@ApodFragment)
                        error(R.drawable.ic_load_error_vector)
                        placeholder(R.drawable.ic_no_photo_vector)
                    }
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                    // bottomSheetBehavior.peekHeight = 500
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

    private fun setBottomAppBar(view: View) {
        val context = activity as MainActivity
        context.setSupportActionBar(binding.bottomAppBar)
        setHasOptionsMenu(true)
        binding.bottomAppBar.fabCradleRoundedCornerRadius = 300f
        fab.setOnClickListener {
            if (binding.inputLayout.visibility != View.GONE) {
                binding.inputLayout.visibility = View.GONE
                binding.bottomAppBar.fabCradleRoundedCornerRadius = 15f
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
