package com.lza.realtimeblur

import android.app.Application
import android.content.Context
import com.lza.realtimeblur.renderScript.blurService

/**
 * @author liuzhongao
 * @Date 2022/11/10 13:39
 */
class AppApplication: Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        ApplicationWrapper.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        blurService.init(this)
    }
}