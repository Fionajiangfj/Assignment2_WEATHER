package com.yj.weather_yan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.yj.weather_yan.data.WeatherRecord
import com.yj.weather_yan.data.WeatherRecordRepository
import com.yj.weather_yan.databinding.ActivityRecordsBinding
import kotlinx.coroutines.launch

class RecordsActivity : AppCompatActivity() {

    private var TAG = this@RecordsActivity.toString()
    private lateinit var binding: ActivityRecordsBinding
    private lateinit var adapter:AdapterActivity
    private lateinit var recordsList: MutableList<WeatherRecord>
    private lateinit var weatherRecordRepository: WeatherRecordRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityRecordsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        this.weatherRecordRepository = WeatherRecordRepository(application)
        this.recordsList = mutableListOf()
        
        // set up adapter
        this.adapter = AdapterActivity(recordsList)
        binding.rv.adapter = adapter
        binding.rv.layoutManager = LinearLayoutManager(this)
        binding.rv.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
    }

    override fun onStart() {
        super.onStart()
        
        // get records List
        weatherRecordRepository.allRecords?.observe(this){ weatherRecordsList ->
            if (weatherRecordsList.isNotEmpty()) {
                Log.d(TAG, "onStart: weatherRecordsList : $weatherRecordsList")
                recordsList.clear()
                recordsList.addAll(weatherRecordsList)
                adapter.notifyDataSetChanged()
            } else {
                Log.d(TAG, "onStart: No data received from observer")
            }
        }
    }
}