package com.example

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.example.services.Appwrite
import com.example.services.CivicDataContract
import org.junit.Assert.assertEquals
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
}
