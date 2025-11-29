package com.pockettilt.maze.game

import android.graphics.PointF
import android.graphics.RectF

/**
 * Game engine handling physics, collision detection, and game state
 */
class GameEngine(private val maze: Maze) {
    
    // Ball properties
    var ballPosition = PointF()
    var ballVelocity = PointF(0f, 0f)
    val ballRadius = 0.3f // Relative to cell size
    
    // Game state
    private var startTime: Long = 0
    private var elapsedTime: Long = 0
    private var isRunning = false
    private var isComplete = false
    
    // Cell dimensions (set by view)
    var cellWidth = 0f
    var cellHeight = 0f
    
    init {
        resetBall()
    }
    
    /**
     * Reset ball to start position
     */
    fun resetBall() {
        ballPosition.x = maze.startCell.col + 0.5f
        ballPosition.y = maze.startCell.row + 0.5f
        ballVelocity.set(0f, 0f)
    }
    
    /**
     * Start the game timer
     */
    fun start() {
        if (!isRunning) {
            startTime = System.currentTimeMillis()
            isRunning = true
        }
    }
    
    /**
     * Pause the game
     */
    fun pause() {
        if (isRunning) {
            elapsedTime += System.currentTimeMillis() - startTime
            isRunning = false
        }
    }
    
    /**
     * Resume the game
     */
    fun resume() {
        if (!isRunning && !isComplete) {
            startTime = System.currentTimeMillis()
            isRunning = true
        }
    }
    
    /**
     * Get current elapsed time in milliseconds
     */
    fun getElapsedTimeMs(): Long {
        return if (isRunning) {
            elapsedTime + (System.currentTimeMillis() - startTime)
        } else {
            elapsedTime
        }
    }
    
    /**
     * Update ball position based on velocity and check collisions
     * @param deltaTime Time since last update in seconds
     * @return true if ball hit a wall
     */
    fun update(deltaTime: Float): Boolean {
        if (!isRunning || isComplete) return false
        
        var hitWall = false
        
        // Calculate new position
        val newX = ballPosition.x + ballVelocity.x * deltaTime
        val newY = ballPosition.y + ballVelocity.y * deltaTime
        
        // Check X-axis collision
        val testX = PointF(newX, ballPosition.y)
        if (!checkCollision(testX)) {
            ballPosition.x = newX
        } else {
            ballVelocity.x = 0f
            hitWall = true
        }
        
        // Check Y-axis collision
        val testY = PointF(ballPosition.x, newY)
        if (!checkCollision(testY)) {
            ballPosition.y = newY
        } else {
            ballVelocity.y = 0f
            hitWall = true
        }
        
        // Apply friction
        ballVelocity.x *= 0.98f
        ballVelocity.y *= 0.98f
        
        // Check win condition
        if (checkWinCondition()) {
            isComplete = true
            pause()
        }
        
        return hitWall
    }
    
    /**
     * Check if ball collides with walls at given position
     */
    private fun checkCollision(position: PointF): Boolean {
        val cellX = position.x.toInt()
        val cellY = position.y.toInt()
        
        // Check bounds
        if (cellX < 0 || cellX >= maze.cols || cellY < 0 || cellY >= maze.rows) {
            return true
        }
        
        val cell = maze.cells[cellY][cellX]
        
        // Get ball bounds relative to cell
        val localX = position.x - cellX
        val localY = position.y - cellY
        
        // Check collision with each wall
        if (cell.topWall && localY - ballRadius < 0) return true
        if (cell.bottomWall && localY + ballRadius > 1) return true
        if (cell.leftWall && localX - ballRadius < 0) return true
        if (cell.rightWall && localX + ballRadius > 1) return true
        
        // Check adjacent cells for corner cases
        return checkAdjacentWalls(position)
    }
    
    /**
     * Check walls in adjacent cells for corner collisions
     */
    private fun checkAdjacentWalls(position: PointF): Boolean {
        val cellX = position.x.toInt()
        val cellY = position.y.toInt()
        val localX = position.x - cellX
        val localY = position.y - cellY
        
        // Check top-left corner
        if (localX - ballRadius < 0 && localY - ballRadius < 0) {
            if (cellX > 0 && cellY > 0) {
                val adjCell = maze.cells[cellY - 1][cellX - 1]
                if (adjCell.bottomWall || adjCell.rightWall) return true
            }
        }
        
        // Check top-right corner
        if (localX + ballRadius > 1 && localY - ballRadius < 0) {
            if (cellX < maze.cols - 1 && cellY > 0) {
                val adjCell = maze.cells[cellY - 1][cellX + 1]
                if (adjCell.bottomWall || adjCell.leftWall) return true
            }
        }
        
        // Check bottom-left corner
        if (localX - ballRadius < 0 && localY + ballRadius > 1) {
            if (cellX > 0 && cellY < maze.rows - 1) {
                val adjCell = maze.cells[cellY + 1][cellX - 1]
                if (adjCell.topWall || adjCell.rightWall) return true
            }
        }
        
        // Check bottom-right corner
        if (localX + ballRadius > 1 && localY + ballRadius > 1) {
            if (cellX < maze.cols - 1 && cellY < maze.rows - 1) {
                val adjCell = maze.cells[cellY + 1][cellX + 1]
                if (adjCell.topWall || adjCell.leftWall) return true
            }
        }
        
        return false
    }
    
    /**
     * Check if ball has reached the goal
     */
    private fun checkWinCondition(): Boolean {
        val goalRect = RectF(
            maze.goalCell.col + 0.2f,
            maze.goalCell.row + 0.2f,
            maze.goalCell.col + 0.8f,
            maze.goalCell.row + 0.8f
        )
        
        return ballPosition.x >= goalRect.left &&
                ballPosition.x <= goalRect.right &&
                ballPosition.y >= goalRect.top &&
                ballPosition.y <= goalRect.bottom
    }
    
    /**
     * Set ball velocity from accelerometer input
     */
    fun setVelocity(vx: Float, vy: Float) {
        ballVelocity.set(vx, vy)
    }
    
    /**
     * Check if game is complete
     */
    fun isGameComplete() = isComplete
    
    /**
     * Check if game is running
     */
    fun isGameRunning() = isRunning
    
    /**
     * Reset the game
     */
    fun reset() {
        resetBall()
        startTime = 0
        elapsedTime = 0
        isRunning = false
        isComplete = false
    }
}
