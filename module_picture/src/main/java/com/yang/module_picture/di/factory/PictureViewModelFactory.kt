package com.yang.module_picture.di.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yang.module_picture.repository.PictureRepository
import java.lang.reflect.InvocationTargetException


class PictureViewModelFactory(private val application: Application, private val pictureRepository: PictureRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return try {
            val className = modelClass.canonicalName
            val classViewModel = Class.forName(className!!)
            val cons = classViewModel.getConstructor(Application::class.java, PictureRepository::class.java)
            val viewModel = cons.newInstance(application,pictureRepository) as ViewModel
            viewModel as T
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            throw java.lang.IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            throw java.lang.IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        } catch (e: InstantiationException) {
            e.printStackTrace()
            throw java.lang.IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
            throw java.lang.IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
            throw java.lang.IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}