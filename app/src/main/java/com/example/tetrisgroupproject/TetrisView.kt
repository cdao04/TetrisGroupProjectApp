package com.example.tetrisgroupproject

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class TetrisView(context: Context, private val width: Int, private val height: Int, private val game: TetrisGameManager) : View(context) {
    companion object {
        private const val GRID_PADDING = 50
    }

    private val paint = Paint()
    private val cellSize: Int
    // Used for centering
    private val gridLeft: Int
    private val gridTop: Int
    private var textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 65f
        isAntiAlias = true
    }

    init {
        paint.isAntiAlias = true

        val availableWidth = width - (2 * GRID_PADDING)
        val availableHeight = height - (2 * GRID_PADDING) - 200
        
        cellSize = minOf(
            availableWidth / TetrisGrid.GRID_WIDTH,
            availableHeight / TetrisGrid.GRID_HEIGHT
        )
        
        gridLeft = (width - (cellSize * TetrisGrid.GRID_WIDTH)) / 2
        gridTop = GRID_PADDING + 100
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // TODO edit to accommodate GUI theme change?
        canvas.drawColor(Color.BLACK)
        drawGridBorder(canvas)
        drawPlacedBlocks(canvas)
        drawCurrentBlock(canvas)
        drawScoreAndLevel(canvas)
        drawProgressBar(canvas)
    }

    private fun drawGridBorder(canvas: Canvas) {
        paint.style = Paint.Style.STROKE
        
        val right = gridLeft + (cellSize * TetrisGrid.GRID_WIDTH)
        val bottom = gridTop + (cellSize * TetrisGrid.GRID_HEIGHT)
        
        paint.color = Color.WHITE
        paint.strokeWidth = 8f
        canvas.drawRect(
            gridLeft.toFloat(),
            gridTop.toFloat(),
            right.toFloat(),
            bottom.toFloat(),
            paint
        )
        
        paint.color = Color.argb(60, 255, 255, 255)
        paint.strokeWidth = 1f
        
        for (col in 1 until TetrisGrid.GRID_WIDTH) {
            val x = gridLeft + (col * cellSize)
            canvas.drawLine(
                x.toFloat(),
                gridTop.toFloat(),
                x.toFloat(),
                bottom.toFloat(),
                paint
            )
        }
        
        for (row in 1 until TetrisGrid.GRID_HEIGHT) {
            val y = gridTop + (row * cellSize)
            canvas.drawLine(
                gridLeft.toFloat(),
                y.toFloat(),
                right.toFloat(),
                y.toFloat(),
                paint
            )
        }
    }

    private fun drawPlacedBlocks(canvas: Canvas) {
        paint.style = Paint.Style.FILL
        
        for (row in 0 until TetrisGrid.GRID_HEIGHT) {
            for (col in 0 until TetrisGrid.GRID_WIDTH) {
                val cellColor = game.getGrid().getCellColor(row, col)
                if (cellColor != TetrisGrid.EMPTY_CELL) {
                    drawCell(canvas, row, col, cellColor)
                }
            }
        }
    }

    private fun drawCurrentBlock(canvas: Canvas) {
        val block = game.getCurrentBlock() ?: return
        
        paint.style = Paint.Style.FILL
        paint.color = block.color
        
        // Transform block coordinates to the live game
        val cells = block.getOccupiedCells()

        for ((row, col) in cells) {
            if (row >= 0) {
                drawCell(canvas, row, col, block.color)
            }
        }
    }

    private fun drawCell(canvas: Canvas, row: Int, col: Int, color: Int) {
        val left = gridLeft + (col * cellSize)
        val top = gridTop + (row * cellSize)
        
        paint.color = color
        paint.style = Paint.Style.FILL
        canvas.drawRect(
            left.toFloat() + 2,
            top.toFloat() + 2,
            (left + cellSize).toFloat() - 2,
            (top + cellSize).toFloat() - 2,
            paint
        )
        
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        canvas.drawRect(
            left.toFloat(),
            top.toFloat(),
            (left + cellSize).toFloat(),
            (top + cellSize).toFloat(),
            paint
        )
    }

    private fun drawScoreAndLevel(canvas: Canvas) {
        var score = game.getScore()
        var level = game.getLevel()

        var textY = (gridTop + cellSize * TetrisGrid.GRID_HEIGHT + 70).toFloat()

        // Score appears on the left
        var scoreX = gridLeft.toFloat()
        canvas.drawText("Score: " + score, scoreX, textY, textPaint)

        // Level appears on the right
        var text = "Level: " + level
        var textWidth = textPaint.measureText(text)
        var levelX = (gridLeft + cellSize * TetrisGrid.GRID_WIDTH - textWidth)
        canvas.drawText(text, levelX.toFloat(), textY, textPaint)
    }

    private fun drawProgressBar(canvas: Canvas) {
        var progressBarPaint = Paint().apply {
            color = Color.GREEN
            isAntiAlias = true
        }

        var progressBarBackgroundPaint = Paint().apply {
            color = Color.DKGRAY
            isAntiAlias = true
        }

        val progress = game.getLevelProgress()  // 0.0 to 1.0

        // Position
        val barLeft = gridLeft.toFloat()
        val barTop = (gridTop + cellSize * TetrisGrid.GRID_HEIGHT + 160).toFloat()
        val barWidth = (cellSize * TetrisGrid.GRID_WIDTH).toFloat()
        val barHeight = 40f

        // Background
        canvas.drawRect(
            barLeft,
            barTop,
            barLeft + barWidth,
            barTop + barHeight,
            progressBarBackgroundPaint
        )

        // Filled portion
        canvas.drawRect(
            barLeft,
            barTop,
            barLeft + barWidth * progress,
            barTop + barHeight,
            progressBarPaint
        )

        // Label centered above the bar
        val label = "Level Progress"
        val textWidth = textPaint.measureText(label)
        val labelX = barLeft + (barWidth - textWidth) / 2f
        val labelY = barTop - 15f
        canvas.drawText(label, labelX, labelY, textPaint)
    }

}
