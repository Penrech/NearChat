package com.enrech.nearchat.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView

import com.enrech.nearchat.R
import com.enrech.nearchat.interfaces.NotifyInteractionUserProfile
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_profile_details.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileDetailsFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private var listener: NotifyInteractionUserProfile? = null

    private var openEditProfileButtonListener = View.OnClickListener {
        listener?.profileOpenEditUserClick(true)
    }

    private var appBarScrollOffset: Int? = null

    private var appBarIsListening = false

    private var onAppBarOffsetChangeListener = AppBarLayout.OnOffsetChangedListener { appBarLayout, offset ->
        Log.i("TIG","Profile scroll offset $offset")
        appBarScrollOffset = offset
    }

    private var scrollOffset: Int? = null

    private var onScrollChangeListener = NestedScrollView.OnScrollChangeListener { view, scrollX, scrollY, oldScrollX, oldScrollY ->
        scrollOffset = scrollY

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_profile_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpButtons()

        setScrollListener()
        addAppBarListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        removeAppBarListener()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NotifyInteractionUserProfile) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun setUpButtons(){
        editUserProfileButton.setOnClickListener(openEditProfileButtonListener)
    }

    private fun setScrollListener(){

        appBarScrollOffset?.let {
            profileDetailsAppBar.top = it
        }
        scrollOffset?.let {
            profileDetailsScrollView.scrollY = it
        }

        profileDetailsScrollView.setOnScrollChangeListener(onScrollChangeListener)
    }

    private fun addAppBarListener(){

        if (!appBarIsListening)
        {
            profileDetailsAppBar.addOnOffsetChangedListener(onAppBarOffsetChangeListener)
            appBarIsListening = true
        }
    }

    private fun removeAppBarListener(){
        if (appBarIsListening)
        {
            profileDetailsAppBar.removeOnOffsetChangedListener(onAppBarOffsetChangeListener)
            appBarIsListening = false
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
