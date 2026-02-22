package com.pdfconverter.export

import com.pdfconverter.model.Transaction
import com.pdfconverter.model.TransactionType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal
import java.time.LocalDate

/**
 * Unit tests for CsvExporter class
 */
class CsvExporterTest {
    
    private val exporter = CsvExporter()
    
    @Test
    fun `test exportToCsvString with single transaction`() {
        // Given
        val transaction = Transaction(
            date = LocalDate.of(2024, 1, 15),
            description = "Coffee Shop",
            amount = BigDecimal("25.50"),
            balance = BigDecimal("1500.00"),
            transactionType = TransactionType.DEBIT,
            reference = "REF123"
        )
        
        val transactions = listOf(transaction)
        
        // When
        val csv = exporter.exportToCsvString(transactions)
        
        // Then
        assertTrue(csv.contains("Date,Description,Amount,Balance,Type,Reference"))
        assertTrue(csv.contains("2024-01-15"))
        assertTrue(csv.contains("Coffee Shop"))
        assertTrue(csv.contains("25.50"))
        assertTrue(csv.contains("DEBIT"))
    }
    
    @Test
    fun `test CSV escaping for fields with commas`() {
        // Given
        val transaction = Transaction(
            date = LocalDate.of(2024, 1, 15),
            description = "Shop, Inc.",
            amount = BigDecimal("100.00"),
            balance = BigDecimal("2000.00")
        )
        
        // When
        val csv = exporter.exportToCsvString(listOf(transaction))
        
        // Then
        assertTrue(csv.contains("\"Shop, Inc.\""))
    }
    
    @Test
    fun `test multiple transactions export`() {
        // Given
        val transactions = listOf(
            Transaction(
                date = LocalDate.of(2024, 1, 15),
                description = "Transaction 1",
                amount = BigDecimal("50.00"),
                balance = BigDecimal("1000.00")
            ),
            Transaction(
                date = LocalDate.of(2024, 1, 16),
                description = "Transaction 2",
                amount = BigDecimal("75.00"),
                balance = BigDecimal("925.00")
            )
        )
        
        // When
        val csv = exporter.exportToCsvString(transactions)
        
        // Then
        val lines = csv.trim().split("\n")
        assertEquals(3, lines.size) // Header + 2 transactions
    }
}
