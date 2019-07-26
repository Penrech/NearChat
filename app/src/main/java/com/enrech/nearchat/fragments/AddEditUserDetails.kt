package com.enrech.nearchat.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.NestedScrollView

import com.enrech.nearchat.R
import com.enrech.nearchat.interfaces.ModifyNavigationBarFromFragments
import com.enrech.nearchat.interfaces.NotifyInteractionUserProfile
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_add_edit_user_details.*
import kotlinx.android.synthetic.main.fragment_add_edit_user_details.editUserProfileCloseButton
import kotlinx.android.synthetic.main.fragment_add_edit_user_details.saveChangesEditProfileToolbarButton

private const val ISADD = "isAdd"

//Este fragment es el encargado de mostrar la ui y gestionar la edición o adición de datos del perfil del usuario, es
//reutilizable para un caso u otro y su estado se determina con la constante
/**
 * @see ISADD
 * */
class AddEditUserDetails : Fragment() {

    //Variables

    private var isAdd: Boolean? = null

    private var listener: NotifyInteractionUserProfile? = null

    private var bottomNavigationListener: ModifyNavigationBarFromFragments? = null

    private var appBarScrollOffset: Int? = null

    private var appBarIsListening = false

    private var scrollOffset: Int? = null

    //Listener objects
    
    private var onScrollChangeListener = NestedScrollView.OnScrollChangeListener { view, scrollX, scrollY, oldScrollX, oldScrollY ->
        scrollOffset = scrollY
    }

    private var onAppBarOffsetChangeListener = AppBarLayout.OnOffsetChangedListener { appBarLayout, offset ->
        appBarScrollOffset = offset
    }

    private var closeWithoutSaveButtonListener = View.OnClickListener {
            listener?.profilePropagateBackButton()
    }

    private var closeSaveButtonListener = View.OnClickListener {
         listener?.profilePropagateBackButton()
    }

    private fun setClickOnScreenListener(){
        rootAddEditProfileLayout.setOnTouchListener { _, _ ->
            hideSoftKeyboard(activity as Activity)
            false
        }
        AddEditProfileContainer.setOnTouchListener { _, _ ->
            hideSoftKeyboard(activity as Activity)
            false
        }
    }

    //Métodos lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isAdd = it.getBoolean(ISADD)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_edit_user_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUIIfAdd()
        setUpButtons()
        setClickOnScreenListener()
        setScrollListener()
        addAppBarListener()

    }

    override fun onDestroyView() {
        super.onDestroyView()

        removeAppBarListener()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NotifyInteractionUserProfile) {
            listener = context
        }
        if (context is ModifyNavigationBarFromFragments){
            bottomNavigationListener = context
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

    //métodos

    //Este método cambia la interfaz en función de si se usa el fragment para editar o para añadir datos de nuevo usuario
    private fun setUIIfAdd(){
        isAdd?.let {
            if (it) {
                toolbarEditProfileTitle.text = "Nuevo Usuario"
                changePhotoButton.text = "Añadir foto de perfil"
            }
        }
    }

    //Este método inicializa los listeners de los botones
    private fun setUpButtons(){
        saveChangesEditProfileToolbarButton.setOnClickListener(closeSaveButtonListener)
        editUserProfileCloseButton.setOnClickListener(closeWithoutSaveButtonListener)
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

    //Las siguientes 3 funciones mantienen el estado del scroll actual del fragment en caso de cambiar y volver de una pestaña a otra
    private fun setScrollListener(){

        appBarScrollOffset?.let {
            AddEditProfileAppBar.top = it
        }

        scrollOffset?.let {
            AddEditScrollView.scrollY = it
        }

        AddEditScrollView.setOnScrollChangeListener(onScrollChangeListener)
    }

    private fun addAppBarListener(){
        if (!appBarIsListening)
        {
            AddEditProfileAppBar.addOnOffsetChangedListener(onAppBarOffsetChangeListener)
            appBarIsListening = true
        }
    }

    private fun removeAppBarListener(){
        if (appBarIsListening)
        {
            AddEditProfileAppBar.removeOnOffsetChangedListener(onAppBarOffsetChangeListener)
            appBarIsListening = false
        }
    }

    //Este objeto permite inicializar el fragment en un estado u otro, en este caso en modo edit o modo add
    companion object {

        @JvmStatic
        fun newInstance(isAdd: Boolean = false) =
            AddEditUserDetails().apply {
                arguments = Bundle().apply {
                    putBoolean(ISADD,isAdd)
                }
            }
    }
}
