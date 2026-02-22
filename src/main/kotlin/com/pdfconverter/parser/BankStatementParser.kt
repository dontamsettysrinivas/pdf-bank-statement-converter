package com.pdfconverter.parser

import com.pdfconverter.extraction.CharAndBound
import com.pdfconverter.headers.HeaderDetector
import com.pdfconverter.model.Transaction

/**
 * Parses bank statement pages to extract transaction data.
 * Uses character positioning and header detection to identify transaction rows.
 */
class BankStatementParser(
    private val headerDetector: HeaderDetector
) {
    
    /**
     * Parses a list of characters from a PDF page into transactions
     * 
     * @param characters List of characters with their bounding boxes
     * @return List of extracted transactions
     */
    fun parseTransactions(characters: List<CharAndBound>): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        
        // Group characters by vertical position (rows)
        val rows = groupCharactersByRow(characters)
        
        // Find header row
        val headerRow = rows.find { row ->
            headerDetector.isHeaderRow(row.second, row.first)
        }
        
        if (headerRow != null) {
            val headerIndex = rows.indexOf(headerRow)
            // Process rows after header
            for (i in (headerIndex + 1) until rows.size) {
                val transactionRow = rows[i]
                val transaction = parseTransactionRow(transactionRow.second)
                if (transaction != null) {
                    transactions.add(transaction)
                }
            }
        }
        
        return transactions
    }
    
    /**
     * Groups characters into rows based on their Y position
     */
    private fun groupCharactersByRow(
        characters: List<CharAndBound>
    ): List<Pair<Float, List<CharAndBound>>> {
        val threshold = 5.0f // Tolerance for same row detection
        val grouped = mutableMapOf<Float, MutableList<CharAndBound>>()
        
        characters.forEach { char ->
            val y = char.bound.y
            val existingKey = grouped.keys.find { kotlin.math.abs(it - y) < threshold }
            
            if (existingKey != null) {
                grouped[existingKey]?.add(char)
            } else {
                grouped[y] = mutableListOf(char)
            }
        }
        
        return grouped.map { (y, chars) -> Pair(y, chars) }
            .sortedBy { it.first }
    }
    
    /**
     * Parses a single transaction row into a Transaction object
     * TODO: Implement column detection and data extraction
     */
    private fun parseTransactionRow(characters: List<CharAndBound>): Transaction? {
        // Placeholder - needs implementation
        return null
    }
}
