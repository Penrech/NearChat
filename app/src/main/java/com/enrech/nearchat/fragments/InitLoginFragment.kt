package com.enrech.nearchat.fragments

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager

import com.enrech.nearchat.R
import com.enrech.nearchat.interfaces.InitActivityInterface
import kotlinx.android.synthetic.main.fragment_init_login.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class InitLoginFragment : Fragment() {

    //Variables

    private var param1: String? = null
    private var param2: String? = null

    private var loginInterface: InitActivityInterface? = null

    //Listeners

    private var goToRegisterFragment = View.OnClickListener {
            loginInterface?.goToRegister()
    }

    private var loginWithEmailAndPassword = View.OnClickListener {
            if (checkIfEmailAndPassAreCorred()) {
                loginInterface?.userLoggedProperly()
            }
    }

    private fun setClickOnScreenListener(){
        rootLoginLayout.setOnTouchListener { _, _ ->
            hideSoftKeyboard(activity as Activity)
            false
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
        return inflater.inflate(R.layout.fragment_init_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClickOnScreenListener()
        setUpButtons()
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

    private fun setUpButtons(){
        loginButton.setOnClickListener(loginWithEmailAndPassword)
        loginGoToRegisterButton.setOnClickListener(goToRegisterFragment)
    }

    private fun checkIfEmailAndPassAreCorred(): Boolean {
        startLoadingWhileChecking(true)
        return true
    }

    private fun startLoadingWhileChecking(start: Boolean) {
        if (start) {
            loginButton.isEnabled = false
            loginButton.visibility = View.INVISIBLE
            loginLoadindProgressBar.visibility = View.VISIBLE
        } else {
            loginButton.isEnabled = true
            loginLoadindProgressBar.visibility = View.INVISIBLE
            loginButton.visibility = View.VISIBLE
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

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InitLoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
