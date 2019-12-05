package com.myapplication


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener


object PermissionUtils {

    interface PermissionsListener {
            /**
 * Function, which will execute onPermissionRequest.
 */
        fun onPermissionRequest(granted: Boolean)
    }

    fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) === PackageManager.PERMISSION_GRANTED
    }

    fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
        if (context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission
                    ) !== PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    private fun showPermissionsDeniedDialog(context: Context, title: String, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("GOTO SETTINGS",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()

                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            })
        builder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
        builder.show()
    }

    fun requestSMSPermissions(activity: Activity) {

        Dexter.withActivity(activity)
            .withPermission(Manifest.permission.RECEIVE_SMS)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {

                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {

                    showPermissionsDeniedDialog(
                        activity,
                        "SMS Permission",
                        "SMS permission is needed to read OTP code"
                    )
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }
    fun requestLocationPermissions(activity: Activity, listener: PermissionsListener) {

        Dexter.withActivity(activity)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (!report.areAllPermissionsGranted()) {

                        showPermissionsDeniedDialog(
                            activity,
                            "Location Permissions",
                            "Location permissions are needed to show you your current coordinates"
                        )

                    } else
                        listener.onPermissionRequest(true)
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }
    fun requestStoragePermissions(activity: Activity, listener: PermissionsListener) {

        Dexter.withActivity(activity)
            .withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (!report.areAllPermissionsGranted()) {

                        showPermissionsDeniedDialog(
                            activity,
                            "Storage Permissions",
                            "Storage permissions are needed to read and write files"
                        )

                    } else
                        listener.onPermissionRequest(true)
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    fun requestCameraPermissions(activity: Activity, listener: PermissionsListener) {

        Dexter.withActivity(activity)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    requestStoragePermissions(activity, listener)
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    listener.onPermissionRequest(false)

                    showPermissionsDeniedDialog(
                        activity,
                        "Camera Permission",
                        "Camera permission is needed to take pictures and videos"
                    )
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    fun requestContactsPermissions(activity: Activity, listener: PermissionsListener) {

        Dexter.withActivity(activity)
            .withPermission(Manifest.permission.READ_CONTACTS)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    listener.onPermissionRequest(true)
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    listener.onPermissionRequest(false)

                    showPermissionsDeniedDialog(
                        activity,
                        "Contacts Permission",
                        "Contacts permission is needed to sync your contacts"
                    )
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    fun requestCallPermissions(activity: Activity, listener: PermissionsListener) {

        Dexter.withActivity(activity)
            .withPermissions(
                Manifest.permission.USE_SIP,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (!report.areAllPermissionsGranted()) {
                        listener.onPermissionRequest(false)

                        showPermissionsDeniedDialog(
                            activity,
                            "Call Permissions",
                            "Telephone and Microphone permissions are needed to make and receive phone calls"
                        )

                    } else
                        listener.onPermissionRequest(true)
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

}
