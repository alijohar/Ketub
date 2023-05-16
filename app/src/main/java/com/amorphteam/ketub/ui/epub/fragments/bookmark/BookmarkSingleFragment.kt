package com.amorphteam.ketub.ui.epub.fragments.bookmark

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.amorphteam.ketub.R

import com.amorphteam.ketub.database.reference.ReferenceDatabase
import com.amorphteam.ketub.model.ReferenceModel
import com.amorphteam.ketub.ui.adapter.DeleteClickListener
import com.amorphteam.ketub.ui.adapter.ItemClickListener
import com.amorphteam.ketub.ui.adapter.ReferenceAdapter
import com.amorphteam.ketub.utility.Keys

class BookmarkSingleFragment : Fragment() {
    private lateinit var binding: com.amorphteam.ketub.databinding.FragmentBookmarkSingleBinding
    private lateinit var viewModel: BookmarkSingleViewModel
    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Create an instance of the ViewModel Factory.
        val application = requireNotNull(this.activity).application
        val dataSource = ReferenceDatabase.getInstance(application).referenceDatabaseDao
        val viewModelFactory = BookmarkSingleViewModelFactory(dataSource)

        // Get a reference to the ViewModel associated with this fragment.
        viewModel =
            ViewModelProvider(this, viewModelFactory)[BookmarkSingleViewModel::class.java]

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_bookmark_single, container, false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.startEpubFrag.observe(viewLifecycleOwner) {
//            if (it) startActivity(Intent(activity, EpubActivity::class.java))
        }

        val adapter = ReferenceAdapter(ItemClickListener {
//            viewModel.openEpubAct()

        }, DeleteClickListener {
            Log.i(Keys.LOG_NAME, "BookmarkDeleteClickListener$it")
            viewModel.deleteBookmark(it)

        })

        viewModel.allBookmarks.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                handleBookmarkRecyclerView(adapter, it)
            }
        }
        binding.searchbar.back.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit();
        }
        handleSearchView(binding.searchbar.searchView, adapter)

        return binding.root
    }

    private fun openEpubFragment() {
        navController = Navigation.findNavController(requireView())
        navController.navigate(R.id.action_bookmarkSingleFragment_to_epubViewFragment)
    }

    private fun handleBookmarkRecyclerView(
        index: ReferenceAdapter,
        bookmarkArrayList: List<ReferenceModel>
    ) {
        index.submitList(bookmarkArrayList)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = index
    }

    private fun handleSearchView(
        searchView: androidx.appcompat.widget.SearchView,
        index: ReferenceAdapter
    ) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterSearch(newText, index)

                return true
            }
        })

    }

    private fun filterSearch(searchString: String, index: ReferenceAdapter) {
        index.filter.filter(searchString)
    }


}