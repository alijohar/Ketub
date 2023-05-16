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


    private val _htmlSourceString = MutableLiveData<String?>()
    val htmlSourceString: LiveData<String?>
        get() = _htmlSourceString

    init {
        Log.i(Keys.LOG_NAME, "open epub viewer view model")

    }

    override fun onCleared() {
        super.onCleared()
        Log.i(Keys.LOG_NAME, "cleared epub viewer view model")
    }


    fun getResourceString(ctx: Context, resourceUri: Uri, classes: String, pos: Int) {
        uiScope.launch {
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
        return withContext(Dispatchers.IO) {
            val resourceString =
                BookHolder.instance?.jsBook?.getResourceString(ctx, resourceUri, classes)
            resourceString
        }
    }



}