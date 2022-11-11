package com.lza.realtimeblur.renderScript

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.Window
import com.lza.realtimeblur.interfaces.BlurTarget
import com.lza.realtimeblur.interfaces.GlobalBlurService
import java.util.concurrent.ConcurrentHashMap

/**
 * @author liuzhongao
 * @Date 2022/11/10 13:57
 */
val windowBlurService: GlobalBlurService
    get() = WindowBlurService

internal object WindowBlurService: GlobalBlurService {

    private val blurImplementations: MutableMap<Window, WindowBlurImpl> = ConcurrentHashMap()

    override fun registerBlurTarget(target: BlurTarget) {
        val window = target.ctx.window ?: return
        if (blurImplementations[window] == null) {
            synchronized(blurImplementations) {
                if (blurImplementations[window] == null) {
                    val impl = WindowBlurImpl(window, { target.blurFactor }, { target.blurRadius })
                    blurImplementations[window] = impl
                }
            }
        }
        blurImplementations[window]?.addBlurTarget(target)
    }

    override fun unRegisterBlurTarget(target: BlurTarget) {
        val window = target.ctx.window ?: return
        val blurImpl = blurImplementations[window]
        if (blurImpl != null) {
            blurImpl.removeBlurTarget(target)
            if (blurImpl.isEmpty) {
                blurImpl.release()
                blurImplementations.remove(window)
            }
        }
    }

    private val Context.window: Window?
        get() {
            var context = this
            var index = 0
            while (context !is Activity && context is ContextWrapper && index < 10) {
                context = context.baseContext
                index++
            }
            if (context is Activity) {
                return context.window
            }
            return null
        }
}