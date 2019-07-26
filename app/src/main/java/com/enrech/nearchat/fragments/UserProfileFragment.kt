package com.enrech.nearchat.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager

import com.enrech.nearchat.R
import com.enrech.nearchat.interfaces.NotifyTopFragmentChange

//Este fragment gestiona todos los fragments encargados de mostrar o editar información del usuario
class UserProfileFragment : Fragment() {

    //Variables

    private val topFragmentNumber = 2

    var currentFragment: Fragment? = null

    var profileDetailsFragment: ProfileDetailsFragment? = null

    private var changeTabListener: NotifyTopFragmentChange? = null

    private var backStackListening = false

    //Listeners

    private var backStackChangeListener = object : FragmentManager.OnBackStackChangedListener {
        override fun onBackStackChanged() {
            currentFragment = childFragmentManager.findFragmentById(R.id.MainProfileFragmentContainer)
        }
    }

    //Métodos lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("FRAGMENTLOADER","FRAGMENT $this")
        initFragments()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        addBackStackChangeListener()

        if (currentFragment == null || currentFragment is ProfileDetailsFragment) {
            loadFragment(profileDetailsFragment)
        }

        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeTabListener?.tabChangeTo(topFragmentNumber)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        removeBackStackChangeListener()
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

    //Métodos

    //Estos dos métodos añaden o eliminan el listener encargado de gestionar que fragmento se está visualizando al
    //Usar el boton de navegación hacia atras
    private fun addBackStackChangeListener(){
        if (!backStackListening) {
            childFragmentManager.addOnBackStackChangedListener(backStackChangeListener)
            backStackListening = true
        }
    }

    private fun removeBackStackChangeListener(){
        if (backStackListening) {
            childFragmentManager.removeOnBackStackChangedListener(backStackChangeListener)
            backStackListening = false
        }
    }

    //métodos fragment

    //Este método inicializa los fragments principales de para esta funcionalidad para tener su estado guardado
    private fun initFragments(){
        profileDetailsFragment = ProfileDetailsFragment()
    }

    //Esta función es la cargada de introducir los fragments en el contenedor del fragment destinado a ello
    fun loadFragment(fragment: Fragment?) : Boolean {

        if (fragment != null && currentFragment != fragment) {
            val transaction = childFragmentManager.beginTransaction()

            transaction.replace(R.id.MainProfileFragmentContainer, fragment)
            if (fragment !is ProfileDetailsFragment) {
                transaction.addToBackStack(null)
            }

            transaction.commit()

            currentFragment = fragment

            return true
        }

        return false
    }

}
