package com.quizapp.data.parser

import android.content.Context
import android.net.Uri
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Row

class ExcelParser(private val context: Context) {

    fun parseFromUri(uri: Uri): List<ParsedQuestion> {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return emptyList()
        val questions = mutableListOf<ParsedQuestion>()

        inputStream.use { stream ->
            val workbook = WorkbookFactory.create(stream)
            val sheet: Sheet = workbook.getSheetAt(0)

            for (row in sheet) {
                if (row.rowNum == 0) continue // skip header

                val content = getCellString(row, 0) ?: continue
                if (content.isBlank()) continue

                val contentType = getCellString(row, 1) ?: "SINGLE"
                val optionsRaw = getCellString(row, 2) ?: ""
                val answer = getCellString(row, 3) ?: ""
                val analysis = getCellString(row, 4) ?: ""

                val type = when {
                    contentType.contains("多选") -> "MULTI"
                    contentType.contains("判断") -> "JUDGE"
                    else -> "SINGLE"
                }

                val options = if (optionsRaw.isNotBlank()) {
                    optionsRaw.split("\n", "|||").filter { it.isNotBlank() }
                } else {
                    emptyList()
                }

                questions.add(
                    ParsedQuestion(
                        content = content,
                        questionType = type,
                        options = options,
                        answer = answer,
                        analysis = analysis
                    )
                )
            }
        }
        return questions
    }

    private fun getCellString(row: Row, index: Int): String? {
        val cell = row.getCell(index) ?: return null
        return when (cell.cellType) {
            org.apache.poi.ss.usermodel.CellType.STRING -> cell.stringCellValue?.trim()
            org.apache.poi.ss.usermodel.CellType.NUMERIC -> {
                val v = cell.numericCellValue
                if (v == v.toLong().toDouble()) v.toLong().toString() else v.toString()
            }
            else -> null
        }
    }
}
