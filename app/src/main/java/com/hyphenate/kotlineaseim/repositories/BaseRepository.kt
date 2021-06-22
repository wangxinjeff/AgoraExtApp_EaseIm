package com.hyphenate.kotlineaseim.repositories

import com.hyphenate.kotlineaseim.manager.ThreadManager

open class BaseRepository {

    fun runOnMainThread(runnable: Runnable){
        ThreadManager.instance.runOnMainThread(runnable)
    }

    fun runOnIOThread(runnable: Runnable){
        ThreadManager.instance.runOnIOThread(runnable)
    }
}