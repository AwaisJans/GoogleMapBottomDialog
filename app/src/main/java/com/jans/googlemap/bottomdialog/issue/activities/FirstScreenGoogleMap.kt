package com.jans.googlemap.bottomdialog.issue.activities

import android.Manifest.permission
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.jans.googlemap.bottomdialog.issue.R
import com.jans.googlemap.bottomdialog.issue.databinding.ActivityFirstScreenGoogleMapBinding


class FirstScreenGoogleMap : AppCompatActivity() {



    lateinit var binding: ActivityFirstScreenGoogleMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstScreenGoogleMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val builder: AlertDialog.Builder = AlertDialog.Builder(this,R.style.CustomAlertDialogTheme)


        binding.btnMap.setOnClickListener {
            if (checkLocationPerm()) {
                startActivity(Intent(this@FirstScreenGoogleMap, MapScreen::class.java))
            } else {
                builder.setTitle("Permission Required")
                    .setMessage("Location Permission is Important for this App")
                    .setPositiveButton("Allow") { dialog, _ ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(permission.ACCESS_FINE_LOCATION), 1
                        )
                        dialog.dismiss()
                    }
                    .setNegativeButton("Deny") { dialog, _ ->
                        Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        }

        binding.btnItemsScreen.setOnClickListener{
            if (checkLocationPerm()) {
                startActivity(Intent(this@FirstScreenGoogleMap, ItemsScreen::class.java))
            } else {
                builder.setTitle("Permission Required")
                    .setMessage("Location Permission is Important for this App")
                    .setPositiveButton("Allow") { dialog, _ ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(permission.ACCESS_FINE_LOCATION), 1
                        )
                        dialog.dismiss()
                    }
                    .setNegativeButton("Deny") { dialog, _ ->
                        Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        }

    }


    private fun checkLocationPerm(): Boolean {
        return (checkSelfPermission(permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }


}