package com.deksi.graduationquiz.sudoku.viewModel

import androidx.lifecycle.ViewModel
import com.deksi.graduationquiz.sudoku.game.SudokuGame

// ViewModel ties to activity, and stores the information that is to be displayed
// tied to a lifecycle, it wont go away when the activity is paused or rotated, so it keeps the state valid
class SudokuViewModel: ViewModel() {

    val sudokuGame = SudokuGame()
}