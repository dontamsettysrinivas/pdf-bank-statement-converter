package com.pdfconverter.export

import com.pdfconverter.model.Transaction
import java.io.File
import java.io.Writer
import java.time.format.DateTimeFormatter

/**
 * Exports transactions to CSV format.
 * Converts parsed transaction data into CSV files for further processing.
 */
class CsvExporter {
    
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    
    /**
     * Exports a list of transactions to a CSV file
     * 
     * @param transactions List of transactions to export
     * @param outputFile The file to write CSV data to
     */
    fun exportToCsv(transactions: List<Transaction>, outputFile: File) {
        outputFile.bufferedWriter().use { writer ->
            writeHeader(writer)
            transactions.forEach { transaction ->
                writeTransaction(writer, transaction)
            }
        }
    }
    
    /**
     * Exports transactions to a CSV string
     * 
     * @param transactions List of transactions to export
     * @return CSV formatted string
     */
    fun exportToCsvString(transactions: List<Transaction>): String {
        val builder = StringBuilder()
        
        // Write header
        builder.append("Date,Description,Amount,Balance,Type,Reference\n")
        
        // Write each transaction
        transactions.forEach { transaction ->
            builder.append(formatTransaction(transaction))
            builder.append("\n")
        }
        
        return builder.toString()
    }
    
    /**
     * Writes the CSV header row
     */
    private fun writeHeader(writer: Writer) {
        writer.write("Date,Description,Amount,Balance,Type,Reference\n")
    }
    
    /**
     * Writes a single transaction as a CSV row
     */
    private fun writeTransaction(writer: Writer, transaction: Transaction) {
        writer.write(formatTransaction(transaction))
        writer.write("\n")
    }
    
    /**
     * Formats a transaction as a CSV row string
     */
    private fun formatTransaction(transaction: Transaction): String {
        return listOf(
            transaction.date.format(dateFormatter),
            escapeCSV(transaction.description),
            transaction.amount.toPlainString(),
            transaction.balance?.toPlainString() ?: "",
            transaction.transactionType?.name ?: "",
            escapeCSV(transaction.reference ?: "")
        ).joinToString(",")
    }
    
    /**
     * Escapes special characters in CSV fields
     */
    private fun escapeCSV(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }
}
