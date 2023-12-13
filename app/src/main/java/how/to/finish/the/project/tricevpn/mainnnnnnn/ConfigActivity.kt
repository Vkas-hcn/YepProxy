package how.to.finish.the.project.tricevpn.mainnnnnnn

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import how.to.finish.the.project.tricevpn.baseeeeee.AppConstant
import how.to.finish.the.project.tricevpn.baseeeeee.BaseAc
import how.to.finish.the.project.tricevpn.baseeeeee.BaseApp
import how.to.finish.the.project.tricevpn.dataaaaaa.SunProfile
import how.to.finish.the.project.tricevpn.dataaaaaa.TriceDataHelper
import how.to.finish.the.project.tricevpn.databinding.ConfigAcLayoutBinding
import how.to.finish.the.project.tricevpn.databinding.SplashAcLayoutBinding

@SuppressLint("CustomSplashScreen")
class ConfigActivity : BaseAc<ConfigAcLayoutBinding>() {

    override val binding: ConfigAcLayoutBinding by lazy {
        ConfigAcLayoutBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val state = intent.getBooleanExtra(AppConstant.IS_CONNECT, false)
        val layoutManager2 = LinearLayoutManager(this)
        val layoutManager1 = LinearLayoutManager(this)
        binding.recentList.layoutManager = layoutManager1
        binding.allConfigList.layoutManager = layoutManager2
        binding.allConfigList.adapter = ConfigAdapter(TriceDataHelper.allLocaleProfiles, state, 1)
        val historyPosList = TriceDataHelper.getHistoryList()
        if (historyPosList.size > 0) {
            val dataList = mutableListOf<SunProfile>()
            historyPosList.forEach {
                if (it.isNotEmpty()) {
                    dataList.add(TriceDataHelper.allLocaleProfiles[it.toInt()])
                }
            }
            binding.recentList.adapter = ConfigAdapter(dataList, state, 2)
            binding.nothingCL.isVisible = false
            binding.recentList.isVisible = true
        } else {
            binding.recentList.isVisible = false
            binding.nothingCL.isVisible = true
        }
        binding.refreshIcon.setOnClickListener {
            refreshList()
        }
        binding.returnIcon.setOnClickListener {
            finish()
        }
    }

    private fun refreshList() {

    }
}