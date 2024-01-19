package com.deksi.graduationquiz.sudoku.game

import android.util.Log
import androidx.lifecycle.MutableLiveData


// keeps the connection here between view and viewModel#
// stores the state of the board and the game
class SudokuGame(
    private val listener: SudokuGameListener
) {

    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var cellsLiveData = MutableLiveData<List<Cell>>()


    private var selectedRow = -1
    private var selectedCol = -1

    private val board: Board

    private var remainingLives = 3
        set(value) {
            field = value
            // Notify the listener when remaining lives change
            listener.onRemainingLivesChanged(value)
        }

    // called whenever the SudokuGame is created
    init {
        val solvedBoard = generateSolvedBoard()
        val cells = solvedBoard.cells.map { Cell(it.row, it.col, it.value) }.toMutableList()

        val blankCellCount = 30
        val blankCellIndices = (0 until 9 * 9).shuffled().take(blankCellCount)

        for (index in blankCellIndices) {
            cells[index].value = 0
        }

        board = Board(9, cells)

        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        cellsLiveData.postValue(board.cells)
    }


    private fun generateSolvedBoard(): Board {
        val board = Board(9, MutableList(9 * 9) { Cell(it / 9, it % 9, 0) })
        solve(board)

        return board
    }

    private fun solve(board: Board): Boolean {
        val emptyCell = findEmptyCell(board)
        if (emptyCell == null) {
            // If there are no empty cells, the board is solved
            return true
        }

        val (row, col) = emptyCell
        val numbers = (1..9).shuffled()

        for (num in numbers) {
            if (isValidMove(board, row, col, num)) {
                board.getCell(row, col).value = num

                if (solve(board)) {
                    return true
                }

                // If the current placement doesn't lead to a solution, backtrack
                board.getCell(row, col).value = 0
            }
        }

        // If no number works in this cell, backtrack
        return false
    }

    private fun findEmptyCell(board: Board): Pair<Int, Int>? {
        for (row in 0 until 9) {
            for (col in 0 until 9) {
                if (board.getCell(row, col).value == 0) {
                    return Pair(row, col)
                }
            }
        }
        return null
    }

    private fun isValidMove(board: Board, row: Int, col: Int, num: Int): Boolean {
        return !isInRow(board, row, num) && !isInCol(board, col, num) && !isInBox(
            board,
            row - row % 3,
            col - col % 3,
            num
        )
    }


    fun isNumberCorrect(board: Board, row: Int, col: Int, num: Int): Boolean {
        return !isInRow(board, row, num) && !isInCol(board, col, num) && !isInBox(
            board,
            row - row % 3,
            col - col % 3,
            num
        )
    }

    private fun isInRow(board: Board, row: Int, num: Int): Boolean {
        return (0 until 9).any { board.getCell(row, it).value == num }
    }

    private fun isInCol(board: Board, col: Int, num: Int): Boolean {
        return (0 until 9).any { board.getCell(it, col).value == num }
    }

    private fun isInBox(board: Board, startRow: Int, startCol: Int, num: Int): Boolean {
        return (0 until 3).any { rowOffset ->
            (0 until 3).any { colOffset ->
                board.getCell(startRow + rowOffset, startCol + colOffset).value == num
            }
        }
    }

    fun decrementLives() {
        remainingLives--
    }


    //it takes a number and decides what to do with it
    fun handleInput(number: Int) {
        if (selectedRow == -1 || selectedCol == -1) return
        val selectedCell = board.getCell(selectedRow, selectedCol)

        if (selectedCell.value == 0 && isNumberCorrect(board, selectedRow, selectedCol, number)) {
            selectedCell.value = number
            cellsLiveData.postValue(board.cells)
            Log.d("Correct number!", "Correct number")

        } else {
            Log.d("Wrong number!", "Wrong number")
            decrementLives()
            updateLivesUI(listener)

            if (remainingLives == 0) {
                // Game over, navigate to HomeActivity
                listener.onRemainingLivesChanged(remainingLives)  // Notify listener about game over
            }
        }
    }

    private fun updateLivesUI(listener: SudokuGameListener) {
        listener.onRemainingLivesChanged(remainingLives)
    }

    interface SudokuGameListener {
        fun onRemainingLivesChanged(remainingLives: Int)
    }


    fun updateSelectedCell(row: Int, col: Int) {
        selectedRow = row
        selectedCol = col
        selectedCellLiveData.postValue(Pair(row, col))
    }

}