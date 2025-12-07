package com.example.tetrisgroupproject

import android.graphics.Color

class TetrisGrid {
    companion object {
        const val GRID_WIDTH = 10
        const val GRID_HEIGHT = 20
        
        // Value to represent empty_cell
        const val EMPTY_CELL = 0
    }

    private val grid: Array<IntArray> = Array(GRID_HEIGHT) { IntArray(GRID_WIDTH) { EMPTY_CELL } }

    fun checkCollision(block: Block): Boolean {
        val cells = block.getOccupiedCells()
        
        for ((row, col) in cells) {
            if (col < 0 || col >= GRID_WIDTH) {
                return true
            }
            
            if (row < 0) {
                return true
            }
            
            if (row >= GRID_HEIGHT) {
                continue
            }
            
            if (grid[row][col] != EMPTY_CELL) {
                return true
            }
        }
        
        return false
    }

    fun checkCeilingCollision(block: Block): Boolean {
        val cells = block.getOccupiedCells()
        
        for ((row, _) in cells) {
            if (row <= 0) {
                return true
            }
        }
        
        val testBlock = block.copy()
        testBlock.moveUp()
        return checkCollision(testBlock)
    }

    fun placeBlock(block: Block) {
        val cells = block.getOccupiedCells()
        
        for ((row, col) in cells) {
            if (row >= 0 && row < GRID_HEIGHT && col >= 0 && col < GRID_WIDTH) {
                grid[row][col] = block.color
            }
        }
    }

    fun isGameOver(): Boolean {
        for (row in (GRID_HEIGHT - 2) until GRID_HEIGHT) {
            for (col in 0 until GRID_WIDTH) {
                if (grid[row][col] != EMPTY_CELL) {
                    return true
                }
            }
        }
        return false
    }

    fun getCellColor(row: Int, col: Int): Int {
        if (row < 0 || row >= GRID_HEIGHT || col < 0 || col >= GRID_WIDTH) {
            return EMPTY_CELL
        }
        return grid[row][col]
    }

    fun isCellEmpty(row: Int, col: Int): Boolean {
        return getCellColor(row, col) == EMPTY_CELL
    }

    fun clear() {
        for (row in 0 until GRID_HEIGHT) {
            for (col in 0 until GRID_WIDTH) {
                grid[row][col] = EMPTY_CELL
            }
        }
    }

    // Copy is made to safely check collision
    fun getGridCopy(): Array<IntArray> {
        return Array(GRID_HEIGHT) { row ->
            grid[row].clone()
        }
    }

    fun canMoveUp(block: Block): Boolean {
        val testBlock = block.copy()
        testBlock.moveUp()
        return !checkCollision(testBlock)
    }
}
