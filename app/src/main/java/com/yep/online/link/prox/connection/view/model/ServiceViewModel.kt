package com.yep.online.link.prox.connection.view.model

import androidx.lifecycle.ViewModel
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.yep.online.link.prox.connection.hlep.AdUtils
import com.yep.online.link.prox.connection.hlep.DataUtils
import com.yep.online.link.prox.connection.hlep.DataUtils.TAG
import com.yep.online.link.prox.connection.hlep.ServiceBean
import com.yep.online.link.prox.connection.hlep.ServiceData
import com.yep.online.link.prox.connection.net.CloakUtils
import com.yep.online.link.prox.connection.net.YepOkHttpUtils
import com.yep.online.link.prox.connection.view.ad.YepLoadBackAd
import com.yep.online.link.prox.connection.view.adapter.ServiceAdapter
import com.yep.online.link.prox.connection.view.ui.ServiceActivity
import com.yep.online.link.prox.connection.view.utils.MainFun
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                com.yep.online.link.prox.connection.base.App.serviceState = 0
                backMain(position, activity)
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
        showDisconnectDialog(position, activity)
    }

    private fun backMain(position: Int, activity: AppCompatActivity) {
        activity.finish()
        ServiceData.saveRecentlyList(position.toString())
    }

    /**
     * 回显服务器
     */
    fun getAllServer() {
        allVpnListData = ArrayList()
        skServiceBean = ServiceBean()
        ServiceData.getAllVpnListData()?.let {
            allVpnListData = it
        }
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
        adapterAll = ServiceAdapter(ecServiceBeanList, true)
    }

    fun getsRecently() {
        recentlyVpnListData = ArrayList()
        recentlyVpnListData = ServiceData.findVpnByPos()
        adapterRecord = ServiceAdapter(recentlyVpnListData, false)
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
        CloakUtils.putPointYep("listback", activity)
    }

    /**
     * 是否断开连接
     */
    private fun showDisconnectDialog(position: Int, activity: AppCompatActivity) {
        if (!whetherToConnect) {
            backMain(position, activity)
            com.yep.online.link.prox.connection.base.App.serviceState = 0
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
                backMain(position, activity)
                com.yep.online.link.prox.connection.base.App.serviceState = 1
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
        activity.lifecycleScope.launch(Dispatchers.IO) {
            CloakUtils.putPointYep("listReload", activity)
            val binding = (activity as ServiceActivity).binding
            withContext(Dispatchers.Main) {
                binding.isLoading = true
                MainFun.rotateImageViewInfinite(binding.imgLoading, 800)
            }
            YepOkHttpUtils().getVpnData(activity)
            delay(2000)
            withContext(Dispatchers.Main) {
                binding.isLoading = false
                MainFun.stopRotation(binding.imgLoading)
            }
            if (ServiceData.deliverServerTransitions(activity)) {
                allVpnListData = ServiceData.getAllVpnListData()
                ecServiceBeanList = allVpnListData
                withContext(Dispatchers.Main) {
                    adapterRecord.clearData(activity)
                }
                ecServiceBeanList.forEachIndexed { index, _ ->
                    if (checkSkServiceBeanClick.best) {
                        ecServiceBeanList[0].check = true
                    } else {
                        ecServiceBeanList[index].check =
                            ecServiceBeanList[index].ip == checkSkServiceBeanClick.ip
                        ecServiceBeanList[0].check = false
                    }
                }
                withContext(Dispatchers.Main) {
                    adapterAll.setData(ecServiceBeanList)
                    if (binding.isEmoryAllData == true) {
                        binding.isEmoryData = false
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    binding.isEmoryAllData = true
                }
            }
        }
    }
}