package com.pockettilt.maze.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.pockettilt.maze.R

/**
 * Manager for AdMob banner and interstitial ads
 */
class AdManager(private val context: Context) {
    
    companion object {
        private const val TAG = "AdManager"
        private const val QUICK_MAZE_AD_FREQUENCY = 3 // Show ad every 3 Quick Mazes
    }
    
    private var interstitialAd: InterstitialAd? = null
    private var quickMazeCompletionCount = 0
    
    /**
     * Initialize AdMob SDK
     */
    fun initialize() {
        MobileAds.initialize(context) { initializationStatus ->
            Log.d(TAG, "AdMob initialized: ${initializationStatus.adapterStatusMap}")
        }
        
        // Preload first interstitial
        loadInterstitialAd()
    }
    
    /**
     * Create and return a banner ad view
     */
    fun createBannerAd(): AdView {
        return AdView(context).apply {
            adUnitId = context.getString(R.string.admob_banner_ad_unit_id)
            setAdSize(AdSize.BANNER)
            
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    Log.d(TAG, "Banner ad loaded")
                }
                
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e(TAG, "Banner ad failed to load: ${error.message}")
                }
            }
            
            loadAd(AdRequest.Builder().build())
        }
    }
    
    /**
     * Load an interstitial ad
     */
    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        
        InterstitialAd.load(
            context,
            context.getString(R.string.admob_interstitial_ad_unit_id),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Interstitial ad loaded")
                    interstitialAd = ad
                    
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            Log.d(TAG, "Interstitial ad dismissed")
                            interstitialAd = null
                            // Preload next ad
                            loadInterstitialAd()
                        }
                        
                        override fun onAdFailedToShowFullScreenContent(error: AdError) {
                            Log.e(TAG, "Interstitial ad failed to show: ${error.message}")
                            interstitialAd = null
                        }
                        
                        override fun onAdShowedFullScreenContent() {
                            Log.d(TAG, "Interstitial ad showed")
                        }
                    }
                }
                
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e(TAG, "Interstitial ad failed to load: ${error.message}")
                    interstitialAd = null
                }
            }
        )
    }
    
    /**
     * Show interstitial ad after Daily Maze completion
     */
    fun showInterstitialForDailyMaze(activity: Activity) {
        showInterstitial(activity)
    }
    
    /**
     * Track Quick Maze completion and show ad if needed
     */
    fun onQuickMazeCompleted(activity: Activity) {
        quickMazeCompletionCount++
        
        if (quickMazeCompletionCount >= QUICK_MAZE_AD_FREQUENCY) {
            quickMazeCompletionCount = 0
            showInterstitial(activity)
        }
    }
    
    /**
     * Show interstitial ad if loaded
     */
    private fun showInterstitial(activity: Activity) {
        interstitialAd?.let { ad ->
            ad.show(activity)
        } ?: run {
            Log.d(TAG, "Interstitial ad not ready, loading...")
            loadInterstitialAd()
        }
    }
    
    /**
     * Reset Quick Maze counter (e.g., when app restarts)
     */
    fun resetQuickMazeCounter() {
        quickMazeCompletionCount = 0
    }
}
