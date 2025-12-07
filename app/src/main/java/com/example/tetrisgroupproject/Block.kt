package com.example.tetrisgroupproject

import android.graphics.Color

class Block(val type: BlockType, var x: Int = 3, var y: Int = TetrisGrid.GRID_HEIGHT - 1) {
    var shape: Array<IntArray>
    var color: Int

    init {
        shape = type.getShape()
        color = type.color
    }

    fun moveUp() {
        y -= 1
    }

    fun moveDown() {
        y += 1
    }

    fun moveLeft() {
        x -= 1
    }

    fun moveRight() {
        x += 1
    }

    fun copy(): Block {
        val newBlock = Block(type, x, y)
        newBlock.shape = this.shape
        return newBlock
    }

    // Converts the block local coordinates into absolute game grid coordinates
    fun getOccupiedCells(): List<Pair<Int, Int>> {
        val cells = mutableListOf<Pair<Int, Int>>()
        for (row in shape.indices) {
            for (col in shape[row].indices) {
                if (shape[row][col] == 1) {
                    cells.add(Pair(y + row, x + col))
                }
            }
        }
        return cells
    }
}

// Refer to tetris shape names for clarity on shapes
// 4x4 array for rotational implementation if wanted
enum class BlockType(val color: Int) {
    // Followed color scheme of start screen excluded L-shape
    I(Color.CYAN),
    O(Color.YELLOW),
    T(Color.MAGENTA),
    S(Color.GREEN),
    Z(Color.RED),
    J(Color.BLUE),
    L(Color.rgb(255, 165, 0));

    fun getShape(): Array<IntArray> {
        return when (this) {
            I -> getIShape()
            O -> getOShape()
            T -> getTShape()
            S -> getSShape()
            Z -> getZShape()
            J -> getJShape()
            L -> getLShape()
        }
    }

    private fun getIShape(): Array<IntArray> {
        return arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(1, 1, 1, 1),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0)
        )
    }

    private fun getOShape(): Array<IntArray> {
        return arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 1, 1, 0),
            intArrayOf(0, 1, 1, 0),
            intArrayOf(0, 0, 0, 0)
        )
    }

    private fun getTShape(): Array<IntArray> {
        return arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 1, 0, 0),
            intArrayOf(1, 1, 1, 0),
            intArrayOf(0, 0, 0, 0)
        )
    }

    private fun getSShape(): Array<IntArray> {
        return arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 1, 1),
            intArrayOf(0, 1, 1, 0),
            intArrayOf(0, 0, 0, 0)
        )
    }

    private fun getZShape(): Array<IntArray> {
        return arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(1, 1, 0, 0),
            intArrayOf(0, 1, 1, 0),
            intArrayOf(0, 0, 0, 0)
        )
    }

    private fun getJShape(): Array<IntArray> {
        return arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 1, 0, 0),
            intArrayOf(0, 1, 0, 0),
            intArrayOf(1, 1, 0, 0)
        )
    }

    private fun getLShape(): Array<IntArray> {
        return arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 1, 0, 0),
            intArrayOf(0, 1, 0, 0),
            intArrayOf(0, 1, 1, 0)
        )
    }

    companion object {
        fun random(): BlockType {
            return values().random()
        }
    }
}
