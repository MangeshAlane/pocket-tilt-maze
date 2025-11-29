package com.pockettilt.maze.data

import android.content.Context
import android.content.SharedPreferences
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Manager for SharedPreferences data storage
 */
class PreferencesManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val PREFS_NAME = "pocket_tilt_maze_prefs"
        
        // Daily Maze keys
        private const val KEY_DAILY_MAZE_DATE = "daily_maze_date"
        private const val KEY_DAILY_MAZE_SEED = "daily_maze_seed"
        private const val KEY_DAILY_MAZE_BEST_TIME = "daily_maze_best_time"
        
        // Settings keys
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val KEY_MAZE_SIZE = "maze_size"
        private const val KEY_SENSITIVITY = "sensitivity"
        
        // Stats keys
        private const val KEY_TOTAL_COMPLETED = "total_completed"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        
        // History keys (last 7 days)
        private const val KEY_HISTORY_PREFIX = "history_"
        
        // Default values
        const val DEFAULT_MAZE_SIZE = 15
        const val DEFAULT_SENSITIVITY = 1.0f
    }
    
    // ========== Daily Maze ==========
    
    /**
     * Get today's date as string
     */
    fun getTodayDateString(): String {
        return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    }
    
    /**
     * Get seed for today's daily maze
     */
    fun getDailyMazeSeed(): Long {
        val today = getTodayDateString()
        val savedDate = prefs.getString(KEY_DAILY_MAZE_DATE, null)
        
        return if (savedDate == today) {
            // Same day, return saved seed
            prefs.getLong(KEY_DAILY_MAZE_SEED, 0L)
        } else {
            // New day, generate new seed from date
            val seed = today.replace("-", "").toLong()
            prefs.edit()
                .putString(KEY_DAILY_MAZE_DATE, today)
                .putLong(KEY_DAILY_MAZE_SEED, seed)
                .apply()
            seed
        }
    }
    
    /**
     * Get best time for today's daily maze (in milliseconds)
     * Returns null if no time recorded
     */
    fun getDailyMazeBestTime(): Long? {
        val today = getTodayDateString()
        val savedDate = prefs.getString(KEY_DAILY_MAZE_DATE, null)
        
        return if (savedDate == today) {
            val time = prefs.getLong(KEY_DAILY_MAZE_BEST_TIME, -1L)
            if (time > 0) time else null
        } else {
            null
        }
    }
    
    /**
     * Save best time for today's daily maze
     * @return true if this is a new best time
     */
    fun saveDailyMazeBestTime(timeMs: Long): Boolean {
        val currentBest = getDailyMazeBestTime()
        val isNewBest = currentBest == null || timeMs < currentBest
        
        if (isNewBest) {
            val today = getTodayDateString()
            prefs.edit()
                .putLong(KEY_DAILY_MAZE_BEST_TIME, timeMs)
                .apply()
            
            // Save to history
            saveToHistory(today, timeMs)
        }
        
        return isNewBest
    }
    
    // ========== Settings ==========
    
    /**
     * Check if vibration is enabled
     */
    fun isVibrationEnabled(): Boolean {
        return prefs.getBoolean(KEY_VIBRATION_ENABLED, true)
    }
    
    /**
     * Set vibration enabled state
     */
    fun setVibrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, enabled).apply()
    }
    
    /**
     * Get maze size for Quick Maze mode
     */
    fun getMazeSize(): Int {
        return prefs.getInt(KEY_MAZE_SIZE, DEFAULT_MAZE_SIZE)
    }
    
    /**
     * Set maze size for Quick Maze mode
     */
    fun setMazeSize(size: Int) {
        prefs.edit().putInt(KEY_MAZE_SIZE, size).apply()
    }
    
    /**
     * Get sensor sensitivity multiplier
     */
    fun getSensitivity(): Float {
        return prefs.getFloat(KEY_SENSITIVITY, DEFAULT_SENSITIVITY)
    }
    
    /**
     * Set sensor sensitivity multiplier
     */
    fun setSensitivity(sensitivity: Float) {
        prefs.edit().putFloat(KEY_SENSITIVITY, sensitivity).apply()
    }
    
    // ========== Stats ==========
    
    /**
     * Get total number of mazes completed
     */
    fun getTotalCompleted(): Int {
        return prefs.getInt(KEY_TOTAL_COMPLETED, 0)
    }
    
    /**
     * Increment total completed counter
     */
    fun incrementTotalCompleted() {
        val current = getTotalCompleted()
        prefs.edit().putInt(KEY_TOTAL_COMPLETED, current + 1).apply()
    }
    
    /**
     * Check if this is the first launch
     */
    fun isFirstLaunch(): Boolean {
        val isFirst = prefs.getBoolean(KEY_FIRST_LAUNCH, true)
        if (isFirst) {
            prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
        }
        return isFirst
    }
    
    // ========== History ==========
    
    /**
     * Save time to history for a specific date
     */
    private fun saveToHistory(date: String, timeMs: Long) {
        prefs.edit()
            .putLong(KEY_HISTORY_PREFIX + date, timeMs)
            .apply()
    }
    
    /**
     * Get history for last N days
     * Returns map of date to best time in milliseconds
     */
    fun getHistory(days: Int = 7): Map<String, Long> {
        val history = mutableMapOf<String, Long>()
        val today = LocalDate.now()
        
        for (i in 0 until days) {
            val date = today.minusDays(i.toLong())
            val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val time = prefs.getLong(KEY_HISTORY_PREFIX + dateStr, -1L)
            
            if (time > 0) {
                history[dateStr] = time
            }
        }
        
        return history
    }
    
    /**
     * Format time in milliseconds to MM:SS.mmm
     */
    fun formatTime(timeMs: Long): String {
        val minutes = (timeMs / 60000).toInt()
        val seconds = ((timeMs % 60000) / 1000).toInt()
        val millis = (timeMs % 1000).toInt()
        return String.format("%02d:%02d.%03d", minutes, seconds, millis)
    }
}
