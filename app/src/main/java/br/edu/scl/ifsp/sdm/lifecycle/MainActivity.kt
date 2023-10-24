package br.edu.scl.ifsp.sdm.lifecycle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.EditText
import androidx.core.view.children
import androidx.core.widget.doAfterTextChanged
import br.edu.scl.ifsp.sdm.lifecycle.databinding.ActivityMainBinding
import br.edu.scl.ifsp.sdm.lifecycle.databinding.TilePhoneBinding
import java.lang.Thread.sleep

class MainActivity : AppCompatActivity() {
    private val activityMainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var filledChars: Int = 0
    private val nameHandler = object: Handler(Looper.myLooper()!!){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            msg.data.getString(NAME).let {
                activityMainBinding.nameEt.setText(it)
            }
        }
    }
    companion object {
        const val FILLED_CHARS = "FILLED_CHARS"
        const val PHONES = "PHONES"
        const val NAME = "NAME"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)

        activityMainBinding.apply {
            setSupportActionBar(toolbarIn.toolbar)

            nameEt.doAfterTextChanged {
                "${getString(R.string.filled_chars)} ${++filledChars}".also {
                    filledCharsTv.text = it
                }
            }

            addPhoneBt.setOnClickListener {
                val tilePhoneBinding = TilePhoneBinding.inflate(layoutInflater)
                phonesLl.addView(tilePhoneBinding.root)
            }

            openAnotherActivityBt.setOnClickListener {
                startActivity(Intent(this@MainActivity, AnotherActivity::class.java))
            }
        }
        supportActionBar?.subtitle = getString(R.string.main)

        Log.v(getString(R.string.app_name), "Main - onCreate(): início COMPLETO")

        Thread {
            sleep(3000)
            nameHandler.also {
                it.sendMessage(Message.obtain(it).apply {
                    data = Bundle().apply {
                        putString(NAME, "SDM")
                    }
                })
            }
        }.start()
    }

    override fun onStart() {
        super.onStart()
        runOnUiThread {
            activityMainBinding.nameEt.setText("SDM")
        }
        Log.v(getString(R.string.app_name), "Main - onStart(): início VISÍVEL")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(FILLED_CHARS, filledChars)

        val phones = mutableListOf<String>()
        activityMainBinding.phonesLl.children.forEachIndexed { index, view ->
            if (index != 0){
                (view as EditText).text.toString().let {
                    phones.add(it)
                }
            }
        }
        outState.putStringArray(PHONES, phones.toTypedArray())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.getInt(FILLED_CHARS, 0).let{
            filledChars = it
            "${getString(R.string.filled_chars)} ${filledChars}".also { fc ->
                activityMainBinding.filledCharsTv.text = fc
            }
        }

        savedInstanceState.getStringArray(PHONES)?.forEach { phone ->
            TilePhoneBinding.inflate(layoutInflater).apply {
                root.setText(phone)
                activityMainBinding.phonesLl.addView(root)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        Log.v(getString(R.string.app_name), "Main - onResumo(): início PRIMEIRO PLANO")
    }

    override fun onPause() {
        super.onPause()

        Log.v(getString(R.string.app_name), "Main - onPause(): fim PRIMEIRO PLANO")
    }

    override fun onStop() {
        super.onStop()

        Log.v(getString(R.string.app_name), "Main - onStop(): fim VISÍVEL")
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.v(getString(R.string.app_name), "Main - onDestroy(): fim COMPLETO")
    }

    override fun onRestart() {
        super.onRestart()

        Log.v(getString(R.string.app_name), "Main - onRestart(): preparanto onStart()")
    }
}