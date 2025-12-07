package com.example.tetrisgroupproject

import android.content.Context

// Class manages higher levle game logic such as spawning and placing blocks
class TetrisGameManager(private val context: Context) {

    private val grid = TetrisGrid()
    private var currentBlock: Block? = null
    private var nextBlock: Block? = null
    
    private var isGameOver: Boolean = false
    
    var onGameOverCallback: (() -> Unit)? = null
    var onViewUpdate: (() -> Unit)? = null

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
        if (currentBlock == null) return

        grid.placeBlock(currentBlock!!)

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
}
