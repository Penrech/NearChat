package com.enrech.nearchat.CustomElements

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.snackbar.Snackbar

class BottomNavigationBehavior(context: Context, attributeSet: AttributeSet)
    : CoordinatorLayout.Behavior<View>(context, attributeSet){

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return dependency is Snackbar.SnackbarLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {

        val distanceY = getViewOffsetForSnackbar(parent, child)
        child.translationY = - distanceY
        return true
    }

    override fun onDependentViewRemoved(parent: CoordinatorLayout, child: View, dependency: View) {
        val animation = ViewCompat.animate(child).translationY(0f)
        animation.duration = 50
        animation.interpolator = AccelerateDecelerateInterpolator()
        animation.start()
        super.onDependentViewRemoved(parent, child, dependency)
    }

    private fun getViewOffsetForSnackbar(parent: CoordinatorLayout, view: View): Float{
        var maxOffset = 0f
        val dependencies = parent.getDependencies(view)

        dependencies.forEach { dependency ->
            if (dependency is Snackbar.SnackbarLayout && parent.doViewsOverlap(view, dependency)){
                maxOffset = Math.max(maxOffset, (dependency.translationY - dependency.height) * -1)
            }
        }

        return maxOffset
    }
}