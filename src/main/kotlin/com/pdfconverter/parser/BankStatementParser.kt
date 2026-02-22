package com.pdfconverter.parser

import com.pdfconverter.extraction.CharAndBound
import com.pdfconverter.headers.HeaderDetector
import com.pdfconverter.model.Transaction
import com.pdfconverter.model.TransactionType
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Parses bank statement pages to extract transaction data.
 * Uses character positioning and header detection to identify transaction rows.
 */
class BankStatementParser(
    private val headerDetector: HeaderDetector
) {
    
    private val datePatterns = listOf(
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("MM/dd/yyyy"),
        DateTimeFormatter.ofPattern("dd MMM yyyy")
    )
    
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
        
        return grouped.map { (y, chars) -> Pair(y, chars.sortedBy { it.bound.x }) }
            .sortedBy { it.first }
    }
    
    /**
     * Parses a single transaction row into a Transaction object
     * Detects columns by analyzing character positions and gaps
     */
    private fun parseTransactionRow(characters: List<CharAndBound>): Transaction? {
        if (characters.isEmpty()) return null
        
        // Group characters into columns based on X position gaps
        val columns = groupIntoColumns(characters)
        
        if (columns.size < 2) return null
        
        try {
            // Extract fields from columns
            var date: LocalDate? = null
            var description = ""
            var amount = BigDecimal.ZERO
            var balance: BigDecimal? = null
            var transactionType: TransactionType? = null
            
            // Try to identify columns
            for (i in columns.indices) {
                val columnText = columnsToText(columns[i])
                
                // Try to parse as date (usually first column)
                if (date == null && i < 2) {
                    date = parseDate(columnText)
                }
                
                // Try to parse as amount (usually has numbers with decimal)
                if (columnText.matches(Regex(".*\\d+[.,]\\d+.*"))) {
                    try {
                        val numericText = columnText.replace("[^0-9.,]".toRegex(), "")
                            .replace(",", ".")
                        val value = BigDecimal(numericText)
                        
                        // Larger value is likely balance, smaller is amount
                        if (balance == null || value > balance) {
                            if (balance != null) {
                                amount = balance
                            }
                            balance = value
                        } else {
                            amount = value
                        }
                    } catch (e: NumberFormatException) {
                        // Not a valid number, skip
                    }
                }
                
                // Collect description (text columns that are not date or amount)
                if (date == null || !columnText.matches(Regex(".*\\d+[.,]\\d+.*"))) {
                    if (description.isEmpty()) {
                        description = columnText
                    } else if (!columnText.matches(Regex("\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}"))) {
                        description += " " + columnText
                    }
                }
            }
            
            // Ensure we have at least date and amount
            if (date != null && (amount > BigDecimal.ZERO || balance != null)) {
                return Transaction(
                    date = date,
                    description = description.trim(),
                    amount = amount,
                    balance = balance,
                    transactionType = transactionType
                )
            }
            
        } catch (e: Exception) {
            // If parsing fails, return null
            return null
        }
        
        return null
    }
    
    /**
     * Groups characters into columns based on horizontal gaps
     */
    private fun groupIntoColumns(characters: List<CharAndBound>): List<List<CharAndBound>> {
        if (characters.isEmpty()) return emptyList()
        
        val sorted = characters.sortedBy { it.bound.x }
        val columns = mutableListOf<MutableList<CharAndBound>>()
        var currentColumn = mutableListOf(sorted[0])
        
        val gapThreshold = 20.0f // Minimum gap to consider a new column
        
        for (i in 1 until sorted.size) {
            val prev = sorted[i - 1]
            val curr = sorted[i]
            val gap = curr.bound.x - (prev.bound.x + prev.bound.width)
            
            if (gap > gapThreshold) {
                // Start new column
                columns.add(currentColumn)
                currentColumn = mutableListOf(curr)
            } else {
                // Add to current column
                currentColumn.add(curr)
            }
        }
        
        // Add last column
        if (currentColumn.isNotEmpty()) {
            columns.add(currentColumn)
        }
        
        return columns
    }
    
    /**
     * Converts column characters to text
     */
    private fun columnsToText(column: List<CharAndBound>): String {
        return column.joinToString("") { it.char.toString() }.trim()
    }
    
    /**
     * Attempts to parse a date string using multiple formats
     */
    private fun parseDate(text: String): LocalDate? {
        for (pattern in datePatterns) {
            try {
                return LocalDate.parse(text.trim(), pattern)
            } catch (e: DateTimeParseException) {
                // Try next pattern
            }
        }
        return null
    }
}
