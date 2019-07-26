package com.enrech.nearchat.fragments

import android.content.Context
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

//Este fragment es el encargado de mostrar la información del perfil de usuario
class ProfileDetailsFragment : Fragment() {

    //Variables

    private var listener: NotifyInteractionUserProfile? = null

    private var appBarScrollOffset: Int? = null

    private var appBarIsListening = false

    private var scrollOffset: Int? = null

    //Listeners

    private var openEditProfileButtonListener = View.OnClickListener {
        listener?.profileOpenEditUserClick(true)
    }

    private var openEditAccessButtonListener = View.OnClickListener {
        listener?.profileOpenEditAccessClick()
    }

    private var onAppBarOffsetChangeListener = AppBarLayout.OnOffsetChangedListener { appBarLayout, offset ->
        Log.i("TIG","Profile scroll offset $offset")
        appBarScrollOffset = offset
    }

    private var onScrollChangeListener = NestedScrollView.OnScrollChangeListener { view, scrollX, scrollY, oldScrollX, oldScrollY ->
        scrollOffset = scrollY
    }

    //Métodos lifeCycle

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

    //Métodos

    //Este método inicializa los listener de los diferentes botones del toolbar
    private fun setUpButtons(){
        editUserProfileButton.setOnClickListener(openEditProfileButtonListener)
        editUserAccessButton.setOnClickListener(openEditAccessButtonListener)
    }

    //Las siguientes 3 funciones mantienen el estado del scroll actual del fragment en caso de cambiar y volver de una pestaña a otra
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

}
