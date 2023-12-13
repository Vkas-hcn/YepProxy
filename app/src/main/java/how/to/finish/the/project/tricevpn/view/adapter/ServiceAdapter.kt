package how.to.finish.the.project.tricevpn.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import how.to.finish.the.project.tricevpn.R
import how.to.finish.the.project.tricevpn.hlep.DataUtils.getServiceFlag
import how.to.finish.the.project.tricevpn.hlep.ServiceBean

class ServiceAdapter(private val dataList: MutableList<ServiceBean>) :
    RecyclerView.Adapter<ServiceAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_country)
        var aivFlag: ImageView = itemView.findViewById(R.id.aiv_flag)
        var llItem: LinearLayout = itemView.findViewById(R.id.ll_item)
        var aivCheck: ImageView = itemView.findViewById(R.id.aiv_check)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // 处理 item 点击事件
                    onItemClick(position)
                }
            }
        }
    }

    // 定义点击事件的回调接口
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    // 在 item 点击事件中触发回调
    private fun onItemClick(position: Int) {
        onItemClickListener?.onItemClick(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context: Context = parent.context
        val inflater = LayoutInflater.from(context)

        // 加载自定义的布局文件
        val itemView: View = inflater.inflate(R.layout.item_service, parent, false)

        // 创建ViewHolder对象
        return ViewHolder(itemView)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 获取数据
        val item = dataList[position]
        // 将数据绑定到视图上
        if (item.best) {
            holder.tvName.text = "Faster Server"
            holder.aivFlag.setImageResource(R.drawable.fast)
        } else {
            holder.tvName.text = String.format(item.country + "-" + item.city)
            holder.aivFlag.setImageResource(item.country.getServiceFlag())
        }
        if (item.check) {
            holder.aivCheck.setImageResource(R.drawable.ic_check)
            holder.llItem.background = holder.itemView.context.getDrawable(R.drawable.bg_check_item)
        } else {
            holder.aivCheck.setImageResource(R.drawable.ic_dis_check)
            holder.llItem.background =
                holder.itemView.context.getDrawable(R.drawable.bg_discheck_item)
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun addData(newData: MutableList<ServiceBean>) {
        dataList.addAll(newData)
        notifyDataSetChanged()
    }
}