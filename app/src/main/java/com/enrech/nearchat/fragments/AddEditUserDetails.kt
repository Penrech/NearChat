package com.enrech.nearchat.fragments

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.NestedScrollView

import com.enrech.nearchat.R
import com.enrech.nearchat.interfaces.NotifyInteractionUserProfile
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_edit_user_activty.*
import kotlinx.android.synthetic.main.fragment_add_edit_user_details.*
import kotlinx.android.synthetic.main.fragment_add_edit_user_details.editUserProfileCloseButton
import kotlinx.android.synthetic.main.fragment_add_edit_user_details.saveChangesEditProfileToolbarButton


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AddEditUserDetails : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private var listener: NotifyInteractionUserProfile? = null

    private var appBarScrollOffset: Int? = null

    private var appBarIsListening = false

    private var scrollOffset: Int? = null
    
    private var onScrollChangeListener = NestedScrollView.OnScrollChangeListener { view, scrollX, scrollY, oldScrollX, oldScrollY ->
        scrollOffset = scrollY

    }

    private var onAppBarOffsetChangeListener = AppBarLayout.OnOffsetChangedListener { appBarLayout, offset ->
        appBarScrollOffset = offset
        Log.i("TIG","offset $offset")
    }

    private var closeWithoutSaveButtonListener = View.OnClickListener {
            listener?.profilePropagateBackButton()
    }

    private var closeSaveButtonListener = View.OnClickListener {
         listener?.profilePropagateBackButton()
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

        return inflater.inflate(R.layout.fragment_add_edit_user_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpButtons()
        setClickOnScreenListener()
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

        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun setUpButtons(){
        saveChangesEditProfileToolbarButton.setOnClickListener(closeSaveButtonListener)
        editUserProfileCloseButton.setOnClickListener(closeWithoutSaveButtonListener)
    }

    private fun setClickOnScreenListener(){
        rootAddEditProfileLayout.setOnTouchListener { _, _ ->
            hideSoftKeyboard(activity as Activity)
            false
        }
        AddEditProfileContainer.setOnTouchListener { _, _ ->
            hideSoftKeyboard(activity as Activity)
            false
        }
    }

    private fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        activity.currentFocus?.let {
            inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus!!.windowToken, 0
            )
            it.clearFocus()
        }

    }

    private fun setScrollListener(){

        appBarScrollOffset?.let {
            AddEditProfileAppBar.top = it
        }

        scrollOffset?.let {
            AddEditScrollView.scrollY = it
        }

        AddEditScrollView.setOnScrollChangeListener(onScrollChangeListener)
    }

    private fun addAppBarListener(){
        if (!appBarIsListening)
        {
            AddEditProfileAppBar.addOnOffsetChangedListener(onAppBarOffsetChangeListener)
            appBarIsListening = true
        }
    }

    private fun removeAppBarListener(){
        if (appBarIsListening)
        {
            AddEditProfileAppBar.removeOnOffsetChangedListener(onAppBarOffsetChangeListener)
            appBarIsListening = false
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddEditUserDetails().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
