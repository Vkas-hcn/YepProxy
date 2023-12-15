package how.to.finish.the.project.tricevpn.view.ui

import android.util.Log
import android.view.KeyEvent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import how.to.finish.the.project.tricevpn.base.BaseActivity
import how.to.finish.the.project.tricevpn.R
import how.to.finish.the.project.tricevpn.databinding.ActivityServiceListBinding
import how.to.finish.the.project.tricevpn.hlep.DataUtils
import how.to.finish.the.project.tricevpn.hlep.ServiceBean
import how.to.finish.the.project.tricevpn.hlep.ServiceData
import how.to.finish.the.project.tricevpn.view.adapter.ServiceAdapter
import how.to.finish.the.project.tricevpn.view.model.ServiceViewModel

class ServiceActivity : BaseActivity<ServiceViewModel, ActivityServiceListBinding>() {

    override fun getLayoutRes(): Int = R.layout.activity_service_list

    override fun getViewModelClass(): Class<ServiceViewModel> = ServiceViewModel::class.java


    override fun init() {

        binding.imageView.setOnClickListener { viewModel.returnToHomePage(this) }
        binding.aivRefley.setOnClickListener { viewModel.refresh(this) }
        val bundle = intent.extras
        viewModel.checkSkServiceBean = ServiceBean()
        viewModel.whetherToConnect = bundle?.getBoolean(DataUtils.whetherYepConnected) == true
        viewModel.checkSkServiceBean = Gson().fromJson(
            bundle?.getString(DataUtils.currentYepService),
            object : TypeToken<ServiceBean?>() {}.type
        )
        viewModel.checkSkServiceBeanClick = viewModel.checkSkServiceBean
        initRecentlyAdapter()
        initAllAdapter()
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
                        Log.e(DataUtils.TAG, "onItemClick: $index")
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
                Log.e(DataUtils.TAG, "onItemClick: $position")
                viewModel.selectServer(this@ServiceActivity, position)
            }
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            viewModel.returnToHomePage(this)
        }
        return true
    }
}