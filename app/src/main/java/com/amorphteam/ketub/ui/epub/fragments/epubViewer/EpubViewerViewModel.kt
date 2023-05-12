package com.amorphteam.ketub.ui.epub.fragments.epubViewer

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amorphteam.ketub.R
import com.amorphteam.ketub.model.BookHolder
import com.amorphteam.ketub.utility.Keys
import kotlinx.coroutines.*
import java.security.Key

class EpubViewerViewModel : ViewModel() {
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val webViewUrl = MutableLiveData<String>().apply { value = "file:///android_asset/sample.html" }
    val defaultStyleStatus = MutableLiveData<Boolean>().apply { value = true }
    val moreReadabilityStyleStatus = MutableLiveData<Boolean>().apply { value = false }
    val highContrastStyleStatus = MutableLiveData<Boolean>().apply { value = false }
    val darkModeStyleStatus = MutableLiveData<Boolean>().apply { value = false }
    val basetTheme = MutableLiveData<Boolean>().apply { value = true }
    val darkTheme = MutableLiveData<Boolean>().apply { value = false }
    val lightTheme = MutableLiveData<Boolean>().apply { value = false }

    val hideToolbar = MutableLiveData<Boolean>().apply { value = false }

    val fontSizeProgress = MutableLiveData<Int>().apply { if (defaultStyleStatus.value!!) value = 2 }
    val lineHightProgress = MutableLiveData<Int>().apply { if (defaultStyleStatus.value!!) value = 2 }

    private val _htmlSourceString = MutableLiveData<String?>()
    val htmlSourceString : LiveData<String?>
    get() = _htmlSourceString

    val pageNumber = MutableLiveData<String>().apply { value = "1" }
    init {

    }

    override fun onCleared() {
        super.onCleared()
        Log.i(Keys.LOG_NAME, "cleared epub viewer view model")
    }

    fun onClickBaseTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        basetTheme.value = true
        lightTheme.value = false
        darkTheme.value = false
    }

    fun onClickDarkTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        basetTheme.value = false
        lightTheme.value = false
        darkTheme.value = true
    }

    fun onClickLightTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        basetTheme.value = false
        lightTheme.value = true
        darkTheme.value = false
    }

    fun onClickOnDefaultStyle() {
        defaultStyleStatus.value = true
        highContrastStyleStatus.value = false
        moreReadabilityStyleStatus.value = false
        darkModeStyleStatus.value = false
        fontSizeProgress.value = 2
        lineHightProgress.value = 2
    }

    fun onClickOnMoreReadabilityStyle() {
        defaultStyleStatus.value = false
        highContrastStyleStatus.value = false
        moreReadabilityStyleStatus.value = true
        darkModeStyleStatus.value = false
        fontSizeProgress.value = 6
        lineHightProgress.value = 6
    }

    fun onClickOnDarkModeStyle() {
        defaultStyleStatus.value = false
        highContrastStyleStatus.value = false
        moreReadabilityStyleStatus.value = false
        darkModeStyleStatus.value = true
        fontSizeProgress.value = 0
        lineHightProgress.value = 0
        onClickDarkTheme()
    }

    fun updateFontSizeSeekerBar(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        fontSizeProgress.value = progress
        Log.i(Keys.LOG_NAME, "fontSize$progress")
    }

    fun updateVerticalSeekBar(seekBar: SeekBar, progress: Int, fromUser: Boolean){
        pageNumber.value = progress.toString()
        Log.i(Keys.LOG_NAME, "mainSeekBar$progress")
    }

    fun updateLineHightSeekerBar(seekBar: SeekBar, progress: Int, fromUser: Boolean){
        lineHightProgress.value = progress
        Log.i(Keys.LOG_NAME, "lineHeight$progress")
    }

    fun onClickOnHighContrast() {
        defaultStyleStatus.value = false
        highContrastStyleStatus.value = true
        moreReadabilityStyleStatus.value = false
        darkModeStyleStatus.value = false
        fontSizeProgress.value = 3
        lineHightProgress.value = 2
    }

    fun onClickChipsView(view: View){
        when (view.id) {
            R.id.font_vazir -> { Log.i(Keys.LOG_NAME, "font vazir")}
            R.id.font_iran_sans -> { Log.i(Keys.LOG_NAME, "font iran sans")}
            R.id.font_dubai -> { Log.i(Keys.LOG_NAME, "font dubai")}
            R.id.font_lotus -> { Log.i(Keys.LOG_NAME, "font lotus")}
            R.id.font_nazanin -> { Log.i(Keys.LOG_NAME, "font nazanin")}
        }
    }



     fun getResourceString(ctx: Context, resourceUri: Uri, classes: String, pos: Int) {
         uiScope.launch{
        try {
            val resourceString = getResourceAsString(ctx, resourceUri, classes)
            _htmlSourceString.value = resourceString
        } catch (t: Throwable) {
            Log.e(Keys.LOG_NAME, t.message.toString())
        }
         }
    }

    private suspend fun getResourceAsString(
        ctx: Context, resourceUri: Uri,
        classes: String
    ): String? {
        Log.i(Keys.LOG_NAME + "2", resourceUri.toString())

        return withContext(Dispatchers.IO) {
            val resourceString =
                BookHolder.instance?.jsBook?.getResourceString(ctx, resourceUri, classes)
            resourceString
        }
    }


}