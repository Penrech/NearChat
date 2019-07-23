package com.enrech.nearchat.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.DialogFragment

import com.enrech.nearchat.R
import kotlinx.android.synthetic.main.fragment_notch_event_chat_visualization.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class NotchEventChatVisualization : DialogFragment() {

    //Variables

    private var param1: String? = null
    private var param2: String? = null

    //Listeners

    private var enableTalk = object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        }
    }

    private var enableShout = object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        }
    }

    private var enableBroadcast = object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
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
            dialog.setCanceledOnTouchOutside(false)
            dialog.window?.setLayout(width, height)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notch_event_chat_visualization, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpButtons()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()

    }

    private fun setUpButtons(){
        notchEventVisualizationEnableTalk.setOnCheckedChangeListener(enableTalk)
        notchEventVisualizationEnableShout.setOnCheckedChangeListener(enableShout)
        notchEventVisualizationEnableBroadcast.setOnCheckedChangeListener(enableBroadcast)
        fragmentNotchDismissButton.setOnClickListener(closeEventInfoListener)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NotchEventChatVisualization().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
