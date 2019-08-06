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
    var helperErrorTextView: CustomHelperTextView? = null
    var hasHelper = false
    var initialHelperTextColor: Int? = null
    var initialHelperText: String? = null

    var ScrollRect: Rect? = null

    var parentScrollID: Int? = null
    var parentScrollView: NestedScrollView? = null

    var errorAnimation: Animation? = null

    var layoutSet = false

    init {

        attrs?.let {

            val ta = context.obtainStyledAttributes(it, R.styleable.CustomTextInputLayout)

            parentScrollID = ta.getResourceId(R.styleable.CustomTextInputLayout_parent_scrollview,-1)

            hasHelper = ta.getBoolean(R.styleable.CustomTextInputLayout_hasHelper, false)

            ta.recycle()
        }

        errorAnimation = AnimationUtils.loadAnimation(context,R.anim.shake_edit_text)
    }

    fun showError(show: Boolean, errorMessage: String? = null) {
        if (show) {
            helperErrorTextView?.setTextColor(context.getColor(R.color.defaultRed))

            if (errorMessage != null) helperErrorTextView?.text = errorMessage

            if (!hasHelper) helperErrorTextView?.visibility = View.VISIBLE

            scrollToElementPosition()
            inputElement?.startAnimation(errorAnimation)
        } else {
            helperErrorTextView?.setTextColor(initialHelperTextColor ?: context.getColor(R.color.defaultWhite))

            if (!hasHelper) {
                helperErrorTextView?.visibility = View.GONE
                helperErrorTextView?.text = ""
            }
            else helperErrorTextView?.text = initialHelperText

        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        setElements()
    }

    private fun setElements(){
        if (layoutSet) return

        label = children.first() as? TextView
        inputElement = if (label == null) children.elementAtOrNull(0) else children.elementAtOrNull(1)

        helperErrorTextView = children.firstOrNull{ it is CustomHelperTextView} as? CustomHelperTextView
        initialHelperTextColor = helperErrorTextView?.currentTextColor

        if (hasHelper) {
            helperErrorTextView?.visibility = View.VISIBLE
            initialHelperText = helperErrorTextView?.text as String
        } else {
            helperErrorTextView?.visibility = View.GONE
        }

        getBoundaries()

        layoutSet = true
    }


    private fun getBoundaries(){
        val rootView = this.rootView as? ViewGroup

        if (rootView != null) {

            if (parentScrollID != null && parentScrollID != -1) parentScrollView = rootView.findViewById(parentScrollID!!) ?: null

            parentScrollView?.let {
                val rect = Rect()
                getDrawingRect(rect)

                rootView.offsetDescendantRectToMyCoords(it,rect)
                ScrollRect = rect
            }
        }
    }

    private fun scrollToElementPosition() {
        if (parentScrollView == null) return

        val rectElement = Rect()
        val rectScrollview = Rect()
        parentScrollView!!.offsetDescendantRectToMyCoords(this, rectElement)
        parentScrollView!!.getLocalVisibleRect(rectScrollview)

        val elementTop = rectElement.top
        val elementBottom = elementTop + this.height

        val scrollViewScroll = parentScrollView!!.scrollY
        val scrollViewBottom = rectScrollview.bottom

        if (scrollViewScroll > elementTop) {
            parentScrollView!!.smoothScrollTo(0, elementTop)
        }

        if (elementBottom > scrollViewBottom) {
            parentScrollView!!.smoothScrollTo(0, scrollViewBottom)
        }

    }
}