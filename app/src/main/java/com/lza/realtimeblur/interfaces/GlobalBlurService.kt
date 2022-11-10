package com.lza.realtimeblur.interfaces

/**
 * @author liuzhongao
 * @Date 2022/11/10 13:55
 */
interface GlobalBlurService {
    fun registerBlurTarget(target: BlurTarget)
    fun unRegisterBlurTarget(target: BlurTarget)
}