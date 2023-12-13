package how.to.finish.the.project.tricevpn.mainnnnnnn

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import how.to.finish.the.project.tricevpn.R
import how.to.finish.the.project.tricevpn.baseeeeee.AppConstant
import how.to.finish.the.project.tricevpn.dataaaaaa.SunProfile
import how.to.finish.the.project.tricevpn.dataaaaaa.TriceDataHelper
import how.to.finish.the.project.tricevpn.databinding.DialogSwitchConfigBinding
import how.to.finish.the.project.tricevpn.databinding.ItemRecentBinding
import how.to.finish.the.project.tricevpn.mainnnnnnn.ConfigAdapter.YourViewHolder
import how.to.finish.the.project.tricevpn.uitlllll.ImageUtils
import how.to.finish.the.project.tricevpn.uitlllll.SPUtil

class ConfigAdapter(
    private val dataList: List<SunProfile>,
    private val isC: Boolean,
    private val type: Int
) :
    RecyclerView.Adapter<YourViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YourViewHolder {
        val binding = ItemRecentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return YourViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: YourViewHolder, position: Int) {
        val data = dataList[position]
        holder.binding.itemCheck.isVisible = type != 2
        if (data.ccccccci.isBlank())
            holder.binding.itemText.text = data.counnnn
        else holder.binding.itemText.text = data.counnnn + "-" + data.ccccccci
        if (data.isFast) {
            holder.binding.itemText.text = "Fast Server"
            holder.binding.itemImage.setImageResource(ImageUtils.getImage("Fast Server"))
        } else {
            holder.binding.itemImage.setImageResource(ImageUtils.getImage(data.counnnn))
        }

        if (isC && TriceDataHelper.curPosition == position)
            holder.binding.itemCheck.setImageResource(R.mipmap.item_checked)
        else {
            holder.binding.itemCheck.setImageResource(R.mipmap.item_uncheck)
        }
        holder.binding.itemFlashImage.isVisible =
            dataList[position].iphostttt == TriceDataHelper.getSmart().iphostttt
        holder.itemView.setOnClickListener {
            if (isC) {
                dialog(position, holder.itemView.context)
            } else {
                toResult(position, holder.itemView.context)
            }
        }
    }

    private fun dialog(position: Int, activity: Context) {
        val customDialog = Dialog(activity, R.style.AppDialogStyle)
        val localLayoutParams = customDialog.window?.attributes
        localLayoutParams?.gravity = Gravity.CENTER
        customDialog.window?.attributes = localLayoutParams
        val binding = DialogSwitchConfigBinding.inflate(LayoutInflater.from(activity))
        customDialog.setContentView(binding.root)
        binding.switchConfirm.setOnClickListener {
            toResult(position, activity, disconnect = true)
            customDialog.dismiss()
        }
        binding.switchCancel.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    private fun toResult(position: Int, context: Context, disconnect: Boolean = false) {
        if (disconnect) {
            TriceDataHelper.cachePosition = TriceDataHelper.curPosition
            AppConstant.isResultState = true
        }else{
            AppConstant.isResultState = false
        }
        if (type == 1) {
            TriceDataHelper.curPosition = position
        } else {
            val profileList = TriceDataHelper.allLocaleProfiles
            TriceDataHelper.curPosition = profileList.indexOfFirst {
                it.counnnn == dataList[position].counnnn && it.iphostttt == dataList[position].iphostttt
            }
        }
        SPUtil(context).putBoolean("icConnect", true)
        val intent = Intent(context, MainActivity::class.java)
        (context as ConfigActivity).setResult(100, intent)
        context.finish()
        TriceDataHelper.saveHistoryList(position.toString())
    }

    class YourViewHolder(var binding: ItemRecentBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}