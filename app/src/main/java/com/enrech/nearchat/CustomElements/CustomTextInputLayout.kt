package com.enrech.nearchat.CustomElements

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.children
import androidx.core.widget.NestedScrollView
import com.enrech.nearchat.R

class CustomTextInputLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0) : androidx.appcompat.widget.LinearLayoutCompat(context,attrs,defStyleAttr)  {

    var label: TextView? = null
    var inputElement: View? = null
    var errorText: CustomErrorTextView? = null
    var helperText: CustomHelperTextView? = null
    var initialHelperTextColor: Int? = null
    var initErrorText: String? = null

    var initialYPosition: Int? = null

    var parentScrollID: Int? = null
    var parentScrollView: NestedScrollView? = null

    var errorAnimation: Animation? = null

    var layoutSet = false

    init {

        attrs?.let {

            val ta = context.obtainStyledAttributes(it, R.styleable.CustomTextInputLayout)

            parentScrollID = ta.getResourceId(R.styleable.CustomTextInputLayout_parent_scrollview,-1)

            ta.recycle()
        }

        errorAnimation = AnimationUtils.loadAnimation(context,R.anim.shake_edit_text)
    }

    var showError: Boolean? = null
    set(value) {
        field = value

        if (value == null) return

        setErrorUI(value)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        setElements()
    }

    private fun setElements(){
        if (layoutSet) return

        label = children.first() as? TextView
        inputElement = children.elementAtOrNull(1)
        errorText = children.firstOrNull { it is CustomErrorTextView } as? CustomErrorTextView
        errorText?.setTextColor(context.getColor(R.color.defaultRed))
        helperText = children.firstOrNull{ it is CustomHelperTextView} as? CustomHelperTextView
        initialHelperTextColor = helperText?.currentTextColor

        getBoundaries()

        layoutSet = true
    }

    private fun changeErrorText(errorText: String?) {
        if (errorText == null) return

        this.errorText?.text = errorText
    }

    private fun setErrorUI(error: Boolean) {
        if (error) {
            if (errorText == null) {
                helperText?.setTextColor(context.getColor(R.color.defaultRed))
            } else {
                helperText?.visibility = View.GONE
                errorText?.visibility = View.VISIBLE
            }

            /*parentScrollView?.let {
                it.smoothScrollTo(it.scrollX,initialYPosition ?: it.scrollY)
            }*/
            scrollToElementPosition()
            inputElement?.startAnimation(errorAnimation)
        } else {
            if (errorText == null) {
                helperText?.setTextColor(initialHelperTextColor ?: context.getColor(R.color.brokenBlack))
            }

            errorText?.visibility = View.GONE
            helperText?.visibility = View.VISIBLE
            changeErrorText(initErrorText)
        }
    }

    private fun getBoundaries(){
        val rootView = this.rootView as? ViewGroup

        if (rootView != null) {
            val rect = Rect()
            getDrawingRect(rect)
            rootView.offsetDescendantRectToMyCoords(this,rect)
            initialYPosition = rect.top

            if (parentScrollID != null && parentScrollID != -1) parentScrollView = rootView.findViewById(parentScrollID!!) ?: null

            Log.i("ROOTVIEW","parentScrollView $parentScrollView")
        }

    }

    private fun scrollToElementPosition(){
        val rect = Rect()
        parentScrollView?.offsetDescendantRectToMyCoords(this,rect)
        Log.i("ROOTVIEW","Position in scrollView: ${rect.top}, scrollView ScrollY: ${parentScrollView?.scrollY}")
    }

}