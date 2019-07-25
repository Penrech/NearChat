package com.enrech.nearchat.fragments

import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager

import com.enrech.nearchat.R
import com.enrech.nearchat.interfaces.NotifyTopFragmentChange

//Este fragment gestiona la UI y las diferentes funcionalidades encargadas de mostrar los eventos cercanos
class HomeFragment : Fragment() {

    //Variables

    private val topFragmentNumber = 0

    var currentFragment: Fragment? = null

    var loadingFragment: LoadingFragment? = null

    var pagerFragment: HomePagerFragment? = null

    private var backStackListening = false

    //Listeners

    private var changeTabListener: NotifyTopFragmentChange? = null

    private var backStackChangeListener = object : FragmentManager.OnBackStackChangedListener {
        override fun onBackStackChanged() {
            currentFragment = childFragmentManager.findFragmentById(R.id.MainHomeFragmentContainer)
        }
    }

    //Métodos lifeCycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initFragments()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        addBackStackChangeListener()

        if (currentFragment == null) {
            loadFragment(pagerFragment)
        }

        return inflater.inflate(R.layout.fragment_home, container, false)
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

    //Métodos fragment

    //Este método inicializa los fragments principales de para esta funcionalidad para tener su estado guardado
    private fun initFragments(){
        loadingFragment = LoadingFragment()
        pagerFragment = HomePagerFragment()
    }

    //Esta función es la cargada de introducir los fragments en el contenedor del fragment destinado a ello
    private fun loadFragment(fragment: Fragment?) : Boolean {

        if (fragment != null && currentFragment != fragment) {

            val transaction = childFragmentManager.beginTransaction()

            transaction.replace(R.id.MainHomeFragmentContainer, fragment)
            if (fragment !is HomePagerFragment) {
                transaction.addToBackStack(null)
            }

            transaction.commit()

            currentFragment = fragment


            return true
        }

        return false
    }


}
