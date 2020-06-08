package util

import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import kotlin.experimental.and


object COFFDump {
    fun exe(args: Array<String>) {
        val fileName = if (args.isNotEmpty()) args[0] else "src\\main\\resources\\testfile.obj"
        try {
            val file: RandomAccessFile = RandomAccessFile(fileName, "r")
            val symbolTablePtr: Int
            val symbolCount: Int
            val stringTable: ByteArray
            val header = ByteArray(20)
            file.read(header)
            symbolTablePtr = getInt32(header, 8)
            symbolCount = getInt32(header, 12)
            val stringTablePtr = symbolTablePtr + 18 * symbolCount
            file.seek(stringTablePtr.toLong())
            val stringTableSizeBytes = ByteArray(4)
            file.read(stringTableSizeBytes)
            val stringTableSize = getInt32(stringTableSizeBytes, 0)
            stringTable = ByteArray(stringTableSize)
            file.read(stringTable)

            System.out.printf("Symbols in %s:\n", fileName)
            file.seek(symbolTablePtr.toLong())

            var aux = 0

            for (i in 0 until symbolCount) {
                val symbol = ByteArray(18)
                file.read(symbol)
                if (aux > 0) {
                    aux--
                } else {
                    val name = getSymbolName(symbol, stringTable)
                    val section = getInt16(symbol, 12)
                    System.out.printf("%s, section %d\n", name, section)
                    aux = (symbol[17] and 0xFF.toByte()).toInt()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun getInt32(array: ByteArray, offset: Int): Int {
        return ByteBuffer.wrap(array, offset, 4).order(ByteOrder.LITTLE_ENDIAN).int
    }

    private fun getInt16(array: ByteArray, offset: Int): Short {
        return ByteBuffer.wrap(array, offset, 2).order(ByteOrder.LITTLE_ENDIAN).short
    }

    private fun getSymbolName(symbol: ByteArray, stringTable: ByteArray): String? {
        if (getInt32(symbol, 0) != 0) {
            for (i in 7 downTo 0) {
                if (symbol[i] != 0.toByte()) {
                    return String(symbol, 0, i + 1, StandardCharsets.US_ASCII)
                }
            }
        } else {
            val stringTableOffset = getInt32(symbol, 4) - 4
            for (i in stringTableOffset until stringTable.size) {
                if (stringTable[i] == 0.toByte()) {
                    return String(stringTable, stringTableOffset, i - stringTableOffset, StandardCharsets.US_ASCII)
                }
            }
        }
        throw IllegalArgumentException()
    }
}