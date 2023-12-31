package vn.wellcare.plugins.capacitor.agora.`agora-ui-kit`

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.view.Gravity
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginTop
import androidx.core.view.setPadding
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.video.VideoCanvas
import vn.wellcare.plugins.capacitor.agora.R

/**
 * View for the individual Agora Camera Feed.
 */
@ExperimentalUnsignedTypes
class AgoraSingleVideoView(context: Context, uid: Int, micColor: Int) : FrameLayout(context) {

    /**
     * Canvas used to render the Agora RTC Video.
     */
    var canvas: VideoCanvas
        internal set
    internal var uid: Int = uid
//    internal var textureView: AgoraTextureView = AgoraTextureView(context)

    /**
     * Is the microphone muted for this user.
     */
    var audioMuted: Boolean = true
        set(value: Boolean) {
            field = value
            (context as Activity).runOnUiThread {
                this.mutedFlag.visibility = if (value) VISIBLE else INVISIBLE
            }
        }

    /**
     * Signal status for this user
     */
    var signalStrength: Int = 3

    /**
     * Is the video turned off for this user.
     */
    var videoMuted: Boolean = true
        set(value: Boolean) {
            if (this.videoMuted != value) {
                this.backgroundView.visibility = if (!value) INVISIBLE else VISIBLE
//                this.textureView.visibility = if (value) INVISIBLE else VISIBLE
            }
            field = value
        }

    internal val hostingView: View
        get() {
            return this.canvas.view
        }

    /**
     * Icon to show if this user is muting their microphone
     */
    var mutedFlag: ImageView
    var signalStatus: ImageView
    var backgroundView: FrameLayout
    var micFlagColor: Int = micColor

    /**
     * Create a new AgoraSingleVideoView to be displayed in your app
     * @param uid: User ID of the `AgoraRtcVideoCanvas` inside this view
     * @param micColor: Color to be applied when the local or remote user mutes their microphone
     */
    init {

        val surfaceView = SurfaceView(getContext())
        this.canvas = VideoCanvas(surfaceView)
        this.canvas.uid = uid
        addView(surfaceView, ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT))
        this.backgroundView = FrameLayout(context)
        this.setBackground()
        this.mutedFlag = ImageView(context)
        this.signalStatus = ImageView(context)
        this.setupMutedFlag()
        this.setupSignalStatus()

        this.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
    }

    private fun setupMutedFlag() {

        val mutedLayout = FrameLayout.LayoutParams(DPToPx(context, 24), DPToPx(context, 24))
//        mutedLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
//        mutedLayout.gravity = Gravity.RIGHT
        mutedLayout.gravity = Gravity.TOP or Gravity.RIGHT
//        mutedLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        mutedLayout.topMargin = DPToPx(context, 5)
        mutedLayout.rightMargin = DPToPx(context, 5)

        mutedFlag.setImageResource(android.R.drawable.stat_notify_call_mute)
        mutedFlag.setBackgroundResource(R.drawable.rounded_end_call)

        mutedFlag.setColorFilter(this.micFlagColor)
        mutedFlag.setPadding(DPToPx(context, 5))
        addView(mutedFlag, mutedLayout)
        this.audioMuted = true
    }

    private fun setupSignalStatus() {
//        signalImageView.id = R.id.icon_signal
        val signalLayout = FrameLayout.LayoutParams(DPToPx(context, 24), DPToPx(context, 24))
//        mutedLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
//        mutedLayout.gravity = Gravity.RIGHT
        signalLayout.gravity = Gravity.TOP or Gravity.RIGHT
//        mutedLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        signalLayout.topMargin = DPToPx(context, 5)
        signalLayout.rightMargin = DPToPx(context, 40)

        this.signalStatus.setImageResource(R.drawable.no_signal)
//        this.signalStatus.setBackgroundResource(R.drawable.rounded_end_call)

        this.signalStatus.setColorFilter(this.micFlagColor)
        this.signalStatus.setPadding(DPToPx(context, 5))
        addView(this.signalStatus, signalLayout)
    }

    fun setBackground() {
        backgroundView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        )
        backgroundView.setBackgroundColor(Color.LTGRAY)
        addView(backgroundView)
        val personIcon = ImageView(context)
        personIcon.setImageResource(R.drawable.ic_person)
        val buttonLayout = FrameLayout.LayoutParams(100, 100)
        buttonLayout.gravity = Gravity.CENTER
        backgroundView.addView(personIcon, buttonLayout)
    }

//    fun signalAndMicHandler(){
//        val signalImageView = ImageView(context)
//        signalImageView.id = R.id.icon_signal
//        signalImageView.layoutParams = FrameLayout.LayoutParams(
//            FrameLayout.LayoutParams.WRAP_CONTENT,
//            FrameLayout.LayoutParams.WRAP_CONTENT
//        ).apply {
//
//            gravity = Gravity.TOP or Gravity.RIGHT
//            setMargins(0,30,50,0)
//        }
//        signalImageView.setImageResource(R.drawable.signal)
//        signalImageView.visibility = View.INVISIBLE
//        val micImageView = ImageView(context)
//        micImageView.id = View.generateViewId()
//        micImageView.layoutParams = FrameLayout.LayoutParams(
//            DPToPx(context, 20), DPToPx(context, 20)
//        ).apply {
//            gravity = Gravity.TOP or Gravity.RIGHT
//            setMargins(0,30,100,0)
//        }
//        micImageView.setImageResource(R.drawable.ic_mic_off)
//
//        addView(signalImageView)
//        addView(micImageView)
//    }
}
