package com.yj.weather_yan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yj.weather_yan.data.WeatherRecord

class AdapterActivity(var records:List<WeatherRecord>) : RecyclerView.Adapter<AdapterActivity.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder (itemView) {}

    // tell the function which layout file to use for each row of the recycler view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.activity_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return records.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.tv_data_city_adapter).text = "${records[position].cityName}"
        holder.itemView.findViewById<TextView>(R.id.tv_data_temp_adapter).text = "${records[position].temp}"
        holder.itemView.findViewById<TextView>(R.id.tv_data_humidity_adapter).text = "${records[position].humidity}"
        holder.itemView.findViewById<TextView>(R.id.tv_data_conditions_adapter).text = "${records[position].conditions}"
        holder.itemView.findViewById<TextView>(R.id.tv_data_time_adapter).text = "${records[position].datetime}"

    }

}
