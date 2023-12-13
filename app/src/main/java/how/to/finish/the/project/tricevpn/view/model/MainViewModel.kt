package how.to.finish.the.project.tricevpn.view.model

import android.content.Intent
import android.net.Uri
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.view.animation.Animation
import com.dual.pro.one.dualprotocolone.base.BaseViewModel
import how.to.finish.the.project.tricevpn.view.ui.MainActivity

class MainViewModel:BaseViewModel() {


    fun shareUrl(activity: MainActivity){
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "https://play.google.com/store/apps/details?id=${activity.packageName}"
        )
        sendIntent.type = "text/plain"
        val shareIntent = Intent.createChooser(sendIntent, null)
        activity.startActivity(shareIntent)
    }
    fun updateUrl(activity: MainActivity){
        activity.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=${activity.packageName}")
            )
        )
    }


    fun rotateImageViewInfinite(imageView: ImageView, duration: Long) {
        val rotateAnimation = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnimation.duration = duration
        rotateAnimation.repeatCount = Animation.INFINITE
        rotateAnimation.repeatMode = Animation.RESTART

        imageView.startAnimation(rotateAnimation)
    }
    fun stopRotation(imageView: ImageView) {
        imageView.clearAnimation()
    }




}