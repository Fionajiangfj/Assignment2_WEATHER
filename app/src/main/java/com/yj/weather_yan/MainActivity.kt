package com.yj.weather_yan

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.yj.weather_yan.api.MyInterface
import com.yj.weather_yan.api.RetrofitInstance
import com.yj.weather_yan.data.WeatherRecord
import com.yj.weather_yan.data.WeatherRecordRepository
import com.yj.weather_yan.databinding.ActivityMainBinding
import com.yj.weather_yan.models.Weather
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val TAG:String = "MY_LOCATION_APP"
    private lateinit var binding:ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var weatherRecordRepository: WeatherRecordRepository

    // permissions array
    private val APP_PERMISSIONS_LIST = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    // showing the permissions dialog box & its result
    private val multiplePermissionsResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { resultsList ->
            Log.d(TAG, resultsList.toString())

            var allPermissionsGrantedTracker = true

            for (item in resultsList.entries) {
                if (item.key in APP_PERMISSIONS_LIST && item.value == false) {
                    allPermissionsGrantedTracker = false
                }
            }
            if (allPermissionsGrantedTracker == true) {
                Log.d(TAG, "multiplePermissionsResultLauncher: All permissions granted")
                getDeviceLocation()

            } else {
                Log.d(TAG, "multiplePermissionsResultLauncher: Some permissions NOT granted")
            }
    }

    private fun getDeviceLocation() {
        // helper function to get device location
        // Before running fusedLocationClient.lastLocation, CHECK that the user gave you permission for FINE_LOCATION and COARSE_LOCATION
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            multiplePermissionsResultLauncher.launch(APP_PERMISSIONS_LIST)
            return
        }

        // if YES, then go get the location, everything is fine ;)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                // Got last known location. In some rare situations this can be null.
                if (location === null) {
                    Log.d(TAG, "Location is null")
                    return@addOnSuccessListener
                }

                // Output the location
                val message = "The device is located at: ${location.latitude}, ${location.longitude}"
                Log.d(TAG, message)
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()

                getUserCity(location.latitude, location.longitude)

            }
    }

    private fun getUserCity(lat: Double, lng: Double){

        Log.d(TAG, "getUserCity: get user city function called.")
        // 1. get the geocoder
        val geocoder: Geocoder = Geocoder(applicationContext, Locale.getDefault())

        // 2. try to find a matching street address
        try {
            val searchResults:MutableList<Address>? = geocoder.getFromLocation(lat, lng, 1)
            if (searchResults == null) {
                Log.e(TAG, "getting Street Address: searchResults is NULL ")
                return
            }

            if (searchResults.size == 0) {
                Log.d(TAG, "Search results <= 0")
            } else {
                // 3. get the result
                val matchingAddress: Address = searchResults[0]

                // 4. output the properties of this address object
                Log.d(TAG, "Search results found")
                Log.d(TAG, "performForwardGeocoding: Locality " + matchingAddress.locality)

                // 5. prefill the text box with the current user location
                binding.edtCityName.setText(matchingAddress.locality)
            }
        } catch(ex:Exception) {
            Log.e(TAG, "Error encountered while getting coordinate location.")
            Log.e(TAG, ex.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // set up menu
        setSupportActionBar(this.binding.menuToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        // instantiate the fusedLocationProvider
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        this.weatherRecordRepository = WeatherRecordRepository(application)

        // get device location when the app launch
        multiplePermissionsResultLauncher.launch(APP_PERMISSIONS_LIST)

        // hide results section
        binding.apply {
            tvResults.isVisible = false
            tvLabelTemp.isVisible = false
            tvLabelHumidity.isVisible = false
            tvLabelConditions.isVisible = false
            tvLabelTime.isVisible = false
            tvDataTemp.isVisible = false
            tvDataHumidity.isVisible = false
            tvDataConditions.isVisible = false
            tvDataTime.isVisible = false
        }

        binding.btnGet.setOnClickListener {
            // get city name from user input
            val currCity = binding.edtCityName.text.toString()
            if (currCity.isEmpty()){
                binding.edtCityName.error = "Please enter a city name."
            }

            // get city coordinates
            // 1. Create an instance of the built in Geocoder class
            val geocoder:Geocoder = Geocoder(applicationContext, Locale.getDefault())

            Log.d(TAG, "Getting coordinates for $currCity")

            // 2. try to get the coordinate
            try {
                val searchResults:MutableList<Address>? = geocoder.getFromLocationName(currCity, 1)
                if (searchResults == null) {
                    Log.e(TAG, "searchResults variable is null")
                    return@setOnClickListener
                }

                if (searchResults.size == 0) {
                    Log.d(TAG, "onCreate: Search results are empty.")
                } else {
                    // 3. Get the coordinate
                    val foundLocation:Address = searchResults.get(0)
                    // 4. output to log
                    Log.d(TAG, "onCreate: foundLocation.latitude: ${foundLocation.latitude}")
                    Log.d(TAG, "onCreate: foundLocation.longitude: ${foundLocation.longitude}")
                    val geo = "${foundLocation.latitude.toString()},${foundLocation.longitude.toString()}"
                    Log.d(TAG, "onCreate: $geo")

                    // 5. call API with the coordinates
                    var api: MyInterface = RetrofitInstance.retrofitService
                    lifecycleScope.launch {
                        val currentWeather : Weather = api.getCurrentWeather("$geo")
                        Log.d(TAG, "onCreate: geo: ${currentWeather.toString()}")

                        binding.apply {
                            tvDataTemp.text = "${currentWeather.currentConditions.temp}"
                            tvDataHumidity.text = "${currentWeather.currentConditions.humidity}"
                            tvDataConditions.text = "${currentWeather.currentConditions.conditions}"
                            tvDataTime.text = "${currentWeather.currentConditions.datetime}"
                        }

                        // show results section
                        binding.apply {
                            tvResults.isVisible = true
                            tvLabelTemp.isVisible = true
                            tvLabelHumidity.isVisible = true
                            tvLabelConditions.isVisible = true
                            tvLabelTime.isVisible = true
                            tvDataTemp.isVisible = true
                            tvDataHumidity.isVisible = true
                            tvDataConditions.isVisible = true
                            tvDataTime.isVisible = true
                        }

                        Snackbar.make(binding.root, "Searching weather for $currCity completed!", Snackbar.LENGTH_SHORT).show()
                    }
                }
        }catch (ex: Exception) {
                Log.e(TAG, "Error encountered while getting coordinate location.")
                Log.e(TAG, ex.toString())
        }
    }

        binding.btnSave.setOnClickListener {
            // get weather data from UI
            val cityName = binding.edtCityName.text.toString()
            val tempFromUI = binding.tvDataTemp.text.toString().toDoubleOrNull()
            val humidityFromUI = binding.tvDataHumidity.text.toString().toDoubleOrNull()
            val conditionsFromUI = binding.tvDataConditions.text.toString()?:""
            val timeFromUI = binding.tvDataTime.text.toString()?:""

            if (tempFromUI != null && humidityFromUI != null && conditionsFromUI.isNotEmpty() && timeFromUI.isNotEmpty()){

                // insert to Room database
                val weatherToInsert = WeatherRecord(cityName = cityName, datetime = timeFromUI, temp = tempFromUI, humidity = humidityFromUI, conditions = conditionsFromUI)
                Log.d(TAG, "onCreate: weatherToInsert: $weatherToInsert")
                lifecycleScope.launch {
                    weatherRecordRepository.insertRecord(weatherToInsert)
                    Snackbar.make(binding.root, "Weather record saved!", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_items, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // decide where to do when menu links clicked
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.mi_weather_records-> {
                //navigate to green screen
                val greenIntent = Intent(this@MainActivity, RecordsActivity::class.java)
                startActivity(greenIntent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

