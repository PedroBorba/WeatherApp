package br.com.weatherapp.feature.setting

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import br.com.weatherapp.Const
import br.com.weatherapp.R
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    private val sp by lazy {
        getSharedPreferences(Const.SHARED_PREFERENCE, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        initUI()
    }

    private fun initUI() {
        val isCelsius = sp.getBoolean(Const.PREF_IS_CELSIUS, true)
        val isPortuguese = sp.getBoolean(Const.PREF_IS_PORTUGUESE, true)
        rbC.isChecked = isCelsius
        rbF.isChecked = !isCelsius

        rbPt.isChecked = isPortuguese
        rbEn.isChecked = !isPortuguese

        btnSave.setOnClickListener {
            sp.edit().apply {
                putBoolean(Const.PREF_IS_CELSIUS, rbC.isChecked)
                putBoolean(Const.PREF_IS_PORTUGUESE, rbPt.isChecked)
                apply()
            }

            Toast.makeText(this, "Configurantions Saved!", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }
}
