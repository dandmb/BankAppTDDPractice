package com.dmb25.bankapp.di

import com.dmb25.bankapp.domain.usecase.AddTransactionUseCase
import com.dmb25.bankapp.domain.usecase.DeleteTransactionUseCase
import com.dmb25.bankapp.domain.usecase.GetBalanceUseCase
import com.dmb25.bankapp.domain.usecase.GetTransactionByIdUseCase
import com.dmb25.bankapp.domain.usecase.GetTransactionsUseCase
import org.koin.dsl.module

val domainModule = module {

    factory {
        AddTransactionUseCase(
            repository = get()
        )
    }

    factory {
        DeleteTransactionUseCase(
            repository = get()
        )
    }

    factory {
        GetBalanceUseCase(
            repository = get()
        )
    }

    factory {
        GetTransactionsUseCase(
            repository = get()
        )
    }

    factory {
        GetTransactionByIdUseCase(
            repository = get()
        )
    }
}