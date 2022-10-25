package com.bashirli.kotlinlocationbook.view

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.bashirli.kotlinlocationbook.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.bashirli.kotlinlocationbook.databinding.ActivityMapsBinding
import com.bashirli.kotlinlocationbook.model.Data
import com.bashirli.kotlinlocationbook.roomdb.RoomDAO
import com.bashirli.kotlinlocationbook.roomdb.RoomDB
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMapLongClickListener {
    var compositeDisposable=CompositeDisposable()
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    lateinit var activityResultLauncher: ActivityResultLauncher<String>
    lateinit var sharedPreferences: SharedPreferences
    var info:Boolean?=null
    var selectedLatitude:Double?=null
    var selectedLongitude:Double?=null
    private  lateinit var roomDAO: RoomDAO
    private lateinit var roomDB: RoomDB
    lateinit var oldData:Data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        activity_launcher()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
       sharedPreferences=this.getSharedPreferences("com.bashirli.kotlinlocationbook", MODE_PRIVATE)
    info=false
        roomDB= Room.databaseBuilder(applicationContext,RoomDB::class.java,"Book").build()
        roomDAO=roomDB.getDAO()

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if(intent.getStringExtra("info").toString().equals("old")){
            oldData=intent.getSerializableExtra("data") as Data
            binding.button.visibility=View.GONE
            binding.button2.visibility=View.VISIBLE
            binding.editTextTextPersonName.setText(oldData.name)
            var oldLat = oldData.latitude
            var oldLong=oldData.longitude
            mMap.addMarker(MarkerOptions().position(LatLng(oldLat,oldLong)).title("Selected Land"))

        }else{
            mMap.clear()
            binding.button2.visibility=View.GONE
            binding.button.visibility=View.VISIBLE
            binding.editTextTextPersonName.setText("")
        }

        locationManager=this.getSystemService(LOCATION_SERVICE)as LocationManager
        locationListener=object:LocationListener {
            override fun onLocationChanged(p0: Location) {
                info=sharedPreferences.getBoolean("info",false)
                if(!info!!){
                    var latLng=LatLng(p0.latitude,p0.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f))
                  sharedPreferences.edit().putBoolean("info",true).apply()

               }
            }

        }

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                Snackbar.make(binding.root,"Icazə yoxdur!",Snackbar.LENGTH_INDEFINITE).setAction("Icazə ver!"){
                    //permission
                    activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }.show()



            }else{
                //permission
                activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }else{
            //granted
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
            mMap.isMyLocationEnabled=true
        var lastLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if(lastLocation!=null){
                val lastUserLocation=LatLng(lastLocation.latitude,lastLocation.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15f))
            }

           mMap.setOnMapLongClickListener(this)


        }


    }

    fun activity_launcher(){
        activityResultLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){
         if(it){
             if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
mMap.isMyLocationEnabled=true
             var lastLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
             if(lastLocation!=null){
                 val lastUserLocation=LatLng(lastLocation.latitude,lastLocation.longitude)
                 mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15f))
             }
         }else{
         Toast.makeText(this,"Icazə yoxdur",Toast.LENGTH_LONG).show()
         }
        }
    }

    fun problem():Int{
        if(binding.editTextTextPersonName.text.toString().equals("")){
            Toast.makeText(applicationContext,"Boş xana var",Toast.LENGTH_LONG).show()
            return 0
        }
        if(selectedLatitude==null||selectedLongitude==null){
            Toast.makeText(applicationContext,"Ərazi seçilməyib",Toast.LENGTH_LONG).show()
            return 0
        }
        return 1
    }

    fun save(view:View){
        if(problem()==0){
            return
        }
        var data=Data(binding.editTextTextPersonName.text.toString(),selectedLatitude!!,selectedLongitude!!)
       compositeDisposable.add(roomDAO.insert(data).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::handler))
    }

    fun handler(){
        var intent=Intent(this@MapsActivity,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun delete(view: View){
        var alert=AlertDialog.Builder(this)
        alert.setTitle("Delete").setMessage("Are you sure to delete?").setNegativeButton("No"){dialog,which->
            return@setNegativeButton
        }.setPositiveButton("Yes"){dialog,which->
            compositeDisposable.add(roomDAO.delete(oldData).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::handler))
        }.show()


    }

    override fun onMapLongClick(p0: LatLng) {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(p0).title("Seçilmiş məkan"))
    selectedLongitude=p0.longitude
        selectedLatitude=p0.latitude

    }


}