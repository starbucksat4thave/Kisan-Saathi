package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.AdvisoryCard
import com.example.data.model.AdvisorySeverity
import com.example.data.model.BarkatEstimate
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class KisanRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun getRoadmapNodes_returnsStandardBaraniWheatNodes() {
        val repository = KisanRepository(firestore)
        val nodes = repository.getRoadmapNodes("2026-11-06")
        assertTrue(nodes.isNotEmpty())
        assertEquals("land_prep", nodes[0].key)
        assertEquals("sowing", nodes[1].key)
    }

    @Test
    fun getSummerRotationOptions_ranksOptionsProperly() {
        val repository = KisanRepository(firestore)
        val options = repository.getSummerRotationOptions(incomeWeight = 1.0f)
        assertEquals(3, options.size)
        // Mungbean or Sorghum should rank higher than fallow
        assertTrue(options[0].id != "fallow")
    }

    @org.junit.Ignore("Requires running Firebase Auth & Firestore Emulator")
    @Test
    fun authenticatedFarmer_canRecordAdvisoryDone() = runBlocking {
        signInTestUser("rashid_test@dhudial.com")
        val repository = KisanRepository(firestore)

        val card = AdvisoryCard(
            id = "test_adv_1",
            key = "wattar_ok_sow_now",
            nodeKey = "sowing",
            severity = AdvisorySeverity.ACT,
            titleUr = "بوائی کریں",
            titleEn = "Sow Now",
            bodyUr = "زمین کا وتر ٹھیک ہے",
            bodyEn = "Soil moisture is good",
            actBy = "2026-11-10",
            sources = listOf("SMAP"),
            barkatIfDone = BarkatEstimate(1.5, "loss_factor_test")
        )

        val saved = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.markAdvisoryCompleted(card)
        }
        assertEquals(1.5, saved, 0.01)
    }

    @org.junit.Ignore("Requires running Firebase Firestore Emulator")
    @Test
    fun unauthenticatedCaller_cannotWriteField() = runBlocking {
        auth.signOut()
        val docRef = firestore.collection("users").document("unauth_uid").collection("fields").document("field_1")

        try {
            withTimeout(DEFAULT_TIMEOUT_MS) {
                val writeTask = docRef.set(
                    mapOf(
                        "userId" to "unauth_uid",
                        "name" to "Illegal Field",
                        "area_acre" to 4.0,
                        "centroid_lat" to 32.93,
                        "centroid_lon" to 72.86
                    )
                )
                writeTask.await()
            }
            fail("Expected PERMISSION_DENIED for unauthenticated write")
        } catch (e: Exception) {
            val firestoreException = e.unwrapFirestoreException()
            assertNotNull("Expected FirebaseFirestoreException", firestoreException)
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException?.code)
        }
    }

    private companion object {
        const val DEFAULT_TIMEOUT_MS = 6000L
    }
}
