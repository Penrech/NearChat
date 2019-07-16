package com.enrech.nearchat.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.enrech.nearchat.R
import android.app.Activity
import kotlinx.android.synthetic.main.activity_edit_user_activty.*


class EditUserActivty : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user_activty)

        setClickOnScreenListener()
    }

    private fun setClickOnScreenListener(){
        rootEditProfileLayout.setOnTouchListener { _, _ ->
            hideSoftKeyboard(this)
            false
        }
        editProfileContainer.setOnTouchListener { _, _ ->
            hideSoftKeyboard(this)
            false
        }
    }

    fun saveUserProfile(view: View) {

    }

    fun closeEditUserProfile(view: View) {
        finish()
    }

    fun changeUserProfilePhoto(view: View) {

    }

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
