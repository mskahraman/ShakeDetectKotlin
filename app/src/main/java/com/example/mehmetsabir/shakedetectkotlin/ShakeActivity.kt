package com.example.mehmetsabir.shakedetectkotlin

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.*

class ShakeActivity : AppCompatActivity() {

    private var mShakeDetector: DetectShake? = null
    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null
    private var count = 0
    private var textViewShakeTarget: TextView? = null
    private var imagePhone: ImageView? = null
    private var counter: TextView? = null
    private var radioGroup : RadioGroup? = null
    private var btnOk :  Button? = null
    private var btnCancel :  Button? = null
    private var targetShake: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shake)
        openDialogScreen()
        init()
        shakePhone()
    }

    fun init() {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        counter = findViewById(R.id.countshake)
        textViewShakeTarget = findViewById(R.id.shake_target)
        counter?.text = "$count"

    }

    private fun openDialogScreen() {


        val inflater = this.layoutInflater
        val view = inflater.inflate(R.layout.alertlayout, null)

        radioGroup = view.findViewById(R.id.rbtnGrpLevel)
        btnOk = view.findViewById(R.id.btnOkay)
        btnCancel = view.findViewById(R.id.btnCancel)

        val alert = AlertDialog.Builder(this)
        alert.setView(view)
        alert.setCancelable(false)
        val dialog = alert.create()

        btnOk?.setOnClickListener {

            changeLevel(radioGroup?.checkedRadioButtonId!!)

            dialog.cancel()
        }
        btnCancel?.setOnClickListener {
            dialog.cancel()
            targetShake = "7"
        }


        dialog.show()

    }

    private fun shakePhone(){
        val shake = AnimationUtils.loadAnimation(this, R.anim.shake_anim)
        imagePhone = findViewById(R.id.phone)
        imagePhone?.startAnimation(shake)
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        assert(mSensorManager != null)
        mAccelerometer = mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mShakeDetector = DetectShake(object : DetectShake.OnShakeListener {
            override fun onShake() {
                if (Integer.parseInt(textViewShakeTarget?.text as String) == count) {
                    Toast.makeText(this@ShakeActivity, "Bitti", Toast.LENGTH_SHORT).show()
                } else {
                    count += 1
                    counter?.text = "$count"
                }

            }
        })
    }
    private fun changeLevel(position : Int)  {

        when (position) {
            R.id.rbEasy -> {
                targetShake = "7"
            }
            R.id.rbMedium ->{
                targetShake= "10"
            }
            R.id.rbHard ->{
                targetShake= "15"
            }
            else -> {
                targetShake = "7"
                Toast.makeText(this@ShakeActivity,"Seçilmedi",Toast.LENGTH_LONG).show()
            }
        }
        textViewShakeTarget?.text = "$targetShake"
    }

    override fun onResume() {
        super.onResume()
        mSensorManager?.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        mSensorManager?.unregisterListener(mShakeDetector)
        super.onPause()
    }
}
