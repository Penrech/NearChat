package com.enrech.nearchat.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat

import com.enrech.nearchat.R
import com.enrech.nearchat.interfaces.InitActivityInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_init_register.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class InitRegisterFragment : Fragment() {

    //Variables

    private var param1: String? = null
    private var param2: String? = null

    private var mAuth: FirebaseAuth? = null
    private var firebaseDatabase: FirebaseFirestore? = null
    private var usersReference: CollectionReference? = null
    private var usersnamesReference: CollectionReference? = null

    private var passwordVisible = false

    private var loginInterface: InitActivityInterface? = null

    enum class validationButton {
        EMAIL,USERNAME
    }

    enum class RegisterError{
        EXISTING_MAIL, WEAK_PASSWORD, UNKNOW_ERROR
    }

    //Listeners

    private var backToLoginFragment = View.OnClickListener {
        loginInterface?.goBackToLogin()
    }

    private var registerUser = View.OnClickListener {
        if (checkIfRegisterDateIsCorrect()) {
            loginInterface?.userLoggedProperly()
        }
    }

    private var viewPasswordClickListener = View.OnClickListener {
        if (passwordVisible) {
            registerPasswordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            registerPasswordEditText.setSelection(registerPasswordEditText.text.length)
            registerPasswordEditText.typeface = registerEmailEditText.typeface
            registerViewPasswordButton.setImageDrawable(context?.getDrawable(R.drawable.icon_visibility_white))
            passwordVisible = false
        } else {
            registerPasswordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            registerPasswordEditText.setSelection(registerPasswordEditText.text.length)
            registerViewPasswordButton.setImageDrawable(context?.getDrawable(R.drawable.icon_no_visibility_white))
            passwordVisible = true
        }
    }

    private fun setClickOnScreenListener(){
        RegisterAppBar.setOnTouchListener { _, _ ->
            hideSoftKeyboard(activity as Activity)
            false
        }
        registerUserScrollView.setOnTouchListener { _, _ ->
            hideSoftKeyboard(activity as Activity)
            false
        }
    }

    private var onClearFocusListener = View.OnFocusChangeListener { view, haveFocus ->
        if (view.id == R.id.registerEmailEditText) {
            if (!haveFocus) {
                checkEmailLive()
            }
        }
        if (view.id == R.id.registerUsernameEditText) {
            if (!haveFocus) {
                checkUserNameLive()
            }
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
        usersReference = firebaseDatabase!!.collection("users")
        usersnamesReference = firebaseDatabase!!.collection("usernames")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_init_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClickOnScreenListener()
        setUpButtons()
    }

    private fun setUpButtons(){
        registerUserBackButton.setOnClickListener(backToLoginFragment)
        registerRegisterButton.setOnClickListener(registerUser)
        registerUserToolbarButton.setOnClickListener(registerUser)
        registerViewPasswordButton.setOnClickListener(viewPasswordClickListener)
        registerEmailEditText.setOnFocusChangeListener(onClearFocusListener)
        registerUsernameEditText.setOnFocusChangeListener(onClearFocusListener)
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

    private fun checkUserNameLive(){
        val userName = registerUsernameEditText.text?.toString()
        registerUserNameLayout.showError(false)
        changeValidationButtonUI(validationButton.USERNAME,true)

        if (checkUserName(userName)) {
            val query = usersnamesReference!!.document(userName!!)
            query
                .get()
                .addOnSuccessListener { result ->
                    if (result.exists()) {
                        registerUserNameLayout.showError(true,"Username no disponible")
                        changeValidationButtonUI(validationButton.USERNAME,false)
                    }
                }
                .addOnFailureListener { exception ->  
                        Log.i("FirebaseExc","Exception $exception")
                }
        } else {
            registerUserNameLayout.showError(true,"Username debe tener al menos 4 caracteres")
            changeValidationButtonUI(validationButton.USERNAME,false)
        }
    }

    private fun checkUserName(username: String?): Boolean{
        val realLength = username?.trim()

        if (username == null || realLength!!.length < 4) return false

        return true
    }

    private fun checkEmailLive(){
        val email = registerEmailEditText.text?.toString()
        registerEmailLayout.showError(false)
        changeValidationButtonUI(validationButton.EMAIL,true)

        if (checkEmail(email)) {
            val query = usersReference!!.whereEqualTo("email",email)
            query
                .get()
                .addOnSuccessListener { result->
                    if (result.size() > 0){
                        registerEmailLayout.showError(true,"Email ya registrado")
                        changeValidationButtonUI(validationButton.EMAIL,false)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.i("FirebaseExc","Exception $exception")
                }

        } else {
            registerEmailLayout.showError(true,"Email no válido")
            changeValidationButtonUI(validationButton.EMAIL,false)
        }

    }

    private fun checkEmail(email: String?): Boolean{
        return if (email != null) android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() else false
    }

    private fun checkPassword(password: String?) :Boolean {
        return !(password == null || password.trim().length < 8)
    }

    private fun checkDataBeforeSend(){
        startLoadingWhileCheking(true)
        val email = registerEmailEditText.text?.toString()
        val userName = registerUsernameEditText.text?.toString()
        val password = registerPasswordEditText.text?.toString()

        registerEmailLayout.showError(false)
        registerUserNameLayout.showError(false)
        registerPasswordLayout.showError(false)

        if (!checkEmail(email)) {
            registerEmailLayout.showError(true,"Email no válido")
            changeValidationButtonUI(validationButton.EMAIL,false)
            startLoadingWhileCheking(false)
        } else if (!checkUserName(userName)) {
            registerUserNameLayout.showError(true,"Username debe tener al menos 4 caracteres")
            changeValidationButtonUI(validationButton.USERNAME,false)
            startLoadingWhileCheking(false)
        } else if (!checkPassword(password)){
            registerPasswordLayout.showError(true,"La constraseña debe tener al menos 8 caracteres")
            startLoadingWhileCheking(false)
        } else {
            preRegisterWithEmailAndPasswordAndUsername(email!!,password!!,userName!!)
        }
    }

    private fun preRegisterWithEmailAndPasswordAndUsername(email: String, password: String, username: String){
        val query = usersReference!!.whereEqualTo("username",username)
        query
            .get()
            .addOnSuccessListener { result ->
                if (result.size() > 0) {
                    registerUserNameLayout.showError(true,"Username no disponible")
                    changeValidationButtonUI(validationButton.USERNAME,false)
                    startLoadingWhileCheking(false)
                } else {
                    registerWithEmailAndPasswordAndUsername(email,password,username)
                }
            }
            .addOnFailureListener { exception ->
                  loginInterface?.unkwonErrorFirebase()
                startLoadingWhileCheking(false)
            }

    }

    private fun registerWithEmailAndPasswordAndUsername(email: String, password: String, username: String) {
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    loginInterface?.userLoggedProperly()
                } else {
                    try {
                        throw task.exception?.fillInStackTrace()!!
                    } catch (e: FirebaseAuthUserCollisionException){
                        checkDataAfterSend(RegisterError.EXISTING_MAIL)
                    } catch (e: FirebaseAuthWeakPasswordException){
                        checkDataAfterSend(RegisterError.WEAK_PASSWORD)
                    } catch (e: Exception){
                        Log.i("ERROR","Excepcion registrando: ${e.cause} , ${e.message}")
                        checkDataAfterSend(RegisterError.UNKNOW_ERROR)
                    }
                }
            }
    }

    private fun addUserToDBAndSuccessLog(username: String, email: String){
        val documentRef = usersnamesReference!!.document(username)
        firebaseDatabase!!.runTransaction { transaction ->

            val snapshot = transaction.get(documentRef)

            if (snapshot.exists()) {

            } else {

            }
        }
    }

    private fun checkDataAfterSend(errorType: RegisterError){

        when (errorType) {
            RegisterError.EXISTING_MAIL -> {
                registerEmailLayout.showError(true,"Email ya registrado")
                changeValidationButtonUI(validationButton.EMAIL,false)
            }
            RegisterError.WEAK_PASSWORD -> {
                registerPasswordLayout.showError(true, "Contraseña demasiado debil, prueba algo más complejo")
            }
            RegisterError.UNKNOW_ERROR -> {
                loginInterface?.unkwonErrorFirebase()
            }
        }

        startLoadingWhileCheking(false)
    }

    private fun changeValidationButtonUI(validationButton: validationButton, correct: Boolean){
        when (validationButton) {
            InitRegisterFragment.validationButton.EMAIL -> {
                changeValidatorTo(correct,registerValidEmailIndicator)
            }
            InitRegisterFragment.validationButton.USERNAME -> {
                changeValidatorTo(correct,registerValidUserNameIndicator)
            }
        }
    }

    private fun changeValidatorTo(succes: Boolean, validatorElement: ImageView) {
        if (succes) {
            validatorElement.background.setTint(ContextCompat.getColor(context!!,R.color.defaultGreen))
            validatorElement.setImageDrawable(ContextCompat.getDrawable(context!!,R.drawable.icon_done_white))
        } else {
            validatorElement.background.setTint(ContextCompat.getColor(context!!,R.color.defaultRed))
            validatorElement.setImageDrawable(ContextCompat.getDrawable(context!!,R.drawable.icon_cancel_white))
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
