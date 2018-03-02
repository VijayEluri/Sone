package net.pterodactylus.sone.utils

fun <T> T?.asList() = this?.let(::listOf) ?: emptyList<T>()
val Any?.unit get() = Unit
