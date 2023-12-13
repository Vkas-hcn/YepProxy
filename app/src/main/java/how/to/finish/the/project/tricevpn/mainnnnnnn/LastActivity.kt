package how.to.finish.the.project.tricevpn.mainnnnnnn

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import how.to.finish.the.project.tricevpn.R
import how.to.finish.the.project.tricevpn.baseeeeee.AppConstant
import how.to.finish.the.project.tricevpn.baseeeeee.BaseAc
import how.to.finish.the.project.tricevpn.dataaaaaa.SunProfile
import how.to.finish.the.project.tricevpn.dataaaaaa.TriceDataHelper
import how.to.finish.the.project.tricevpn.databinding.ConfigAcLayoutBinding
import how.to.finish.the.project.tricevpn.databinding.LastAcLayoutBinding
import how.to.finish.the.project.tricevpn.databinding.SplashAcLayoutBinding
import how.to.finish.the.project.tricevpn.uitlllll.ImageUtils

@SuppressLint("CustomSplashScreen")
class LastActivity : BaseAc<LastAcLayoutBinding>() {

    override val binding: LastAcLayoutBinding by lazy {
        LastAcLayoutBinding.inflate(
            layoutInflater
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val state = intent.getBooleanExtra(AppConstant.IS_CONNECT, false)
        setInforData()
        if (state) {
            binding.lastImage.setImageResource(R.mipmap.last_ok)
            binding.lastText.text = "Connection successful!"
        } else {
            binding.lastImage.setImageResource(R.mipmap.last_no)
            binding.lastText.text = "Disconnected!"
        }

        binding.returnIcon.setOnClickListener {
            jumptoMain()
        }
    }
    fun jumptoMain(){
        if(AppConstant.isResultState){
            TriceDataHelper.cachePosition = -1
            AppConstant.isResultState = false
        }
        finish()
    }

     fun setInforData() {
        val node: SunProfile = if (TriceDataHelper.cachePosition != -1) {

            TriceDataHelper.allLocaleProfiles[TriceDataHelper.cachePosition]
        } else {
            TriceDataHelper.allLocaleProfiles[TriceDataHelper.curPosition]
        }
        setVpnData(node)
    }

    private fun setVpnData(it: SunProfile){
        if (it.isFast) {
            binding.lastInfoName.text = "Fast Server"
            binding.lastInfoImage.setImageResource(ImageUtils.getImage("Fast Server"))
        } else {
            binding.lastInfoName.text = it.counnnn
            binding.lastInfoImage.setImageResource(ImageUtils.getImage(it.counnnn))
        }
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            jumptoMain()
            return false
        }
        return super.onKeyDown(keyCode, event)
    }

}