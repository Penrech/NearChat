package com.enrech.nearchat.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.ViewPager

import com.enrech.nearchat.R
import com.enrech.nearchat.adapters.DefaultPagerAdapter
import com.enrech.nearchat.interfaces.ModifyNavigationBarFromFragments
import com.enrech.nearchat.interfaces.NotifyInteractionEventTab
import com.enrech.nearchat.interfaces.TypeOfComunication
import com.enrech.nearchat.utils.ToolbarAnimationManager
import kotlinx.android.synthetic.main.fragment_even_pager.*
import kotlinx.android.synthetic.main.fragment_even_pager.view.*

private const val INFO_NOTCH = "EventInfoNotch"
private const val VISUAL_NOTCH = "EventVisualizationNotch"
private const val USER_NOTCH = "UserNotch"
private const val CONVERSATION_NOTCH = "EventChatConversation"

//Este fragment se encarga de las pager que conforman el mapa y la lista de chat del evento
class EventPagerFragment : Fragment() , ViewPager.OnPageChangeListener{

    //Variables

    private var mPager: ViewPager? = null
    private var pagerAdapter: DefaultPagerAdapter? = null
    private var pagerFragments: ArrayList<Fragment> = arrayListOf()

    private var eventMapFragment: EventMapFragment? = null
    private var eventListFragment: EventChatFragment? = null

    private var toolbarElementsData = ArrayList<Any>()

    private var toolbarAnimationUtils: ToolbarAnimationManager? = null

    private var bottomNavigationListener: ModifyNavigationBarFromFragments? = null

    private var notifyInteractionEventTab: NotifyInteractionEventTab? = null

    private var pagerIsListening = false

    var currentPage : Int? = null

    var isScrolling: Boolean = false

    //Listeners

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        isScrolling = positionOffset != 0.0f

        if (position == 0) {
            bottomNavigationListener?.slideWithScrollView(positionOffsetPixels)
        }

        run{
            toolbarAnimationUtils?.changeIconsDinamically(position,positionOffset)
        }
    }

    override fun onPageSelected(position: Int) {
        currentPage = position
    }

    private var changePageButtonListener = View.OnClickListener {
        if (mPager?.currentItem == 0) {
            mPager?.currentItem = 1
        } else {
            mPager?.currentItem = 0
        }
    }

    private var openEventInfoDialog = View.OnClickListener {
       loadDialogFragment(INFO_NOTCH,NotchEventInfo.newInstance(false,null))
    }

    private var openEventVisualizationDialog = View.OnClickListener {
       loadDialogFragment(VISUAL_NOTCH,NotchEventChatVisualization())
    }

    private var openComunicationOrPositionUser = View.OnClickListener {
        if (mPager?.currentItem == 0) {

        } else {
            val actualCommunication = eventListFragment!!.getActualCommunicationType()
            loadDialogFragment(CONVERSATION_NOTCH,NotchEventChatConversationType.newInstance(actualCommunication))

        }
    }

    //Métodos lifeCycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initPagerFragments()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_even_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPager = view.EventFragmentsPager

        setUpToolbar()

        setUpPager()

        setUpButtons()

        notifyInteractionEventTab?.eventPagerLoadedWithCurrentItem(mPager!!.currentItem)

    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ModifyNavigationBarFromFragments){
            bottomNavigationListener = context
        }
        if (context is NotifyInteractionEventTab) {
            notifyInteractionEventTab = context
        }
        else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onResume() {
        super.onResume()
        startListenersOnFragmentVisibleOrInResume()
    }

    override fun onPause() {
        super.onPause()
        stopListenersOnFragmentNotVisibleOrInPause()
    }

    override fun onDetach() {
        super.onDetach()
        bottomNavigationListener = null
        notifyInteractionEventTab = null
    }

    fun receiveHidePropagation(hide: Boolean) {
        if (hide) {
            onPause()
        } else {
            notifyInteractionEventTab?.eventPagerLoadedWithCurrentItem(mPager!!.currentItem)
            onResume()
        }

        propageDarkDeeperHideEvent(hide)
    }

    private fun startListenersOnFragmentVisibleOrInResume(){
        listenPagerEvents()
    }

    private fun stopListenersOnFragmentNotVisibleOrInPause(){
        deleteListenerPagerEvents()
    }

    private fun propageDarkDeeperHideEvent(hide: Boolean){
        when(mPager!!.currentItem) {
            0 -> {
                eventMapFragment?.receiveDeepHidePropagation(hide)
            }
            1 -> {
                eventListFragment?.receiveDeepHidePropagation(hide)
            }
        }
    }

    //Métodos

    //Métodos pager

    //Esta función inicializa el pager
    private fun setUpPager(){

        pagerAdapter = DefaultPagerAdapter(childFragmentManager,pagerFragments)

        mPager?.adapter = pagerAdapter

        currentPage?.let {
            mPager?.currentItem = it
        }

        setToolbarUIAfterPageChange()

    }

    //Esta función inicia, si es necesario, el listener
    //encargado de escuchar los cambios entre las páginas del view pager
    private fun listenPagerEvents(){
        if (!pagerIsListening) {
            mPager?.addOnPageChangeListener(this)
            pagerIsListening = true
        }
    }

    //Esta función elimina, si es necesario, el listener
    //encargado de escuchar los cambios entre las páginas del view pager
    private fun deleteListenerPagerEvents(){
        if (pagerIsListening) {
            mPager?.removeOnPageChangeListener(this)
            pagerIsListening = false
        }
    }

    //métodos fragment

    //Este método inicializa los fragments principales de para esta funcionalidad para tener su estado guardado
    private fun initPagerFragments(){
        eventMapFragment = EventMapFragment()
        eventListFragment = EventChatFragment()
        pagerFragments.add(eventMapFragment!!)
        pagerFragments.add(eventListFragment!!)
    }

    fun changeComunicationTypeFromChat(typeOfComunication: TypeOfComunication) {
        eventListFragment?.changeComunication(typeOfComunication)
    }

    //Estos dos métodos sirven para volver a la página anterior del pager en caso de presionar el botón atrás
    fun getActualPage(): Int {
        return mPager?.currentItem ?: 0
    }

    fun setPagerPageBackwards() {
        val currentItem = mPager?.currentItem ?: 0
        mPager?.currentItem = currentItem - 1
    }

    private fun isFragmentInTheBackStack(): Boolean {
        for (index in 0 until childFragmentManager.backStackEntryCount) {

            if (childFragmentManager.getBackStackEntryAt(index).name.equals(INFO_NOTCH,true)) {
                return true
            }
            if (childFragmentManager.getBackStackEntryAt(index).name.equals(VISUAL_NOTCH,true)) {
                return true
            }
            if (childFragmentManager.getBackStackEntryAt(index).name.equals(USER_NOTCH,true)) {
                return true
            }
            if (childFragmentManager.getBackStackEntryAt(index).name.equals(CONVERSATION_NOTCH,true)) {
                return true
            }

        }
        return false
    }

    private fun loadDialogFragment(fragmentTag: String, dialogFragment: DialogFragment) {
        if (!isScrolling) {
            if (!isFragmentInTheBackStack()) {
                val transaction = childFragmentManager.beginTransaction()
                val prev = childFragmentManager.findFragmentByTag(fragmentTag)
                if (prev != null) {
                    transaction.remove(prev)
                }
                transaction.addToBackStack(fragmentTag)

                dialogFragment.show(transaction, fragmentTag)
            }
        }
    }

    //Métodos custom toolbar

    //Este método inicializa la clase
    /**
     * @see toolbarAnimationUtils
     * */
    //Que ese encarga de la animcación dinámica de los elementos del toolbar
    private fun setUpToolbar(){

        toolbarAnimationUtils = ToolbarAnimationManager(activity)

        LinerLayoutEventToolbar.children.forEach {
            toolbarElementsData.add(it)
        }

        toolbarAnimationUtils?.setToolbarElementsData(toolbarElementsData)

    }

    //Este método inicializa los listener de los diferentes botones del toolbar
    private fun setUpButtons(){
        changeBetweenMapAndChatButton.setOnClickListener(changePageButtonListener)
        showEventInfoButton.setOnClickListener(openEventInfoDialog)
        showHideConversationsButton.setOnClickListener(openEventVisualizationDialog)
        showMyPositionButton.setOnClickListener(openComunicationOrPositionUser)
    }

    //La función de este método es evitar que la interfaz del toolbar sea erronea respecto a su página actual
    //Como resultado de un cambio en el tab bar mientras se esté realizando un cambio de página
    private fun setToolbarUIAfterPageChange(){
        val currentPage = mPager?.currentItem

        when (currentPage) {
            0 -> {
                run {
                    toolbarAnimationUtils?.changeIconsDinamically(0 ,0.0f)
                }
            }
            1-> {
                run {
                    toolbarAnimationUtils?.changeIconsDinamically(0 ,1.0f)
                }
            }
        }
    }

}
