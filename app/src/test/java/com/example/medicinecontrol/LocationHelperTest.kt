package com.example.medicinecontrol

import android.Manifest
import android.app.Application
import android.location.Geocoder
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class LocationHelperTest {

    @Test
    fun `hasLocationPermission retorna false sin permisos`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val result = LocationHelper.hasLocationPermission(app)
        assertFalse(result)
    }

    @Test
    fun `hasLocationPermission retorna true con permiso FINE`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val shadowApp = Shadows.shadowOf(app)
        shadowApp.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)

        val result = LocationHelper.hasLocationPermission(app)
        assertTrue(result)
    }

    @Test
    fun `hasLocationPermission retorna true con permiso COARSE`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val shadowApp = Shadows.shadowOf(app)
        shadowApp.grantPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)

        val result = LocationHelper.hasLocationPermission(app)
        assertTrue(result)
    }
}
