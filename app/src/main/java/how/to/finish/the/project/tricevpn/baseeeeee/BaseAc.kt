package how.to.finish.the.project.tricevpn.baseeeeee

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import how.to.finish.the.project.tricevpn.uitlllll.IPUtils
import how.to.finish.the.project.tricevpn.uitlllll.NetworkChangeListener
import how.to.finish.the.project.tricevpn.uitlllll.NetworkChangeReceiver

abstract class BaseAc<V : ViewBinding> : AppCompatActivity(), NetworkChangeListener {
    var isResume = false
    abstract val binding: V
    private var networkChangeReceiver: NetworkChangeReceiver? = null
    var isShowBandedDialog = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val metrics: DisplayMetrics = resources.displayMetrics
        val td = metrics.heightPixels / 760f
        val dpi = (160 * td).toInt()
        metrics.density = td
        metrics.scaledDensity = td
        metrics.densityDpi = dpi
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onNetworkChanged(isConnected: Boolean) {
    }

}