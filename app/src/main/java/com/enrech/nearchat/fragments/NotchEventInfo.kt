package com.enrech.nearchat.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment

import com.enrech.nearchat.R
import com.enrech.nearchat.interfaces.NotifyInteractionEventTab
import com.enrech.nearchat.interfaces.NotifyInteractionHomeTab
import kotlinx.android.synthetic.main.fragment_notch_event_info.*

private const val WILLENTER = "willEnter"
private const val EVENTID = "eventID"

class NotchEventInfo : DialogFragment() {

    //Variables

    private var willEnter: Boolean? = null
    private var eventId: String? = null

    private var notifyInteractionEventTab: NotifyInteractionEventTab? = null

    private var notifyInteractionHomeTab: NotifyInteractionHomeTab? = null

    //Listeners

    private var closeEventInfoListener = View.OnClickListener {
        dismiss()
    }

    private var enterOrExitFromEventListener = View.OnClickListener {
        willEnter?.let {
            if (it) {

            } else {
                notifyInteractionEventTab?.eventClose()
            }
        }
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
            willEnter = it.getBoolean(WILLENTER)
            eventId = it.getString(EVENTID)
        }
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notch_event_info, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpButtons()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NotifyInteractionHomeTab) {
            notifyInteractionHomeTab = context
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
        notifyInteractionHomeTab = null
        notifyInteractionEventTab = null
    }

    private fun setUpButtons(){
        fragmentNotchEventDismissButton.setOnClickListener(closeEventInfoListener)
        fragmentNotchEventLeftEventButton.setOnClickListener(enterOrExitFromEventListener)
    }

    companion object {

        @JvmStatic
        fun newInstance(willEnter: Boolean, eventId: String?) =
            NotchEventInfo().apply {
                arguments = Bundle().apply {
                    putBoolean(WILLENTER, willEnter)
                    putString(EVENTID, eventId)
                }
            }
    }
}
