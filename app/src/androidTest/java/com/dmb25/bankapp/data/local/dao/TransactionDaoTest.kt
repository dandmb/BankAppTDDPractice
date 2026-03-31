package com.dmb25.bankapp.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.dmb25.bankapp.data.local.BankDatabase
import com.dmb25.bankapp.data.local.entity.TransactionEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransactionDaoTest {

    private lateinit var database: BankDatabase
    private lateinit var dao: TransactionDao

    private fun makeEntity(
        id: Long = 0L,
        amount: Double = 100.0,
        type: String = "EXPENSE",
        accountId: Long = 1L,
        date: String = "2024-01-01T00:00:00"
    ) = TransactionEntity(
        id = id,
        amount = amount,
        description = "Test",
        type = type,
        categoryId = 1L,
        date = date,
        accountId = accountId
    )

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            BankDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.transactionDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun when_the_transaction_is_inserted_It_can_be_retrieved_by_id() = runTest {
        val entity = makeEntity(amount = 250.0)

        val insertedId = dao.insert(entity)

        val result = dao.getById(insertedId)
        assertNotNull(result)
        assertEquals(250.0, result!!.amount, 0.0)
    }

    @Test
    fun whenTheTransactionIsInserted_ItIsAssignedAnId() = runTest {
        val id = dao.insert(makeEntity())

        assert(id > 0)
    }


    @Test
    fun whenTheTransactionIsDeleted_ItIsNoLongerInDatabase() = runTest {
        val insertedId = dao.insert(makeEntity())
        val inserted = dao.getById(insertedId)!!

        dao.delete(inserted)

        val result = dao.getById(insertedId)
        assertNull(result)
    }

    @Test
    fun whenTheTransactionIsDeleted_ArowIsAffected() = runTest {
        val insertedId = dao.insert(makeEntity())
        val inserted = dao.getById(insertedId)!!

        val rowsAffected = dao.delete(inserted)

        assertEquals(1, rowsAffected)
    }


    @Test
    fun whenThereIsNoTransaction_FlowIsEmpty() = runTest {
        dao.getAll().test {
            val result = awaitItem()
            assertEquals(0, result.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenThreeTransactionsAreInserted_FlowContainsThem() = runTest {
        dao.insert(makeEntity(amount = 100.0))
        dao.insert(makeEntity(amount = 200.0))
        dao.insert(makeEntity(amount = 300.0))

        dao.getAll().test {
            val result = awaitItem()
            assertEquals(3, result.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenTheTransactionIsAdded_FlowIsUpdated() = runTest {
        dao.getAll().test {
            assertEquals(0, awaitItem().size)

            dao.insert(makeEntity())

            assertEquals(1, awaitItem().size)

            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun whenThereAreManyTransactions_OnlyCorrectOnesAreReturned() = runTest {
        // ARRANGE
        dao.insert(makeEntity(accountId = 1L, amount = 100.0))
        dao.insert(makeEntity(accountId = 1L, amount = 200.0))
        dao.insert(makeEntity(accountId = 2L, amount = 999.0)) // autre compte

        // ASSERT
        dao.getByAccount(accountId = 1L).test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assert(result.all { it.accountId == 1L })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenTransactionInserted_TheyAreOrderedByDate() = runTest {
        dao.insert(makeEntity(date = "2024-06-01T00:00:00"))
        dao.insert(makeEntity(date = "2024-01-01T00:00:00"))
        dao.insert(makeEntity(date = "2024-12-01T00:00:00"))

        dao.getAll().test {
            val result = awaitItem()
            assertEquals("2024-12-01T00:00:00", result[0].date)
            assertEquals("2024-06-01T00:00:00", result[1].date)
            assertEquals("2024-01-01T00:00:00", result[2].date)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
