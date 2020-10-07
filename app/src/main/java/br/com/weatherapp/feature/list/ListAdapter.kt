package br.com.weatherapp.feature.list

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import br.com.weatherapp.Const
import br.com.weatherapp.R
import br.com.weatherapp.entity.City
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.row_city_layout.view.*

class ListAdapter(private val callback: (City) -> Unit)  : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private var items : List<City>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.row_city_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        items?.let { holder.bind(it[position], callback) }
    }

    fun data(items: List<City>) {
        this.items = items
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(city: City, callback: (City) -> Unit){
            if (city.weatherList.size > 0) {
                val icon = city.weatherList[0].icon
                val url = "http://openweathermap.org/img/w/$icon.png"

                Glide.with(itemView.context)
                    .load(url)
                    .placeholder(R.drawable.placeholder)
                    .into(itemView.imgIcon)

                val sp = itemView.context.getSharedPreferences(Const.SHARED_PREFERENCE, Context.MODE_PRIVATE)
                val temperatura =  if(sp.getBoolean(Const.PREF_IS_CELSIUS, true)){"°C"}else{"°F"}

                itemView.tvCityName.text = city.name +", " + city.pais.sigla
                itemView.tvCloudsState.text = city.weatherList[0].description
                itemView.tvTemperature.text = city.main.temp.toInt().toString()
                itemView.tvTemperatureType.text = temperatura

                val ventos = if(sp.getBoolean(Const.PREF_IS_PORTUGUESE, true)){"ventos "}else{"winds "}
                val nuvens = if(sp.getBoolean(Const.PREF_IS_PORTUGUESE, true)){"nuvens "}else{"clouds "}
                val outros =  ventos + city.wind.speed.toString() + "m/s | " + nuvens + city.clouds.all.toString() + "% | " + city.main.pressure.toInt().toString() + "hpa "
                itemView.tvOthersStates.text = outros

                mudarImagem(city.favorite)

                itemView.imgFavorito.setOnClickListener{
//                    Toast.makeText(itemView.context, "Cidade: ${city.name} Id: ${city.id}", Toast.LENGTH_LONG).show()
                    callback(city)
                    mudarImagem(city.favorite)
                }
            }
        }

        private fun mudarImagem(isFavorito: Boolean){
            if(isFavorito){
                itemView.imgFavorito.setImageBitmap(
                    BitmapFactory.decodeResource(itemView.context.resources, R.drawable.btn_star_big_on)
                )
            } else {
                itemView.imgFavorito.setImageBitmap(
                    BitmapFactory.decodeResource(itemView.context.resources, R.drawable.btn_star_big_off)
                )
            }
        }

    }

}