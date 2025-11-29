package com.pockettilt.maze

import com.pockettilt.maze.game.MazeGenerator
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for MazeGenerator
 */
class MazeGeneratorTest {
    
    @Test
    fun testMazeGeneration() {
        val maze = MazeGenerator.generateMaze(10, 10)
        
        // Verify dimensions
        assertEquals(10, maze.rows)
        assertEquals(10, maze.cols)
        assertEquals(10, maze.cells.size)
        assertEquals(10, maze.cells[0].size)
        
        // Verify start and goal are set
        assertNotNull(maze.startCell)
        assertNotNull(maze.goalCell)
    }
    
    @Test
    fun testDeterministicGeneration() {
        val seed = 20251128L
        
        val maze1 = MazeGenerator.generateMaze(15, 15, seed)
        val maze2 = MazeGenerator.generateMaze(15, 15, seed)
        
        // Verify same seed produces same maze
        assertEquals(maze1.rows, maze2.rows)
        assertEquals(maze1.cols, maze2.cols)
        
        // Check that walls match
        for (row in 0 until maze1.rows) {
            for (col in 0 until maze1.cols) {
                val cell1 = maze1.cells[row][col]
                val cell2 = maze2.cells[row][col]
                
                assertEquals(cell1.topWall, cell2.topWall)
                assertEquals(cell1.bottomWall, cell2.bottomWall)
                assertEquals(cell1.leftWall, cell2.leftWall)
                assertEquals(cell1.rightWall, cell2.rightWall)
            }
        }
    }
    
    @Test
    fun testAllCellsVisited() {
        val maze = MazeGenerator.generateMaze(15, 15, 12345L)
        
        // All cells should be visited (connected)
        for (row in 0 until maze.rows) {
            for (col in 0 until maze.cols) {
                assertTrue(maze.cells[row][col].visited)
            }
        }
    }
    
    @Test
    fun testDifferentSeedsProduceDifferentMazes() {
        val maze1 = MazeGenerator.generateMaze(10, 10, 111L)
        val maze2 = MazeGenerator.generateMaze(10, 10, 222L)
        
        // Count differences in walls
        var differences = 0
        for (row in 0 until maze1.rows) {
            for (col in 0 until maze1.cols) {
                val cell1 = maze1.cells[row][col]
                val cell2 = maze2.cells[row][col]
                
                if (cell1.topWall != cell2.topWall) differences++
                if (cell1.bottomWall != cell2.bottomWall) differences++
                if (cell1.leftWall != cell2.leftWall) differences++
                if (cell1.rightWall != cell2.rightWall) differences++
            }
        }
        
        // Different seeds should produce different mazes
        assertTrue("Mazes should be different", differences > 0)
    }
    
    @Test
    fun testStartAndGoalPositions() {
        val maze = MazeGenerator.generateMaze(15, 15)
        
        // Start should be at top-left
        assertEquals(0, maze.startCell.row)
        assertEquals(0, maze.startCell.col)
        
        // Goal should be at bottom-right
        assertEquals(14, maze.goalCell.row)
        assertEquals(14, maze.goalCell.col)
    }
}
