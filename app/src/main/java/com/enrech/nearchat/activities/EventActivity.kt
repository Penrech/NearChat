package com.enrech.nearchat.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.iterator
import androidx.viewpager.widget.ViewPager
import com.enrech.nearchat.R
import com.enrech.nearchat.adapters.DefaultPagerAdapter
import com.enrech.nearchat.fragments.EventChatFragment
import com.enrech.nearchat.fragments.EventListFragment
import com.enrech.nearchat.fragments.EventMapFragment
import com.enrech.nearchat.fragments.HomeMapFragment
import com.enrech.nearchat.models.DynamicAnimatableToolbarElement
import com.enrech.nearchat.navigation.BottomNavigationListener
import com.enrech.nearchat.utils.ToolbarAnimationUtils
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_event.*

class EventActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {

    // VARIABLES Y FUNCIONES DE NAVEGACIÓN

    //Cada una de las actividades principales posee estas dos variables.
    //La primera es un listener de la custom class BottomNavigationListener que gestiona la navegación con el bottom navigation View
    private var navigationListener: BottomNavigationListener? = null
    //La segunda indica que número de indice tiene la actividad en el menu de su correspondiente bottom navigation View
    private val activityNumber = 1

    //Esta función por un lado inicializa el fondo circular de los iconos del bottom navigation View
    //Por otro lado inicializa la variable navigationListener y el listener onNavigationItemSelectedListener del bottom navigation View
    private fun setBottomNavigation(){
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val menuView: BottomNavigationMenuView = navView.getChildAt(0) as BottomNavigationMenuView

        for (i in menuView.iterator()) {
            val itemView = i as BottomNavigationItemView
            val iconView = itemView.getChildAt(0)
            iconView.background = getDrawable(R.drawable.bottom_item_switchable_background)
        }

        navigationListener = BottomNavigationListener(this)

        navView.setOnNavigationItemSelectedListener(navigationListener)
    }

    //Esta función cambia el icono del bottom navigation view al que le corresponde la actividad actual. Cada actividad
    //Tiene su propio bottom navigation view y esta función los "sincroniza" visualmente.
    private fun updateBottomNavigationSelection(){
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val menu = navView.menu
        menu.getItem(activityNumber).isChecked = true
    }

    //Variables y funciones toolbar

    private var eventChatIcon: Drawable? = null
    private var eventMapIcon: Drawable? = null
    private var eventVisibilityIcon: Drawable? = null
    private var eventInfoIcon: Drawable? = null
    private var myPositionIcon: Drawable? = null

    private var blueBackgroundButtonColor: Int? = null
    private var whiteBackgroundButtonColor: Int? = null

    private var toolbarElementsData = ArrayList<DynamicAnimatableToolbarElement>()

    private var toolbarAnimationUtils: ToolbarAnimationUtils? = null

    //Variables y funciones del View Pager

    private lateinit var mPager: ViewPager
    private var pagerAdapter: DefaultPagerAdapter? = null

    private var pagerIsListening = false

    private var mapFragment: EventMapFragment? = null
    private var chatFragment: EventChatFragment? = null

    //Implementación view pager adapter listener

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        run {
            toolbarAnimationUtils?.changeIconsDinamically(position,positionOffset)
        }
    }

    override fun onPageSelected(position: Int) {}

    //Funciones lifecycle de la actividad

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        /**
         * @see setBottomNavigation
         */
        setBottomNavigation()

        setUpToolbar()
        setUpPager()

    }

    override fun onResume() {
        super.onResume()

        /**
         * @see updateBottomNavigationSelection
         */
        updateBottomNavigationSelection()

        listenPagerEvents()

        overridePendingTransition(0,0)
    }

    override fun onPause() {
        super.onPause()

        deleteListenerPagerEvents()
    }

    /**
     *
     * En esta función se inicializa las funcionalidades y las animaciones del toolbar de la actividad
     * parte de esta inicialización se realiza gracias a la clase util
     * @see toolbarAnimationUtils
     * Esta clase incluye una serie de métodos que permiten implementar un cambio de la interfaz gráfica al cambiar
     * de fragment de forma dinámica.
     *
     */

    private fun setUpToolbar(){
        //Se inicializan una serie de drawables de forma global con el activity y la función mutate para evitar futuros problemas de UI.
        //Esto último se realiza porque si no se usa mutate, la cache que guarda getdrawable puede hacer que el recurso obtenido en esta
        //Actividad aparezca en otra de la misma manera. Esto no sería un problema si no fuera porque muchos de estos recursos se tintan
        //Y modifican para afrontar los cambios de interfaz al cambiar de un fragment a otro del pager. Si no se utilizara mutate, un fondo
        //blanco de un icono en esta actividad podría aparecer igual en otra donde no le corresponde.
        eventChatIcon = getDrawable(R.drawable.icon_list_white)?.mutate()
        eventMapIcon = getDrawable(R.drawable.icon_position_pin_white)?.mutate()
        myPositionIcon = getDrawable(R.drawable.icon_position_white)?.mutate()
        eventVisibilityIcon = getDrawable(R.drawable.icon_visibility_white)?.mutate()
        eventInfoIcon = getDrawable(R.drawable.icon_info_white)?.mutate()

        blueBackgroundButtonColor = getColor(R.color.defaultBlue)
        whiteBackgroundButtonColor = Color.WHITE

        toolbarAnimationUtils = ToolbarAnimationUtils(this,
            blueBackgroundButtonColor!!,
            whiteBackgroundButtonColor!!,
            resources.displayMetrics)

        val toolbarItemHeight = resources.getDimension(R.dimen.custom_toolbar_item_height)
        val textParameters = toolbarAnimationUtils?.getToolbarTitleParameters(3,0,toolbarItemHeight)
        Log.i("TOG","Event TitleText params: $textParameters")
        val changeFragmentButtonParameters = toolbarAnimationUtils?.getToolbarIconParameters(1,toolbarItemHeight)
        val visibilityButtonParameters = toolbarAnimationUtils?.getToolbarIconParameters(2,toolbarItemHeight)
        val infoButtonParameters = toolbarAnimationUtils?.getToolbarIconParameters(3,toolbarItemHeight)
        val myPositionButtonParameters = toolbarAnimationUtils?.getToolbarIconParameters(4,toolbarItemHeight)

        //Aquí se inicializan las propiedades de cada uno de los elementos del toolbar, ya sea el textView que contiene el titulo como los
        //diversos botones. Está implementación puede parecer tediosa, pero la cantidad de elementos de los toolbar a implentar no es elevada y
        //la realización de esta implementación permite más adelante facilitar y ahora mucho código cuando se realiza la animación.
        val toolbarTitle = DynamicAnimatableToolbarElement(toolbarEventTitle,
            DynamicAnimatableToolbarElement.TypeOfToolbarElements.TITLE,
            textParameters!!.first,
            textParameters.second,
            textParameters.third,
            false,
            true,
            toolbarEventTitle.alpha,
            toolbarEventTitle.elevation,
            toolbarEventTitle.visibility,
            false)

        val changeFragmentButton = DynamicAnimatableToolbarElement(changeBetweenMapAndChatButton,
            DynamicAnimatableToolbarElement.TypeOfToolbarElements.BUTTON,
            changeFragmentButtonParameters!!.first,
            changeFragmentButtonParameters.second,
            changeFragmentButtonParameters.third,
            true,
            false,
            changeBetweenMapAndChatButton.alpha,
            changeBetweenMapAndChatButton.elevation,
            changeBetweenMapAndChatButton.visibility,
            false,
            blueBackgroundButtonColor,
            whiteBackgroundButtonColor,
            eventChatIcon,
            Pair(eventChatIcon!!,eventMapIcon!!)
        )

        val visibilityButton = DynamicAnimatableToolbarElement(showHideConversationsButton,
            DynamicAnimatableToolbarElement.TypeOfToolbarElements.BUTTON,
            visibilityButtonParameters!!.first,
            visibilityButtonParameters.second,
            visibilityButtonParameters.third,
            true,
            false,
            showHideConversationsButton.alpha,
            showHideConversationsButton.elevation,
            showHideConversationsButton.visibility,
            false,
            blueBackgroundButtonColor,
            whiteBackgroundButtonColor,
            eventVisibilityIcon)

        val infoButton = DynamicAnimatableToolbarElement(showEventInfoButton,
            DynamicAnimatableToolbarElement.TypeOfToolbarElements.BUTTON,
            infoButtonParameters!!.first,
            infoButtonParameters.second,
            infoButtonParameters.third,
            true,
            false,
            showEventInfoButton.alpha,
            showEventInfoButton.elevation,
            showEventInfoButton.visibility,
            false,
            blueBackgroundButtonColor,
            whiteBackgroundButtonColor,
            eventInfoIcon)

        val myPositionButton = DynamicAnimatableToolbarElement(showMyPositionButton,
            DynamicAnimatableToolbarElement.TypeOfToolbarElements.BUTTON,
            myPositionButtonParameters!!.first,
            myPositionButtonParameters.second,
            myPositionButtonParameters.third,
            false,
            true,
            showMyPositionButton.alpha,
            showMyPositionButton.elevation,
            showMyPositionButton.visibility,
            false,
            blueBackgroundButtonColor,
            whiteBackgroundButtonColor,
            myPositionIcon)


        toolbarElementsData.add(toolbarTitle)
        toolbarElementsData.add(changeFragmentButton)
        toolbarElementsData.add(visibilityButton)
        toolbarElementsData.add(infoButton)
        toolbarElementsData.add(myPositionButton)

        toolbarAnimationUtils?.setToolbarElementsData(toolbarElementsData)

    }

    //Funciones del Pager

    //Esta función inicializa el pager asi como los dos fragments principales e inicia, si es necesario, el listener
    //encargado de escuchar los cambios entre las páginas del view pager
    private fun setUpPager(){
        mPager = EventFragmentsPager

        mapFragment = EventMapFragment()
        chatFragment = EventChatFragment()

        val listOfFragments = listOf(mapFragment!!,chatFragment!!)

        pagerAdapter = DefaultPagerAdapter(supportFragmentManager,listOfFragments)
        mPager.adapter = pagerAdapter

        listenPagerEvents()
    }

    //Esta función inicia, si es necesario, el listener
    //encargado de escuchar los cambios entre las páginas del view pager
    private fun listenPagerEvents(){
        if (!pagerIsListening) {
            mPager.addOnPageChangeListener(this)
            pagerIsListening = true
        }
    }

    //Esta función elimina, si es necesario, el listener
    //encargado de escuchar los cambios entre las páginas del view pager
    private fun deleteListenerPagerEvents(){
        if (pagerIsListening) {
            mPager.removeOnPageChangeListener(this)
            pagerIsListening = false
        }
    }

    //Esta función está asociada con el botón que permite cambiar de un fragment del pager a otro
    fun changeBetweenPages(view: View) {
        if (mPager.currentItem == 0) {
            mPager.currentItem = 1
        } else {
            mPager.currentItem = 0
        }
    }
}
