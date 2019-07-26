package com.enrech.nearchat.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.enrech.nearchat.R
import com.enrech.nearchat.interfaces.NotifyInteractionEventTab
import com.enrech.nearchat.models.EventHelper
import kotlinx.android.synthetic.main.fragment_event_helper.*

private const val EVENT_TYPE = "eventType"

class EventHelperFragment : Fragment() {

    //Variables

    private var event_type: Int? = null
    private var currentHelperType: EventHelper.TypeOFHelper? = EventHelper.TypeOFHelper.NOEVENT

    private var allHelpers: ArrayList<EventHelper> = arrayListOf()

    private var notifyInteractionEventTab: NotifyInteractionEventTab? = null

    //Listener

    private var onClickOnCloseButton = View.OnClickListener {
        backToDefaultState()
    }

    private var onClickOnExitEnterButton = View.OnClickListener {
        when (currentHelperType) {
            EventHelper.TypeOFHelper.NOEVENT -> {
                notifyInteractionEventTab?.eventOpenAddEditEventClick(true)
            }
            EventHelper.TypeOFHelper.EVENTEND -> {
                backToDefaultState()
            }
            EventHelper.TypeOFHelper.TOOFAR -> {

            }
            EventHelper.TypeOFHelper.CANTENTER -> {

            }
            EventHelper.TypeOFHelper.LOADINGEVENT -> {}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            event_type = it.getInt(EVENT_TYPE)
            event_type?.let {type ->
                currentHelperType = EventHelper.TypeOFHelper.fromInt(type)
            }
        }

        initAllHelpers()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_event_helper, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUI()
        setUpButtons()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NotifyInteractionEventTab) {
            notifyInteractionEventTab = context
        }
        else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        notifyInteractionEventTab = null
    }

    private fun initAllHelpers(){
        allHelpers.add(EventHelper(EventHelper.TypeOFHelper.NOEVENT,getString(R.string.no_event_description),
            getString(R.string.no_event_button_label), context?.getDrawable(R.drawable.icon_event_helper_normal), false))
        allHelpers.add(EventHelper(EventHelper.TypeOFHelper.EVENTEND,
            getString(R.string.event_end_description),getString(R.string.event_end_button_label), context?.getDrawable(R.drawable.icon_event_helper_end), true))
        allHelpers.add(EventHelper(EventHelper.TypeOFHelper.LOADINGEVENT,
            getString(R.string.loading_event_description),"",context?.getDrawable(R.drawable.icon_event_helper_normal),true))
        allHelpers.add(EventHelper(EventHelper.TypeOFHelper.CANTENTER,
            getString(R.string.cant_enter_description),getString(R.string.cant_enter_button_label),context?.getDrawable(R.drawable.icon_event_helper_distance),true))
        allHelpers.add(EventHelper(EventHelper.TypeOFHelper.TOOFAR,
            getString(R.string.too_far_description),getString(R.string.too_far_button_label),context?.getDrawable(R.drawable.icon_event_helper_distance),true))
    }

    private fun setUpButtons(){
        eventHelperActionButton.setOnClickListener(onClickOnExitEnterButton)
        eventHelperCloseButton.setOnClickListener(onClickOnCloseButton)
    }

    private fun setUI(){
        val currentHelper = allHelpers.first { it.type == currentHelperType }

        eventHelperCloseButton.visibility = if (currentHelper.hasCloseButton) View.VISIBLE else View.INVISIBLE

        eventHelperDescription.text = currentHelper.description

        if (currentHelper.buttonLabel != null) {
            eventHelperActionButton.visibility = View.VISIBLE
            eventHelperActionButton.text = currentHelper.buttonLabel
        } else {
            eventHelperActionButton.visibility = View.INVISIBLE
            eventHelperActionButton.text = ""
        }

        eventHelperImageView.setImageDrawable(currentHelper.drawable)
    }

    private fun backToDefaultState(){
        currentHelperType = EventHelper.TypeOFHelper.NOEVENT
        setUI()
    }

    companion object {

        fun newInstance(type: EventHelper.TypeOFHelper) =
            EventHelperFragment().apply {
                arguments = Bundle().apply {
                    putInt(EVENT_TYPE, type.type)
                }
            }
    }
}
