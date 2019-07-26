package com.enrech.nearchat.fragments

import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager

import com.enrech.nearchat.R
import com.enrech.nearchat.interfaces.TypeOfComunication
import kotlinx.android.synthetic.main.fragment_event_chat.*

//Este fragment contiene la interfaz y parte de la funcionalidad del chat público de un evento
class EventChatFragment : Fragment() {

    //Variables

    private var talkDrawable: Drawable? = null
    private var shoutDrawable: Drawable? = null
    private var broadcastDrawable: Drawable? = null

    private var actualComunicationType = TypeOfComunication.TALK

    //Listener

    private fun setClickOnScreenListener(){
        eventChatRootContainer.setOnTouchListener { _, _ ->
            hideSoftKeyboard(activity as Activity)
            false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_event_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDrawables()
        setClickOnScreenListener()
    }

    override fun onResume() {
        super.onResume()
        turnOnListeners()
    }

    override fun onPause() {
        super.onPause()
        turnOffListeners()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            turnOnListeners()
        } else {
            turnOffListeners()
        }
    }

    fun receiveDeepHidePropagation(hide: Boolean) {
        if (hide) {
            turnOffListeners()
        } else {
            turnOnListeners()
        }
    }

    private fun turnOnListeners(){}

    private fun turnOffListeners(){}


    private fun initDrawables(){

        talkDrawable = context?.getDrawable(R.drawable.icon_talk_white)
        shoutDrawable = context?.getDrawable(R.drawable.icon_shout_white)
        broadcastDrawable = context?.getDrawable(R.drawable.icon_broadcast_white)

        changeComunication(actualComunicationType)
    }

    fun getActualCommunicationType(): TypeOfComunication {
        return actualComunicationType
    }

    fun changeComunication(typeOfComunication: TypeOfComunication) {
        when (typeOfComunication) {
            TypeOfComunication.TALK -> {
                actualComunicationType = TypeOfComunication.TALK
                eventChatSendMessage.setImageDrawable(talkDrawable)
            }
            TypeOfComunication.SHOUT -> {
                actualComunicationType = TypeOfComunication.SHOUT
                eventChatSendMessage.setImageDrawable(shoutDrawable)
            }
            TypeOfComunication.BROADCAST ->{
                actualComunicationType = TypeOfComunication.BROADCAST
                eventChatSendMessage.setImageDrawable(broadcastDrawable)
            }
        }
    }

    //Esta función oculta el teclado al apretar fuera de los límites del textEdit
    private fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        activity.currentFocus?.let {
            inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus!!.windowToken, 0
            )
            it.clearFocus()
        }
    }

}
