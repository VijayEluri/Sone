package net.pterodactylus.sone.test

import com.google.inject.Module
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.OngoingStubbing

inline fun <reified T : Any> mock(): T = Mockito.mock<T>(T::class.java)!!
inline fun <reified T : Any> mockBuilder(): T = Mockito.mock<T>(T::class.java, Mockito.RETURNS_SELF)!!
inline fun <reified T : Any> deepMock(): T = Mockito.mock<T>(T::class.java, Mockito.RETURNS_DEEP_STUBS)!!
inline fun <reified T : Any> selfMock(): T = Mockito.mock<T>(T::class.java, Mockito.RETURNS_SELF)!!
inline fun <reified T : Any> capture(): ArgumentCaptor<T> = ArgumentCaptor.forClass(T::class.java)

inline fun <reified E: Throwable> OngoingStubbing<*>.doThrow(): OngoingStubbing<*> = thenThrow(E::class.java)

inline fun <reified T : Any> bind(implementation: T): Module =
		Module { it!!.bind(T::class.java).toInstance(implementation) }

inline fun <reified T : Any> bindMock(): Module =
		Module { it!!.bind(T::class.java).toInstance(mock<T>()) }

inline fun <reified T: Any?> whenever(methodCall: T) = Mockito.`when`(methodCall)!!

inline fun <reified T : Any> OngoingStubbing<T>.thenReturnMock(): OngoingStubbing<T> = this.thenReturn(mock<T>())

operator fun <T> InvocationOnMock.get(index: Int): T = getArgument(index)

inline fun <reified T> argumentCaptor(): ArgumentCaptor<T> = ArgumentCaptor.forClass<T, T>(T::class.java)!!
