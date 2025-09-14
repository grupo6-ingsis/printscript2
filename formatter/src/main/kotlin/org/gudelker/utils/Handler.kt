package org.gudelker.utils

interface Handler {
    fun matches(): Boolean

    fun handle(): String
}
