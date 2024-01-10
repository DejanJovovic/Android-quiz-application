package com.deksi.graduationquiz.sudoku


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.deksi.graduationquiz.databinding.ActivitySudokuBinding

import com.deksi.graduationquiz.sudoku.game.Cell
import com.deksi.graduationquiz.sudoku.view.SudokuBoardView
import com.deksi.graduationquiz.sudoku.viewModel.SudokuViewModel


class Sudoku : AppCompatActivity(), SudokuBoardView.OnTouchListener {

    private lateinit var viewModel: SudokuViewModel
    private lateinit var binding: ActivitySudokuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySudokuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sudokuBoardView.registerListener(this)
        viewModel = ViewModelProvider(this)[SudokuViewModel::class.java]
        // observing what's happening to the selecetedCellData
        viewModel.sudokuGame.selectedCellLiveData.observe(this, Observer { updateSelectedCellUI(it) })
        viewModel.sudokuGame.cellsLiveData.observe(this, Observer { updateCells(it) })


       val buttons = listOf(binding.buttonOne, binding.buttonTwo, binding.buttonThree, binding.buttonFour,
            binding.buttonFive, binding.buttonSix, binding.buttonSeven, binding.buttonEight, binding.buttonNine)

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                viewModel.sudokuGame.handleInput(index + 1)
            }
        }
    }

    //runs only when the cells is not null
    private fun updateCells(cells: List<Cell>?) = cells?.let {
        binding.sudokuBoardView.updateCells(cells)
    }

    //this will only run if the pair is not null
    private fun updateSelectedCellUI(cell: Pair<Int, Int>?) = cell?.let {
        binding.sudokuBoardView.updatedSelectedCellUI(cell.first, cell.second)
    }

    override fun onCellTouched(row: Int, col: Int){
        viewModel.sudokuGame.updateSelectedCell(row, col)
    }
}