package com.deksi.graduationquiz.sudoku.game

// stores an entire board
class Board(val size: Int, val cells: List<Cell>) {

    fun getCell(row: Int, col: Int) = cells[row * size + col]

}