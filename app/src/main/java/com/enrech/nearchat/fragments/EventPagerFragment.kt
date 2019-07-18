package com.enrech.nearchat.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.viewpager.widget.ViewPager

import com.enrech.nearchat.R
import com.enrech.nearchat.adapters.DefaultPagerAdapter
import com.enrech.nearchat.utils.ToolbarAnimationManager
import kotlinx.android.synthetic.main.fragment_even_pager.*
import kotlinx.android.synthetic.main.fragment_even_pager.view.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class EventPagerFragment : Fragment() , ViewPager.OnPageChangeListener{
    private var param1: String? = null
    private var param2: String? = null
    private var mPager: ViewPager? = null
    private var pagerAdapter: DefaultPagerAdapter? = null
    private var pagerFragments: ArrayList<Fragment> = arrayListOf()

    private var homeMapFragment: EventMapFragment? = null
    private var homeListFragment: EventChatFragment? = null

    private var toolbarElementsData = ArrayList<Any>()

    private var toolbarAnimationUtils: ToolbarAnimationManager? = null

    private var pagerIsListening = false

    private var currentPage : Int? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

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

        listenPagerEvents()

        setUpButtons()

    }

    override fun onDestroyView() {
        super.onDestroyView()

        deleteListenerPagerEvents()
    }

    private fun setUpPager(){

        pagerAdapter = DefaultPagerAdapter(childFragmentManager,pagerFragments)

        mPager?.adapter = pagerAdapter

        currentPage?.let {
            mPager?.currentItem = it
        }

        setToolbarUIAfterPageChange()

    }

    private fun initPagerFragments(){
        homeMapFragment = EventMapFragment()
        homeListFragment = EventChatFragment()
        pagerFragments.add(homeMapFragment!!)
        pagerFragments.add(homeListFragment!!)
    }

    private fun setUpToolbar(){

        toolbarAnimationUtils = ToolbarAnimationManager(activity)

        LinerLayoutEventToolbar.children.forEach {
            toolbarElementsData.add(it)
        }

        toolbarAnimationUtils?.setToolbarElementsData(toolbarElementsData)

    }

    private fun setUpButtons(){
        changeBetweenMapAndChatButton.setOnClickListener(changePageButtonListener)

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

    private fun setToolbarUIAfterPageChange(){
        val currentPage = mPager?.currentItem
        Log.i("TIG","currentPage: $currentPage")

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


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EventPagerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
