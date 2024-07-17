package com.appscrip.olamapdemo

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.PointF
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.appscrip.olamapdemo.R
import com.appscrip.olamapdemo.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.mapboxsdk.geometry.LatLng
import com.ola.maps.mapslibrary.models.OlaLatLng
import com.ola.maps.mapslibrary.models.OlaMapsConfig
import com.ola.maps.mapslibrary.models.OlaMarkerOptions
import com.ola.maps.navigation.ui.v5.MapStatusCallback
import com.appscrip.olamapdemo.adapter.OlaSearchAutoCompleteAdapter
import com.appscrip.olamapdemo.fragments.OlaAddressBottomSheet
import com.appscrip.olamapdemo.model.response.autocompletesearch.Prediction
import com.appscrip.olamapdemo.util.DataConstants.ADDRESS_DATA
import com.appscrip.olamapdemo.util.DataConstants.MAP_BASE_URL
import com.appscrip.olamapdemo.viewmodel.MapsViewModel
import com.appscrip.olamapdemo.viewmodel.OlaSearchAutoCompleteViewModel

class MainActivity : AppCompatActivity(), MapStatusCallback,
    OlaSearchAutoCompleteAdapter.OnAddressClickListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MapsViewModel by viewModels()
    private val autoCompleteViewModel: OlaSearchAutoCompleteViewModel by viewModels()
    private var isMapReady = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLatLng: LatLng
    private lateinit var textWatcher: TextWatcher
    private val searchAdapter = OlaSearchAutoCompleteAdapter(this)
    val addressDetailBottomSheet = OlaAddressBottomSheet()
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            olaMapsInit()
        } else {
            Toast.makeText(this, "The app needs Location permission to work", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.olaMapView.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()

    }

    private val markerViewOptions = OlaMarkerOptions.Builder()
        .setIconIntRes(R.drawable.ic_location)
        .setMarkerId("1")
        .setIconSize(0.05f)
        .build()


    override fun onMapReady() {
        isMapReady = true
        binding.olaMapView.apply {
            setFloatingPinToLocation(OlaLatLng(13.035450, 77.597930), true)
            val shashankLocation = LatLng(13.035450, 77.597930)
            addMarker(shashankLocation, "my", R.drawable.ic_shashank_logo, true)
        }
        Toast.makeText(this, "map is ready", Toast.LENGTH_SHORT).show()

        if (isMapReady) {
            binding.olaMapView.apply {
                moveToCurrentLocation()
            }
            binding.rvSearchedLocation.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = searchAdapter
            }
            currentLocation()
            searchLocation()
            subscribeGetLocation()
        }
    }

    /*
    * This function for current location
    */
    private fun currentLocation() {
        binding.cvCurrentLocation.setOnClickListener {
            binding.olaMapView.moveToCurrentLocation()
        }
    }

    override fun onMapLoadFailed(p0: String?) {
        Log.d("ola", "map loading failed: $p0")
        p0?.let {
            val errorCode = p0.substring(it.lastIndex - 2)
            if (errorCode == "401") {
                viewModel.getAccessToken(
                    clientId = BuildConfig.CLIENT_ID,
                    clientSecret = BuildConfig.CLIENT_SECRET,
                    onSuccess = { olaMapsInit() }
                )
            }

        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            olaMapsInit()
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        currentLatLng = LatLng(it.latitude, it.longitude)

                        binding.olaMapView.addHuddleMarkerView(
                            olaLatLng = OlaLatLng(
                                latitude = currentLatLng.latitude,
                                longitude = currentLatLng.longitude
                            ),
                            headerText = "Current Location",
                            descriptionText = "This is your location"
                        )

                    }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        "unable to fetch current latitude, longitude",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            locationPermissionRequest.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun olaMapsInit() {
        binding.olaMapView.initialize(
            mapStatusCallback = this,
            olaMapsConfig = OlaMapsConfig.Builder()
                .setApplicationContext(applicationContext) //pass the application context here, it is mandatory
                .setClientId(BuildConfig.CLIENT_ID) //pass the Organization ID here, it is mandatory
                .setMapBaseUrl(MAP_BASE_URL) // pass the Base URL of Ola-Maps here (Stage/Prod URL), it is mandatory
                .setInterceptor { chain ->
                    val originalRequest = chain.request()

                    val newRequest = originalRequest.newBuilder()
                        .addHeader(
                            "Authorization",
                            "Bearer ${viewModel.accessToken}"
                        )
                        .build()

                    chain.proceed(newRequest)
                } // Instance of okhttp3.Interceptor for with Bearer access token, it is mandatory
                .setMinZoomLevel(4.0)
                .setMaxZoomLevel(21.0)
                .setZoomLevel(14.0)
                .build()
        )
    }

    private fun searchLocation() {
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                autoCompleteViewModel.callSearchAutoCompleteApi(input = s.toString())
            }

            override fun afterTextChanged(p0: Editable?) {}
        }
        binding.etSearchLocation.addTextChangedListener(textWatcher)
    }

    private fun subscribeGetLocation() {
        autoCompleteViewModel.mSearchedLocationLiveData.observe(this) {
            Log.d("Searched", it.predictions.toString())
            searchAdapter.submitList(it.predictions)
            binding.rvSearchedLocation.visibility =
                if (it.predictions?.isNotEmpty() == true) View.VISIBLE else View.GONE
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etSearchLocation.windowToken, 0)
    }

    private fun addressMapDetailBottomSheet(item: Prediction?) {
        supportFragmentManager.let {
            addressDetailBottomSheet.show(it, OlaAddressBottomSheet.TAG)
            val bundle = Bundle()
            bundle.putParcelable(ADDRESS_DATA, item)
        }
    }

    override fun onStop() {
        super.onStop()
        binding.olaMapView.onStop()
    }

    override fun onDestroy() {
        binding.olaMapView.onDestroy()
        super.onDestroy()
    }

    override fun onAddressSelected(
        item: Prediction?,
        position: Int,
        mListener: OlaSearchAutoCompleteAdapter.OnAddressClickListener
    ) {
        binding.etSearchLocation.removeTextChangedListener(textWatcher)
        binding.rvSearchedLocation.visibility = View.GONE
        binding.etSearchLocation.setText(item?.description)
        hideKeyboard()
        binding.etSearchLocation.clearFocus()
        binding.etSearchLocation.addTextChangedListener(textWatcher)
        if (item != null) {
            binding.olaMapView.moveCameraToLatLong(
                item.geometry?.location?.lng?.let {
                    OlaLatLng(
                        item.geometry.location.lat!!,
                        it, 0.0
                    )
                }, 14.0
            )
        }
        //addressMapDetailBottomSheet(item)
    }
}