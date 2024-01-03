package com.yep.online.link.prox.connection.view.ui

import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yep.online.link.prox.connection.base.BaseActivity
import com.yep.online.link.prox.connection.R
import com.yep.online.link.prox.connection.databinding.ActivityServiceListBinding
import com.yep.online.link.prox.connection.hlep.DataUtils
import com.yep.online.link.prox.connection.hlep.ServiceBean
import com.yep.online.link.prox.connection.net.CloakUtils
import com.yep.online.link.prox.connection.view.adapter.ServiceAdapter
import com.yep.online.link.prox.connection.view.model.ServiceViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ServiceActivity : BaseActivity<ServiceViewModel, ActivityServiceListBinding>() {

    override fun getLayoutRes(): Int = R.layout.activity_service_list

    override fun getViewModelClass(): Class<ServiceViewModel> = ServiceViewModel::class.java

    private var isEmptyData: Boolean = false
    override fun init() {
        binding.imageView.setOnClickListener { viewModel.returnToHomePage(this) }
        binding.aivRefley.setOnClickListener { viewModel.refresh(this) }
        val bundle = intent.extras
        viewModel.checkSkServiceBean = ServiceBean()
        viewModel.whetherToConnect = bundle?.getBoolean(DataUtils.whetherYepConnected) == true
        isEmptyData = bundle?.getBoolean(DataUtils.ypeVpnEmpty) == true
        if (isEmptyData) {
            binding.isEmoryAllData = true
            return
        } else {
            binding.isEmoryAllData = false
        }
        viewModel.checkSkServiceBean = Gson().fromJson(
            bundle?.getString(DataUtils.currentYepService),
            object : TypeToken<ServiceBean?>() {}.type
        )
        viewModel.checkSkServiceBeanClick = viewModel.checkSkServiceBean
        initRecentlyAdapter()
        initAllAdapter()
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.returnToHomePage(this@ServiceActivity)
            }
        })
    }

    private fun initRecentlyAdapter() {
        viewModel.getsRecently()
        binding.isEmoryData = viewModel.recentlyVpnListData.size <= 0
        binding.rvRecentlyConnected.adapter = viewModel.adapterRecord
        binding.rvRecentlyConnected.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this)
        viewModel.adapterRecord.setOnItemClickListener(object :
            ServiceAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                viewModel.allVpnListData.forEachIndexed { index, _ ->
                    if (viewModel.recentlyVpnListData[position].ip == viewModel.allVpnListData[index].ip) {
                        viewModel.selectServer(this@ServiceActivity, index)

                    }
                }
            }
        })
    }

    private fun initAllAdapter() {
        viewModel.getAllServer()
        binding.rvAll.adapter = viewModel.adapterAll
        binding.rvAll.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        viewModel.adapterAll.setOnItemClickListener(object : ServiceAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                viewModel.selectServer(this@ServiceActivity, position)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            delay(200)
            if (lifecycle.currentState != Lifecycle.State.RESUMED) {
                return@launch
            }
            CloakUtils.putPointYep("listview", this@ServiceActivity)
        }
    }
}