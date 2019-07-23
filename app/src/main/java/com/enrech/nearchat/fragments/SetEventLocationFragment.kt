package com.enrech.nearchat.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.enrech.nearchat.R
import com.enrech.nearchat.interfaces.ModifyNavigationBarFromFragments
import com.enrech.nearchat.interfaces.NotifyInteractionEventTab
import kotlinx.android.synthetic.main.fragment_set_event_location.*

private const val LAT_LNG = "latLng"

class SetEventLocationFragment : Fragment() {

    private var latLng: String? = null

    private var listener: NotifyInteractionEventTab? = null

    private var bottomNavigationListener: ModifyNavigationBarFromFragments? = null

    private var onCloseWithoutSaveListener = View.OnClickListener {
        listener?.eventPropagateBackButton()
    }

    private var onCloseSavingListener = View.OnClickListener {
        listener?.eventPropagateBackButton()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            latLng = it.getString(LAT_LNG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_set_event_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpButtons()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NotifyInteractionEventTab) {
            listener = context
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
        listener = null
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


    //Este método inicializa los listener de los diferentes botones del toolbar
    private fun setUpButtons(){
        setEventLocationCloseButton.setOnClickListener(onCloseWithoutSaveListener)
        setEventLocationSaveButton.setOnClickListener(onCloseSavingListener)
    }

    companion object {

        @JvmStatic
        fun newInstance(latLng: String) =
            SetEventLocationFragment().apply {
                arguments = Bundle().apply {
                    putString(LAT_LNG, latLng)
                }
            }
    }
}
