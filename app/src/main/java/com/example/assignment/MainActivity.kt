package com.example.assignment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.BatteryManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.auth.FirebaseAuthCredentialsProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

var counter = 0
var counter1 = 0
var handler: Handler = Handler()
var runnable:Runnable? = null
var delay = 900000
class MainActivity : AppCompatActivity() {
    private lateinit var cld:connection
    private lateinit var layout1:ConstraintLayout
    private lateinit var layout2:ConstraintLayout
     var batterystatus : TextView? = null
    private lateinit var latitude:TextView
    private lateinit var longitude:TextView
    private lateinit var btn: Button
    private lateinit var edt:EditText
    private lateinit var button: Button
    private lateinit var imgview:ImageView
    private lateinit var textView: TextView
     lateinit var imageUri: Uri
//     private val contract= registerForActivityResult(ActivityResultContracts.TakePicture()){
//         imgview.setImageURI(imageUri)
//     }
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
private var db =Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checknetwork()
//     edt = findViewById(R.id.edtname)
     btn = findViewById(R.id.save)
        btn.setOnClickListener {
            val lat = latitude.text.toString().trim()
            val long=longitude.text.toString().trim()
//            val edit=edt.text.toString().trim()
            val id:String = Settings.Secure.getString(contentResolver,Settings.Secure.ANDROID_ID)
val usermap = hashMapOf(
    "lattitude" to lat,
   "longitude" to long,
    "id" to id,
//"edt" to edit
)
            val userid=FirebaseAuth.getInstance().currentUser!!.uid
            db.collection("user").document(userid).set(usermap)
                .addOnSuccessListener {
                    Toast.makeText(this, "succeddfull", Toast.LENGTH_SHORT).show()
//            edt.text.clear()
                }
                .addOnFailureListener{
                    Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show()
                }

        }




//         btn.setOnClickListener {
//             var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//             startActivityForResult(intent,123)
//         }





//        imageUri = createimageuri()!!
//        btn.setOnClickListener {
//            contract.launch(imageUri)
//        }
        layout1 = findViewById(R.id.layout1)
        layout2 = findViewById(R.id.layout2)
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this)
        val id:String = Settings.Secure.getString(contentResolver,Settings.Secure.ANDROID_ID)
       val textView:TextView=findViewById(R.id.unique)
        textView.text = id
//        for finding location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        latitude = findViewById(R.id.latitude)
        longitude = findViewById(R.id.longitude)
       val button = findViewById<Button>(R.id.getlocation)
        button.setOnClickListener {
            fetchlocation()
        }

        batterystatus = findViewById(R.id.battery)
        registerReceiver(this.mbattery, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data){
//            if(requestCode == 123 ){
//                var bmp =data.extras.get("data") as Bitmap
//            }
//        }
//    }




//private  fun createimageuri(): Uri? {
//    val image = File(applicationContext.filesDir , "camera_photo.png")
//    return FileProvider.getUriForFile(applicationContext,"com.example.assignment.fileprovider",image)
//}




    private fun fetchlocation() {
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED){
 ActivityCompat.requestPermissions(this,
          arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),100)
            return
        }
        val location = fusedLocationProviderClient.lastLocation
        location.addOnSuccessListener{
            if(it != null){
                val textlatitude = "Latitude"+it.latitude.toString()
                val textlongitude = "Longitude"+it.longitude.toString()
                latitude.text=textlatitude
                longitude.text=textlongitude
            }
        }
    }


    private val mbattery:BroadcastReceiver=object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val scale = intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL,-1)
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1)
            val btn = level*100/scale.toFloat()
            batterystatus!!.text = "Battery status is $btn%"
        }
    }

    private fun checknetwork() {
        cld = connection(application)
        cld.observe(this) { isConnected ->
            if (isConnected) {
                layout1.visibility = View.VISIBLE
                layout2.visibility = View.GONE
            } else {
                layout1.visibility = View.GONE
                layout2.visibility = View.VISIBLE
            }
        }
    }

        override fun onResume() {
        handler.postDelayed(Runnable{
            val id:String = Settings.Secure.getString(contentResolver,Settings.Secure.ANDROID_ID)
            handler.postDelayed(runnable!!,delay.toLong())

            Toast.makeText(this, " $latitude", Toast.LENGTH_SHORT).show()
            Toast.makeText(this, " $longitude", Toast.LENGTH_SHORT).show()
            Toast.makeText(this, " $id", Toast.LENGTH_SHORT).show()
        }.also { runnable = it }, delay.toLong())
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable!!)
    }
}