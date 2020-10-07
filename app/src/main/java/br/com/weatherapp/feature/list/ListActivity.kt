package br.com.weatherapp.feature.list

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.weatherapp.Const
import br.com.weatherapp.R
import br.com.weatherapp.api.RetrofitManager
import br.com.weatherapp.database.RoomManager
import br.com.weatherapp.entity.City
import br.com.weatherapp.entity.FavoriteCity
import br.com.weatherapp.entity.FindResult
import br.com.weatherapp.feature.setting.SettingActivity
import kotlinx.android.synthetic.main.activity_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListActivity : AppCompatActivity() {

    private val sp by lazy {
        getSharedPreferences(Const.SHARED_PREFERENCE, Context.MODE_PRIVATE)
    }

    private val roomManager by lazy { RoomManager.instance(this) }

    private val adapter = ListAdapter {salvarFavorito(it)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        getFavoriteCitiesAsync()
        initUI()
    }

    private fun salvarFavorito(city: City){
        salvarFavoritoAsync(this, city)
    }

    @SuppressLint("StaticFieldLeak")
    private fun salvarFavoritoAsync(context: Context, city: City) {
        val task = object : AsyncTask<Void, Void, List<FavoriteCity>>() {

            override fun onPreExecute() {
            }

            override fun doInBackground(vararg p0: Void?): List<FavoriteCity>? {
                var favoriteCities: List<FavoriteCity>? = null

                RoomManager.instance(this@ListActivity).getFavoriteDao().apply {
                    city.let {
                        val (id, name) = it
                        if (selectById(it.id) == null) {
                            addCity(FavoriteCity(id, name, true))
                        } else {
                            remove(FavoriteCity(id, name))
                        }
                    }

                    favoriteCities = getFavoriteCities()
                }

                return favoriteCities
            }

            override fun onPostExecute(favoriteCities: List<FavoriteCity>?) {
                super.onPostExecute(favoriteCities)
                favoriteCities?.let {
                    findCity(it)
                }
            }
        }
        task.execute()
    }

    @SuppressLint("StaticFieldLeak")
    private fun getFavoriteCitiesAsync() {
        val task = object : AsyncTask<Void, Void, List<FavoriteCity>>() {

            override fun onPreExecute() {
                progressBar.visibility = View.VISIBLE
            }

            override fun doInBackground(vararg p0: Void?): List<FavoriteCity> {
                return RoomManager.instance(this@ListActivity)
                    .getFavoriteDao()
                    .getFavoriteCities()
            }

            override fun onPostExecute(result: List<FavoriteCity>?) {
                super.onPostExecute(result)
                findCity(result)
            }
        }
        task.execute()
    }

    private fun initUI() {
        rvCities.apply {
            layoutManager = LinearLayoutManager(this@ListActivity)
            adapter = this@ListActivity.adapter
        }

        btnSearch.setOnClickListener {
            findCity(null)
        }
    }

    fun findCity(list: List<FavoriteCity>?) {
        if (isDeviceConnected()) {
            progressBar.visibility = View.VISIBLE

            var favoriteCities = list

            val isCelsius = sp.getBoolean(Const.PREF_IS_CELSIUS, true)
            val isPortuguese = sp.getBoolean(Const.PREF_IS_PORTUGUESE, true)

            var language = if(isPortuguese) { "pt" } else { "en"}
            var temperature = if(isCelsius) { "metric" } else { "imperial"}

            var call : Call<FindResult>? = null

            if(edtCityName.text.isNotBlank()){
                call = RetrofitManager
                    .getWeatherService()
                    .find(edtCityName.text.toString(), Const.APP_KEY, language, temperature)
            } else {
                if(favoriteCities == null){
                    getFavoriteCitiesAsync()
                } else {
                    favoriteCities?.let {
                        val ids = it.joinToString(","){"${it.id}"}
                        call = RetrofitManager
                            .getWeatherService()
                            .group(ids, Const.APP_KEY, language, temperature)
                    }
                }
            }


            call?.enqueue(object : Callback<FindResult> {

                override fun onFailure(call: Call<FindResult>, t: Throwable) {
                    Log.e("WELL", "Error", t)
                    progressBar.visibility = View.GONE
                }

                override fun onResponse(call: Call<FindResult>, response: Response<FindResult>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            if(favoriteCities != null && favoriteCities.size > 0){
                                it.items.forEach { it2 ->
                                    if(favoriteCities.contains(favoriteCities.find { fc -> fc.id == it2.id })){
                                        it2.favorite = true
                                    }
                                }
                            }
                            adapter.data(it.items)
                        }
                    }
                    progressBar.visibility = View.GONE
                }
            })
        } else {
            Toast.makeText(this,
                "Device is not connected. Try again later",
                Toast.LENGTH_LONG).show()
        }
    }

    fun isDeviceConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected();
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_setting) {
            startActivity(Intent(this,
                SettingActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
}
