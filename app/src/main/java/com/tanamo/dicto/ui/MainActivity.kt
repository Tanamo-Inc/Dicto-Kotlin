@file:Suppress("DEPRECATION")

package com.tanamo.dicto.ui


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.tanamo.dicto.R
import com.tanamo.dicto.db.Dict
import com.tanamo.dicto.util.Constant
import com.tanamo.dicto.util.Constant.App_name
import com.tanamo.dicto.util.Constant.DWORD
import com.tanamo.dicto.util.Constant.RMEANING
import com.tanamo.dicto.util.Constant.RWORD
import com.tanamo.dicto.util.Constant.TAG
import com.tanamo.dicto.util.Constant.UWORD
import com.tanamo.dicto.util.Constant.suV
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


/**
 * Created by ${TANDOH} on ${6/20/2017}.
 */

class MainActivity : AppCompatActivity() {

    private val ctx = this
    var dia: Dialog? = null
    var txt3: TextView? = null
    var txt4: TextView? = null
    var dict: Dict? = null
    val lis = ArrayList<String>()
    var ada: ArrayAdapter<String>? = null
    var word: String? = null
    var meaning: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        setContentView(R.layout.activity_main)
        dict = Dict(this@MainActivity)
        Proc().execute(RWORD)
        init()
        shwDialog()

    }

    private fun init() {
        Log.d(TAG, "init: ")
        val content: View = findViewById(R.id.content)

        setSupportActionBar(toolBar)

        sbT()

        tio.setText(R.string.app_name)

        navigation_view.setNavigationItemSelectedListener { menuItem ->
            drawer_layout.closeDrawers()
            val intent: Intent

            when {
                menuItem.itemId == R.id.home -> {

                    intent = Intent(this@MainActivity, Ques::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)

                }
                menuItem.itemId == R.id.rat -> try {
                    val sendIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.market_link) + App_name))
                    sendIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(sendIntent)
                } catch (exx: ActivityNotFoundException) {
                    val sendIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.playstore_link) + App_name))
                    sendIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(sendIntent)
                }
                menuItem.itemId == R.id.tell -> {
                    intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sha))
                    startActivity(Intent.createChooser(intent, getString(R.string.shavi)))

                }
                menuItem.itemId == R.id.more -> try {
                    val sendIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=tanacom&c=apps"))
                    sendIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(sendIntent)
                } catch (exx: ActivityNotFoundException) {
                    val sendIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.playstore_link) + App_name))
                    sendIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(sendIntent)
                }
                menuItem.itemId == R.id.feedback -> {
                    intent = Intent(Intent.ACTION_SEND)
                    intent.type = "message/rfc822"
                    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(Constant.AUTHOR_EMAIL_ADDRESS))
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.fb))
                    try {
                        startActivity(Intent.createChooser(intent, getString(R.string.fb)))
                    } catch (ex: ActivityNotFoundException) {
                        Toast.makeText(this@MainActivity, R.string.no_em_ins, Toast.LENGTH_SHORT).show()
                    }

                    startActivity(intent)
                }
                menuItem.itemId == R.id.quit -> {

                    val dialog = AlertDialog.Builder(this@MainActivity)
                    dialog.setTitle(R.string.app_name)
                    dialog.setIcon(R.drawable.ic_launcher)
                    dialog.setMessage(R.string.dwq)
                    dialog.setNegativeButton("No") { _, _ -> }
                    dialog.setPositiveButton("Yes") { _, _ ->
                        finish()
                        Toast.makeText(applicationContext, R.string.log_out_suc, Toast.LENGTH_SHORT).show()
                    }.show()

                }
            }

            false
        }

        val abt = ActionBarDrawerToggle(this@MainActivity, drawer_layout, toolBar, R.string.app_name, R.string.app_name)
        abt.isDrawerIndicatorEnabled = true
        drawer_layout.setDrawerListener(abt)
        abt.syncState()


        ada = ArrayAdapter(this, R.layout.spinner, R.id.spin, lis)
        searchw!!.setAdapter<ArrayAdapter<String>>(ada)
        searchw!!.threshold = 1

        ser!!.setOnClickListener {
            word = searchw!!.text.toString()
            if (word != null && word!!.trim { it <= ' ' } != "") {
                Proc().execute(RMEANING)
            } else {
                Snackbar.make(content, getString(R.string.word_to_ser), Snackbar.LENGTH_LONG).show()
            }
        }

        del!!.setOnClickListener {
            if (word != null && word!!.trim { it <= ' ' } != "") {
                Proc().execute(DWORD)
                searchw!!.text = null
                wordt!!.text = ""
                meanin!!.text = ""
            } else {
                Snackbar.make(content, getString(R.string.word_to_del), Snackbar.LENGTH_LONG).show()

            }


        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)

        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.abt -> About(this@MainActivity).show()

        }
        return super.onOptionsItemSelected(item)
    }

    private fun shwDialog() {
        Log.d(TAG, "shwDialog: ")
        dia = Dialog(ctx)
        dia!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dia!!.setContentView(R.layout.dialog)

        txt3 = dia!!.findViewById(R.id.words)
        txt4 = dia!!.findViewById(R.id.meanings)


        val but: ImageView = dia!!.findViewById(R.id.update)
        but.setOnClickListener {
            word = txt3!!.text.toString()
            meaning = txt4!!.text.toString()

            if (word != null && word!!.trim { it <= ' ' } != "" && meaning != null && meaning!!.trim { it <= ' ' } != "") {
                Proc().execute(UWORD)
                dia!!.hide()
            } else {
                Snackbar.make(content, getString(R.string.fill_all), Snackbar.LENGTH_LONG).show()
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class Proc : AsyncTask<Byte, Void, Byte>() {

        override fun doInBackground(vararg p0: Byte?): Byte? {
            Log.d(TAG, "doInBackground: ")
            val tag = p0[0]

            when (tag) {
                RWORD -> {
                    lis.clear()
                    lis.addAll(dict!!.getWord())
                    ada!!.notifyDataSetChanged()

                }
                RMEANING -> meaning = dict!!.getMeaning(word as String)
                UWORD -> {
                    val word = txt3!!.text.toString()
                    val meanin = txt4!!.text.toString()
                    dict!!.addDict(word, meanin)
                    lis.clear()
                    lis.addAll(dict!!.getWord())
                    ada!!.notifyDataSetChanged()

                }
                DWORD -> {
                    val word = wordt!!.text.toString()
                    val meanin = meanin!!.text.toString()
                    dict!!.delDict(word, meanin)
                    lis.clear()

                }
            }

            return tag

        }

        override fun onPostExecute(result: Byte?) {
            Log.d(TAG, "onPostExecute: ")
            val tag = result

            if (tag == RMEANING) {

                if (meaning != "") {
                    wordt!!.text = word
                    meanin!!.text = meaning

                } else {
                    txt3!!.text = word
                    dia!!.show()
                }


            }

            super.onPostExecute(result)
        }

    }

    @SuppressLint("InlinedApi")
    private fun sbT() {
        suV {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

}
