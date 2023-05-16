package com.amorphteam.ketub.ui.epub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.amorphteam.ketub.database.reference.ReferenceRepository
import com.amorphteam.ketub.ui.main.tabs.bookmark.tabs.first_and_second.BookmarkViewModel

class EpubViewModelFactory (private val referenceRepository: ReferenceRepository
    ) : ViewModelProvider.Factory {

        @Suppress("unckecked_cast")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EpubViewModel::class.java)) {
                return EpubViewModel(referenceRepository) as T
            }
            throw IllegalThreadStateException("Unknow ViewModel class")
        }
    }