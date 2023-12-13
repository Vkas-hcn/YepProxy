package how.to.finish.the.project.tricevpn.view.ui

import android.view.KeyEvent
import androidx.lifecycle.lifecycleScope
import com.dual.pro.one.dualprotocolone.base.BaseActivity
import how.to.finish.the.project.tricevpn.view.model.MainViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.VpnService
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import de.blinkt.openvpn.api.ExternalOpenVPNService
import de.blinkt.openvpn.api.IOpenVPNAPIService
import de.blinkt.openvpn.api.IOpenVPNStatusCallback
import how.to.finish.the.project.tricevpn.R
import how.to.finish.the.project.tricevpn.databinding.ActivityMainBinding
import how.to.finish.the.project.tricevpn.hlep.DataUtils.TAG
import how.to.finish.the.project.tricevpn.hlep.ServiceData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    override fun getLayoutRes(): Int = R.layout.activity_main

    override fun getViewModelClass(): Class<MainViewModel> = MainViewModel::class.java
    private lateinit var requestPermissionForResultVPN: ActivityResultLauncher<Intent?>

    override fun init() {
        mainClick()
        initVpnSetting()
    }
    private fun mainClick(){
        binding.showGuide = true
        binding.viewBg.setOnClickListener {
        }
        binding.atvPp.setOnClickListener {
            launchActivity(PpWebActivity::class.java)
        }
        binding.atvUpdate.setOnClickListener {
            viewModel.updateUrl(this)
        }
        binding.atvShare.setOnClickListener {
            viewModel.shareUrl(this)
        }
        binding.imgMenu.setOnClickListener {
            binding.dlMain.open()
        }
        binding.llAuto.setOnClickListener {
            binding.agreement = 0
        }
        binding.llSs.setOnClickListener {
            binding.agreement = 1
        }
        binding.llOpen.setOnClickListener {
            binding.agreement = 2
        }
        binding.llService.setOnClickListener {
            launchActivity(ServiceActivity::class.java)
        }
    }

    fun toClickConnect(v: View){
        mService?.let {
            step2(it)
        }
        lifecycleScope.launch {
            binding.showGuide = false
            viewModel.rotateImageViewInfinite(binding.imageView3, 800)
            binding.vpnState = 1
            delay(2000)
            binding.vpnState = 2
            viewModel.stopRotation(binding.imageView3)
        }
    }
    fun step2(server: IOpenVPNAPIService): Job? {
        if (checkVPNPermission(this)) {
            val job = MainScope().launch(Dispatchers.IO) {
                    val data = ServiceData.getAllVpnListData()[0]
                    runCatching {
                        val conf = assets.open("fast_trice.ovpn")
                        val br = BufferedReader(InputStreamReader(conf))
                        val config = StringBuilder()
                        var line: String?
                        while (true) {
                            line = br.readLine()
                            if (line == null) break
                            if (line.contains("remote 222", true)) {
                                line = "remote ${data.ip} ${data.port}"
                            } else if (line.contains("wrongpassword", true)) {
                                line = data.password
                            } else if (line.contains("cipher AES-256-GCM", true)) {
                                line = "cipher ${data.agreement}"
                            }
                            config.append(line).append("\n")
                        }
                        br.close()
                        conf.close()
                        Log.e(TAG, "step2: =${config}")
                        server.startVPN(config.toString())

                    }.onFailure {
                    }
            }
            return job
        } else {
            VpnService.prepare(this).let {
                requestPermissionForResultVPN.launch(it)
            }
        }
        return null
    }

    var mService: IOpenVPNAPIService? = null
    fun initVpnSetting() {
        bindService(
            Intent(this, ExternalOpenVPNService::class.java),
            mConnection,
            BIND_AUTO_CREATE
        )
        requestPermissionForResultVPN =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                requestPermissionForResult(it)
            }
    }
    private fun requestPermissionForResult(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            mService?.let { it1 -> step2(it1) }
            Log.e(TAG, "requestPermissionForResult: 1", )
        } else {
//            openServerState.postValue(OpenServiceState.DISCONNECTED)
            Log.e(TAG, "requestPermissionForResult: 2", )
        }
    }
    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            className: ComponentName?,
            service: IBinder?,
        ) {
            mService = IOpenVPNAPIService.Stub.asInterface(service)
            try {
                mService?.registerStatusCallback(mCallback)
                Log.e("open vpn mService", "mService onServiceConnected")
            } catch (e: Exception) {
                Log.e("open vpn error", e.message.toString())
            }
        }

        override fun onServiceDisconnected(className: ComponentName?) {
            Log.e("open vpn mService", "mService onServiceDisconnected")
            mService = null
        }
    }
    private val mCallback = object : IOpenVPNStatusCallback.Stub() {
        override fun newStatus(uuid: String?, state: String?, message: String?, level: String?) {
            // NOPROCESS 未连接 // CONNECTED 已连接
            // RECONNECTING 尝试重新链接 // EXITING 连接中主动掉用断开
            Log.e(TAG, "newStatus: state=$state;message=$message", )
            when (state) {
                "CONNECTED" -> {

                }

                "RECONNECTING" -> {
                }

                "NOPROCESS" -> {

                }


                else -> {}
            }

        }

    }
    private fun checkVPNPermission(activity: MainActivity): Boolean {
        VpnService.prepare(activity).let {
            return it == null
        }
    }
    private fun isAppOnline(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        if (networkCapabilities != null) {
            return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }
        return false
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (binding.showGuide==true) {
                binding.showGuide = false
            } else {
//                if (!(lavViewOg.isAnimating && MainFun.statusAtTheTimeOfClick == BaseService.State.Stopped.name)) {
//                    finish()
//                }
                finish()
            }
        }
        return true
    }

}