package com.pockettilt.maze.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pockettilt.maze.MainActivity
import com.pockettilt.maze.R
import com.pockettilt.maze.data.PreferencesManager
import com.pockettilt.maze.databinding.FragmentGameBinding
import com.pockettilt.maze.game.GameEngine
import com.pockettilt.maze.game.MazeGenerator

/**
 * Game screen fragment with tilt controls
 */
class GameFragment : Fragment(), SensorEventListener {
    
    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!
    
    private val args: GameFragmentArgs by navArgs()
    
    private lateinit var prefsManager: PreferencesManager
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var gameEngine: GameEngine
    private lateinit var vibrator: Vibrator
    
    private var isPaused = false
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        prefsManager = PreferencesManager(requireContext())
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!
        vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        
        setupGame()
        setupUI()
        setupBannerAd()
    }
    
    private fun setupGame() {
        // Generate maze
        val maze = if (args.isDaily) {
            val seed = prefsManager.getDailyMazeSeed()
            MazeGenerator.generateMaze(15, 15, seed)
        } else {
            MazeGenerator.generateMaze(args.mazeSize, args.mazeSize)
        }
        
        // Create game engine
        gameEngine = GameEngine(maze)
        
        // Set up maze view
        binding.mazeView.setMaze(maze, gameEngine)
        
        // Set callbacks
        binding.mazeView.onWallHit = {
            if (prefsManager.isVibrationEnabled()) {
                vibrate()
            }
        }
        
        binding.mazeView.onGameComplete = { timeMs ->
            onGameComplete(timeMs)
        }
        
        // Start game
        gameEngine.start()
        binding.mazeView.startGameLoop()
        startTimer()
    }
    
    private fun setupUI() {
        binding.btnPause.setOnClickListener {
            showPauseMenu()
        }
    }
    
    private fun setupBannerAd() {
        val adManager = (requireActivity() as MainActivity).adManager
        val bannerAd = adManager.createBannerAd()
        binding.adContainer.removeAllViews()
        binding.adContainer.addView(bannerAd)
    }
    
    private fun startTimer() {
        binding.tvTimer.post(object : Runnable {
            override fun run() {
                if (!isPaused && _binding != null) {
                    val timeMs = gameEngine.getElapsedTimeMs()
                    binding.tvTimer.text = prefsManager.formatTime(timeMs)
                    binding.tvTimer.postDelayed(this, 50)
                }
            }
        })
    }
    
    private fun vibrate() {
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
    
    private fun showPauseMenu() {
        isPaused = true
        gameEngine.pause()
        binding.mazeView.stopGameLoop()
        sensorManager.unregisterListener(this)
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.pause))
            .setItems(arrayOf(
                getString(R.string.resume),
                getString(R.string.restart),
                getString(R.string.quit)
            )) { dialog, which ->
                when (which) {
                    0 -> resumeGame()
                    1 -> restartGame()
                    2 -> quitGame()
                }
            }
            .setOnCancelListener {
                resumeGame()
            }
            .show()
    }
    
    private fun resumeGame() {
        isPaused = false
        gameEngine.resume()
        binding.mazeView.startGameLoop()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        startTimer()
    }
    
    private fun restartGame() {
        gameEngine.reset()
        isPaused = false
        gameEngine.start()
        binding.mazeView.startGameLoop()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        startTimer()
    }
    
    private fun quitGame() {
        findNavController().navigateUp()
    }
    
    private fun onGameComplete(timeMs: Long) {
        sensorManager.unregisterListener(this)
        
        // Update stats
        prefsManager.incrementTotalCompleted()
        
        // Check for best time and show ad
        val isNewBest = if (args.isDaily) {
            val newBest = prefsManager.saveDailyMazeBestTime(timeMs)
            (requireActivity() as MainActivity).adManager.showInterstitialForDailyMaze(requireActivity())
            newBest
        } else {
            (requireActivity() as MainActivity).adManager.onQuickMazeCompleted(requireActivity())
            false
        }
        
        // Show completion dialog
        showCompletionDialog(timeMs, isNewBest)
    }
    
    private fun showCompletionDialog(timeMs: Long, isNewBest: Boolean) {
        val message = buildString {
            append(getString(R.string.time_taken, prefsManager.formatTime(timeMs)))
            if (args.isDaily) {
                append("\n")
                if (isNewBest) {
                    append(getString(R.string.new_best_time))
                } else {
                    val bestTime = prefsManager.getDailyMazeBestTime()
                    if (bestTime != null) {
                        append(getString(R.string.best_time, prefsManager.formatTime(bestTime)))
                    }
                }
            }
        }
        
        val buttons = if (args.isDaily) {
            arrayOf(getString(R.string.play_again), getString(R.string.home))
        } else {
            arrayOf(getString(R.string.new_quick_maze), getString(R.string.home))
        }
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.level_complete))
            .setMessage(message)
            .setPositiveButton(buttons[0]) { _, _ ->
                restartGame()
            }
            .setNegativeButton(buttons[1]) { _, _ ->
                findNavController().navigateUp()
            }
            .setCancelable(false)
            .show()
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER && !isPaused) {
            val ax = event.values[0]
            val ay = event.values[1]
            
            val sensitivity = prefsManager.getSensitivity()
            
            // Convert tilt to velocity (inverted for natural feel)
            val vx = -ax * sensitivity * 2f
            val vy = ay * sensitivity * 2f
            
            gameEngine.setVelocity(vx, vy)
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed
    }
    
    override fun onResume() {
        super.onResume()
        if (!isPaused) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        }
    }
    
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        binding.mazeView.stopGameLoop()
        sensorManager.unregisterListener(this)
        _binding = null
    }
}
