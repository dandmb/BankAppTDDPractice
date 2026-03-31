package com.dmb25.bankapp.di

import androidx.room.Room
import com.dmb25.bankapp.data.local.BankDatabase
import com.dmb25.bankapp.data.repository.TransactionRepositoryImpl
import com.dmb25.bankapp.domain.repository.TransactionRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),    // get() le contexte Android
            BankDatabase::class.java,
            "bank_database"
        ).build()
    }

    single {
        get<BankDatabase>().transactionDao()
    }


    single<TransactionRepository> {
        TransactionRepositoryImpl(
            transactionDao = get()
        )
    }
}