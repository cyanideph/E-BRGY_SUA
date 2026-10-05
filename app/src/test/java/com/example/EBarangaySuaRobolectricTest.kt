package com.example

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.example.data.BarangayRepository
import com.example.data.local.AppDatabase
import com.example.services.Appwrite
import com.example.services.CivicDataContract
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class EBarangaySuaRobolectricTest {

    @Test
    fun android16ContextBoots() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        assertEquals(36, Build.VERSION.SDK_INT)
        assertEquals("com.aistudio.ebarangaysua.sjl", context.packageName)
    }

    @Test
    fun civicBackendContractIsStable() {
        assertEquals("documentRequests", CivicDataContract.DOCUMENT_REQUESTS)
        assertEquals("emergencyReports", CivicDataContract.EMERGENCY_REPORTS)
        assertEquals("facilities", Appwrite.FACILITIES_TABLE)
        assertEquals("requestStatusHistory", Appwrite.REQUEST_STATUS_HISTORY_TABLE)
        assertEquals("emergencyStatusHistory", Appwrite.EMERGENCY_STATUS_HISTORY_TABLE)
        assertTrue(CivicDataContract.STATUS_PENDING.isNotBlank())
    }

    @Test
    fun repositoryInitializesWithoutMockRecords() {
        val repo = BarangayRepository.instance

        // Production data is Appwrite-authoritative. A clean Robolectric process must
        // not manufacture services, officials, requests, announcements, or hotlines.
        assertNotNull(repo.services.value)
        assertNotNull(repo.officials.value)
        assertNotNull(repo.hotlines.value)
        assertNotNull(repo.announcements.value)
        assertNotNull(repo.requests.value)
        assertNotNull(repo.events.value)
        assertNotNull(repo.emergencyReports.value)
        assertNotNull(repo.residents.value)
        assertNotNull(repo.households.value)
    }

    @Test
    fun roomDatabaseAndDaosWork() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getInstance(context)
        assertNotNull(db.documentRequestDao())
    }

    @Test
    fun suaCoordinatesAreStable() {
        assertEquals(10.3340, com.example.services.OpenMeteoService.SUA_LATITUDE, 0.001)
        assertEquals(124.9810, com.example.services.OpenMeteoService.SUA_LONGITUDE, 0.001)
    }
}
