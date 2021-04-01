package com.example.fingerprintscanner

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CancellationSignal
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var cancellationSignal: CancellationSignal? = null

    private val authenticationCallback: BiometricPrompt.AuthenticationCallback
        get() = @RequiresApi(Build.VERSION_CODES.P)
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                notifuUser("Ошибка при авторизации $errString")
                super.onAuthenticationError(errorCode, errString)
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                notifuUser("Вы усешно авторизовались!")
                startActivity(Intent(this@MainActivity, SecondActivity::class.java))
                super.onAuthenticationSucceeded(result)
            }
        }


    private fun notifuUser(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkBiomatricSupport()

        btn.setOnClickListener {
            val biometricPrompt = BiometricPrompt.Builder(this).setTitle("Заголовок").setSubtitle("Подзаголовок").setDescription("Описание цели").setNegativeButton("Отмена", this.mainExecutor, DialogInterface.OnClickListener { dialog, which ->
                notifuUser("Авторизация отменена")
            }).build()

            biometricPrompt.authenticate(getCansellationSignal(), mainExecutor,authenticationCallback)
        }


    }

    private fun getCansellationSignal(): CancellationSignal {
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            notifuUser("Пользователь оменил скан отпечатка пальца")
        }
        return cancellationSignal as CancellationSignal
    }


    private fun checkBiomatricSupport(): Boolean {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (keyguardManager.isKeyguardSecure) {
            notifuUser("аутентификация по отпечатку пальца не была включена в настройках")
            return false
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED)
        {
            notifuUser("Вы не дали разрешение на работу с отпечатком пальца")
        }

        return if(packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT))
        {
            true
        }else
        {
            true
        }

    }
}