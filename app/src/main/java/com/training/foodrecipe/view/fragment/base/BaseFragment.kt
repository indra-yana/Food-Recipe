package com.training.foodrecipe.view.fragment.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.training.foodrecipe.repository.BaseRepository
import com.training.foodrecipe.viewmodel.ViewModelFactory

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 02/04/2021 21.29
 * https://gitlab.com/indra-yana
 ****************************************************/

abstract class BaseFragment<VB : ViewBinding, VM : ViewModel, BR : BaseRepository> :
    Fragment(),
    FragmentContract<VB, VM, BR>
{

    private var _viewBinding: VB? = null
    private var _viewModel: VM? = null

    override val viewBinding get() = _viewBinding!!
    override val viewModel get() = _viewModel!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _viewModel = ViewModelProvider(this, ViewModelFactory(getRepository())).get(getViewModel())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _viewBinding = getViewBinding(inflater, container)

        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

}