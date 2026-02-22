package com.pdfconverter.model

import java.time.LocalDate
import java.math.BigDecimal

/**
 * Data class representing a bank transaction.
 * Extracted from PDF bank statement pages.
 */
data class Transaction(
    val date: LocalDate,
    val description: String,
    val amount: BigDecimal,
    val balance: BigDecimal? = null,
    val transactionType: TransactionType? = null,
    val reference: String? = null
)

/**
 * Enum representing the type of transaction
 */
enum class TransactionType {
    DEBIT,
    CREDIT,
    WITHDRAWAL,
    DEPOSIT
}
