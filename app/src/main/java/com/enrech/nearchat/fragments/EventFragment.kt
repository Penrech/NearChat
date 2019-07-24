package com.enrech.nearchat.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager

import com.enrech.nearchat.R
import com.enrech.nearchat.interfaces.NotifyInteractionEventTab
import com.enrech.nearchat.interfaces.NotifyTopFragmentChange

//Este fragment gestiona la UI y las diferentes funcionalidades dentro de un evento
class EventFragment : Fragment() {

    //Variables

    private val topFragmentNumber = 1

    var currentFragment: Fragment? = null

    var loadingFragment: LoadingFragment? = null

    var pagerFragment: EventPagerFragment? = null

    private var notifyInteractionEventTab: NotifyInteractionEventTab? = null

    //Listeners

    private var changeTabListener: NotifyTopFragmentChange? = null

    private var backStackListening = false

    private var backStackChangeListener = object : FragmentManager.OnBackStackChangedListener {
        override fun onBackStackChanged() {
            currentFragment = childFragmentManager.findFragmentById(R.id.MainEventFragmentContainer)
        }
    }

    //Métodos de lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("EVENTLOG","Creo Evento principal")
        initFragments()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        addBackStackChangeListener()

        if (currentFragment == null || currentFragment is EventPagerFragment ) {
            loadFragment(pagerFragment)
        }

        return inflater.inflate(R.layout.fragment_event, container, false)
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
        }
        if (context is NotifyInteractionEventTab) {
            notifyInteractionEventTab = context
        }
        else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        changeTabListener = null
        notifyInteractionEventTab = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i("EVENTLOG","Guardo instancia")
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

    //Métodos fragments

    //Este método inicializa los fragments principales de para esta funcionalidad para tener su estado guardado
    private fun initFragments(){
        loadingFragment = LoadingFragment()
        pagerFragment = EventPagerFragment()
    }

    //Esta función es la cargada de introducir los fragments en el contenedor del fragment destinado a ello
    fun loadFragment(fragment: Fragment?) : Boolean {

        if (fragment != null && currentFragment != fragment) {
                val transaction = childFragmentManager.beginTransaction()

                transaction.replace(R.id.MainEventFragmentContainer, fragment)
                if (fragment !is EventPagerFragment) {
                    transaction.addToBackStack(null)
                }

                transaction.commit()

                currentFragment = fragment

            return true
        }

        return false
    }

}
