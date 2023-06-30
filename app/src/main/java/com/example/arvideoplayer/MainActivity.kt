package com.example.arvideoplayer

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.arvideoplayer.databinding.ActivityMainBinding
import com.example.arvideoplayer.utils.MainViewModel
import com.google.ar.sceneform.ux.ArFragment


class MainActivity : AppCompatActivity(), ARViewCallback {

    private lateinit var mBinding: ActivityMainBinding
    private var arViewManager: ARViewManager? = null
    private val mainViewModel: MainViewModel by viewModels()
    private var newVideoHeight: Int = 0
    private var newVideoWidth: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       mBinding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(this.root)
        }

        arViewManager = ARViewManager(this, this.applicationContext)
        createARSession()
        val videoPath = "android.resource://" + packageName + "/" + R.raw.lion_chroma
        arViewManager?.initializeMediaPlayer(videoPath)
    }

    private fun createARSession() {
        arViewManager?.apply {
            arFragment = supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment
            disablePlaneDiscovery()
            enableCameraAutoFocusMode()
        }
    }

    override fun initializeVideoPlayback() {
        arViewManager?.mediaPlayer?.let { mediaPlayer ->
            mediaPlayer.setOnPreparedListener {
                newVideoHeight = it.videoHeight
                newVideoWidth = it.videoWidth
                it.start()
                arViewManager?.updateAnchorPositionAndScale(
                    newVideoHeight,
                    newVideoWidth
                )
            }
            arViewManager?.initARSession()
        }
        arViewManager?.prepareModel()
    }


}