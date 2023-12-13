package how.to.finish.the.project.tricevpn.view.ui

import android.util.Log
import com.dual.pro.one.dualprotocolone.base.BaseActivity
import com.dual.pro.one.dualprotocolone.base.BaseViewModel
import com.google.gson.Gson
import how.to.finish.the.project.tricevpn.R
import how.to.finish.the.project.tricevpn.databinding.ActivityStartBinding
import how.to.finish.the.project.tricevpn.hlep.DataUtils.TAG
import how.to.finish.the.project.tricevpn.hlep.ServiceData

class StartActivity : BaseActivity<BaseViewModel, ActivityStartBinding>() {

    override fun getLayoutRes(): Int = R.layout.activity_start

    override fun getViewModelClass(): Class<BaseViewModel> = BaseViewModel::class.java

    override fun init() {

    }

    //倒计时2秒跳转首页
    override fun onResume() {
        super.onResume()
        Thread(Runnable {
            for (i in 0..100) {
                binding.progressBarStart.progress = i
                Thread.sleep(10)
            }
            launchActivity(MainActivity::class.java)
            finish()
        }).start()
    }
}