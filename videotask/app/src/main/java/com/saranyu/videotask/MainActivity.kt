package com.saranyu.videotask

/**
 * Created by Vinayak
 * Record video with option of flash, camera face,
 */
import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View.GONE
import androidx.appcompat.app.AppCompatActivity
import com.otaliastudios.cameraview.*
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.controls.Flash
import com.otaliastudios.cameraview.controls.Mode
import com.saranyu.videotask.databinding.ActivityMainBinding
import com.saranyu.videotask.utility.isPermissionGranted
import com.saranyu.videotask.utility.requestPermission
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {
    var isFlashOn = true
    var isBackFacing = true
    lateinit var binding:ActivityMainBinding
    lateinit var recordTimer: CountDownTimer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uIEvents()
    }

    /**
     * UI handling
     */
    private fun uIEvents() {
        var sec=0
        with(binding) {

            if(CameraUtils.hasCameras(this@MainActivity)) {
                cameraPrev.mode = Mode.VIDEO
                cameraPrev.setRequestPermissions(true)
                cameraPrev.setLifecycleOwner(this@MainActivity)
                recordVideo.setImageResource(R.drawable.record_start)
                flash.setImageResource(R.drawable.flash_on)

                recordVideo.setOnClickListener {
                    if (!cameraPrev.isTakingVideo) {
                        recordTimer.start()
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
                        cameraFacing.visibility = GONE
                        cameraPrev.takeVideo(File(filesDir,"Video.mp4"))
                        recordVideo.setImageResource(R.drawable.record)
                    } else {
                        cameraPrev.stopVideo()
                        recordVideo.setImageResource(R.drawable.record_start)

                    }

                }

                /**
                 *set camera facing onclick of camera face icon
                 */
                cameraFacing.setOnClickListener {

                  isBackFacing = !isBackFacing
                    if(isBackFacing) cameraPrev.facing = Facing.BACK
                    else cameraPrev.facing = Facing.FRONT
                }
                flash.setOnClickListener {
                    /**
                     * Turn on/off flash based onclick of flash icon
                     */
                    isFlashOn = !isFlashOn
                    if(isFlashOn) {
                        flash.setImageResource(R.drawable.flash_off)
                        cameraPrev.flash = Flash.TORCH
                    } else {
                        flash.setImageResource(R.drawable.flash_on)
                        cameraPrev.flash = Flash.OFF
                    }
                }

                cameraPrev.addCameraListener(object : CameraListener() {
                    override fun onVideoTaken(result: VideoResult) {
                        super.onVideoTaken(result)
                        /**
                         * Stop timer and proceed recorded video
                         */
                        recordTimer.cancel()
                        VideoPreviewActivity.videoResult = result
                        val intent = Intent(this@MainActivity, VideoPreviewActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    override fun onCameraError(exception: CameraException) {
                        super.onCameraError(exception)
                        recordTimer.cancel()
                    }
                })
            }

            recordTimer = object : CountDownTimer(86400000,1000) {
                override fun onTick(p0: Long) {
                    /**
                     * Update timer for each second
                     */
                    val hours = sec/3600
                    val minutes = (sec%3600)/60
                    val second = sec%60
                    val time = String.format(Locale.getDefault(),"%d:%02d:%02d",hours,minutes,second)
                    sec+=1
                    binding.timer.text = time
                }

                override fun onFinish() {
                    Log.e("timer","Finished")
                }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(::recordTimer.isInitialized) {
            recordTimer.cancel()
        }
    }

    override fun onResume() {
        super.onResume()
        if(!isPermissionGranted(Manifest.permission.CAMERA)) requestPermission(Manifest.permission.CAMERA, 999)
    }
}