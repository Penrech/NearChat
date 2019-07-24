package com.enrech.nearchat.interfaces

interface InitActivityInterface {
    fun userLoggedProperly()
    fun goToRegister()
    fun goBackToLogin()
    fun loadingEnableBack(enable: Boolean)
}