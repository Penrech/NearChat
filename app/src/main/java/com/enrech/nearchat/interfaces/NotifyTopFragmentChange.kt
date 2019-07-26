package com.enrech.nearchat.interfaces

interface NotifyTopFragmentChange {
    fun fragmentLoaded(fragmentTag: String)
    fun fragmentUnLoaded(fragmentTag: String)
}