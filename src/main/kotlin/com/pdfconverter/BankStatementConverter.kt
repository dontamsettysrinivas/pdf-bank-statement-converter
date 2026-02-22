package com.pdfconverter

import com.pdfconverter.extraction.CharacterExtractor
import com.pdfconverter.headers.HeaderDetector
import com.pdfconverter.parser.BankStatementParser
import com.pdfconverter.export.CsvExporter
import com.pdfconverter.model.Transaction
import org.apache.pdfbox.pdmodel.PDDocument
import java.io.File

/**
 * Main class for converting bank statement PDFs to CSV format.
 * Follows the architecture from Angus Cheng's Bank Statement Converter.
 * 
 * @see https://bankstatementconverter.com/book
 */
class BankStatementConverter {
    
    private val extractor = CharacterExtractor()
    private val headerDetector = HeaderDetector()
    private val parser = BankStatementParser(headerDetector)
    private val exporter = CsvExporter()
    
    /**
     * Extracts transactions from a PDF bank statement file
     * 
     * @param pdfPath Path to the PDF file
     * @return List of Transaction objects extracted from the PDF
     */
    fun extractTransactions(pdfPath: String): List<Transaction> {
        try {
            val pdfFile = File(pdfPath)
            if (!pdfFile.exists()) {
                throw IllegalArgumentException("PDF file not found: $pdfPath")
            }
            
            val allTransactions = mutableListOf<Transaction>()
            
            // Load PDF document
            PDDocument.load(pdfFile).use { document ->
                println("Processing PDF with ${document.numberOfPages} pages...")
                
                // Process each page
                for (pageNum in 0 until document.numberOfPages) {
                    val page = document.getPage(pageNum)
                    println("Processing page ${pageNum + 1}...")
                    
                    // Extract characters from page
                    val characters = extractor.extractCharacters(page)
                    println("  Extracted ${characters.size} characters")
                    
                    // Parse transactions from characters
                    val transactions = parser.parseTransactions(characters)
                    println("  Found ${transactions.size} transactions")
                    
                    allTransactions.addAll(transactions)
                }
            }
            
            println("Total transactions extracted: ${allTransactions.size}")
            return allTransactions
            
        } catch (e: Exception) {
            println("Error extracting transactions: ${e.message}")
            e.printStackTrace()
            return emptyList()
        }
    }
    
    /**
     * Converts a PDF bank statement to CSV format
     * 
     * @param pdfPath Path to the input PDF file
     * @param csvPath Path to the output CSV file
     * @return Number of transactions converted
     */
    fun convertToCSV(pdfPath: String, csvPath: String): Int {
        try {
            // Extract transactions from PDF
            val transactions = extractTransactions(pdfPath)
            
            if (transactions.isEmpty()) {
                println("No transactions found in PDF")
                return 0
            }
            
            // Export transactions to CSV
            val csvFile = File(csvPath)
            exporter.exportToCsv(transactions, csvFile)
            
            println("Successfully exported ${transactions.size} transactions to $csvPath")
            return transactions.size
            
        } catch (e: Exception) {
            println("Error converting PDF to CSV: ${e.message}")
            e.printStackTrace()
            return 0
        }
    }
}

/**
 * Main function for command-line usage
 */
fun main(args: Array<String>) {
    if (args.size < 2) {
        println("Usage: BankStatementConverter <input.pdf> <output.csv>")
        println("Example: BankStatementConverter statement.pdf transactions.csv")
        return
    }
    
    val pdfPath = args[0]
    val csvPath = args[1]
    
    println("PDF Bank Statement Converter")
    println("=============================")
    println("Input:  $pdfPath")
    println("Output: $csvPath")
    println()
    
    val converter = BankStatementConverter()
    val count = converter.convertToCSV(pdfPath, csvPath)
    
    println()
    if (count > 0) {
        println("✓ Conversion completed successfully!")
        println("  Transactions converted: $count")
    } else {
        println("✗ Conversion failed or no transactions found")
    }
}
