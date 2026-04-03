package com.dmb25.bankapp.presentation.transaction.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmb25.bankapp.domain.model.Category
import com.dmb25.bankapp.domain.model.Transaction
import com.dmb25.bankapp.domain.model.TransactionType
import com.dmb25.bankapp.domain.usecase.AddTransactionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddTransactionViewModel(
    private val addTransactionUseCase: AddTransactionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddTransactionUiState>(AddTransactionUiState.Idle)
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _type = MutableStateFlow(TransactionType.EXPENSE)
    val type: StateFlow<TransactionType> = _type.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    private val _amountError = MutableStateFlow<String?>(null)
    val amountError: StateFlow<String?> = _amountError.asStateFlow()

    private val _descriptionError = MutableStateFlow<String?>(null)
    val descriptionError: StateFlow<String?> = _descriptionError.asStateFlow()

    private val _isFormValid = MutableStateFlow(false)
    val isFormValid: StateFlow<Boolean> = _isFormValid.asStateFlow()


    fun onAmountChange(value: String) {
        _amount.value = value
        validateAmount(value)
        updateFormValidity()
    }

    fun onDescriptionChange(value: String) {
        _description.value = value
        validateDescription(value)
        updateFormValidity()
    }

    fun onTypeChange(type: TransactionType) {
        _type.value = type
    }

    fun onCategoryChange(category: Category) {
        _selectedCategory.value = category
        updateFormValidity()
    }


    fun saveTransaction() {
        if (!_isFormValid.value) {
            validateAmount(_amount.value)
            validateDescription(_description.value)
            return
        }

        _uiState.value = AddTransactionUiState.Loading

        viewModelScope.launch {
            try {
                addTransactionUseCase(
                    Transaction(
                        amount = _amount.value.toDouble(),
                        description = _description.value,
                        type = _type.value,
                        category = _selectedCategory.value!!,
                        accountId = 1L
                    )
                )
                _uiState.value = AddTransactionUiState.Success
            } catch (e: Exception) {
                _uiState.value = AddTransactionUiState.Error(
                    message = e.message ?: "Une erreur est survenue"
                )
            }
        }
    }


    private fun validateAmount(value: String) {
        _amountError.value = when {
            value.isBlank() -> "Le montant doit être supérieur à 0"
            value.toDoubleOrNull() == null -> "Le montant doit être supérieur à 0"
            value.toDouble() <= 0 -> "Le montant doit être supérieur à 0"
            else -> null
        }
    }

    private fun validateDescription(value: String) {
        _descriptionError.value = when {
            value.isBlank() -> "La description ne peut pas être vide"
            else -> null
        }
    }

    private fun updateFormValidity() {
        _isFormValid.value = _amountError.value == null
                && _descriptionError.value == null
                && _amount.value.isNotBlank()
                && _description.value.isNotBlank()
                && _selectedCategory.value != null
    }
}
