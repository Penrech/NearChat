package com.enrech.nearchat.fragments

import android.content.Context
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

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

    fun showSearchToolbar(show: Boolean){
        eventListSearchEventToolbar.visibility = if (show) View.VISIBLE else View.GONE
    }

}
