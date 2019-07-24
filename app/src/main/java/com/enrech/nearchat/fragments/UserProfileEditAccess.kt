package com.enrech.nearchat.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.enrech.nearchat.R
import com.enrech.nearchat.interfaces.ModifyNavigationBarFromFragments
import com.enrech.nearchat.interfaces.NotifyInteractionEventTab
import kotlinx.android.synthetic.main.fragment_user_profile_edit_access.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class UserProfileEditAccess : Fragment() {

    //Variables

    private var param1: String? = null
    private var param2: String? = null

    private var oldPasswordVisible = false
    private var newPasswordVisible = false

    private var notifyInteractionEventTab: NotifyInteractionEventTab? = null

    private var bottomNavigationListener: ModifyNavigationBarFromFragments? = null

    //Listener

    private var viewNewPasswordClickListener = View.OnClickListener {
        if (newPasswordVisible) {
            editAccessNewPasswordEditText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            newPasswordVisible = false
        } else {
            editAccessNewPasswordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            newPasswordVisible = true
        }
    }

    private var viewOldPasswordClickListener = View.OnClickListener {
        if (oldPasswordVisible) {
            editAccessOldPasswordEditText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            oldPasswordVisible = false
        } else {
            editAccessOldPasswordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            oldPasswordVisible = true
        }
    }

    private var exitSavingListener = View.OnClickListener {
        notifyInteractionEventTab?.eventPropagateBackButton()
    }

    private var exitWithoutSavingListener = View.OnClickListener {
        notifyInteractionEventTab?.eventPropagateBackButton()
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
        return inflater.inflate(R.layout.fragment_user_profile_edit_access, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpButtons()
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NotifyInteractionEventTab) {
            notifyInteractionEventTab = context
        }
        if (context is ModifyNavigationBarFromFragments){
            bottomNavigationListener = context
        }
        else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        notifyInteractionEventTab = null
        bottomNavigationListener = null
    }

    override fun onStop() {
        super.onStop()
        bottomNavigationListener?.showBottomNavigationBar(true)
    }

    override fun onResume() {
        super.onResume()
        bottomNavigationListener?.showBottomNavigationBar(false)
    }

    private fun setUpButtons(){
        editAccessViewActualPasswordButton.setOnClickListener(viewOldPasswordClickListener)
        editAccessViewPasswordButton.setOnClickListener(viewNewPasswordClickListener)
        editAccessCloseButton.setOnClickListener(exitWithoutSavingListener)
        saveChangesEditAccessToolbarButton.setOnClickListener(exitSavingListener)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserProfileEditAccess().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
