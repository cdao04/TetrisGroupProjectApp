package com.example.tetrisgroupproject

import android.content.Context
import android.util.Log

// Class manages higher levle game logic such as spawning and placing blocks
class TetrisGameManager(private val context: Context) {

    private val grid = TetrisGrid()
    private var currentBlock: Block? = null
    private var nextBlock: Block? = null
    private var score = 0
    private var level = 1
    private var rowsCleared = 0
    private var isGameOver: Boolean = false
    var onGameOverCallback: (() -> Unit)? = null
    var onViewUpdate: (() -> Unit)? = null
    var onRowCleared: (() -> Unit)? = null
    var onLevelUp: (() -> Unit)? = null

    companion object {
        const val ROWS_PER_LEVEL = 10

    }

    fun getScore() : Int {
        return score
    }

    fun getLevel() : Int {
        return level
    }

    fun getTickInterval(): Long {
        val base = 900.0
        val factor = 1.0 + (level * 0.5)    // increase speed by factor of half the level value
        val interval = base / factor
        return interval.toLong().coerceAtLeast(80L)
    }

    fun startNewGame() {
        isGameOver = false
        
        spawnNewBlock()
        spawnNextBlock()
    }

    private fun spawnNewBlock() {
        currentBlock = nextBlock ?: Block(BlockType.random())
        spawnNextBlock()
        
        if (grid.checkCollision(currentBlock!!)) {
            isGameOver = true
            onGameOverCallback?.invoke()
        }
    }

    private fun spawnNextBlock() {
        nextBlock = Block(BlockType.random())
    }

    fun tick(): Boolean {
        if (isGameOver || currentBlock == null) {
            return false
        }

        onViewUpdate?.invoke()

        if (grid.canMoveUp(currentBlock!!)) {
            currentBlock!!.moveUp()
            onViewUpdate?.invoke()
            return true
        } else {
            placeCurrentBlock()
            return false
        }
    }

    private fun placeCurrentBlock() {
        if (currentBlock == null) {
            return
        }

        grid.placeBlock(currentBlock!!)
        score += 4      // Increase score by # of squares in block placed
        // Log.w("MainActivity", "Score: " + score)

        // Add scoring
        var cleared = grid.clearFullRows()
        if (cleared > 0) {
            onRowCleared?.invoke()

            // Check number of rows cleared; increment points based on that
            if (cleared == 1) {
                score += (level * 100)
            } else if (cleared == 2) {
                score += (level * 300)
            } else if (cleared == 3) {
                score += (level * 500)
            } else if (cleared == 4) {
                score += (level * 800)
            } else {
                score += 0
            }
            Log.w("MainActivity", "New score is: " + score)

            rowsCleared += cleared
            if (rowsCleared >= ROWS_PER_LEVEL) {
                rowsCleared -= ROWS_PER_LEVEL
                level++
                onLevelUp?.invoke()
                Log.w("MainActivity", "New level is: " + level)
            }
        }

        if (grid.isGameOver()) {
            isGameOver = true
            onGameOverCallback?.invoke()
            return
        }

        spawnNewBlock()
        onViewUpdate?.invoke()
    }

    fun isGameOver(): Boolean = isGameOver
    fun getCurrentBlock(): Block? = currentBlock
    fun getNextBlock(): Block? = nextBlock
    fun getGrid(): TetrisGrid = grid

    // Get progress of level clear
    fun getLevelProgress(): Float {
        return rowsCleared / ROWS_PER_LEVEL.toFloat()
    }

    // Shift block to the left
    fun moveLeft() {
        var block = currentBlock ?: return
        block.x--
        if (grid.checkCollision(block)) {
            block.x++
        }
        onViewUpdate?.invoke()
    }

    // Shift block to the right
    fun moveRight() {
        var block = currentBlock ?: return
        block.x++
        if (grid.checkCollision(block)) {
            block.x--
        }
        onViewUpdate?.invoke()
    }

    // Rotate the block
    fun rotate() {
        val block = currentBlock ?: return
        block.rotate()
        if (grid.checkCollision(block)) {
            block.rotateBack()
        }
        onViewUpdate?.invoke()
    }

}

