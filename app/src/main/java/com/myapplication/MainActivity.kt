package com.myapplication

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), LocationListener {

    private val locationManager: LocationManager? = null
    private val locationListener: LocationListener? = null
    private val context: Context? = null
    private val SELECT_LOCATION = 8
    private val dialog: ProgressDialog? = null
    internal var locationProviderClient: FusedLocationProviderClient? = null
    private var coordinates: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        niko_btn.setOnClickListener(View.OnClickListener {
            fetchLocation() })



    }
    private fun fetchLocation() {
        if (!checkLocationPermission())


        if (locationManager != null) {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER
                )
            ) {
                buildAlertMessageNoGps()
            } else {
                locationProviderClient?.getLastLocation()?.addOnSuccessListener { location ->
                    if (location != null) {
                        var coordinates: String? = null
                        try {
                            coordinates =
                                "Longitude: " + location.longitude + "  \nLatitude: " + location.latitude.toString()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                        niko_et?.setText(coordinates)
                        if (dialog != null) {
                            if (dialog.isShowing()) {
                                dialog.dismiss()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        val granted = booleanArrayOf(false)

        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {

                    if (!report.areAllPermissionsGranted()) {
                        DialogOnAnyDeniedMultiplePermissionsListener.Builder
                            .withContext(this@MainActivity)
                            .withTitle("Location permission")
                            .withMessage("Location permissions are required to send location")
                            .withButtonText(android.R.string.ok)
                            .build()
                    } else if (report.areAllPermissionsGranted()) {

                        if (locationManager != null) {
                            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(
                                    LocationManager.NETWORK_PROVIDER
                                )
                            ) {
                                buildAlertMessageNoGps()
                            } else {


                                if (ActivityCompat.checkSelfPermission(
                                        applicationContext,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                        applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION
                                    ) != PackageManager.PERMISSION_GRANTED
                                ) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return
                                }

                                if (dialog != null) {
                                    dialog.show()
                                }
                                locationProviderClient?.getLastLocation()
                                    ?.addOnSuccessListener { location ->
                                        if (dialog != null) {
                                            dialog.setCancelable(false)
                                        }

                                        if (location != null) {
                                            //                                        dialog.dismiss();
                                            coordinates = null
                                            try {
                                                coordinates =
                                                    "Longitude: " + location.longitude + "  \nLatitude: " + location.latitude.toString()
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }

                                            // Pass the coordinates to the Map View.
                                            if (dialog != null) {
                                                if (dialog.isShowing()) {
                                                    dialog.dismiss()
                                                }
                                            }
                                        }
                                    }?.addOnCompleteListener {
                                        //                                        dialog.dismiss();
                                        niko_et?.setText(coordinates)
                                        if (dialog != null) {
                                            if (dialog.isShowing()) {
                                                dialog.dismiss()
                                            }
                                        }
                                    }
                            }
                        }
                    } else {

                        granted[0] = true
                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // permission is denied permenantly, navigate user to app settings
                        // show alert dialog navigating to Settings
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()

        return granted[0]
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialg, id ->
                //                        dialog.setMessage("Getting co-ordinates, please wait.");
                //                        dialog.show();
                startActivityForResult(
                    Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                    SELECT_LOCATION
                )
            }
            .setNegativeButton(
                "No"
            ) { dialog, id -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }
        private fun showSettingsDialog() {
val builder = AlertDialog.Builder(this@MainActivity)
builder.setTitle("Need Permissions")
builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
builder.setPositiveButton("GOTO SETTINGS"
) { dialog, which ->
    dialog.cancel()
    openSettings()
}
            builder.setNegativeButton("Cancel", object:DialogInterface.OnClickListener {
override fun onClick(dialog:DialogInterface, which:Int) {
dialog.cancel()
}
})
builder.show()

}


    // navigating user to app settings
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }
    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderEnabled(p0: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderDisabled(p0: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLocationChanged(p0: Location?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
