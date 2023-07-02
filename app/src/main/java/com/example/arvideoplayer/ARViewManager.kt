package com.example.arvideoplayer

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.view.MotionEvent
import android.widget.Toast
import com.example.arvideoplayer.utils.Constants
import com.example.arvideoplayer.utils.Logger
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Pose
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.ExternalTexture
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment

class ARViewManager(
    private val arViewCallback: ARViewCallback,
    private val context: Context,
) {
    companion object {
        const val HEIGHT = 0.65f
        private var lastTouchX: Float = 0f
        private var lastTouchY: Float = 0f
        private var initialFingerSpacing = 0f
    }

    var videoRenderable: ModelRenderable? = null
    var arFragment: ArFragment? = null
    var mediaPlayer: MediaPlayer? = null
    private var anchorNode: AnchorNode? = null
    private var videoTexture: ExternalTexture? = null
    private var customAnchor: Anchor? = null
    private var videoNode: Node? = null
    private var isScaling = false

    /**
     * Disables plane discovery, initializes the media player with the provided video URI,
     * and hides the plane discovery UI elements.
     *
     */
    fun disablePlaneDiscovery() {
        arFragment?.apply {
            planeDiscoveryController.hide()
            planeDiscoveryController.setInstructionView(null)
            arSceneView.planeRenderer.isEnabled = false
        }
    }

    /**
     * Enables the auto-focus mode for the camera in the ARSceneView.
     * This sets the update mode to LATEST_CAMERA_IMAGE and the focus mode to AUTO in the AR session's configuration.
     */
    fun enableCameraAutoFocusMode() {
        arFragment?.arSceneView?.session?.config?.let { config ->
            config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
            config.focusMode = Config.FocusMode.AUTO
        }
    }

    /**
     * Initializes the media player with the provided video URI and prepares it asynchronously.
     *
     * @param videoUri The URI of the video to be played.
     */
    @SuppressLint("ClickableViewAccessibility")
    fun initializeMediaPlayer(
        videoUri: String
    ) {
        if (mediaPlayer != null) {
            mediaPlayer?.release()
        }
        videoTexture = ExternalTexture()
        mediaPlayer = MediaPlayer()

        mediaPlayer?.run {
            setSurface(videoTexture?.surface)
            setDataSource(context, Uri.parse(videoUri))
            prepareAsync()
/*
            setOnCompletionListener {
                arViewCallback.updateUIOnVideoCompletion()
            }*/

            createAnchorNodeWithCustomAnchor()
            createVideoNode()
            arViewCallback.initializeVideoPlayback()
        }
    }

    /**
     * Creates a video node and attaches it to the anchor node.
     * The video node uses the video renderable for rendering.
     */
    private fun createVideoNode() {
        videoNode = Node().apply {
            setParent(anchorNode)
            renderable = videoRenderable
        }
    }

    /**
     * Creates an AnchorNode with a custom anchor and attaches it to the ARSceneView's scene.
     * The existing video node is removed from the previous anchor node, if any.
     *
     */
    private fun createAnchorNodeWithCustomAnchor() {
        anchorNode?.removeChild(videoNode)
        anchorNode = AnchorNode(customAnchor).apply {
            setParent(arFragment?.arSceneView?.scene)
        }
    }

    /**
     * Prepares the model by loading the video renderable from a source file.
     *
     * @throws IllegalArgumentException If the model source file is not found or an error occurs during rendering.
     */
    fun prepareModel() {
        ModelRenderable.builder()
            .setSource(
                context,
                R.raw.chroma_key_video
            )
            .build()
            .thenAccept { renderable: ModelRenderable ->
                videoRenderable = renderable
                videoRenderable?.apply {
                    isShadowCaster = false
                    isShadowReceiver = false
                    material?.setExternalTexture(Constants.VIDEO_TEXTURE, videoTexture)
                    material?.setFloat4(
                        Constants.KEY_COLOR,
                        Color(0.1843f, 1.0f, 0.098f)
                    )
                }
            }.exceptionally {
                Logger.debug("PN session failure :: ${it.stackTraceToString()}")
                Logger.debug("PN model creation exception :: $it")

                Toast.makeText(
                    context,
                    "${it.printStackTrace()} ${it.message}",
                    Toast.LENGTH_LONG
                )
                    .show()
                null
            }
    }

    /**
     * Updates the position and scale of the anchor node to match the new video dimensions.
     *
     * @param newVideoHeight The height of the new video in pixels.
     * @param newVideoWidth  The width of the new video in pixels.
     */
    fun updateAnchorPositionAndScale(newVideoHeight: Int, newVideoWidth: Int) {
        anchorNode?.apply {
            localScale = Vector3(
                HEIGHT * (newVideoWidth.toFloat() / newVideoHeight.toFloat()),
                HEIGHT,
                0.90f
            )
            localPosition = Vector3(0.0f, -0.3f, -0.5f)
            arFragment?.arSceneView?.scene?.addChild(this)
        }
    }

    /**
     * Create AR session and custom Anchor to place object without plane detection.
     */
    fun initARSession() {
        arFragment?.setOnSessionInitializationListener { session ->
            session?.let { arSession ->
                arFragment?.setOnTapArPlaneListener { _, _, _ ->
                    customAnchor =
                        arSession.createAnchor(Pose.IDENTITY)
                }
            }
            handleVideoGesture()
        }
        if (anchorNode != null) {
            videoTexture
                ?.surfaceTexture
                ?.setOnFrameAvailableListener { surfaceTexture ->
                    videoNode?.renderable = videoRenderable
                    surfaceTexture.setOnFrameAvailableListener(null)
                }
        }
    }

    /**
     * Function to handle video object gesture.
     * Able to resize & drag video Node.
     */

    @SuppressLint("ClickableViewAccessibility")
    fun handleVideoGesture() {
        /**
         * resize video screen
         */
        // TODO add video scaling feature
        /*       var scaleFactor = 1.0f
                   val scaleGestureDetector =
                       ScaleGestureDetector(mContext, object : ScaleGestureDetector.OnScaleGestureListener {
                           override fun onScale(detector: ScaleGestureDetector): Boolean {
                               scaleFactor *= detector.scaleFactor
                               scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f))
                               anchorNode?.localScale = Vector3(scaleFactor, scaleFactor, 0.95f)
                               return true
                           }
                           override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                               return true
                           }
                           override fun onScaleEnd(detector: ScaleGestureDetector) {}
                       })*/

        arFragment?.arSceneView?.setOnTouchListener { _, event ->
            val x = event.rawX
            val y = event.rawY

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastTouchX = x
                    lastTouchY = y
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_MOVE -> {
                    /*       //TODO() add movie gesture
                           isVideoSelected = true
                           if (event.pointerCount > 1 && isVideoSelected) {
                               //     scaleGestureDetector.onTouchEvent(event)
                           } else {
                               val dx = x - lastTouchX
                               val dy = y - lastTouchY

                               val newX = videoNode?.localPosition?.x?.plus(dx * 0.001f)
                               val newY = videoNode?.localPosition?.y?.minus(dy * 0.001f)

                               newX?.let { updatedXAxis ->
                                   newY?.let { updatedYAxis ->
                                       videoNode?.localPosition =
                                           videoNode?.localPosition?.z?.let {
                                               Vector3(
                                                   updatedXAxis,
                                                   updatedYAxis,
                                                   it
                                               )
                                           }
                                   }
                               }

                               lastTouchX = x
                               lastTouchY = y
                           }*/
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    /**
                     * rotating video screen logic
                     */
                    //TODO add rotate feature
                    /*   val rotationAngleDeg = y * 0.2f
                         videoNode.localRotation = Quaternion.multiply(
                             Quaternion(Vector3.up(), rotationAngleDeg),
                             (videoNode.localRotation)
                         )
                         lastTouchX = x
                         lastTouchY = y*/

                    if (event.pointerCount <= 1) {
                        initialFingerSpacing = 0f
                        isScaling = false
                    }
                    return@setOnTouchListener true
                }
                else -> return@setOnTouchListener false
            }
        }
    }

    /**
     * To destroy AR Scene view & release MediaPlayer.
     */
    fun release() {
        arFragment?.arSceneView?.destroy()
        mediaPlayer?.release()
        mediaPlayer = null
        videoTexture = null
    }

    fun releaseMediaPlayer() {
        anchorNode?.removeChild(videoNode)
        videoNode = null
        videoTexture = null
    }

    fun destroyARMedia() {
        arFragment?.arSceneView?.destroy()
        mediaPlayer?.release()
    }
}