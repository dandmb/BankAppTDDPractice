package com.dmb25.bankapp.di

import android.content.Context
import androidx.room.Room
import com.dmb25.bankapp.data.local.BankDatabase
import com.dmb25.bankapp.data.repository.TransactionRepositoryImpl
import com.dmb25.bankapp.domain.repository.TransactionRepository
import io.mockk.mockk
import org.junit.After
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.check.checkModules

class KoinModulesTest : KoinTest {

    @After
    fun teardown() {
        stopKoin()
    }

    @Test
    fun verifyKoinModules() {
        val testModule = module {
            single<Context> { mockk(relaxed = true) }
            single<BankDatabase> { mockk(relaxed = true) }
            single { get<BankDatabase>().transactionDao() }
            single<TransactionRepository> {
                TransactionRepositoryImpl(get())
            }
        }

        startKoin {
            modules(testModule, domainModule)
        }.checkModules()
    }
}


