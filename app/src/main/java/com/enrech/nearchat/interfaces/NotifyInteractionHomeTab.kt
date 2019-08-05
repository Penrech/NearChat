package com.enrech.nearchat.interfaces

interface NotifyInteractionHomeTab {
    fun homePagerLoadedWithCurrentItem(item: Int)
    fun homeMapInitMap()
    fun checkWhyLastLocationIsNull()
}