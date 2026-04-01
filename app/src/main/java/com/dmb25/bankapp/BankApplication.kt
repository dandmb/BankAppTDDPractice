package com.dmb25.bankapp

import android.app.Application
import com.dmb25.bankapp.di.dataModule
import com.dmb25.bankapp.di.domainModule
import com.dmb25.bankapp.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BankApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()

            androidContext(this@BankApplication)

            modules(
                dataModule,
                domainModule,
                presentationModule
            )
        }
    }
}
