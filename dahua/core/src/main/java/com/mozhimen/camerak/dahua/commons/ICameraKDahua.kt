package com.mozhimen.camerak.dahua.commons

/**
 * @ClassName ICameraKDahua
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/10/24 23:14
 * @Version 1.0
 */
interface ICameraKDahua<C: ICameraKDahuaProvider> {
    /**
     * 携带sp名称
     * @param name String
     * @return T
     */
    fun with(name: String):C
}