package com.example.arvideoplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.arvideoplayer.databinding.ActivityMainBinding
import com.google.ar.sceneform.ux.ArFragment

class MainActivity : AppCompatActivity(), ARViewCallback {

    private lateinit var mBinding: ActivityMainBinding
    private var arViewManager: ARViewManager? = null
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

        mBinding.btnMediaReplay.setOnClickListener {
            arViewManager?.mediaPlayer?.pause()
            arViewManager?.mediaPlayer?.seekTo(0)
            arViewManager?.mediaPlayer?.start()
        }

        mBinding.btnMediaPlayPause.setOnClickListener {
            handelMediaPlayPause()
        }
    }

    private fun handelMediaPlayPause() {
        if (arViewManager?.mediaPlayer?.isPlaying == true) {
            arViewManager?.mediaPlayer?.pause()
            mBinding.btnMediaPlayPause.setImageResource(R.drawable.play_circle)
        } else {
            arViewManager?.mediaPlayer?.start()
            mBinding.btnMediaPlayPause.setImageResource(R.drawable.pause_circle)
        }
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

    override fun onPause() {
        super.onPause()
        handelMediaPlayPause()
    }

    override fun onResume() {
        super.onResume()
        if (arViewManager?.mediaPlayer?.isPlaying == true) {
            arViewManager?.mediaPlayer?.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        arViewManager?.destroyARMedia()
    }

}