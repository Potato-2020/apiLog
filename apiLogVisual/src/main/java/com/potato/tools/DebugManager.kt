package com.potato.tools

import android.app.Activity
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_ACCELEROMETER
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_NORMAL
import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import com.potato.apilogvisual.ActivityDebug


/**
 * @Author wangguoli
 * 翻一翻振动
 */
class DebugManager : SensorEventListener {

    private var style = SHAKE_SHOCK//默认摇一摇
    private var mContext: Context? = null
    private var mSensorManager: SensorManager? = null
    private var mSensor: Sensor? = null
    private var lastZ: Int = 0//上一次坐标值
    private var lastX: Int = 0//上一次坐标值
    private var lastMis: Long = 0
    private var lastCountMis: Long = 0// 上次记录时间
    private var countS: Int = 0// 计数
    private var isEnable = true// 是否使用
    private var debugListener: DebugListener? = null// 翻转
    //无线振动时间
    private val time = longArrayOf(40, 40, 40, 40, 40, 40, 40, 40, 40, // ramp-up sampling rate = 40ms
            40, 40, 40, 40, 40, 40, 40
    )
    //振动振幅
    private val amplitude = intArrayOf(1, 4, 11, 25, 44, 67, 91, 114, 123, // ramp-up amplitude (from 0 to 50%)
            103, 79, 55, 34, 17, 7, 2 // ramp-up amplitude
    )
    //振动效应
    private val effect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        VibrationEffect.createWaveform(time, amplitude, -1)
    } else {
//        TODO("VERSION.SDK_INT < O")
        null
    }
    //用途
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private val unknownAttrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_UNKNOWN).build()

    fun setListener(context: Context, debugListener: DebugListener) {
        try {// 初始化
            this.mContext = context
            this.debugListener = debugListener
            mSensorManager = mContext!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            mSensor = mSensorManager!!.getDefaultSensor(TYPE_ACCELEROMETER)// TYPE_GRAVITY
            if (null == mSensorManager) return
            // 参数三，检测的精准度
            mSensorManager!!.registerListener(this, mSensor, SENSOR_DELAY_NORMAL)// SENSOR_DELAY_GAME
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    //设置风格
    fun setStyle(style: Int){
        this.style = style
    }

    @Suppress("DEPRECATED_IDENTITY_EQUALS")
    override fun onSensorChanged(sensorEvent: SensorEvent) {
        if (sensorEvent.sensor == null || !isEnable) return
        if (sensorEvent.sensor.type === TYPE_ACCELEROMETER) {
            //----------------横向手机------------------------
            //屏幕朝上（0,   0,  10）
            //屏幕朝下（0,   0, -10）
            //屏幕朝左（10,  0,   0）
            //屏幕朝右（-10, 0,   0）
            //----------------竖立手机------------------------
            //上下左右都是（0,10,0）
            val x = sensorEvent.values[0].toInt()
            val z = sensorEvent.values[2].toInt()
            if (System.currentTimeMillis() - lastMis > 100) {
                //在时间间隔内翻转了，verseCount++，计数大于1，振动
                verse(z,x)
                lastZ = z
                lastX = x
                lastMis = System.currentTimeMillis()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    private fun verse(z: Int, x: Int) {
        when (style) {
            SHAKE_SHOCK -> {
                if (z * lastZ < 0 || x * lastX < 0) {
                    shake(z, x)
                }
            }
            VERSE_SHOCK -> {
                reverse(z)
            }
        }
    }

    private fun shake(z: Int, x: Int) {
        if (z * lastZ < 0 || x * lastX < 0) {
            // 更新赋值记录数据时间
            if (System.currentTimeMillis() - lastCountMis > 1000) countS = 0
            lastCountMis = System.currentTimeMillis()
            // 计数设计
            countS++
            if (countS > 3) {
                countS = 0
                val vibrator = mContext!!.getSystemService(VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(effect, unknownAttrs)
                } else {
                    vibrator.vibrate(300)
                }
                if (debugListener != null) {
                    isEnable = false
                    debugListener!!.debugApiLog()
                }
            }
        }
    }

    private fun reverse(z: Int) {
        if (z * lastZ < 0 && Math.abs(z) * Math.abs(lastZ) > 50) {
            // 更新赋值记录数据时间
            if (System.currentTimeMillis() - lastCountMis > 3000) countS = 0
            lastCountMis = System.currentTimeMillis()
            // 计数设计
            countS++
            if (countS > 1) {
                countS = 0
                val vibrator = mContext!!.getSystemService(VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(effect, unknownAttrs)
                } else {
                    vibrator.vibrate(300)
                }
                if (debugListener != null) {
                    isEnable = false
                    debugListener!!.debugApiLog()
                }
            }
        }
    }

    fun onResume() {
        isEnable = true// 可用
    }

    fun onPause() {
        isEnable = false// 不可用
    }

    // 销毁监听
    fun onDestory() {
        isEnable = false// 不可用
        if (mSensorManager != null && mSensor != null)
            mSensorManager!!.unregisterListener(this, mSensor)
        mSensorManager = null
        mSensor = null
    }

    interface DebugListener {
        fun debugApiLog()
    }

    companion object{
        const val SHAKE_SHOCK = 1//摇一摇
        const val VERSE_SHOCK = 2//翻一翻
    }

    /**
     * 去调试界面
     * @baseUrl: 接口host（例如：https://www.baidu.com）
     * @versionName: 版本名称
     * @version：版本标识
     */
    fun openDebug(activity: Activity, baseUrl: String, versionName: String, version: String) {
        val intent = Intent(activity, ActivityDebug::class.java)
        intent.putExtra("baseUrl", baseUrl)
        intent.putExtra("versionName", versionName)
        intent.putExtra("version", version)
        activity.startActivity(intent)
    }

}