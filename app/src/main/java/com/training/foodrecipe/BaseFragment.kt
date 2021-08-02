package com.training.foodrecipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.training.foodrecipe.datasource.remote.RecipeApiClient
import com.training.foodrecipe.repository.BaseRepository
import com.training.foodrecipe.viewmodel.BaseViewModel
import com.training.foodrecipe.viewmodel.ViewModelFactory

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 02/04/2021 21.29
 * https://gitlab.com/indra-yana
 ****************************************************/
abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel, BR : BaseRepository> : Fragment() {

    protected lateinit var viewBinding: VB
    protected lateinit var viewModel: VM
    protected val apiClient = RecipeApiClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        viewBinding = getViewBinding(inflater, container)
        viewModel = ViewModelProvider(this, ViewModelFactory(getRepository())).get(getViewModel())

        return viewBinding.root
    }

    abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB
    abstract fun getViewModel(): Class<VM>
    abstract fun getRepository(): BR

}