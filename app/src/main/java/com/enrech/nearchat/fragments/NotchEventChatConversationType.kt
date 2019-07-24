package com.enrech.nearchat.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment

import com.enrech.nearchat.R
import com.enrech.nearchat.interfaces.NotifyInteractionEventTab
import com.enrech.nearchat.interfaces.TypeOfComunication
import kotlinx.android.synthetic.main.fragment_event_chat.*
import kotlinx.android.synthetic.main.fragment_notch_event_chat_conversation_type.*

private const val COMMUNICATION_TYPE = "ComunicationType"

class NotchEventChatConversationType : DialogFragment() {

    private var communicationType: Int? = null

    private var comunicationTypeNormalized: TypeOfComunication? = null

    private var notifyInteractionEventTab: NotifyInteractionEventTab? = null

    //Listener
    private var radioGroupListener = RadioGroup.OnCheckedChangeListener { radioGroup, i ->

        when (i) {
            notchEventComunicateBroadcast.id -> {
                notifyInteractionEventTab?.eventChangeChatConversationType(TypeOfComunication.BROADCAST)
            }
            notchEventComunicateShouting.id -> {
                notifyInteractionEventTab?.eventChangeChatConversationType(TypeOfComunication.SHOUT)
            }
            notchEventComunicateTalking.id -> {
                notifyInteractionEventTab?.eventChangeChatConversationType(TypeOfComunication.TALK)
            }
        }
    }

    private var closeEventInfoListener = View.OnClickListener {
        dismiss()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dialog?.window?.attributes?.windowAnimations = R.style.dialogAnimation
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            val params = dialog.window?.attributes
            params?.gravity = Gravity.TOP
            dialog.window?.setLayout(width, height)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            communicationType = it.getInt(COMMUNICATION_TYPE)
            communicationType?.let {
                comunicationTypeNormalized = TypeOfComunication.fromInt(communicationType!!)
            }
        }
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notch_event_chat_conversation_type, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        comunicationTypeNormalized?.let {
            checkActualCommunication(comunicationTypeNormalized!!)
        }

        setUpButtons()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NotifyInteractionEventTab) {
            notifyInteractionEventTab = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        notifyInteractionEventTab = null
    }

    private fun setUpButtons(){
        notchEventComunicationRadioGroup.setOnCheckedChangeListener(radioGroupListener)
        fragmentNotchDismissButton.setOnClickListener(closeEventInfoListener)
    }

    private fun checkActualCommunication(typeOfComunication: TypeOfComunication) {
        when (typeOfComunication) {
            TypeOfComunication.TALK -> {
               notchEventComunicationRadioGroup.check(notchEventComunicateTalking.id)
            }
            TypeOfComunication.SHOUT -> {
                notchEventComunicationRadioGroup.check(notchEventComunicateShouting.id)
            }
            TypeOfComunication.BROADCAST ->{
                notchEventComunicationRadioGroup.check(notchEventComunicateBroadcast.id)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(actualTypeOfComunication: TypeOfComunication) =
            NotchEventChatConversationType().apply {
                arguments = Bundle().apply {
                    putInt(COMMUNICATION_TYPE,actualTypeOfComunication.type)
                }
            }
    }
}
