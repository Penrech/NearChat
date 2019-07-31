package com.enrech.nearchat.fragments

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
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
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.enrech.nearchat.CustomApplication
import com.enrech.nearchat.CustomElements.CustomTimePickerDialog

import com.enrech.nearchat.R
import com.enrech.nearchat.interfaces.ModifyNavigationBarFromFragments
import com.enrech.nearchat.interfaces.NotifyInteractionEventTab
import com.enrech.nearchat.models.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.fragment_add_edit_event.*
import java.util.*
import kotlin.math.round

private const val ISADD = "isAdd"
private const val METERS = "M"
private const val FEET = "Ft"
private const val HELPER_TEXT = "Mínimo %d %s , máximo %d %s"
private const val MIN_RANGE = 100
private const val MAX_RANGE = 1000
private const val LOCATION_REQUEST_CODE = 101

class AddEditEventFragment : Fragment() {

    //Variables

    private var isAdd: Boolean? = null

    private var listener: NotifyInteractionEventTab? = null

    private var bottomNavigationListener: ModifyNavigationBarFromFragments? = null

    private var appBarScrollOffset: Int? = null

    private var appBarIsListening = false

    private var scrollOffset: Int? = null
    
    private var dateTimeModel: EventTimeModel? = null

    private var selectedDate: Date? = null

    private var selectedLatLon: LatLng? = null

    private var googleLocationManager: FusedLocationProviderClient? = null

    //Listener objects

    private var onScrollChangeListener = NestedScrollView.OnScrollChangeListener { view, scrollX, scrollY, oldScrollX, oldScrollY ->
        scrollOffset = scrollY
    }

    private var onAppBarOffsetChangeListener = AppBarLayout.OnOffsetChangedListener { appBarLayout, offset ->
        appBarScrollOffset = offset
    }

    private var closeWithoutSaveButtonListener = View.OnClickListener {
        listener?.eventPropagateBackButton()
    }

    private var closeSaveButtonListener = View.OnClickListener {
        //listener?.eventPropagateBackButton()
        getCurrentPositionAndCalculateDistance()
    }

    private var enableCapacityLimit = object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
            editEventCapacityEditText.isEnabled = p1
        }
    }

    private var openLocationFragmentToGetLocation = View.OnClickListener {
        listener?.eventOpenGetLocationEventClick(null)
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

        requestPermission()
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


    //métodos

    private fun getCurrentPositionAndCalculateDistance(){
        val permiso = ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permiso == PackageManager.PERMISSION_GRANTED){
            googleLocationManager?.let { location ->
                location.locationAvailability.addOnCompleteListener {
                    if (it.isSuccessful) {
                        location.lastLocation.addOnCompleteListener { locationTask ->
                            val currentLocation = locationTask.result
                            if (currentLocation != null) {
                                val localizacionUsuario = LatLng(currentLocation.latitude,currentLocation.longitude)
                                if (calculateDistance(localizacionUsuario)) {
                                    checkDataBeforeSave()
                                } else {
                                    (activity?.application as? CustomApplication)?.showMessage("No es posible crear un evento sin estar dentro de su rango")
                                }
                            } else {
                                (activity?.application as? CustomApplication)?.showMessage("No se ha podido determinar la ubicación")
                            }
                        }
                    } else {
                        (activity?.application as? CustomApplication)?.showMessage("La ubicación está desactivada")
                    }
                }
            }
        } else {
            (activity?.application as? CustomApplication)?.showMessage("No se ha permitido la ubicación")
        }
    }

    private fun calculateDistance(currentLocation: LatLng): Boolean {
        val userLocation = Location("")
        userLocation.latitude = currentLocation.latitude
        userLocation.longitude = currentLocation.longitude

        if (selectedLatLon == null ||
            editEventRangeEditText.text == null ||
            (editEventRangeEditText.text!!.toString().toInt() < calculateRange(true ) ||
                    editEventRangeEditText.text!!.toString().toInt() > calculateRange(false ))) return true

        val eventLocation = Location("")
        eventLocation.latitude = selectedLatLon!!.latitude
        eventLocation.longitude = selectedLatLon!!.longitude

        val rangeDistance = editEventRangeEditText.text.toString().toInt()

        return userLocation.distanceTo(eventLocation) < rangeDistance

    }

    private fun requestPermission(){
        val permiso = ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permiso == PackageManager.PERMISSION_GRANTED){
            if (googleLocationManager == null) googleLocationManager = FusedLocationProviderClient(activity!!)
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    (activity?.application as? CustomApplication)?.showMessage("No se ha permitido la ubicación")
                }
                else{
                    requestPermission()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

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
            selectedDate = Date(calendar.timeInMillis)

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

    private fun checkDataBeforeSave(){
        val eventName = editEventNameEditText.text?.toString()
        val eventDate = selectedDate
        val eventRange = editEventRangeEditText.text?.toString()
        val eventPosition = selectedLatLon
        val eventMaxPeopleEnabled = editEventEnableCapacityLimit.isChecked
        val eventMaxPeople = editEventCapacityEditText.text?.toString()

        editEventNameLayout.showError(false)
        editEventDateLayout.showError(false)
        editRangeLayout.showError(false)
        editEventLocationLayout.showError(false)
        eventMaxPeopleLayout.showError(false)

        if (eventName == null || eventName.length < 4) {
            editEventNameLayout.showError(true,"El nombre debe tener mínimo 4 caracteres")
        } else if (eventDate == null) {
            editEventDateLayout.showError(true,"Es necesaria una fecha de finalización")
        } else if (eventDate.after(Date(Calendar.getInstance().timeInMillis))) {
            editEventDateLayout.showError(true,"La fecha de finalización debe ser superior a la fecha y hora actual")
        } else if (eventRange == null){
            editRangeLayout.showError(true, "Es necesario especificar el rango de acción del evento")
        } else if (eventRange.toInt() < calculateRange(true) || eventRange.toInt() > calculateRange(false))  {
            editRangeLayout.showError(true)
        } else if (eventPosition == null) {
            editEventLocationLayout.showError(true, "Es necesario posicionar el evento en una localización")
        } else{
            //Enviar al servidor
            if (isAdd!!) {
                val maxUserActive = if (eventMaxPeopleEnabled && eventMaxPeople != null && eventMaxPeople.toIntOrNull() != null) eventMaxPeople.toInt() else null

                val newEvent = Event.newInstance("currentUser","eventId",eventName,eventRange.toInt(),eventPosition,eventDate,
                    null,null,maxUserActive)
                //...
            } else {

            }
        }

    }

    private fun calculateRange(calculateMin: Boolean): Int{
        if (calculateMin){
            return if (editEventMeassureChangeButton.text == FEET) convertMetersToFeet(MIN_RANGE) else MIN_RANGE
        } else {
            return if (editEventMeassureChangeButton.text == FEET) convertMetersToFeet(MAX_RANGE) else MAX_RANGE
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
