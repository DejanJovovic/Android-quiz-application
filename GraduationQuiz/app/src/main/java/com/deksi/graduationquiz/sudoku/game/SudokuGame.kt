package com.deksi.graduationquiz.sudoku.game

import androidx.lifecycle.MutableLiveData

// keeps the connection here between view and viewModel#
// stores the state of the board and the game
class SudokuGame {

    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var cellsLiveData = MutableLiveData<List<Cell>>()

    private var selectedRow = -1
    private var selectedCol = -1

    private val board: Board

    // called whenever the SudokuGame is created
    init {
        val cells = List(9 * 9) {
            i -> Cell(i / 9, i % 9, i % 9)
        }
        board = Board(9, cells)

        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        cellsLiveData.postValue(board.cells)
    }

    //it takes a number and decides what to do with it
    fun handleInput(number: Int){
        if(selectedRow == -1 || selectedCol == -1) return
        //gets the corrected cell, updates it, and posts back
        board.getCell(selectedRow, selectedCol).value = number
        cellsLiveData.postValue(board.cells)

    }

    fun updateSelectedCell(row: Int, col: Int){
        selectedRow = row
        selectedCol = col
        selectedCellLiveData.postValue(Pair(row, col))
    }
}