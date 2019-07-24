package com.enrech.nearchat.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.enrech.nearchat.R
import com.enrech.nearchat.interfaces.InitActivityInterface
import kotlinx.android.synthetic.main.fragment_init_register.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class InitRegisterFragment : Fragment() {

    //Variables

    private var param1: String? = null
    private var param2: String? = null

    private var loginInterface: InitActivityInterface? = null

    //Listeners

    private var backToLoginFragment = View.OnClickListener {
        loginInterface?.goBackToLogin()
    }

    private var registerUser = View.OnClickListener {
        if (checkIfRegisterDateIsCorrect()) {
            loginInterface?.userLoggedProperly()
        }
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
        return inflater.inflate(R.layout.fragment_init_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpButtons()
    }

    private fun setUpButtons(){
        registerUserBackButton.setOnClickListener(backToLoginFragment)
        registerRegisterButton.setOnClickListener(registerUser)
        registerUserToolbarButton.setOnClickListener(registerUser)
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

    private fun checkIfRegisterDateIsCorrect(): Boolean{
        startLoadingWhileCheking(true)
        return true
    }

    private fun startLoadingWhileCheking(start: Boolean) {
        if (start) {
            registerUserToolbarButton.isEnabled = false
            registerRegisterButton.visibility = View.INVISIBLE
            registerRegisterButton.isEnabled = false
            registerLoadingProgressBar.visibility = View.VISIBLE
        } else {
            registerUserToolbarButton.isEnabled = true
            registerLoadingProgressBar.visibility = View.GONE
            registerRegisterButton.visibility = View.VISIBLE
            registerRegisterButton.isEnabled = true

        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InitRegisterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
