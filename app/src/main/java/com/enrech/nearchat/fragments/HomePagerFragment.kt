package com.enrech.nearchat.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.core.view.children
import androidx.viewpager.widget.ViewPager

import com.enrech.nearchat.R
import com.enrech.nearchat.adapters.DefaultPagerAdapter
import com.enrech.nearchat.utils.ToolbarAnimationManager
import kotlinx.android.synthetic.main.fragment_home_pager.*
import kotlinx.android.synthetic.main.fragment_home_pager.view.*
import kotlinx.android.synthetic.main.fragment_home_pager.view.LinerLayoutHomeToolbar
import kotlin.math.max

//Este fragment se encarga de las pager que conforman el mapa y la lista eventos cercanos
class HomePagerFragment : Fragment() , ViewPager.OnPageChangeListener{

    //Variables

    private var mPager: ViewPager? = null
    private var pagerAdapter: DefaultPagerAdapter? = null
    private var pagerFragments: ArrayList<Fragment> = arrayListOf()

    private var homeMapFragment: HomeMapFragment? = null
    private var homeListFragment: EventListFragment? = null

    private var toolbarElementsData = ArrayList<Any>()

    private var toolbarAnimationUtils: ToolbarAnimationManager? = null

    private var pagerIsListening = false

    var currentPage : Int? = null

    //Listeners

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
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

    //Métodos lifeCycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initPagerFragments()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPager = view.homeFragmentsPager

        setUpToolbar()

        setUpPager()

        listenPagerEvents()

        setUpButtons()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        deleteListenerPagerEvents()
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
        homeMapFragment = HomeMapFragment()
        homeListFragment = EventListFragment()
        pagerFragments.add(homeMapFragment!!)
        pagerFragments.add(homeListFragment!!)
    }

    //Estos dos métodos sirven para volver a la página anterior del pager en caso de presionar el botón atrás
    fun getActualPage(): Int {
        return mPager?.currentItem ?: 0
    }

    fun setPagerPageBackwards() {
        val currentItem = mPager?.currentItem ?: 0
        mPager?.currentItem = currentItem - 1
    }

    //Métodos custom toolbar

    //Este método inicializa la clase
    /**
     * @see toolbarAnimationUtils
     * */
    //Que ese encarga de la animcación dinámica de los elementos del toolbar
    private fun setUpToolbar(){
        toolbarAnimationUtils = ToolbarAnimationManager(activity)

        LinerLayoutHomeToolbar.children.forEach {
            toolbarElementsData.add(it)
        }


        toolbarAnimationUtils?.setToolbarElementsData(toolbarElementsData)

    }

    //Este método inicializa los listener de los diferentes botones del toolbar
    private fun setUpButtons(){
        changeBetweenMapAndListButton.setOnClickListener(changePageButtonListener)
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
