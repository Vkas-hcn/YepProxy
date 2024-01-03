package com.yep.online.link.prox.connection.hlep

import android.content.Context
import com.yep.online.link.prox.connection.net.CloakUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.*
import java.text.DecimalFormat

object YepTimerUtils {
    private val job = Job()
    private val timerThread = CoroutineScope(job)
    var skTime = 0
    var isStopThread = true
    var showTime = 0

    /**
     * 发送定时器信息
     */
    fun sendTimerInformation() {
        timerThread.launch {
            while (isActive) {
                skTime++
                if (!isStopThread) {
//                    Apollo.emit(Q.timerMeteorData,formatTime(skTime))
                }
                delay(1000)
            }
        }
    }

    /**
     * 开始计时
     */
    fun startTiming() {
        if (isStopThread) {
            skTime = 0
        }
        isStopThread = false
    }

    /**
     * 结束计时
     */
    fun endTiming(context: Context, isDisConnect: Boolean = false) {
        if (isDisConnect) {
            CloakUtils.putPointTimeYep(
                "vpntime",
                (skTime / 60),
                "usetime",
                context
            )
        }
        skTime = 0
        isStopThread = true

    }

    /**
     * 设置时间格式
     */
    private fun formatTime(timerData: Int): String {
        val hh: String = DecimalFormat("00").format(timerData / 3600)
        val mm: String = DecimalFormat("00").format(timerData % 3600 / 60)
        val ss: String = DecimalFormat("00").format(timerData % 60)
        return "$hh:$mm:$ss"
    }

    fun getTiming(): String {
        return if (!isStopThread) {
            formatTime(skTime)
        } else {
            "00:00:00"
        }

    }
}