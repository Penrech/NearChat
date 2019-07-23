package com.enrech.nearchat.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.get
import androidx.core.widget.NestedScrollView
import com.enrech.nearchat.CustomElements.CustomTimePickerDialog

import com.enrech.nearchat.R
import com.enrech.nearchat.interfaces.ModifyNavigationBarFromFragments
import com.enrech.nearchat.interfaces.NotifyInteractionEventTab
import com.enrech.nearchat.interfaces.NotifyInteractionUserProfile
import com.enrech.nearchat.models.EventTimeModel
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_add_edit_event.*
import java.util.*
import kotlin.math.round

private const val ISADD = "isAdd"
private const val METERS = "M"
private const val FEET = "Ft"
private const val HELPER_TEXT = "Mínimo %d %s , máximo %d %s"
private const val MIN_RANGE = 100
private const val MAX_RANGE = 1000

class AddEditEventFragment : Fragment() {

    //Variables

    private var isAdd: Boolean? = null

    private var listener: NotifyInteractionUserProfile? = null

    private var listener2: NotifyInteractionEventTab? = null

    private var bottomNavigationListener: ModifyNavigationBarFromFragments? = null

    private var appBarScrollOffset: Int? = null

    private var appBarIsListening = false

    private var scrollOffset: Int? = null
    
    private var dateTimeModel: EventTimeModel? = null

    //Listener objects

    private var onScrollChangeListener = NestedScrollView.OnScrollChangeListener { view, scrollX, scrollY, oldScrollX, oldScrollY ->
        scrollOffset = scrollY
    }

    private var onAppBarOffsetChangeListener = AppBarLayout.OnOffsetChangedListener { appBarLayout, offset ->
        appBarScrollOffset = offset
    }

    private var closeWithoutSaveButtonListener = View.OnClickListener {
        listener2?.eventPropagateBackButton()
    }

    private var closeSaveButtonListener = View.OnClickListener {
        listener2?.eventPropagateBackButton()
    }

    private var enableCapacityLimit = object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
            editEventCapacityEditText.isEnabled = p1
        }
    }

    private var openLocationFragmentToGetLocation = View.OnClickListener {
        listener2?.eventOpenGetLocationEventClick(true)
    }

    private var openDateTimePickerListener = View.OnClickListener {
        datePicker()
    }

    private var changeUnitsOfMeassure = View.OnClickListener {
        if (editEventMeassureChangeButton.text == METERS) {
            editEventMeassureChangeButton.text = FEET
            val helperText = String.format(HELPER_TEXT,convertMetersToFeet(MIN_RANGE),"pies",convertMetersToFeet(
                MAX_RANGE),"pies")
            editEventHelperRangeLabel.text = helperText
        } else {
            editEventMeassureChangeButton.text = METERS
            val helperText = String.format(HELPER_TEXT, MIN_RANGE,"metros", MAX_RANGE,"metros")
            editEventHelperRangeLabel.text = helperText
        }
    }

    private fun setClickOnScreenListener(){
        rootAddEditEventLayout.setOnTouchListener { _, _ ->
            hideSoftKeyboard(activity as Activity)
            false
        }
        AddEditEventContainer.setOnTouchListener { _, _ ->
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
        return inflater.inflate(R.layout.fragment_add_edit_event, container, false)
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
        if (context is NotifyInteractionEventTab) {
            listener2 = context
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
        listener2 = null
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
                toolbarEditEventTitle.text = "Nuevo Evento"
                changePhotoButton.text = "Añadir foto"
            }
        }
    }

    //Este método inicializa los listeners de los botones
    private fun setUpButtons(){
        saveChangesEditEventToolbarButton.setOnClickListener(closeSaveButtonListener)
        editEventCloseButton.setOnClickListener(closeWithoutSaveButtonListener)
        editEventEnableCapacityLimit.setOnCheckedChangeListener(enableCapacityLimit)
        editEventMeassureChangeButton.setOnClickListener(changeUnitsOfMeassure)
        editEventEndDateEdiText.setOnClickListener(openDateTimePickerListener)
        editEventLocationCardView.setOnClickListener(openLocationFragmentToGetLocation)
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
            AddEditEventAppBar.top = it
        }

        scrollOffset?.let {
            AddEditEventScrollView.scrollY = it
        }

        AddEditEventScrollView.setOnScrollChangeListener(onScrollChangeListener)
    }

    private fun addAppBarListener(){
        if (!appBarIsListening)
        {
            AddEditEventAppBar.addOnOffsetChangedListener(onAppBarOffsetChangeListener)
            appBarIsListening = true
        }
    }

    private fun removeAppBarListener(){
        if (appBarIsListening)
        {
            AddEditEventAppBar.removeOnOffsetChangedListener(onAppBarOffsetChangeListener)
            appBarIsListening = false
        }
    }

    //Utilizo esta función para realizar el cambio de metros a pies
    private fun convertMetersToFeet(number: Int): Int {
        val feetConstant = 3.281
        return round(number.toDouble() * feetConstant).toInt()
    }


    //Métodos date and time picker

    private fun datePicker(){

        val calendar = Calendar.getInstance()

        if (dateTimeModel != null) {
            calendar.timeInMillis = dateTimeModel!!.timeInMillis
        }

        val mYear = calendar.get(Calendar.YEAR)
        val mMonth = calendar.get(Calendar.MONTH)
        val mDay = calendar.get(Calendar.DAY_OF_MONTH)


        if (context == null) return

        val datePickerDialog = DatePickerDialog(context!!,DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->

            timePicker(year,month,day)

        }, mYear, mMonth, mDay)

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000

        datePickerDialog.show()

    }

    private fun timePicker(year: Int, month: Int, day: Int){

        val calendar = Calendar.getInstance()

        val checkCalendar = Calendar.getInstance()

        checkCalendar.set(year,month,day)

        val isCurrentDay = DateUtils.isToday(checkCalendar.timeInMillis)

        if (dateTimeModel != null) {
            calendar.timeInMillis = dateTimeModel!!.timeInMillis
        }

        val mHour = calendar.get(Calendar.HOUR_OF_DAY)
        val mMinute = calendar.get(Calendar.MINUTE)

        if (context == null) return

        val timePickerDialog = CustomTimePickerDialog(context!!,TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->

            calendar.set(year, month, day, hour, minute)
            dateTimeModel = EventTimeModel(getCurrentLocale(context!!),calendar.timeInMillis)
            editEventEndDateEdiText.setText(dateTimeModel!!.getTimeInString())

        }, mHour,mMinute,true,isCurrentDay)


        timePickerDialog.show()

    }

    private fun getCurrentLocale(context: Context) : Locale{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.resources.configuration.locales.get(0)
        } else {
            return context.resources.configuration.locale
        }
    }

    //Este objeto permite inicializar el fragment en un estado u otro, en este caso en modo edit o modo add
    companion object {

        fun newInstance(isAdd: Boolean) =
            AddEditEventFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ISADD, isAdd)
                }
            }
    }
}
