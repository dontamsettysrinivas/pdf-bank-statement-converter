package com.pdfconverter

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File

/**
 * Main class for converting bank statement PDFs to CSV format.
 * Follows the architecture from Angus Cheng's Bank Statement Converter.
 * 
 * https://bankstatementconverter.com/book
 */
class BankStatementConverter {
    
    /**
     * Extracts transactions from a PDF bank statement file
     * 
     * @param pdfPath Path to the PDF file
     * @return List of Transaction objects extracted from the PDF
     */
    fun extractTransactions(pdfPath: String): List<Transaction> {
        try {
            val document = PDDocument.load(File(pdfPath))
            val transactions = mutableListOf<Transaction>()
            
            for (pageNum in 0 until document.numberOfPages) {
                val page = document.getPage(pageNum)
                // TODO: Implement character extraction
                // Step 1: Extract characters with bounding boxes using FastCharacterParser
                // Step 2: Merge characters into words
                // Step 3: Detect transaction table headers
                // Step 4: Map words to columns
                // Step 5: Create Transaction objects
            }
            
            document.close()
            return transactions
        } catch (e: Exception) {
            throw RuntimeException("Failed to extract transactions from PDF: ${e.message}", e)
        }
    }
    
    /**
     * Exports transactions to a CSV file
     * 
     * @param transactions List of transactions to export
     * @param outputPath Path for the output CSV file
     */
    fun exportToCSV(transactions: List<Transaction>, outputPath: String) {
        try {
            val file = File(outputPath)
            file.bufferedWriter().use { writer ->
                // Write header
                writer.write("Date,Description,Debit,Credit,Balance\n")
                
                // Write transactions
                for (transaction in transactions) {
                    writer.write(transaction.toCSV())
                    writer.write("\n")
                }
            }
            println("Exported ${transactions.size} transactions to $outputPath")
        } catch (e: Exception) {
            throw RuntimeException("Failed to export transactions to CSV: ${e.message}", e)
        }
    }
}

/**
 * Data class representing a single transaction
 */
data class Transaction(
    val date: String,
    val description: String,
    val debit: Double = 0.0,
    val credit: Double = 0.0,
    val balance: Double = 0.0
) {
    fun toCSV(): String {
        val debitStr = if (debit > 0) debit.toString() else ""
        val creditStr = if (credit > 0) credit.toString() else ""
        return "\"$date\",\"$description\",\"$debitStr\",\"$creditStr\",\"$balance\""
    }
}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Usage: java -jar converter.jar <input.pdf> [output.csv]")
        return
    }
    
    val inputPath = args[0]
    val outputPath = args.getOrNull(1) ?: "output.csv"
    
    val converter = BankStatementConverter()
    
    try {
        println("Extracting transactions from: $inputPath")
        val transactions = converter.extractTransactions(inputPath)
        println("Found ${transactions.size} transactions")
        
        converter.exportToCSV(transactions, outputPath)
        println("✓ Successfully converted to: $outputPath")
    } catch (e: Exception) {
        println("✗ Error: ${e.message}")
        e.printStackTrace()
    }
}
