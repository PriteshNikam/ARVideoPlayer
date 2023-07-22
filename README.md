# ARVideoPlayer: Augmented Reality Video Playback without plane detection using SceneForm and ARCore

ARVideoPlayer is an Android application that utilizes sceneForm and ARCore to deliver augmented reality video playback directly in your environment. With the ability to place videos without the need for plane detection.

## Key Features:

1. **Immersive Augmented Reality Video Playback:** ARVideoPlayer uses ARCore, a technology that combines virtual elements with the real world, to create an exciting experience. It allows users to watch videos in an augmented-reality environment.

2. **Plane Detection Not Required:** One of the standout features of ARVideoPlayer is its ability to place videos without the need for plane detection. Unlike conventional AR applications that rely on flat surfaces, this advancement lets users place videos freely in the environment.

3. **Intuitive User Interaction:** The app boasts an intuitive and user-friendly interface, empowering users to effortlessly interact with virtual videos. With simple gestures, users can manipulate the position, orientation, and scale of the video objects to seamlessly integrate them into their surroundings.

4. **Gesture-Based Controls:** To enhance user interaction, the application incorporates gesture-based controls. Users can utilize taps, and other intuitive gestures to play and pause video.

ARVideoPlayer sets a new benchmark in augmented reality video playback by leveraging the combined strengths of SceneForm and ARCore. With its groundbreaking ability to place videos without the constraints of plane detection, the app unlocks endless creative possibilities for users to explore and enjoy augmented reality in their own environment. Whether for entertainment, educational purposes, or innovative applications, ARVideoPlayer offers an unparalleled and enchanting AR video experience.

I have created a class 'ARViewManager' to handle the AR session and explained below its functionality. 
- This class `ARViewManager` is responsible for managing the augmented reality (AR) video playback in an Android application using ARCore and Sceneform. It handles various tasks related to AR setup, video rendering, touch gestures, and media player control. Let's go through the main functionalities and features of this class:

1. `disablePlaneDiscovery()`: Disables the plane discovery UI elements to prevent detecting and rendering planes for AR interactions.

2. `enableCameraAutoFocusMode()`: Configures the AR session to enable auto-focus mode for the camera.

3. `initializeMediaPlayer(videoUri: String)`: Initializes the media player with the provided video URI, prepares it asynchronously, and creates an anchor node for video rendering. You can even pass streaming URL to play video online.

4. `prepareModel()`: Loads the video renderable from a source file and prepares it for rendering. It applies a green chroma key color to enable transparency in the video.

5. `createAnchorNodeWithCustomAnchor()`: Creates an `AnchorNode` with a custom anchor to place the video object without relying on plane detection.

6. `createVideoNode()`: Creates a video node and attaches it to the anchor node. The video node uses the video renderable for rendering.

7. `updateAnchorPositionAndScale(newVideoHeight: Int, newVideoWidth: Int)`: Updates the position and scale of the anchor node to match the new video dimensions when the video changes.

8. `initARSession()`: Initializes the AR session and custom anchor to place the video object in the AR scene.

9. `handleVideoGesture()`: Manages the touch gestures for the video object, including drag and resize operations. However, some parts of the code are commented out, and certain features like video scaling and rotation are marked as "TODO" and need to be implemented.

10. `release()`: Cleans up and releases the AR scene view and the media player resources.

11. `releaseMediaPlayer()`: Removes the video node from the anchor node and releases the media player resources.

12. `destroyARMedia()`: Destroys the AR scene view and releases the media player resources.

Overall, the `ARViewManager` class acts as a bridge between the ARCore-based AR scene and the video player, handling the video rendering, anchor management, touch gestures, and AR session initialization.
In this class, I have commented on video gestures and video resize code which can be uncommented as per use case.
