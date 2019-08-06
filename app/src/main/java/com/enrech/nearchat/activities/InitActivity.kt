package com.enrech.nearchat.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.enrech.nearchat.R
import com.enrech.nearchat.fragments.InitLoginFragment
import com.enrech.nearchat.fragments.InitRegisterFragment
import com.enrech.nearchat.fragments.LoadingRegistrationOrLogin
import com.enrech.nearchat.interfaces.InitActivityInterface
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_init.*

class InitActivity : AppCompatActivity(), InitActivityInterface {

    //Variables

    private var loginFragment: InitLoginFragment? = null
    private var registerFragment: InitRegisterFragment? = null

    private var currentFragment: Fragment? = null

    private var enableBack = true

    //Listeners

    private var backStackListening = false

    private var backStackChangeListener = object : FragmentManager.OnBackStackChangedListener {
        override fun onBackStackChanged() {
            currentFragment = supportFragmentManager.findFragmentById(R.id.RootInitActivityContainer)
            changeKeyboardInputType(currentFragment!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)

        initTopFragments()
        isUserLogged()
    }

    override fun onPause() {
        removeBackStackChangeListener()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        addBackStackChangeListener()
    }

    //Este método inicializa los cuatro fragments principales de la aplicación, y se guarda siempre una instacia de ellos
    //para mantener de forma sencilla su instancia aun cambiando de tab y volviendo nuevamente a ella
    private fun initTopFragments(){
        loginFragment = InitLoginFragment()
        registerFragment = InitRegisterFragment()
    }

    private fun isUserLogged(){
        val mAuth = FirebaseAuth.getInstance()
        val isLogged = mAuth?.currentUser != null
        if (isLogged) {
            loadMainActivity()
        } else {
            loadFragment(loginFragment,false)
        }
    }

    //Esta función es la cargada de introducir los fragments en el contenedor de la actividad destinado a ello
    private fun loadFragment(fragment: Fragment?, shouldAddToBackStack: Boolean) : Boolean {

        if (fragment != null && currentFragment != fragment) {
            val transaction = supportFragmentManager.beginTransaction()

            changeKeyboardInputType(fragment)

            if (shouldAddToBackStack) {
                transaction.addToBackStack(null)
            }

            transaction.replace(R.id.RootInitActivityContainer, fragment)
            transaction.setPrimaryNavigationFragment(fragment)
            transaction.commit()

            currentFragment = fragment

            return true
        }

        return false
    }

    private fun changeKeyboardInputType(fragment: Fragment){
        if (fragment == loginFragment) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        } else {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }

    private fun addBackStackChangeListener(){
        if (!backStackListening) {
            supportFragmentManager.addOnBackStackChangedListener(backStackChangeListener)
            backStackListening = true
        }
    }

    private fun removeBackStackChangeListener(){
        if (backStackListening) {
            supportFragmentManager.removeOnBackStackChangedListener(backStackChangeListener)
            backStackListening = false
        }
    }

    private fun loadMainActivity(){
        val intent = Intent(this,RootActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
        overridePendingTransition(0, 0)
    }

    override fun userLoggedProperly() {
        loadFragment(LoadingRegistrationOrLogin(),true)
        loadMainActivity()
    }

    override fun goToRegister() {
        loadFragment(registerFragment,true)
    }

    override fun goBackToLogin() {
        onBackPressed()
    }

    override fun loadingEnableBack(enable: Boolean) {
        enableBack = enable
    }

    override fun onBackPressed() {
        if (enableBack) super.onBackPressed()
        else { }
    }

    override fun defaultErrorLoading() {
        showMessage("Error iniciando sesión")
    }

    fun showMessage(message: String) {
        val snack = Snackbar.make(RootInitActivityContainer,message, Snackbar.LENGTH_LONG)
        snack.show()
    }
}
