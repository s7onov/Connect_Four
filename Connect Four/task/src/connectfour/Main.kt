package connectfour

import java.lang.Exception

const val GAME_NAME = "Connect Four"
const val INVALID_INPUT = "Invalid input"
const val EMPTY_PLACE_CHAR = ' '
const val FIRST_CHAR = 'o'
const val SECOND_CHAR = '*'

fun main() {
    val game = Game()
    game.startGame()
}

class Game {
    private var firstPlayer = "firstPlayer"
    private var secondPlayer = "secondPlayer"
    private var rows = 6
    private var columns = 7
    private var turn = 1
    private var board = Array(rows) { Array(columns) { EMPTY_PLACE_CHAR } }

    fun startGame() {
        println(GAME_NAME)
        println("First player's name:")
        firstPlayer = readln()
        println("Second player's name:")
        secondPlayer = readln()

        do {
            val isInputCorrect = readDimensions()
        } while (!isInputCorrect)

        println("$firstPlayer VS $secondPlayer")
        println("$rows X $columns board")
        board = Array(rows) { Array(columns) { EMPTY_PLACE_CHAR } }
        printBoard()

        do {
            val turnCode = readPlayersTurn()
            if (turnCode > 0) {
                printBoard()
                if (checkForEnd()) break
                turn++
            }
        } while (turnCode >= 0)
        println("Game over!")
    }

    private fun checkForEnd(): Boolean {
        val checkList = mutableListOf<String>()
        for (i in rows - 1 downTo 0) { // horizontals
            checkList.add(board[i].joinToString(""))
        }
        for (j in 0 until columns) {  // verticals
            val sb = StringBuilder()
            for (i in rows - 1 downTo 0) {
                sb.append(board[i][j])
            }
            checkList.add(sb.toString())
        }
        for (i in 0 until  rows - 3) { // diagonals +1 +1
            for (j in 0 until  columns - 3) {
                val sb = StringBuilder()
                sb.append(board[i][j]).append(board[i + 1][j + 1])
                    .append(board[i + 2][j + 2]).append(board[i + 3][j + 3])
                checkList.add(sb.toString())
            }
        }
        for (i in 3 until  rows) { // diagonals -1 +1
            for (j in 0 until  columns - 3) {
                val sb = StringBuilder()
                sb.append(board[i][j]).append(board[i - 1][j + 1])
                    .append(board[i - 2][j + 2]).append(board[i - 3][j + 3])
                checkList.add(sb.toString())
            }
        }
        val firstRegex = Regex(""".*?$FIRST_CHAR{4}.*?""")
        val secondRegex = Regex(""".*?\$SECOND_CHAR{4}.*?""")
        for (str in checkList) {
            if (str.matches(firstRegex)) { println("Player $firstPlayer won"); return true }
            if (str.matches(secondRegex)) { println("Player $secondPlayer won"); return true }
        }
        if (turn == rows * columns) { println("It is a draw"); return true }
        return false
    }

    private fun readPlayersTurn(): Int {
        println(if (turn % 2 == 1) "$firstPlayer's turn:" else "$secondPlayer's turn:")
        val input = readln().trim().lowercase()
        if (input == "end") return -1
        if (!input.matches(Regex("""\d+"""))) {
            println("Incorrect column number")
            return 0
        }
        try {
            val column = input.toInt()
            if (column !in 1..columns) {
                println("The column number is out of range (1 - $columns)")
                return 0
            }
            if (board[0][column - 1] != EMPTY_PLACE_CHAR) {
                println("Column $column is full")
                return 0
            } else {
                val char = if (turn % 2 == 1) FIRST_CHAR else SECOND_CHAR
                for (i in rows - 1 downTo 0) {
                    if (board[i][column - 1] == EMPTY_PLACE_CHAR) {
                        board[i][column - 1] = char
                        break
                    }
                }
            }
            return column

        } catch (ex: Exception) {
            println(INVALID_INPUT)
            return 0
        }
    }

    private fun readDimensions(): Boolean {
        println("Set the board dimensions (Rows x Columns)\nPress Enter for default (6 x 7)")
        val input = readln().trim().lowercase()
        if (input.isBlank()) return true
        if (!input.matches(Regex("""\d+\s*?x\s*?\d+"""))) {
            println(INVALID_INPUT)
            return false
        }

        try {
            val list = input.split("x")
            val r = list[0].trim().toInt()
            val c = list[1].trim().toInt()
            if (r !in 5..9) {
                println("Board rows should be from 5 to 9")
                return false
            }
            if (c !in 5..9) {
                println("Board columns should be from 5 to 9")
                return false
            }
            rows = r
            columns = c

        } catch (ex: Exception) {
            println(INVALID_INPUT)
            return false
        }

        return true
    }

    private fun printBoard() {
        println(Array(columns) { it + 1 }.joinToString(" ", " "))
        for (i in 0 until rows) {
            println(board[i].joinToString("║", "║", "║"))
        }
        println(Array(columns) { "═" }.joinToString("╩", "╚", "╝"))
    }
}


