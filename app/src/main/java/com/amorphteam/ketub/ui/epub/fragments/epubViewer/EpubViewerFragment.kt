package com.amorphteam.ketub.ui.epub.fragments.epubViewer

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.fonts.Font
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.amorphteam.ketub.R
import com.amorphteam.ketub.databinding.FragmentEpubViewBinding
import com.amorphteam.ketub.model.BookHolder
import com.amorphteam.ketub.model.FontName
import com.amorphteam.ketub.model.FontSize
import com.amorphteam.ketub.model.LineSpace
import com.amorphteam.ketub.ui.epub.EpubActivity
import com.amorphteam.ketub.ui.epub.EpubVerticalDelegate
import com.amorphteam.ketub.ui.epub.fragments.StyleListener
import com.amorphteam.ketub.utility.Keys
import com.amorphteam.ketub.utility.PreferencesManager
import com.mehdok.fineepublib.epubviewer.epub.Book
import com.mehdok.fineepublib.epubviewer.epub.ManifestItem
import com.mehdok.fineepublib.epubviewer.jsepub.client.JsPictureListener
import com.mehdok.fineepublib.epubviewer.jsepub.client.JsPictureListener.WebViewPictureListener
import com.mehdok.fineepublib.interfaces.EpubScrollListener
import com.mehdok.fineepublib.interfaces.EpubTapListener
import kotlinx.android.synthetic.main.bottom_sheet_style.bottom_sheet
import java.util.*


class EpubViewerFragment : Fragment(), StyleListener, WebViewPictureListener, EpubTapListener, EpubScrollListener {
    private lateinit var binding: FragmentEpubViewBinding
    private lateinit var viewModel: EpubViewerViewModel
    lateinit var webView: LocalWebView
    private var jsPictureListener: JsPictureListener? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_epub_view, container, false
        )
        viewModel = ViewModelProvider(this)[EpubViewerViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EpubVerticalDelegate.get()?.activity?.addStyleListener(this)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            val manifestItem: ManifestItem? = requireArguments().getParcelable(Keys.MANIFEST_ITEM)
            val position = requireArguments().getInt(Keys.POSITION_ITEM)
            viewModel.getResourceString(
                requireContext(),
                Book.resourceName2Url(manifestItem?.href),
                EpubVerticalDelegate.get()?.activity?.viewModel?.styleBookPref?.getClasses()!!,
                position
            )
            viewModel.htmlSourceString.observe(viewLifecycleOwner) {
                if (it != null) {
                    fillWebView(it)
                }
            }

        }
    }


    private fun fillWebView(it: String) {
        webView = LocalWebView(requireContext())
        jsPictureListener = JsPictureListener(this)
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        webView.setTapListener(this)
        webView.setScrollListener(this)
        webView.setPictureListener(jsPictureListener)
        webView.setBook(BookHolder.instance?.jsBook)
        webView.loadResourcePage(it)
        webView.layoutParams = params
        binding.mainEpubContainer.addView(webView)
    }


    companion object {
        fun newInstance(
            manifestItem: ManifestItem?,
            pos: Int
        ): EpubViewerFragment {
            val bundle = Bundle()
            bundle.putParcelable(Keys.MANIFEST_ITEM, manifestItem)
            bundle.putInt(Keys.POSITION_ITEM, pos)
            val instance: EpubViewerFragment =
                EpubViewerFragment()
            instance.arguments = bundle
            return instance
        }

    }

    override fun onDrawingFinished() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (jsPictureListener != null) {
            jsPictureListener?.setWebViewPictureListener(null)
        }

        try {
            EpubVerticalDelegate.get()?.activity?.removeStyleListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPageScrolled(scrollY: Int) {

    }

    override fun onEmailTapped(email: String?) {
        Toast.makeText(requireContext(), email, Toast.LENGTH_SHORT).show()
    }

    override fun onWebTapped(link: String?) {
        if (!link!!.startsWith("http://localhost")) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(browserIntent)
        }
    }

    override fun onEmptySpaceTapped() {
        val activity = requireActivity() as EpubActivity
        activity.viewModel.toggle()
    }

    override fun onExternalLinkCLicked(uri: Uri?) {
        if (!uri.toString().startsWith("http://localhost")) {
            val browserIntent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(browserIntent)
        }
    }

    override fun getHitTestResult(): WebView.HitTestResult {
        return webView.hitTestResult
    }

    override fun onPageScrolled() {

    }

    override fun changeFontSize(fontSize: Int?) {
        val js = String.format(Locale.US, "javascript:setFontSize('%s');", FontSize.from(fontSize!!))
        webView.exeJs(js)
    }

    override fun changeLineSpace(lineSpace: Int?) {
        val js = String.format(Locale.US, "javascript:setLineHeight('%s');", LineSpace.from(lineSpace!!))
        webView.exeJs(js)
    }

    override fun changeFontName(font: Int?) {
        val js = String.format(Locale.US, "javascript:changeTypeFace('%s');", FontName.from(font!!))
        webView.exeJs(js)
    }

    override fun changeBkColor(BkColor: Int?) {

    }


}