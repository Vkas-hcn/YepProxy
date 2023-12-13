package how.to.finish.the.project.tricevpn.mainnnnnnn

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.lifecycle.Lifecycle
import how.to.finish.the.project.tricevpn.baseeeeee.BaseAc
import how.to.finish.the.project.tricevpn.databinding.SplashAcLayoutBinding
import how.to.finish.the.project.tricevpn.uitlllll.IPUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseAc<SplashAcLayoutBinding>() {

    override val binding: SplashAcLayoutBinding by lazy {
        SplashAcLayoutBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rotateAnimation = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )

        // 设置动画属性，如旋转时长、重复次数等
        rotateAnimation.duration = 2000 // 旋转时长，单位毫秒
        rotateAnimation.repeatCount = Animation.INFINITE // 无限循环
        rotateAnimation.fillAfter = true // 动画结束后保持最后的状态
        // 启动动画
        binding.spLoading.startAnimation(rotateAnimation)
        MainScope().launch {
            delay(2000)
            if (lifecycle.currentState == Lifecycle.State.RESUMED) startActivity(
                Intent(
                    this@SplashActivity,
                    MainActivity::class.java
                )
            )
            finish()
        }

    }

    override fun onBackPressed() {

    }

}