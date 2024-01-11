package com.deksi.graduationquiz.sudoku.game

import androidx.lifecycle.MutableLiveData

// keeps the connection here between view and viewModel#
// stores the state of the board and the game
class SudokuGame {

    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var cellsLiveData = MutableLiveData<List<Cell>>()
    val isTakingNotesLiveData = MutableLiveData<Boolean>()
    val highlightedKeysLiveData = MutableLiveData<Set<Int>>()

    private var selectedRow = -1
    private var selectedCol = -1
    private var isTakingNotes = false

    private val board: Board

    // called whenever the SudokuGame is created
    init {
        val cells = List(9 * 9) { i ->
            Cell(i / 9, i % 9, i % 9)
        }
        cells[0].notes = mutableSetOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        board = Board(9, cells)

        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        cellsLiveData.postValue(board.cells)
        isTakingNotesLiveData.postValue(isTakingNotes)
    }

    //it takes a number and decides what to do with it
    fun handleInput(number: Int) {
        if (selectedRow == -1 || selectedCol == -1) return
        val cell = board.getCell(selectedRow, selectedCol)

        // if selectedRow or selectedCol happen to be startingCell when the user clicks, dont let him input anything
        if (cell.isStartingCell) return

        if (isTakingNotes) {
            if (cell.notes.contains(number)) {
                cell.notes.remove(number)
            } else {
                cell.notes.add(number)
            }
            highlightedKeysLiveData.postValue(cell.notes)
        } else {
            //gets the corrected cell, updates it, and posts back
            cell.value = number
        }
        cellsLiveData.postValue(board.cells)
    }

    fun updateSelectedCell(row: Int, col: Int) {
        val cell = board.getCell(row, col)

        // if selectedRow or selectedCol happen to be startingCell when the user clicks, dont let him input anything
        if (!cell.isStartingCell) {
            selectedRow = row
            selectedCol = col
            selectedCellLiveData.postValue(Pair(row, col))

            if (isTakingNotes) {
                highlightedKeysLiveData.postValue(cell.notes)
            }
        }
    }

    fun changeNoteTakingState() {
        // if taking notes, grab the notes of the current cell, if not, just grab an empty cell of notes

        isTakingNotes = !isTakingNotes
        isTakingNotesLiveData.postValue(isTakingNotes)
        val currentNotes =
            if (isTakingNotes) {
                board.getCell(selectedRow, selectedCol).notes
            } else {
                setOf<Int>()
            }
        highlightedKeysLiveData.postValue(currentNotes)
    }
}