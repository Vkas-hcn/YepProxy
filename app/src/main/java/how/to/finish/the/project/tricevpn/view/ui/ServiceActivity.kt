package how.to.finish.the.project.tricevpn.view.ui

import android.util.Log
import com.dual.pro.one.dualprotocolone.base.BaseActivity
import com.dual.pro.one.dualprotocolone.base.BaseViewModel
import how.to.finish.the.project.tricevpn.R
import how.to.finish.the.project.tricevpn.databinding.ActivityServiceListBinding
import how.to.finish.the.project.tricevpn.hlep.DataUtils
import how.to.finish.the.project.tricevpn.hlep.ServiceBean
import how.to.finish.the.project.tricevpn.hlep.ServiceData
import how.to.finish.the.project.tricevpn.view.adapter.ServiceAdapter

class ServiceActivity : BaseActivity<BaseViewModel, ActivityServiceListBinding>() {

    override fun getLayoutRes(): Int = R.layout.activity_service_list

    override fun getViewModelClass(): Class<BaseViewModel> = BaseViewModel::class.java
    private val adapterAll by lazy { ServiceAdapter(ServiceData.getAllVpnListData()) }
    private val adapterRecently by lazy { ServiceAdapter(ServiceData.getAllVpnListData()) }

    private lateinit var allVpnListData: MutableList<ServiceBean>
    private lateinit var recentlyVpnListData: MutableList<ServiceBean>

    override fun init() {
        initRecentlyAdapter()
        initAllAdapter()
        binding.imageView.setOnClickListener { finish() }
    }
    private fun initRecentlyAdapter(){
        binding.isEmoryData = true
        recentlyVpnListData = ServiceData.findVpnByPos()
        binding.isEmoryData = recentlyVpnListData.size <= 0
        binding.rvRecentlyConnected.adapter = adapterRecently
        binding.rvRecentlyConnected.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        adapterRecently.setOnItemClickListener(object : ServiceAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                Log.e(DataUtils.TAG, "onItemClick: $position")
                allVpnListData.forEachIndexed { index, serviceBean ->
                    if (index == position) {
                        serviceBean.check = true
                    } else {
                        serviceBean.check = false
                    }
                }
                adapterAll.notifyDataSetChanged()
            }
        })
    }
    private fun initAllAdapter(){
        binding.isEmoryData = true
        allVpnListData = ServiceData.getAllVpnListData()
        binding.rvAll.adapter = adapterAll
        binding.rvAll.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        adapterAll.setOnItemClickListener(object : ServiceAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                Log.e(DataUtils.TAG, "onItemClick: $position")
                allVpnListData.forEachIndexed { index, serviceBean ->
                    if (index == position) {
                        serviceBean.check = true
                    } else {
                        serviceBean.check = false
                    }
                }
                adapterAll.notifyDataSetChanged()
            }
        })
    }
}