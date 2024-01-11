package com.deksi.graduationquiz.sudoku.game

// this class stores an information about a specific cell
class Cell(
    val row: Int,
    val col: Int,
    var value: Int,
    var isStartingCell: Boolean = false,
    var notes: MutableSet<Int> = mutableSetOf<Int>()
)