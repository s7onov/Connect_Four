package connectfour

import java.lang.Exception

const val GAME_NAME = "Connect Four"
const val INVALID_INPUT = "Invalid input"
const val EMPTY_PLACE_CHAR = ' '
const val FIRST_CHAR = 'o'
const val SECOND_CHAR = '*'
const val NUMBER_OF_GAMES = """
    Do you want to play single or multiple games?
    For a single game, input 1 or press Enter
    Input a number of games:
"""

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
    private var numberOfGames = 1
    private var currentGame = 1
    private var firstScore = 0
    private var secondScore = 0

    fun startGame() {
        println(GAME_NAME)
        println("First player's name:")
        firstPlayer = readln()
        println("Second player's name:")
        secondPlayer = readln()

        do { val isInputCorrect = readDimensions() } while (!isInputCorrect)
        do { numberOfGames = readNumberOfGames() } while (numberOfGames <= 0)

        println("$firstPlayer VS $secondPlayer")
        println("$rows X $columns board")
        println(if (numberOfGames == 1) "Single game" else "Total $numberOfGames games")
        board = Array(rows) { Array(columns) { EMPTY_PLACE_CHAR } }

        for (i in 1..numberOfGames) {
            currentGame = i
            if (numberOfGames > 1) println("Game #$i")
            printBoard()
            do {
                val turnCode = readPlayersTurn()
                if (turnCode > 0) {
                    printBoard()
                    if (checkForEnd()) break
                    turn++
                }
            } while (turnCode >= 0)
        }
        println("Game over!")
    }

    private fun readNumberOfGames(): Int {
        println(NUMBER_OF_GAMES.trimIndent())
        val input = readln().trim()
        if (input.isEmpty()) return 1
        var number = -1
        try {
            if (input.matches(Regex("""\d+"""))) number = input.toInt()
        } catch (ex: Exception) { number = -2 }
        if (number <= 0) println(INVALID_INPUT)
        return number
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
            if (str.matches(firstRegex)) { println("Player $firstPlayer won"); firstScore += 2; gameEnded(); return true }
            if (str.matches(secondRegex)) { println("Player $secondPlayer won"); secondScore += 2; gameEnded(); return true }
        }
        if (turn == rows * columns) { println("It is a draw"); firstScore++; secondScore++; gameEnded(); return true }
        return false
    }

    private fun gameEnded() {
        if (numberOfGames > 1) println("Score\n$firstPlayer: $firstScore $secondPlayer: $secondScore")
        board = Array(rows) { Array(columns) { EMPTY_PLACE_CHAR } } // clear the board
        turn = 1
    }

    private fun readPlayersTurn(): Int {
        println(if ((turn + currentGame) % 2 == 0) "$firstPlayer's turn:" else "$secondPlayer's turn:")
        val input = readln().trim()
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
                val char = if ((turn + currentGame) % 2 == 0) FIRST_CHAR else SECOND_CHAR
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
        //for (i in 0 until rows) println(board[i].joinToString("║", "║", "║"))
        for (i in 0 until rows) {
            print("║")
            for (j in 0 until columns) {
                if (board[i][j] == FIRST_CHAR) print("\u001B[48:5:9m${board[i][j]}\u001B[0m║")
                else if (board[i][j] == SECOND_CHAR) print("\u001B[48:5:11m${board[i][j]}\u001B[0m║")
                else print("${board[i][j]}║")
            }
            println()
        }
        println(Array(columns) { "═" }.joinToString("╩", "╚", "╝"))
    }
}


