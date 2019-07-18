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

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class UserProfileFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private val topFragmentNumber = 2

    private var currentFragment: Fragment? = null

    var profileDetailsFragment: ProfileDetailsFragment? = null

    private var changeTabListener: NotifyTopFragmentChange? = null

    private var backStackListening = false

    private var backStackChangeListener = object : FragmentManager.OnBackStackChangedListener {
        override fun onBackStackChanged() {
            currentFragment = childFragmentManager.findFragmentById(R.id.MainProfileFragmentContainer)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        initFragments()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        addBackStackChangeListener()

        if (currentFragment == null) {
            Log.i("TIG","es null")
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

    private fun initFragments(){
        profileDetailsFragment = ProfileDetailsFragment()
    }

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


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
