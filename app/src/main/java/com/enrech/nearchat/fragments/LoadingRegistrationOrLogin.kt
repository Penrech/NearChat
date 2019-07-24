package com.enrech.nearchat.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.enrech.nearchat.R
import com.enrech.nearchat.interfaces.InitActivityInterface

class LoadingRegistrationOrLogin : Fragment() {

    //Variables

    private var loginInterface: InitActivityInterface? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loading_registration_or_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginInterface?.loadingEnableBack(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        loginInterface?.loadingEnableBack(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is InitActivityInterface){
            loginInterface = context
        }
        else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        loginInterface = null
    }
}
