package com.saranyu.videotask

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.otaliastudios.cameraview.*
import com.otaliastudios.cameraview.controls.Mode
import com.saranyu.videotask.utility.isPermissionGranted
import com.saranyu.videotask.utility.requestPermission
import java.io.File

class MainActivity : AppCompatActivity() {
    lateinit var cameraView: CameraView
    lateinit var actionButton:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cameraView = findViewById(R.id.cameraPrev)
        actionButton = findViewById(R.id.action)
        if(CameraUtils.hasCameras(this)) {
            cameraView.mode = Mode.VIDEO
            cameraView.setLifecycleOwner(this)
            cameraView.videoBitRate = 10000
            actionButton.setOnClickListener {
                if (actionButton.text.toString() == "Capture") {
                    cameraView.takeVideo(File(filesDir,"Video.mp4"))
                }
                if(actionButton.text.toString()=="Save") {
                    cameraView.stopVideo()
                }
            }
        }

      /*  val options: List<Option<*>> = listOf(
            // Layout
            Option.Width(), Option.Height(),
            // Engine and preview
            Option.Mode(), Option.Engine(), Option.Preview(),
            // Some controls
            Option.Flash(), Option.WhiteBalance(), Option.Hdr(),
            Option.PictureMetering(), Option.PictureSnapshotMetering(),
            Option.PictureFormat(),
            // Video recording
            Option.PreviewFrameRate(), Option.VideoCodec(), Option.Audio(), Option.AudioCodec(),
            // Gestures
            Option.Pinch(), Option.HorizontalScroll(), Option.VerticalScroll(),
            Option.Tap(), Option.LongTap(),
            // Watermarks
            Option.OverlayInPreview(watermark),
            Option.OverlayInPictureSnapshot(watermark),
            Option.OverlayInVideoSnapshot(watermark),
            // Frame Processing
            Option.FrameProcessingFormat(),
            // Other
            Option.Grid(), Option.GridColor(), Option.UseDeviceOrientation()
        )*/
        cameraView.addCameraListener(object : CameraListener() {
            override fun onCameraOpened(options: CameraOptions) {
                super.onCameraOpened(options)

            }
            override fun onVideoTaken(result: VideoResult) {
                super.onVideoTaken(result)
                actionButton.text = "Start"
                VideoPreviewActivity.videoResult = result
                val intent = Intent(this@MainActivity, VideoPreviewActivity::class.java)
                startActivity(intent)

            }

            override fun onVideoRecordingStart() {
                super.onVideoRecordingStart()
                actionButton.text="Save"
            }

            override fun onCameraError(exception: CameraException) {
                super.onCameraError(exception)
                /**
                 * Error handling
                 */
            }
        })

    }

    override fun onResume() {
        super.onResume()
        if(!isPermissionGranted(Manifest.permission.CAMERA)) requestPermission(Manifest.permission.CAMERA, 999)
    }
}