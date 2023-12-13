package com.dual.pro.one.dualprotocolone.base

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseActivity<VM : ViewModel, DB : ViewDataBinding> : AppCompatActivity() {
    protected lateinit var binding: DB
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, getLayoutRes())
        binding.lifecycleOwner = this
        init()
    }
    protected val viewModel: VM by lazy {
        ViewModelProvider(this).get(getViewModelClass())
    }
    abstract fun getLayoutRes(): Int
    abstract fun getViewModelClass(): Class<VM>
    abstract fun init()
    fun launchActivity(destinationActivity: Class<*>) {
        val intent = Intent(this, destinationActivity)
        startActivity(intent)
    }

    fun launchActivityWithExtras(destinationActivity: Class<*>, extras: Bundle) {
        val intent = Intent(this, destinationActivity)
        intent.putExtras(extras)
        startActivity(intent)
    }
}

abstract class BaseViewModel : ViewModel() {
    // Add common ViewModel functionality here

}
