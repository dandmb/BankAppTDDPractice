package com.dmb25.bankapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dmb25.bankapp.data.local.dao.TransactionDao

import com.dmb25.bankapp.data.local.entity.AccountEntity
import com.dmb25.bankapp.data.local.entity.CategoryEntity
import com.dmb25.bankapp.data.local.entity.TransactionEntity
@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        AccountEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BankDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}