//package com.example.glucodialog.tests
//
//import android.content.Context
//import com.example.glucodialog.data.*
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.test.runTest
//import org.junit.Test
//import org.junit.Before
//import org.mockito.kotlin.*
//
//@OptIn(ExperimentalCoroutinesApi::class)
//class HealthAnalyzerTest {
//
//    private val context: Context = mock()
//    private val db: AppDatabase = mock()
//
//    private val glucoseDao: GlucoseDao = mock()
//    private val insulinDao: InsulinDao = mock()
//    private val foodDao: FoodDao = mock()
//    private val activityDao: ActivityDao = mock()
//    private val medicationDao: MedicationDao = mock()
//
//    private val baseTime = System.currentTimeMillis()
//
//    @Before
//    fun setup() {
//        whenever(db.glucoseDao()).thenReturn(glucoseDao)
//        whenever(db.insulinDao()).thenReturn(insulinDao)
//        whenever(db.foodDao()).thenReturn(foodDao)
//        whenever(db.activityDao()).thenReturn(activityDao)
//        whenever(db.medicationDao()).thenReturn(medicationDao)
//    }
//
//    @Test
//    fun `should warn about insufficient insulin dose`() = runTest {
//        val prevEntry = GlucoseEntry(id = 1, timestamp = baseTime - 3 * 60 * 60 * 1000, glucoseLevel = 5.0, note = "")
//        val newEntry = GlucoseEntry(id = 2, timestamp = baseTime, glucoseLevel = 7.2, note = "")
//
//        whenever(glucoseDao.getAllGlucoseEntriesOnce()).thenReturn(listOf(prevEntry))
//        whenever(insulinDao.getAllInsulinEntriesOnce()).thenReturn(listOf(
//            InsulinEntry(1, baseTime - 2_500_000, 5, 1)
//        ))
//        whenever(foodDao.getAllFoodEntriesOnce()).thenReturn(listOf(
//            FoodEntry(1, baseTime - 2_500_000, 1)
//        ))
//        whenever(activityDao.getAllActivityEntriesOnce()).thenReturn(emptyList())
//        whenever(medicationDao.getAllMedicationEntriesOnce()).thenReturn(emptyList())
//
//        HealthAnalyzer.analyzeGlucoseEntry(context, db, newEntry)
//
//        verify(glucoseDao).updateNoteForEntry(eq(2), contains("недостаточная доза инсулина"))
//    }
//
//    @Test
//    fun `should warn about hypoglycemia after insulin`() = runTest {
//        val prevEntry = GlucoseEntry(id = 1, timestamp = baseTime - 3 * 60 * 60 * 1000, glucoseLevel = 8.0, note = "")
//        val newEntry = GlucoseEntry(id = 2, timestamp = baseTime, glucoseLevel = 5.5, note = "")
//
//        whenever(glucoseDao.getAllGlucoseEntriesOnce()).thenReturn(listOf(prevEntry))
//        whenever(insulinDao.getAllInsulinEntriesOnce()).thenReturn(listOf(
//            InsulinEntry(1, baseTime - 2_500_000, 5, 1)
//        ))
//        whenever(foodDao.getAllFoodEntriesOnce()).thenReturn(emptyList())
//        whenever(activityDao.getAllActivityEntriesOnce()).thenReturn(emptyList())
//        whenever(medicationDao.getAllMedicationEntriesOnce()).thenReturn(emptyList())
//
//        HealthAnalyzer.analyzeGlucoseEntry(context, db, newEntry)
//
//        verify(glucoseDao).updateNoteForEntry(eq(2), contains("Риск гипогликемии"))
//    }
//
//    @Test
//    fun `should recommend quick carbs after activity`() = runTest {
//        val prevEntry = GlucoseEntry(id = 1, timestamp = baseTime - 1 * 60 * 60 * 1000, glucoseLevel = 9.0, note = "")
//        val newEntry = GlucoseEntry(id = 2, timestamp = baseTime, glucoseLevel = 5.5, note = "")
//
//        whenever(glucoseDao.getAllGlucoseEntriesOnce()).thenReturn(listOf(prevEntry))
//        whenever(insulinDao.getAllInsulinEntriesOnce()).thenReturn(emptyList())
//        whenever(foodDao.getAllFoodEntriesOnce()).thenReturn(emptyList())
//        whenever(activityDao.getAllActivityEntriesOnce()).thenReturn(listOf(
//            ActivityEntry(1, baseTime - 30 * 60 * 1000, 1)
//        ))
//        whenever(medicationDao.getAllMedicationEntriesOnce()).thenReturn(emptyList())
//
//        HealthAnalyzer.analyzeGlucoseEntry(context, db, newEntry)
//
//        verify(glucoseDao).updateNoteForEntry(eq(2), contains("приём быстрых углеводов"))
//    }
//}
