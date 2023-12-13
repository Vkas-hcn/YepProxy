package how.to.finish.the.project.tricevpn.mainnnnnnn

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.net.VpnService
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Chronometer
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import de.blinkt.openvpn.api.ExternalOpenVPNService
import de.blinkt.openvpn.api.IOpenVPNAPIService
import de.blinkt.openvpn.api.IOpenVPNStatusCallback
import how.to.finish.the.project.tricevpn.R
import how.to.finish.the.project.tricevpn.baseeeeee.AppConstant
import how.to.finish.the.project.tricevpn.baseeeeee.AppConstant.userInterrupt
import how.to.finish.the.project.tricevpn.baseeeeee.BaseAc
import how.to.finish.the.project.tricevpn.baseeeeee.BaseApp
import how.to.finish.the.project.tricevpn.dataaaaaa.SunProfile
import how.to.finish.the.project.tricevpn.dataaaaaa.TriceDataHelper
import how.to.finish.the.project.tricevpn.databinding.MainAcLayoutBinding
import how.to.finish.the.project.tricevpn.uitlllll.IPUtils
import how.to.finish.the.project.tricevpn.uitlllll.ImageUtils
import how.to.finish.the.project.tricevpn.uitlllll.NetworkChangeListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@SuppressLint("CustomSplashScreen")
class MainActivity : BaseAc<MainAcLayoutBinding>() {
    private var isShowGuide = true

    private lateinit var openServerState: ServerState
    private var shadowsocksJob: Job? = null
    private var toAction = false


    private lateinit var requestPermissionForResultVPN: ActivityResultLauncher<Intent?>
    private lateinit var mainToListResultIntent: ActivityResultLauncher<Intent>
    private var curServerState: String? = ""
    private var rotateAnimation: RotateAnimation? = null
    val TAG = "trice"
    override val binding: MainAcLayoutBinding by lazy {
        MainAcLayoutBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        IPUtils().showIpDialog(this)
        openServerState = ServerState.DISCONNECTED
        initAnimateGuide()
        initL()
        initA()
        bindService(
            Intent(this, ExternalOpenVPNService::class.java),
            mConnection,
            BIND_AUTO_CREATE
        )
        requestPermissionForResultVPN =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                requestPermissionForResult(it)
            }
        mainToListResultIntent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == 100) {
                    toConnectStep1()
                }
            }
    }


    private fun initA() {
        rotateAnimation = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnimation?.duration = 2000
        rotateAnimation?.repeatCount = Animation.INFINITE
        rotateAnimation?.fillAfter = true
    }

    override fun onResume() {
        super.onResume()
        val node: SunProfile = if (TriceDataHelper.cachePosition != -1) {
            TriceDataHelper.allLocaleProfiles[TriceDataHelper.cachePosition]
        } else {
            TriceDataHelper.allLocaleProfiles[TriceDataHelper.curPosition]
        }
        setVpnData(node)
    }

    private fun setVpnData(node: SunProfile) {
        Log.e(TAG, "setVpnData: ${node.counnnn}")
        if (node.isFast) {
            binding.mainInfoName.text = "Fast Server"
            binding.mainInfoImage.setImageResource(ImageUtils.getImage("Fast Server"))
        } else {
            binding.mainInfoName.text = node.counnnn
            binding.mainInfoImage.setImageResource(ImageUtils.getImage(node.counnnn))
        }
    }

    override fun onPause() {
        super.onPause()
        if (shadowsocksJob?.isActive == true) {
            shadowsocksJob?.cancel()
            Log.e(TAG, "onPause: openServerState=$openServerState")
            when (openServerState) {
                ServerState.CONNECTING -> {
                    dealResult(ServerState.DISCONNECTED)
                    mService?.disconnect()
                    cancelConnect = true
                }

                ServerState.DISCONNECTING -> {
                    userInterrupt = true
                    dealResult(ServerState.CONNECTED)
                }

                else -> {}
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initL() {
        binding.mainChronometer.onChronometerTickListener =
            Chronometer.OnChronometerTickListener { cArg ->
                val time = System.currentTimeMillis() - cArg.base
                val d = Date(time)
                val sdf = SimpleDateFormat("HH:mm:ss", Locale.US)
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                binding.mainChronometer.text = sdf.format(d)
            }

        binding.mainInfoCl.setOnClickListener {
            if (openServerState == ServerState.CONNECTING) {
                Toast.makeText(this, "Connecting, please try again later.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            val intent = Intent(this, ConfigActivity::class.java)
            intent.putExtra(
                AppConstant.IS_CONNECT,
                openServerState == ServerState.CONNECTED
            )

            mainToListResultIntent.launch(intent)
        }
        binding.connectImage.setOnClickListener {
            toConnectStep1()
        }
        binding.mainConnectImageBg.setOnClickListener {
            toConnectStep1()
        }
        binding.mainSetIcon.setOnClickListener {
            if (openServerState == ServerState.CONNECTING) {
                Toast.makeText(this, "Connecting, please try again later.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            binding.mainDrawer.open()
            stopToConnectOrDisConnect()

        }
        binding.lottieGuide.setOnClickListener {
            toConnectStep1()
        }
        binding.guideMask.setOnClickListener {

        }
        binding.guideMask.setOnTouchListener { _, _ -> return@setOnTouchListener true }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isShowGuide) {
                    cancelGuideLottie()
                } else if (binding.mainDrawer.isOpen) {
                    binding.mainDrawer.close()
                } else if (openServerState == ServerState.CONNECTING) {
                } else if (openServerState == ServerState.DISCONNECTING) {
                    stopToConnectOrDisConnect()
                } else {
                    moveTaskToBack(true)
                    BaseApp.isActivityBack = true
                }
            }
        })
        binding.slideP.setOnClickListener {
            startActivity(Intent(this, TriceWebActivity::class.java))
        }
        binding.slideS.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                "https://play.google.com/store/apps/details?id=${packageName}"
            )
            sendIntent.type = "text/plain"
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
        binding.slideU.setOnClickListener {
            val appPackageName = packageName
            try {
                val launchIntent = Intent()
                launchIntent.data = Uri.parse("market://details?id=$appPackageName")
                startActivity(launchIntent)
            } catch (anfe: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
            }
        }
    }

    suspend fun disConnectFun() {
        openServerState = ServerState.DISCONNECTING
        delay(2000)
        mService?.disconnect()
    }


    @SuppressLint("CutPasteId", "SetTextI18n")
    private fun toConnectStep1() {
        if (isShowGuide) cancelGuideLottie()
        if (!isAppOnline(this)) {
            showNetworkErrorDialog()
            return
        }
        toAction = true
        cancelConnect = false
        Log.e(TAG, "toConnectStep1: openServerState=${openServerState}")

        playConnectAnimation()

        when (openServerState) {
            ServerState.CONNECTED -> handleConnectedState()
            ServerState.DISCONNECTED -> handleDisconnectedState()
            ServerState.CONNECTING -> {
                if (TriceDataHelper.cachePosition == -1) {
                    handleDisconnectedState()
                }
            }

            else -> {} // Do nothing for other states
        }
    }

    private fun handleConnectedState() {
        shadowsocksJob = lifecycleScope.launch(Dispatchers.IO) {
            if (binding.mainDrawer.isOpen) {
                userInterrupt = true
                stopToConnectOrDisConnect()
                return@launch
            }
            if (isActive && lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                disConnectFun()
            }
        }
    }

    private fun handleDisconnectedState() {

        shadowsocksJob = mService?.let {
            openServerState = ServerState.CONNECTING
            step2(it)
        }
    }

    private fun showNetworkErrorDialog() {
        val customDialog = Dialog(this, R.style.AppDialogStyle).apply {
            window?.attributes = window?.attributes?.apply {
                gravity = Gravity.CENTER
            }
            setContentView(R.layout.dialog_check_net)
            findViewById<AppCompatTextView>(R.id.switchText).apply {
                text = "Network request timed out. Please make sure your network is connected"
                setOnClickListener { dismiss() }
            }
            findViewById<AppCompatTextView>(R.id.netConfirm).setOnClickListener {
                dismiss()
            }
        }
        customDialog.show()
    }

    private fun initAnimateGuide() {
        binding.lottieGuide.visibility = View.VISIBLE
        binding.lottieGuide.setAnimation("guide.json")
        binding.lottieGuide.repeatCount = ValueAnimator.INFINITE
        binding.lottieGuide.playAnimation()
    }

    private fun cancelGuideLottie() {
        binding.lottieGuide.cancelAnimation()
        binding.lottieGuide.isVisible = false
        isShowGuide = false
        binding.guideMask.isVisible = false
    }

    private fun stopConnectAnimation(isConnect: Boolean) {
        binding.connectImage.clearAnimation()
        if (isConnect) {
            binding.mainConnectImageBg.setImageResource(R.mipmap.main_connect_bg_ok)
            binding.connectImage.setImageResource(R.mipmap.main_connect_flash_ok)
        } else {
            binding.mainConnectImageBg.setImageResource(R.mipmap.main_connect_bg)
            binding.connectImage.setImageResource(R.mipmap.main_connect_flash)
        }
    }

    fun stopToConnectOrDisConnect() {
        if (shadowsocksJob?.isActive == true) {
            shadowsocksJob?.cancel()
            when (openServerState) {
                ServerState.CONNECTING -> {
                    dealResult(ServerState.DISCONNECTED)
                    mService?.disconnect()
                    cancelConnect = true
                }

                ServerState.DISCONNECTING -> {
                    userInterrupt = true
                    dealResult(ServerState.CONNECTED)
                }

                else -> {}
            }
        }
    }

    fun playConnectAnimation() {
        binding.connectImage.setImageResource(R.mipmap.main_c_loading)
        binding.connectImage.startAnimation(rotateAnimation)
    }

    fun dealResult(state: ServerState) {
        lifecycleScope.launch {
            when (state) {
                ServerState.CONNECTING -> {
                    setViewEnabled(false)
                    playConnectAnimation()
                }

                ServerState.CONNECTED -> {
                    openServerState = ServerState.CONNECTED
                    setViewEnabled(true)
                    stopConnectAnimation(true)
                    setChronometer()
                }

                ServerState.DISCONNECTING -> {
                    setViewEnabled(true)
                }

                ServerState.DISCONNECTED -> {
                    openServerState = ServerState.DISCONNECTED

                    setViewEnabled(true)
                    stopConnectAnimation(false)
                    setChronometer()
                }
            }

        }
    }

    private fun setChronometer() {
        Log.e(TAG, "setChronometer: userInterrupt$userInterrupt")
        if (userInterrupt) {
            userInterrupt = false
            if (TriceDataHelper.cachePosition != -1) {
                TriceDataHelper.curPosition = TriceDataHelper.cachePosition
                TriceDataHelper.cachePosition = -1
            }
            return
        }
        when (openServerState) {
            ServerState.CONNECTED -> {
                binding.mainChronometer.base = System.currentTimeMillis()
                binding.mainChronometer.start()

            }

            ServerState.DISCONNECTED -> {
                binding.mainChronometer.stop()
                binding.mainChronometer.base = SystemClock.elapsedRealtime()

            }

            else -> {}
        }
    }


    private fun setViewEnabled(b: Boolean) {
        binding.mainConnectImageBg.isEnabled = b
        binding.connectImage.isEnabled = b
    }

    private fun checkVPNPermission(ac: Activity): Boolean {
        VpnService.prepare(ac).let {
            return it == null
        }
    }


    var mService: IOpenVPNAPIService? = null

    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            className: ComponentName?,
            service: IBinder?,
        ) {
            mService = IOpenVPNAPIService.Stub.asInterface(service)
            try {
                mService?.registerStatusCallback(mCallback)
            } catch (e: Exception) {
            }
        }

        override fun onServiceDisconnected(className: ComponentName?) {
            mService = null
        }
    }

    private val mCallback = object : IOpenVPNStatusCallback.Stub() {
        override fun newStatus(uuid: String?, state: String?, message: String?, level: String?) {
            curServerState = state
            Log.d(TAG, "newStatus: state=$state;message=$message")
            when (state) {
                ServerState.CONNECTED.value -> {
                    if (toAction) toAction = false
                    userInterrupt = false
                    goWatch()
                    dealResult(ServerState.CONNECTED)
                    openServerState = ServerState.CONNECTED
                }

                ServerState.DISCONNECTED.value -> {
                    openServerState = ServerState.DISCONNECTED
                    if (toAction) {
                        toAction = false
                        userInterrupt = false
                        dealResult(ServerState.DISCONNECTED)
                        if (!cancelConnect) goWatch()
                        else cancelConnect = false
                    }
                }

                else -> {}
            }

        }

    }

    private fun goWatch() {
        val intent = Intent(this, LastActivity::class.java)
        intent.putExtra(
            AppConstant.IS_CONNECT, curServerState == ServerState.CONNECTED.value
        )
        startActivity(intent)
    }

    var cancelConnect = false

    private fun step2(server: IOpenVPNAPIService): Job? {
        if (checkVPNPermission(this)) {
            val job = MainScope().launch(Dispatchers.IO) {
                dealResult(ServerState.CONNECTING)
                if (isAppOnline(this@MainActivity)) {
                    val data = TriceDataHelper.allLocaleProfiles[TriceDataHelper.curPosition]
                    runCatching {
                        val conf = assets.open("fast_trice.ovpn")
                        val br = BufferedReader(InputStreamReader(conf))
                        val config = StringBuilder()
                        var line: String?
                        while (true) {
                            line = br.readLine()
                            if (line == null) break
                            if (line.contains("remote 103", true)) {
                                line = "remote ${data.iphostttt} ${data.ppppppppport}"
                            } else if (line.contains("wrongpassword", true)) {
                                line = data.ppppppppass
                            } else if (line.contains("cipher AES-256-GCM", true)) {
                                line = "cipher ${data.polyyyyyyy}"
                            }
                            config.append(line).append("\n")
                        }
                        br.close()
                        conf.close()
                        Log.e(TAG, "step2: =${config}")
                        server.startVPN(config.toString())
                        if (TriceDataHelper.historyPosList.isEmpty()) {
                            TriceDataHelper.saveHistoryList("0")
                        }
                        delay(10000)
                        if (curServerState != ServerState.CONNECTED.value && lifecycle.currentState == Lifecycle.State.RESUMED) {
                            cancelConnect = true
                            stopToConnectOrDisConnect()
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Connect Failed!",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                            cancel()
                        }
                    }.onFailure {
                    }
                } else {
                    stopToConnectOrDisConnect()
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

    private fun requestPermissionForResult(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            mService?.let { toConnectStep1() }
        } else {
            dealResult(ServerState.DISCONNECTED)
        }
    }

}

enum class ServerState(val value: String) {
    CONNECTED("CONNECTED"), DISCONNECTED("NOPROCESS"), CONNECTING(""), DISCONNECTING("")
}