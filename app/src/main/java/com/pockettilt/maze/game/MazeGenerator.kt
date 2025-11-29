package com.pockettilt.maze.game

import kotlin.random.Random

/**
 * Represents a single cell in the maze grid
 */
data class Cell(
    val row: Int,
    val col: Int,
    var topWall: Boolean = true,
    var bottomWall: Boolean = true,
    var leftWall: Boolean = true,
    var rightWall: Boolean = true,
    var visited: Boolean = false
)

/**
 * Represents a complete maze with grid, start, and goal positions
 */
data class Maze(
    val rows: Int,
    val cols: Int,
    val cells: Array<Array<Cell>>,
    val startCell: Cell,
    val goalCell: Cell
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Maze

        if (rows != other.rows) return false
        if (cols != other.cols) return false
        if (!cells.contentDeepEquals(other.cells)) return false
        if (startCell != other.startCell) return false
        if (goalCell != other.goalCell) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rows
        result = 31 * result + cols
        result = 31 * result + cells.contentDeepHashCode()
        result = 31 * result + startCell.hashCode()
        result = 31 * result + goalCell.hashCode()
        return result
    }
}

/**
 * Generates random mazes using recursive backtracking algorithm
 */
object MazeGenerator {
    
    /**
     * Generate a maze with the specified dimensions
     * @param rows Number of rows in the maze grid
     * @param cols Number of columns in the maze grid
     * @param seed Optional seed for deterministic generation (used for Daily Maze)
     * @return Generated Maze object
     */
    fun generateMaze(rows: Int, cols: Int, seed: Long? = null): Maze {
        val random = if (seed != null) Random(seed) else Random.Default
        
        // Initialize grid with all walls
        val cells = Array(rows) { row ->
            Array(cols) { col ->
                Cell(row, col)
            }
        }
        
        // Start from random cell (or top-left for consistency)
        val startRow = 0
        val startCol = 0
        val startCell = cells[startRow][startCol]
        
        // Generate maze using recursive backtracking
        generateMazeRecursive(cells, startCell, random)
        
        // Set goal at opposite corner
        val goalCell = cells[rows - 1][cols - 1]
        
        return Maze(rows, cols, cells, startCell, goalCell)
    }
    
    /**
     * Recursive backtracking algorithm to carve paths through the maze
     */
    private fun generateMazeRecursive(
        cells: Array<Array<Cell>>,
        current: Cell,
        random: Random
    ) {
        current.visited = true
        
        // Get unvisited neighbors in random order
        val neighbors = getUnvisitedNeighbors(cells, current).shuffled(random)
        
        for (neighbor in neighbors) {
            if (!neighbor.visited) {
                // Remove wall between current and neighbor
                removeWall(current, neighbor)
                
                // Recursively visit neighbor
                generateMazeRecursive(cells, neighbor, random)
            }
        }
    }
    
    /**
     * Get all unvisited neighboring cells
     */
    private fun getUnvisitedNeighbors(
        cells: Array<Array<Cell>>,
        cell: Cell
    ): List<Cell> {
        val neighbors = mutableListOf<Cell>()
        val rows = cells.size
        val cols = cells[0].size
        
        // Top neighbor
        if (cell.row > 0) {
            neighbors.add(cells[cell.row - 1][cell.col])
        }
        
        // Bottom neighbor
        if (cell.row < rows - 1) {
            neighbors.add(cells[cell.row + 1][cell.col])
        }
        
        // Left neighbor
        if (cell.col > 0) {
            neighbors.add(cells[cell.row][cell.col - 1])
        }
        
        // Right neighbor
        if (cell.col < cols - 1) {
            neighbors.add(cells[cell.row][cell.col + 1])
        }
        
        return neighbors.filter { !it.visited }
    }
    
    /**
     * Remove the wall between two adjacent cells
     */
    private fun removeWall(current: Cell, neighbor: Cell) {
        val rowDiff = neighbor.row - current.row
        val colDiff = neighbor.col - current.col
        
        when {
            rowDiff == 1 -> {
                // Neighbor is below
                current.bottomWall = false
                neighbor.topWall = false
            }
            rowDiff == -1 -> {
                // Neighbor is above
                current.topWall = false
                neighbor.bottomWall = false
            }
            colDiff == 1 -> {
                // Neighbor is to the right
                current.rightWall = false
                neighbor.leftWall = false
            }
            colDiff == -1 -> {
                // Neighbor is to the left
                current.leftWall = false
                neighbor.rightWall = false
            }
        }
    }
}
