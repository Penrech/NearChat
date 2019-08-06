package com.enrech.nearchat.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager

import com.enrech.nearchat.R
import com.enrech.nearchat.interfaces.InitActivityInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_init_login.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class InitLoginFragment : Fragment() {

    //Variables

    private var mAuth: FirebaseAuth? = null
    private var firebaseDatabase: FirebaseFirestore? = null
    private var param1: String? = null
    private var param2: String? = null

    private var loginInterface: InitActivityInterface? = null

    enum class LoginError{
        WRONG_MAIL,WRONG_PASSWORD,WRONG_ACCOUNT, UNKNOW_ERROR
    }

    //Listeners

    private var goToRegisterFragment = View.OnClickListener {
            loginInterface?.goToRegister()
    }

    private var loginWithEmailAndPassword = View.OnClickListener {
            checkDataBeforeSend()
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
        mAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseFirestore.getInstance()
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

    private fun checkDataBeforeSend(){
        startLoadingWhileChecking(true)

        val login = loginEmailEditText.text?.toString()
        val password = loginPasswordEditText.text?.toString()
        val loginRealLength = login?.trim()?.length ?: 0
        val passwordRealLength = password?.trim()?.length ?: 0

        restartUI()

        if (login.isNullOrEmpty() || loginRealLength < 1) {
            loginInputLayout.showError(true, "Introduce tu email")
            startLoadingWhileChecking(false)
        } else if (password.isNullOrEmpty() || passwordRealLength < 1) {
            passwordInputLayout.showError(true,"Introduce la contraseña")
            startLoadingWhileChecking(false)
        } else {
            LogInWithMailAndPassword(login,password)
        }
    }

    private fun LogInWithMailAndPassword(email: String, password: String){
        mAuth!!.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loginInterface?.userLoggedProperly()
                } else {
                    try {
                        throw task.exception?.fillInStackTrace()!!
                    } catch (e: FirebaseAuthInvalidUserException){
                        checkDataAferSend(LoginError.WRONG_ACCOUNT)
                    } catch (e: FirebaseAuthInvalidCredentialsException){
                        checkDataAferSend(LoginError.WRONG_PASSWORD)
                    } catch (e: Exception){
                        checkDataAferSend(LoginError.UNKNOW_ERROR)
                    }
                }
            }
    }

    private fun checkDataAferSend(errorType: LoginError){
        when(errorType){
            LoginError.WRONG_ACCOUNT ->{
                loginInputLayout.showError(true,"Email y/o contraseña incorrectos")
            }
            LoginError.WRONG_PASSWORD ->{
                passwordInputLayout.showError(true,"Contraseña incorrecta")
            }
            LoginError.WRONG_MAIL ->{
                loginInputLayout.showError(true,"Email incorrecto")
            }
            else ->{
                loginInterface?.defaultErrorLoading()
            }
        }
        startLoadingWhileChecking(false)
    }

    private fun restartUI(){
        loginInputLayout.showError(false)
        passwordInputLayout.showError(false)
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
