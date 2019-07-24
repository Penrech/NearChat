package com.enrech.nearchat.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.enrech.nearchat.R
import com.enrech.nearchat.fragments.InitLoginFragment
import com.enrech.nearchat.fragments.InitRegisterFragment
import com.enrech.nearchat.fragments.LoadingRegistrationOrLogin
import com.enrech.nearchat.interfaces.InitActivityInterface

class InitActivity : AppCompatActivity(), InitActivityInterface {

    //Variables

    private var loginFragment: InitLoginFragment? = null
    private var registerFragment: InitRegisterFragment? = null

    private var currentFragment: Fragment? = null

    private var enableBack = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)

        initTopFragments()
        isUserLogged()
    }

    //Este método inicializa los cuatro fragments principales de la aplicación, y se guarda siempre una instacia de ellos
    //para mantener de forma sencilla su instancia aun cambiando de tab y volviendo nuevamente a ella
    private fun initTopFragments(){
        loginFragment = InitLoginFragment()
        registerFragment = InitRegisterFragment()
    }

    private fun isUserLogged(){
        val isLogged = true
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

            if (fragment == loginFragment) {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            } else {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            }

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
        else {

        }
    }
}
