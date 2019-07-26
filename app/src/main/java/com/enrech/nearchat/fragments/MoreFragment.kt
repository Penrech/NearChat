package com.enrech.nearchat.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.enrech.nearchat.R
import com.enrech.nearchat.activities.RootActivity
import com.enrech.nearchat.interfaces.NotifyTopFragmentChange

//Este fragment será el encargado de incorporar nuevas funcionalidades en un futuro
class MoreFragment : Fragment() {

    //Variables

    private val topFragmentNumber = 3

    private var changeTabListener: NotifyTopFragmentChange? = null

    //Métodos lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_more, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeTabListener?.fragmentLoaded(RootActivity.TAG_FOURTH)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NotifyTopFragmentChange) {
            changeTabListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        changeTabListener = null
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {

        } else {
            changeTabListener?.fragmentLoaded(RootActivity.TAG_FOURTH)
        }
    }

}
