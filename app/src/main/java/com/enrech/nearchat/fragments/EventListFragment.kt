package com.enrech.nearchat.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager

import com.enrech.nearchat.R
import com.enrech.nearchat.interfaces.ModifyNavigationBarFromFragments
import kotlinx.android.synthetic.main.fragment_event_list.*

//Este fragment contiene la interfaz y parte de la funcionalidad de la lista de eventos cercanos
class EventListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_event_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickOnScreenListener()
    }

    private fun setClickOnScreenListener(){
        eventListRootContainer.setOnTouchListener { _, _ ->
            hideSoftKeyboard(activity as Activity)
            false
        }
    }

    //Esta función oculta el teclado al apretar fuera de los límites del textEdit
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

}
