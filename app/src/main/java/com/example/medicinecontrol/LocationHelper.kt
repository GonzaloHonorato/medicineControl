package com.example.medicinecontrol

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

object LocationHelper {

    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressWarnings("MissingPermission")
    suspend fun getCurrentLocation(context: Context): Location? {
        if (!hasLocationPermission(context)) return null
        return try {
            val client = LocationServices.getFusedLocationProviderClient(context)
            val cancellationToken = CancellationTokenSource()
            suspendCancellableCoroutine { cont ->
                client.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    cancellationToken.token
                ).addOnSuccessListener { location ->
                    cont.resume(location)
                }.addOnFailureListener {
                    cont.resume(null)
                }
                cont.invokeOnCancellation { cancellationToken.cancel() }
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getCityName(context: Context, lat: Double, lng: Double): String? {
        return try {
            val geocoder = Geocoder(context, Locale("es", "CL"))
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                // Prefer subAdminArea (comuna) then locality (city)
                address.subAdminArea ?: address.locality ?: address.adminArea
            } else null
        } catch (e: Exception) {
            null
        }
    }

    fun getFullAddress(context: Context, lat: Double, lng: Double): String? {
        return try {
            val geocoder = Geocoder(context, Locale("es", "CL"))
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val parts = listOfNotNull(
                    address.subAdminArea ?: address.locality,
                    address.adminArea
                )
                if (parts.isNotEmpty()) parts.joinToString(", ") else null
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
