package com.dmb25.bankapp.di

import com.dmb25.bankapp.presentation.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    // viewModel { } — Koin gère le cycle de vie du ViewModel
    viewModel {
        HomeViewModel(
            getBalanceUseCase = get(),
            getTransactionsUseCase = get()
        )
    }
}