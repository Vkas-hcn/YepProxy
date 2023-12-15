package how.to.finish.the.project.tricevpn.view.model

import androidx.lifecycle.ViewModel
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import how.to.finish.the.project.tricevpn.base.App
import how.to.finish.the.project.tricevpn.hlep.AdUtils
import how.to.finish.the.project.tricevpn.hlep.DataUtils
import how.to.finish.the.project.tricevpn.hlep.DataUtils.TAG
import how.to.finish.the.project.tricevpn.hlep.ServiceBean
import how.to.finish.the.project.tricevpn.hlep.ServiceData
import how.to.finish.the.project.tricevpn.view.ad.YepLoadBackAd
import how.to.finish.the.project.tricevpn.view.adapter.ServiceAdapter
import how.to.finish.the.project.tricevpn.view.ui.EndActivity
import how.to.finish.the.project.tricevpn.view.ui.ServiceActivity
import how.to.finish.the.project.tricevpn.view.utils.MainFun
import kotlinx.coroutines.Job

class ServiceViewModel : ViewModel() {
    lateinit var skServiceBean: ServiceBean
    lateinit var allVpnListData: MutableList<ServiceBean>
    lateinit var recentlyVpnListData: MutableList<ServiceBean>
    lateinit var adapterAll: ServiceAdapter
    lateinit var adapterRecord: ServiceAdapter
    var ecServiceBeanList: MutableList<ServiceBean> = ArrayList()
    // 是否连接
    var whetherToConnect = false
    //选中服务器
    lateinit var checkSkServiceBean: ServiceBean
    lateinit var checkSkServiceBeanClick: ServiceBean

    /**
     * 选中服务器
     */
    fun selectServer(activity: AppCompatActivity, position: Int) {
        if (ecServiceBeanList[position].ip == checkSkServiceBeanClick.ip && ecServiceBeanList[position].best == checkSkServiceBeanClick.best) {
            if (!whetherToConnect) {
                Log.e(TAG, "选中服务器")
                backMain(position,activity)
                DataUtils.connect_vpn = Gson().toJson(checkSkServiceBean)
            }
            return
        }
        ecServiceBeanList.forEachIndexed { index, _ ->
            ecServiceBeanList[index].check = position == index
            if (ecServiceBeanList[index].check) {
                checkSkServiceBean = ecServiceBeanList[index]
            }
        }
        adapterAll.notifyDataSetChanged()
        showDisconnectDialog(position,activity)
    }

    private fun backMain(position: Int, activity: AppCompatActivity){
        activity.finish()
        ServiceData.saveRecentlyList(position.toString())
    }
    /**
     * 回显服务器
     */
    fun getAllServer() {
        allVpnListData = ArrayList()
        skServiceBean = ServiceBean()
        allVpnListData = ServiceData.getAllVpnListData()
        ecServiceBeanList = allVpnListData
        ecServiceBeanList.forEachIndexed { index, _ ->
            if (checkSkServiceBeanClick.best) {
                ecServiceBeanList[0].check = true
            } else {
                ecServiceBeanList[index].check =
                    ecServiceBeanList[index].ip == checkSkServiceBeanClick.ip
                ecServiceBeanList[0].check = false
            }
        }
        Log.e(TAG, "ecServiceBeanList=${Gson().toJson(ecServiceBeanList)}")
        adapterAll = ServiceAdapter(ecServiceBeanList,true)
    }

    fun getsRecently() {
        recentlyVpnListData = ArrayList()
//        skServiceBean = ServiceBean()
        recentlyVpnListData = ServiceData.findVpnByPos()
//        ecServiceBeanList = allVpnListData
//        ecServiceBeanList.forEachIndexed { index, _ ->
//            if (checkSkServiceBeanClick.best) {
//                ecServiceBeanList[0].check = true
//            } else {
//                ecServiceBeanList[index].check =
//                    ecServiceBeanList[index].ip == checkSkServiceBeanClick.ip
//                ecServiceBeanList[0].check = false
//            }
//        }
//        Log.e(TAG, "ecServiceBeanList=${Gson().toJson(ecServiceBeanList)}")
        adapterRecord = ServiceAdapter(recentlyVpnListData,false)
    }

    /**
     * 返回主页
     */
    fun returnToHomePage(activity: AppCompatActivity) {
        if (YepLoadBackAd.displayBackAdvertisementYep(activity, cloneAd = {
                activity.finish()
            }) != 2) {
            activity.finish()
        }
    }

    /**
     * 是否断开连接
     */
    private fun showDisconnectDialog(position:Int,activity: AppCompatActivity) {
        if (!whetherToConnect) {
            backMain(position,activity)
            Log.e(TAG, "是否断开连接")
            App.serviceState = 0
            DataUtils.connect_vpn = Gson().toJson(checkSkServiceBean)
            return
        }
        val dialog: AlertDialog? = AlertDialog.Builder(activity)
            .setTitle("Are you sure to disconnect current server")
            //设置对话框的按钮
            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
                ecServiceBeanList.forEachIndexed { index, _ ->
                    ecServiceBeanList[index].check =
                        (ecServiceBeanList[index].ip == checkSkServiceBeanClick.ip && ecServiceBeanList[index].best == checkSkServiceBeanClick.best)
                }
                adapterAll.notifyDataSetChanged()
            }
            .setPositiveButton("DISCONNECT") { dialog, _ ->
                dialog.dismiss()
                backMain(position,activity)
                App.serviceState = 1
                DataUtils.connect_vpn = Gson().toJson(checkSkServiceBean)
            }.create()
        val params = dialog!!.window!!.attributes
        params.width = 200
        params.height = 200
        dialog.window!!.attributes = params
        dialog.setCancelable(false)
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.BLACK)
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(Color.BLACK)
    }
    fun refresh(activity: AppCompatActivity) {
        val binding  = (activity as ServiceActivity).binding
        binding.isLoading = true
        MainFun.rotateImageViewInfinite(binding.imgLoading,800)
        AdUtils.getFileBaseData(activity, loadAdFun = {
            allVpnListData = ServiceData.getAllVpnListData()
            ecServiceBeanList = allVpnListData
            ecServiceBeanList.forEachIndexed { index, _ ->
                if (checkSkServiceBeanClick.best) {
                    ecServiceBeanList[0].check = true
                } else {
                    ecServiceBeanList[index].check =
                        ecServiceBeanList[index].ip == checkSkServiceBeanClick.ip
                    ecServiceBeanList[0].check = false
                }
            }
            adapterAll.setData(ecServiceBeanList)
            binding.isLoading = false
            MainFun.stopRotation(binding.imgLoading)
        })
    }
}