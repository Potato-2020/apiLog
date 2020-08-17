package com.epoch.rupeeLoan.tools.db

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

/**
 * create by Potato
 * create time 2020/5/30
 * Description：* 创建Flowable
 * 因为Room数据库查询之后，返回类型写成Flowable会报错，官方的Demo也是无敌了
 */
fun <T> createFlowable(dbHandler: DBHandler<T>): Flowable<T> {
    return Flowable.create(
            {
                val t: T = try {
                    dbHandler.process()!!
                } catch (e: Throwable) {
                    return@create it.onError(e)
                }
                it.onNext(t)
                it.onComplete()
                //执行数据方法且发送到下一步
            },//调度回调成功方法
            BackpressureStrategy.BUFFER//背压策略
    )
}

//操作数据库接口
abstract class DBHandler<T> {
    abstract fun process(): T?
}

/**
 * 查询数据库后，Flowable的拓展函数，回调成功和失败告知UI
 */
fun <T> Flowable<T>.dbSubscribe(
        onSuccess: (data: T) -> Unit,//成功
        onFailed: (e: Throwable) -> Unit,//失败
        onCatchSub: (s: Subscription) -> Unit) {//捕捉订阅者
    subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Subscriber<T> {
                override fun onComplete() {}

                override fun onNext(t: T) {
                    onSuccess(t)
                }

                override fun onError(e: Throwable?) {
                    e?.let {
                        onFailed(it)
                    }
                }

                override fun onSubscribe(s: Subscription?) {
                    s?.request(Long.MAX_VALUE)
                    s?.let { onCatchSub(it) }
                }

            })
}
