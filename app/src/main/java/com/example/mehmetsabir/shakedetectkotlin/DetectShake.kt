package com.example.mehmetsabir.shakedetectkotlin

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.Log
import android.widget.Toast

class DetectShake(shakeListener :OnShakeListener ) : SensorEventListener {

    private val MIN_SHAKE_ACCELERATION = 8
    private val MIN_MOVEMENTS = 2
    private val MAX_SHAKE_DURATION = 500
    private val mGravity = floatArrayOf(0.0f, 0.0f, 0.0f)
    private val mLinearAcceleration = floatArrayOf(0.0f, 0.0f, 0.0f)
    private val X = 0
    private val Y = 1
    private val Z = 2
    private var mShakeListener: OnShakeListener? = null
    private var startTime: Long = 0
    private var moveCount = 0

    init {
        mShakeListener = shakeListener
    }


    override fun onSensorChanged(event: SensorEvent?) {
        setCurrentAcceleration(event!!)

        val maxLinearAcceleration = getMaxCurrentLinearAcceleration()


        if (maxLinearAcceleration > MIN_SHAKE_ACCELERATION) {
            val now = System.currentTimeMillis()


            if (startTime == 0L) {
                startTime = now
            }

            val elapsedTime = now - startTime


            if (elapsedTime > MAX_SHAKE_DURATION) {

                resetShakeDetection()
            } else {

                moveCount++


                if (moveCount > MIN_MOVEMENTS) {

                    mShakeListener?.onShake()


                    resetShakeDetection()
                }
            }
        }
    }

    private fun setCurrentAcceleration(event: SensorEvent) {

        val alpha = 0.8f

        // Gravity components of x, y, and z acceleration
        mGravity[X] = alpha * mGravity[X] + (1 - alpha) * event.values[X]
        mGravity[Y] = alpha * mGravity[Y] + (1 - alpha) * event.values[Y]
        mGravity[Z] = alpha * mGravity[Z] + (1 - alpha) * event.values[Z]

        // Linear acceleration along the x, y, and z axes (gravity effects removed)
        mLinearAcceleration[X] = event.values[X] - mGravity[X]
        mLinearAcceleration[Y] = event.values[Y] - mGravity[Y]
        mLinearAcceleration[Z] = event.values[Z] - mGravity[Z]

    }

    private fun getMaxCurrentLinearAcceleration(): Float {

        var maxLinearAcceleration = mLinearAcceleration[X]

        if (mLinearAcceleration[Y] > maxLinearAcceleration) {
            maxLinearAcceleration = mLinearAcceleration[Y]
        }
        if (mLinearAcceleration[Z] > maxLinearAcceleration) {
            maxLinearAcceleration = mLinearAcceleration[Z]
        }
        return maxLinearAcceleration
    }

    private fun resetShakeDetection() {
        startTime = 0
        moveCount = 0
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("onAccuracyChanged","onAccuracyChanged worked on.")
    }

    interface OnShakeListener {
        fun onShake()
    }
}