package damayanti.evi.covidinfo.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import damayanti.evi.covidinfo.R
import damayanti.evi.covidinfo.commons.CustomAdapter
import damayanti.evi.covidinfo.commons.LoadingAlert
import damayanti.evi.covidinfo.commons.RxBaseActivity
import damayanti.evi.covidinfo.commons.Utils
import damayanti.evi.covidinfo.database.DbHelper
import damayanti.evi.covidinfo.model.ChatDetailData
import damayanti.evi.covidinfo.service.ServiceGetData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_no_internet.view.bt_close_noinet
import kotlinx.android.synthetic.main.dialog_warning.view.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule


@Suppress("DEPRECATION", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : RxBaseActivity() {

    private var loading: Dialog? = null
    private var chatDetailsList = mutableListOf<ChatDetailData>()
    private lateinit var chatDetailsAdapter: CustomAdapter
    private lateinit var dbHelper : DbHelper
    private var sourceString: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loading = LoadingAlert.progressDialog(this, this)
        dbHelper = DbHelper(this)

        // update table for today only
        if(dbHelper.readDate().isEmpty())
            initData()
        else if(getDate() != dbHelper.readDate()[0]){
            dbHelper.deleteAllData()
            initData()
        }
        sourceString = "<br>Please following the statement to get information<br>" +
                "1. To get information about active cases in the countries please send reply : <br>&nbsp;&nbsp;   <b>CASES IN (Name Country)</b><br><br>" +
                "2. To get information about deaths cases in the countries please send reply : <br>&nbsp;&nbsp;&nbsp;   <b>DEATHS IN (Name Country)</b><br><br>" +
                "3. To get information about total active cases please send reply <br>&nbsp;&nbsp;&nbsp;     <b>CASES TOTAL</b><br><br>" +
                "4. To get information about total deaths cases please send reply <br>&nbsp;&nbsp;&nbsp;     <b>DEATHS TOTAL</b>"


        chatDetailsList.add(ChatDetailData("bot", ""+Html.fromHtml("<b>Hello WELCOME To COVID INFO</b>$sourceString"), ""+getTime()))
    }

    override fun onResume() {
        super.onResume()

        chatDetailsAdapter = CustomAdapter(chatDetailsList)
        val mLayoutManger = LinearLayoutManager(applicationContext)
        rv_chat_details.layoutManager = mLayoutManger
        rv_chat_details.adapter = chatDetailsAdapter

        send.setOnClickListener {
            if(inputEditText.text.toString().isNotEmpty()) {
                val textSend = inputEditText.text.toString()
                chatDetailsList.add(ChatDetailData("sender", "" + textSend, "" + getTime()))
                inputEditText.setText("")
                rv_chat_details.post {
                    chatDetailsAdapter.notifyDataSetChanged()
                    rv_chat_details.scrollToPosition(chatDetailsAdapter.itemCount-1)
                    Timer("Waiting..", false).schedule(800) {
                        getDataCovid(textSend)
                    }
                }


            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initData(){
        loading?.show()
        subscriptions.add(provideService()
                .getData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ resp ->
                    loading?.dismiss()
                    dbHelper.insertCountries(resp.Countries)
                    dbHelper.insertGlobal(resp.Global, resp.Date)

                }) {
                    err ->
                    loading?.dismiss()
                    if (err.localizedMessage.contains("resolve host")) {
                        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_no_internet, null)
                        val mBuilder = AlertDialog.Builder(this)
                                .setView(mDialogView)

                        val  mAlertDialog = mBuilder.setCancelable(false).show()

                        mDialogView.bt_close_noinet.setOnClickListener {
                            mAlertDialog.dismiss()
                        }

                    } else {

                        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_warning, null)
                        val mBuilder = AlertDialog.Builder(this)
                                .setView(mDialogView)

                        val  mAlertDialog = mBuilder.setCancelable(false).show()

                        mDialogView.bt_close_wrning.setOnClickListener {
                            mAlertDialog.dismiss()
                        }

                        mDialogView.title_wrning.text = "FAILED TO GET DATA! "

                        mDialogView.content_wrning.text = err.localizedMessage

                    }
                }
        )
    }

    private fun getDataCovid(msg:String){

        if(msg.toLowerCase(Locale.getDefault()).contains("cases total")){
            val total = formatNumber(dbHelper.getTotalCases()?.toDouble()!!)
            chatDetailsList.add(ChatDetailData("bot", "Total Active Cases $total", "" + getTime()))
            rv_chat_details.post {
                chatDetailsAdapter.notifyDataSetChanged()
                rv_chat_details.scrollToPosition(chatDetailsAdapter.itemCount-1)
            }
        }
        else if(msg.toLowerCase(Locale.getDefault()).contains("deaths total")){
            val total = formatNumber(dbHelper.getTotalCases()?.toDouble()!!)
            chatDetailsList.add(ChatDetailData("bot", "Total Deaths $total", "" + getTime()))
            rv_chat_details.post {
                chatDetailsAdapter.notifyDataSetChanged()
                rv_chat_details.scrollToPosition(chatDetailsAdapter.itemCount-1)
            }
        }
        else if(msg.toLowerCase(Locale.getDefault()).contains("cases in")){
            val countries: MutableList<String>
            val country = msg.substring(9, msg.length)
            val cases = dbHelper.getCasesCountries(country)

            if(cases!!.isEmpty()){
                var txtCountry = ""
                countries = dbHelper.getListCountry("%$country%")
                for(city in countries)
                    txtCountry = txtCountry+city+"\n"

                chatDetailsList.add(ChatDetailData("bot", "Sorry country not found, did you mean ?\n$txtCountry", "" + getTime()))
                rv_chat_details.post {
                    chatDetailsAdapter.notifyDataSetChanged()
                    rv_chat_details.scrollToPosition(chatDetailsAdapter.itemCount-1)
                }
            }
            else {
                val total = formatNumber(dbHelper.getCasesCountries(country)?.toDouble()!!)
                chatDetailsList.add(ChatDetailData("bot", country.toUpperCase(Locale.getDefault()) + " Active Cases " + total, "" + getTime()))
                rv_chat_details.post {
                    chatDetailsAdapter.notifyDataSetChanged()
                    rv_chat_details.scrollToPosition(chatDetailsAdapter.itemCount-1)
                }
            }
        }
        else if(msg.toLowerCase(Locale.getDefault()).contains("deaths in ")){
            val countries: MutableList<String>
            val country = msg.substring(10, msg.length)
            val cases = dbHelper.getCasesCountries(country)

            if(cases!!.isEmpty()){
                var txtCountry = ""
                countries = dbHelper.getListCountry("%$country%")
                for(city in countries)
                    txtCountry = txtCountry+city+"\n"

                chatDetailsList.add(ChatDetailData("bot", "Sorry country not found, did you mean ?\n$txtCountry", "" + getTime()))
                rv_chat_details.post {
                    chatDetailsAdapter.notifyDataSetChanged()
                    rv_chat_details.scrollToPosition(chatDetailsAdapter.itemCount-1)
                }
            }
            else {
                val total = formatNumber(dbHelper.getCasesCountries(country)?.toDouble()!!)
                chatDetailsList.add(ChatDetailData("bot", country.toUpperCase(Locale.getDefault()) + " Active Cases " + total, "" + getTime()))
                rv_chat_details.post {
                    chatDetailsAdapter.notifyDataSetChanged()
                    rv_chat_details.scrollToPosition(chatDetailsAdapter.itemCount-1)
                }
            }
        }
        else {
            chatDetailsList.add(ChatDetailData("bot", "Sorry i didn't understand your reply\n "+Html.fromHtml(sourceString), "" + getTime()))
            rv_chat_details.post {
                chatDetailsAdapter.notifyDataSetChanged()
                rv_chat_details.scrollToPosition(chatDetailsAdapter.itemCount-1)
            }
        }

    }

    private fun formatNumber(value: Double): String? {
        val df = DecimalFormat("###,###,###")
        return df.format(value)
    }


    private fun provideService(): ServiceGetData {
        val clientBuilder: OkHttpClient.Builder = Utils.buildClient()
        val retrofit = Retrofit.Builder()
                .baseUrl(Utils.ENDPOINT)
                .client(clientBuilder
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()
        return retrofit.create(ServiceGetData::class.java)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDate():String{
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(Date())
    }

    @SuppressLint("SimpleDateFormat")
    private fun getTime():String{
        val sdf = SimpleDateFormat("HH:mm")
        return sdf.format(Date())
    }

}