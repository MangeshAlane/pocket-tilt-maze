package com.pockettilt.maze.game

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.Choreographer
import android.view.View
import com.pockettilt.maze.R

/**
 * Custom view for rendering the maze game
 */
class MazeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), Choreographer.FrameCallback {

    private var maze: Maze? = null
    private var gameEngine: GameEngine? = null
    
    // Paint objects
    private val wallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.maze_wall)
        strokeWidth = 4f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    
    private val backgroundPaint = Paint().apply {
        color = context.getColor(R.color.maze_background)
        style = Paint.Style.FILL
    }
    
    private val ballPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.ball_color)
        style = Paint.Style.FILL
    }
    
    private val ballShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.ball_shadow)
        style = Paint.Style.FILL
        alpha = 100
    }
    
    private val goalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.goal_color)
        style = Paint.Style.FILL
    }
    
    private val goalGlowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.goal_glow)
        style = Paint.Style.FILL
        alpha = 150
    }
    
    private val startMarkerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.start_marker)
        style = Paint.Style.FILL
        alpha = 100
    }
    
    // Rendering properties
    private var cellWidth = 0f
    private var cellHeight = 0f
    private var offsetX = 0f
    private var offsetY = 0f
    
    // Game loop
    private val choreographer = Choreographer.getInstance()
    private var lastFrameTime = 0L
    private var isAnimating = false
    
    // Callback for wall collisions
    var onWallHit: (() -> Unit)? = null
    var onGameComplete: ((Long) -> Unit)? = null
    
    /**
     * Set the maze to render
     */
    fun setMaze(maze: Maze, engine: GameEngine) {
        this.maze = maze
        this.gameEngine = engine
        calculateDimensions()
        invalidate()
    }
    
    /**
     * Calculate cell dimensions and offsets to center the maze
     */
    private fun calculateDimensions() {
        val maze = this.maze ?: return
        
        val availableWidth = width - paddingLeft - paddingRight
        val availableHeight = height - paddingTop - paddingBottom
        
        cellWidth = availableWidth.toFloat() / maze.cols
        cellHeight = availableHeight.toFloat() / maze.rows
        
        // Use square cells (smallest dimension)
        val cellSize = minOf(cellWidth, cellHeight)
        cellWidth = cellSize
        cellHeight = cellSize
        
        // Center the maze
        offsetX = paddingLeft + (availableWidth - cellWidth * maze.cols) / 2
        offsetY = paddingTop + (availableHeight - cellHeight * maze.rows) / 2
        
        // Update game engine
        gameEngine?.cellWidth = cellWidth
        gameEngine?.cellHeight = cellHeight
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateDimensions()
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val maze = this.maze ?: return
        val engine = this.gameEngine ?: return
        
        // Draw background
        canvas.drawRect(
            offsetX,
            offsetY,
            offsetX + cellWidth * maze.cols,
            offsetY + cellHeight * maze.rows,
            backgroundPaint
        )
        
        // Draw start marker
        drawStartMarker(canvas, maze)
        
        // Draw goal
        drawGoal(canvas, maze)
        
        // Draw maze walls
        drawWalls(canvas, maze)
        
        // Draw ball
        drawBall(canvas, engine)
    }
    
    /**
     * Draw start position marker
     */
    private fun drawStartMarker(canvas: Canvas, maze: Maze) {
        val centerX = offsetX + (maze.startCell.col + 0.5f) * cellWidth
        val centerY = offsetY + (maze.startCell.row + 0.5f) * cellHeight
        val radius = minOf(cellWidth, cellHeight) * 0.15f
        
        canvas.drawCircle(centerX, centerY, radius, startMarkerPaint)
    }
    
    /**
     * Draw goal area with glow effect
     */
    private fun drawGoal(canvas: Canvas, maze: Maze) {
        val left = offsetX + (maze.goalCell.col + 0.2f) * cellWidth
        val top = offsetY + (maze.goalCell.row + 0.2f) * cellHeight
        val right = offsetX + (maze.goalCell.col + 0.8f) * cellWidth
        val bottom = offsetY + (maze.goalCell.row + 0.8f) * cellHeight
        
        val goalRect = RectF(left, top, right, bottom)
        
        // Draw glow
        val glowRect = RectF(
            left - cellWidth * 0.1f,
            top - cellHeight * 0.1f,
            right + cellWidth * 0.1f,
            bottom + cellHeight * 0.1f
        )
        canvas.drawRoundRect(glowRect, 10f, 10f, goalGlowPaint)
        
        // Draw goal
        canvas.drawRoundRect(goalRect, 8f, 8f, goalPaint)
    }
    
    /**
     * Draw all maze walls
     */
    private fun drawWalls(canvas: Canvas, maze: Maze) {
        for (row in 0 until maze.rows) {
            for (col in 0 until maze.cols) {
                val cell = maze.cells[row][col]
                val left = offsetX + col * cellWidth
                val top = offsetY + row * cellHeight
                val right = left + cellWidth
                val bottom = top + cellHeight
                
                // Draw walls
                if (cell.topWall) {
                    canvas.drawLine(left, top, right, top, wallPaint)
                }
                if (cell.bottomWall) {
                    canvas.drawLine(left, bottom, right, bottom, wallPaint)
                }
                if (cell.leftWall) {
                    canvas.drawLine(left, top, left, bottom, wallPaint)
                }
                if (cell.rightWall) {
                    canvas.drawLine(right, top, right, bottom, wallPaint)
                }
            }
        }
    }
    
    /**
     * Draw the ball with shadow
     */
    private fun drawBall(canvas: Canvas, engine: GameEngine) {
        val centerX = offsetX + engine.ballPosition.x * cellWidth
        val centerY = offsetY + engine.ballPosition.y * cellHeight
        val radius = engine.ballRadius * minOf(cellWidth, cellHeight)
        
        // Draw shadow
        canvas.drawCircle(
            centerX + radius * 0.2f,
            centerY + radius * 0.2f,
            radius * 0.9f,
            ballShadowPaint
        )
        
        // Draw ball
        canvas.drawCircle(centerX, centerY, radius, ballPaint)
        
        // Add highlight
        val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            alpha = 150
        }
        canvas.drawCircle(
            centerX - radius * 0.3f,
            centerY - radius * 0.3f,
            radius * 0.3f,
            highlightPaint
        )
    }
    
    /**
     * Start the game loop
     */
    fun startGameLoop() {
        if (!isAnimating) {
            isAnimating = true
            lastFrameTime = System.nanoTime()
            choreographer.postFrameCallback(this)
        }
    }
    
    /**
     * Stop the game loop
     */
    fun stopGameLoop() {
        isAnimating = false
        choreographer.removeFrameCallback(this)
    }
    
    /**
     * Choreographer frame callback for game loop
     */
    override fun doFrame(frameTimeNanos: Long) {
        if (!isAnimating) return
        
        val engine = gameEngine ?: return
        
        // Calculate delta time
        val deltaTime = (frameTimeNanos - lastFrameTime) / 1_000_000_000f
        lastFrameTime = frameTimeNanos
        
        // Update game state
        val hitWall = engine.update(deltaTime)
        
        // Trigger callbacks
        if (hitWall) {
            onWallHit?.invoke()
        }
        
        if (engine.isGameComplete()) {
            onGameComplete?.invoke(engine.getElapsedTimeMs())
            stopGameLoop()
        }
        
        // Redraw
        invalidate()
        
        // Schedule next frame
        if (isAnimating) {
            choreographer.postFrameCallback(this)
        }
    }
    
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopGameLoop()
    }
}
