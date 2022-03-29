package com.yang.module_mine.di.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yang.module_mine.repository.MineRepository
import java.lang.reflect.InvocationTargetException


class MineViewModelFactory(private val application: Application, private val mineRepository: MineRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return try {
            val className = modelClass.canonicalName
            val classViewModel = Class.forName(className!!)
            val cons = classViewModel.getConstructor(Application::class.java, MineRepository::class.java)
            val viewModel = cons.newInstance(application,mineRepository) as ViewModel
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