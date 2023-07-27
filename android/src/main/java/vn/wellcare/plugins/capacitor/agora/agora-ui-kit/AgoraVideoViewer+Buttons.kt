package vn.wellcare.plugins.capacitor.agora.`agora-ui-kit`

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.LinearLayout.LayoutParams
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.getcapacitor.JSObject
import com.google.android.material.bottomsheet.BottomSheetBehavior
import vn.wellcare.plugins.capacitor.agora.CapacitorPluginAgoraPlugin
import vn.wellcare.plugins.capacitor.agora.R
import vn.wellcare.plugins.capacitor.agora.util.IParticipant
import kotlin.math.roundToInt

internal class BottomSheetContainer(context: Context) : LinearLayout(context)

internal class ButtonContainer(context: Context) : LinearLayout(context)
internal class TopLayoutView(context: Context) : LinearLayout(context)
internal class AlertLayoutView(context: Context) : RelativeLayout(context)


//@ExperimentalUnsignedTypes
//internal fun AgoraVideoViewer.getControlContainer(): ButtonContainer {
//    this.controlContainer?.let {
//        return it
//    }
//    val container = ButtonContainer(context)
//    container.visibility = View.VISIBLE
//    container.gravity = Gravity.CENTER
//    val containerLayout = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 200, Gravity.BOTTOM)
//
//    this.addView(container, containerLayout)
//
//    this.controlContainer = container
//    return container
//}

fun Int.dpToPx(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}

@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.getAlertLayout(): AlertLayoutView {
    this.alertLayoutContainer?.let {
        return it
    }

    val relativeLayout = AlertLayoutView(context)
    relativeLayout.layoutParams = CoordinatorLayout.LayoutParams(
            CoordinatorLayout.LayoutParams.MATCH_PARENT,
            CoordinatorLayout.LayoutParams.WRAP_CONTENT
    ).apply {
        gravity = Gravity.CENTER
//        setMargins(0,40,0,0)
    }

    val alertContainer = LinearLayout(context)
    alertContainer.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
    ).apply {
        gravity = Gravity.CENTER
        marginStart = 25.dpToPx()
        marginEnd = 25.dpToPx()
    }
    alertContainer.setPadding(0, 7.dpToPx(), 0, 7.dpToPx())
    alertContainer.weightSum = 5f
    alertContainer.setBackgroundResource(R.drawable.alram_layout_bg)


    val subContainer = LinearLayout(context)
    subContainer.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
    ).apply {
        gravity = Gravity.CENTER
        setMargins(0, 10.dpToPx(), 0, 10.dpToPx())
    }
    subContainer.orientation = LinearLayout.HORIZONTAL


    val iconView = ImageView(context)
    val imageViewLayoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            0.5f
    )
    imageViewLayoutParams.gravity = Gravity.CENTER
    iconView.layoutParams = imageViewLayoutParams
    iconView.setImageResource(R.drawable.alarm)

    val textView = TextView(context)
    textView.id = R.id.text
    val textViewLayoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            4f
    )
    textView.layoutParams = textViewLayoutParams
    textView.maxLines = 5
    textView.text = "alert"
    textView.setTextColor(Color.WHITE)
//    textView.setPadding(0,0,10,0)

    val actionView = TextView(context)
    val okTextViewLayoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            0.5f,
    )
    okTextViewLayoutParams.gravity = Gravity.CENTER or Gravity.BOTTOM
    okTextViewLayoutParams.setMargins(0, 5.dpToPx(), 10.dpToPx(), 0)
    actionView.layoutParams = okTextViewLayoutParams
    actionView.text = "OK"
    actionView.textSize = 15f
    actionView.setTextColor(Color.WHITE)
    actionView.setPadding(20, 0, 0, 0)
    actionView.setBackgroundResource(R.drawable.text_ok_bg)
    actionView.setOnClickListener{
        relativeLayout.removeView(alertContainer)
    }

    subContainer.addView(iconView)
    subContainer.addView(textView)
    subContainer.addView(actionView)

    alertContainer.addView(subContainer)

    relativeLayout.addView(alertContainer)

    // this.addView(relativeLayout)
    this.alertLayoutContainer = relativeLayout

    return relativeLayout
}


@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.getTopLayoutView(): TopLayoutView {
    this.topViewContainer?.let {
        return it
    }
    val linearLayout = TopLayoutView(context)
    linearLayout.id = View.generateViewId()
    val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
    )
    layoutParams.setMargins(60, 25, 60, 0)
    linearLayout.layoutParams = layoutParams
    linearLayout.orientation = LinearLayout.HORIZONTAL

    val leftImage = ImageView(context)
    leftImage.id = R.id.icon_recording
    val layoutParamsLeftImage = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
    ).apply {
        gravity = Gravity.CENTER or Gravity.START
        setMargins(0, 0, 10, 0)
    }
    leftImage.layoutParams = layoutParamsLeftImage
    leftImage.setImageResource(R.drawable.ic_ellipse)
    leftImage.visibility = View.INVISIBLE
    linearLayout.addView(leftImage)

    val textView = TextView(context)
    textView.id = R.id.text_countdown
    textView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
    )
    textView.text = " --:-- "
    textView.textSize = 15f
    textView.setShadowLayer(10f, 0f, 0f, Color.BLACK)
    textView.setBackgroundResource(R.drawable.timer_text_background)
    textView.setPadding(5, 5, 5, 5)
    textView.setTextColor(Color.WHITE)
    val drawableLeft = context.resources.getDrawable(R.drawable.timer)
    textView.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null)

    linearLayout.addView(textView)
//    linearLayout.addView(signalImageView)

    /*val innerLinearLayout = LinearLayout(context)
    innerLinearLayout.layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
    innerLinearLayout.gravity = Gravity.END


    val rightImage = ImageView(context)
    val layoutParamsRightImage = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    ).apply {
        gravity = Gravity.CENTER or Gravity.END
        setMargins(0,0,0,0)
    }
    rightImage.layoutParams = layoutParamsRightImage
    rightImage.setImageResource(R.drawable.signal)
    innerLinearLayout.addView(rightImage)
    linearLayout.addView(innerLinearLayout)
*/
    this.addView(linearLayout)
    this.topViewContainer = linearLayout

    return linearLayout

}


@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.getControlContainer(): BottomSheetContainer {
    this.controlContainer?.let {
        return it
    }
    val bottomSheet = BottomSheetContainer(context)
    bottomSheet.id = View.generateViewId()
    bottomSheet.layoutParams = ViewGroup.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
    )
    bottomSheet.orientation = LinearLayout.VERTICAL


    this.addView(bottomSheet)

    // todo: Add Constraint Layout


    // Set the bottom sheet behavior3
    val bottomSheetBehavior = BottomSheetBehavior<LinearLayout>()
    bottomSheetBehavior.isFitToContents = true
    bottomSheetBehavior.halfExpandedRatio = 0.5f
    bottomSheetBehavior.isHideable = false
    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    bottomSheetBehavior.peekHeight = resources.getDimensionPixelSize(R.dimen.bs_peek_height)
    // bottomSheetBehavior.
    bottomSheetBehavior.isDraggable = true
    bottomSheetBehavior.expandedOffset = 0
    val reCreate = this
    Log.i("MY_TAG", "bottomSheet.id : ${bottomSheet.id}")
    bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    if (bottomSheetBehavior.peekHeight === resources.getDimensionPixelSize(R.dimen.bs_peek_height)) {
                        Log.i("MY_TAG", "COLLAPSED 3 : Called")
//                        reCreate.reorganiseVideos()
//                        if (reCreate.recyclerView.visibility == View.VISIBLE){
//                            reCreate.recyclerView.visibility = View.GONE
//                            Log.i("MY_TAG","visibility: GONE")
//                        }
//                        bottomSheetHandler.postDelayed({
//                            bottomSheetBehavior.peekHeight =
//                                resources.getDimensionPixelSize(R.dimen.bs_min_height)
//                        }, 5000)
                    }
                }
                BottomSheetBehavior.STATE_EXPANDED -> {
                    Log.i("MY_TAG", "EXPANDED 1 : Called")
//                    reCreate.reorganiseVideos()
                    bottomSheetHandler.removeCallbacksAndMessages(null)
                }
                BottomSheetBehavior.STATE_DRAGGING -> {
                    Log.i("MY_TAG", "DRAGGING 2 : Called")
//                    reCreate.reorganiseVideos()
//                    if (reCreate.recyclerView.visibility == View.GONE){
//                        reCreate.recyclerView.visibility = View.VISIBLE
//                        Log.i("MY_TAG","visibility: VISIBLE")
//                    }else if (reCreate.recyclerView.visibility == View.VISIBLE){
//                        reCreate.recyclerView.visibility = View.GONE
//                        Log.i("MY_TAG","visibility: GONE")
//                    }
                    bottomSheetBehavior.peekHeight =
                            resources.getDimensionPixelSize(R.dimen.bs_peek_height)
                }
                else -> {}
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {

        }
    })
    bottomSheet.background = ContextCompat.getDrawable(context, R.drawable.bottom_sheet_background)
    val layoutParams = bottomSheet.layoutParams as CoordinatorLayout.LayoutParams
    layoutParams.behavior = bottomSheetBehavior
    layoutParams.marginStart = 10
    layoutParams.marginEnd = 10
    bottomSheet.layoutParams = layoutParams
//    bottomSheetHandler.postDelayed({
//        Log.i("MY_TAG","postDelayed : Called")
//        bottomSheetBehavior.peekHeight = resources.getDimensionPixelSize(R.dimen.bs_min_height)
//        reCreate.reorganiseVideos()
//    }, 5000)
    this.controlContainer = bottomSheet
    return bottomSheet
}

internal fun AgoraVideoViewer.getParticipantLayout(): LinearLayout {
    this.participantsLayout?.let {
        return it
    }
    val participants = LinearLayout(context)
    val layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.MATCH_PARENT
    )
    participants.orientation = LinearLayout.VERTICAL
    participants.gravity = Gravity.TOP
    participants.setBackgroundResource(R.drawable.bottom_sheet_background)

    val participantHeader = LinearLayout(context)
    participantHeader.layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        200
    )
    (participantHeader.layoutParams as ViewGroup.MarginLayoutParams).apply {
        setMargins(0, 32, 0, 0)
    }
    participantHeader.setPadding(48)
    participantHeader.gravity = LinearLayout.HORIZONTAL
//    participantHeader.marginTop = 20

    val headerText = TextView(context).apply {
        textSize = 26.0F
    }
    val headerTextLayoutParams = LayoutParams(
        LayoutParams.WRAP_CONTENT,
        96
    ).apply {
        weight = 1.0F
    }
    headerText.text = "Participants"
    headerText.setTextColor(Color.WHITE)

    val closeIcon = View(context)
    closeIcon.setBackgroundResource(R.drawable.close_icon)
    val closeIconLayoutParams = LinearLayout.LayoutParams(
        96,
        96
    )
    closeIcon.setOnClickListener {
        this.participantsLayout?.visibility = View.GONE
    }
//    closeIcon.setPadding(96)
//    closeIcon.apply {
//        setPadding(16, 48, 16, 16)
//    }

//    participantHeader.setBackgroundColor( Color.YELLOW)
    participantHeader.addView(headerText, headerTextLayoutParams)
    participantHeader.addView(closeIcon, closeIconLayoutParams)
    participants.addView(participantHeader)

    this.addView(participants, layoutParams)
    this.participantsLayout = participants
    this.participantsLayout?.visibility = View.GONE
    return participants
}


internal fun AgoraVideoViewer.getRecyclerView(): RecyclerView {
    val recyclerView = RecyclerView(context)
    recyclerView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            500
    )
    recyclerView.layoutManager = LinearLayoutManager(context)
    return recyclerView
}

internal fun AgoraVideoViewer.getControlContainerHandle(): LinearLayout {
    // Create the handle
    val handle = LinearLayout(context)
    val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            10
    )
    handle.orientation = LinearLayout.HORIZONTAL
    handle.gravity = Gravity.CENTER
    val view = View(context)
    view.setBackgroundResource(R.drawable.handle)
    val viewLayoutParams = LinearLayout.LayoutParams(
            80,
            10
    )
    handle.addView(view, viewLayoutParams)

    this.controlContainer?.addView(handle)
    return handle
}

internal fun AgoraVideoViewer.getHorizontalButtonContainer(): ButtonContainer {
    this.buttonContainer?.let {
        return it
    }
    // Create the horizontal buttons LinearLayout
    val horizontalButtons = ButtonContainer(context)
    horizontalButtons.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
    )
    horizontalButtons.orientation = LinearLayout.HORIZONTAL
    horizontalButtons.gravity = Gravity.CENTER
    horizontalButtons.setBackgroundColor(Color.TRANSPARENT)
//    horizontalButtons.setPadding(16, 16, 16, 16)
    this.controlContainer?.addView(horizontalButtons)

    this.buttonContainer = horizontalButtons
    return horizontalButtons
}

@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.getTopButtonContainer(): ButtonContainer {
    this.topButtonContainer?.let {
        return it
    }
    // Create the horizontal buttons LinearLayout
    val topButtons = ButtonContainer(context)
    topButtons.layoutParams = CoordinatorLayout.LayoutParams(
            CoordinatorLayout.LayoutParams.WRAP_CONTENT,
            CoordinatorLayout.LayoutParams.WRAP_CONTENT
    ).apply {
        gravity = Gravity.END or Gravity.TOP
        setMargins(70, 70, 0, 0)
    }
    topButtons.orientation = LinearLayout.VERTICAL
    topButtons.setBackgroundColor(Color.TRANSPARENT)
    topButtons.visibility = View.VISIBLE
//    topButtons.setPadding(16, 16, 16, 16)
    this.addView(topButtons)
    this.topButtonContainer = topButtons
    return topButtons
}

@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.getBackButtonContainer(): ButtonContainer {
    this.backButtonContainer?.let {
        return it
    }

    val backButtonContainer = ButtonContainer(context)
    backButtonContainer.layoutParams = CoordinatorLayout.LayoutParams(
            CoordinatorLayout.LayoutParams.WRAP_CONTENT,
            CoordinatorLayout.LayoutParams.WRAP_CONTENT
    ).apply {
        gravity = Gravity.START or Gravity.TOP
        setMargins(0, 70, 0, 0)
    }
    backButtonContainer.setBackgroundColor(Color.TRANSPARENT)
    backButtonContainer.visibility = View.VISIBLE
//    topButtons.setPadding(16, 16, 16, 16)
    this.addView(backButtonContainer)

    this.backButtonContainer = backButtonContainer
    return backButtonContainer
}

@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.getCameraButton(): AgoraButtonBottom {
    this.camButton?.let {
        return it
    }
    val agCamButton = AgoraButtonBottom(context = this.context)
    agCamButton.clickAction = {
        (this.context as Activity).runOnUiThread {
            it.isSelected = !it.isSelected
//            it.background.setTint(if (it.isSelected) Color.RED else Color.GRAY)
            it.setImageResource(if (it.isSelected) R.drawable.ic_videocam_off else R.drawable.ic_videocam)
            this.agkit.enableLocalVideo(!it.isSelected)
        }
    }
    this.camButton = agCamButton
    agCamButton.setImageResource(R.drawable.ic_videocam)
    return agCamButton
}

@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.getMicButton(): AgoraButtonBottom {
    this.micButton?.let {
        return it
    }
    val agMicButton = AgoraButtonBottom(context = this.context)
    agMicButton.clickAction = {
        it.isSelected = !it.isSelected
//        it.background.setTint(if (it.isSelected) Color.RED else Color.GRAY)
        Log.d("android agora", "microphone selected: ${it.isSelected}")
        it.setImageResource(if (it.isSelected) R.drawable.ic_speaker_mute else R.drawable.ic_speaker)
        this.userVideoLookup[this.userID]?.audioMuted = it.isSelected
        this.agkit.muteLocalAudioStream(it.isSelected)
    }
    this.micButton = agMicButton
    agMicButton.setImageResource(R.drawable.ic_speaker)
    return agMicButton
}

@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.getChatButton(): AgoraButtonBottom {
    this.chatButton?.let {
        return it
    }
    val agChatButton = AgoraButtonBottom(context = this.context)
    agChatButton.clickAction = {
        CapacitorPluginAgoraPlugin.sendEvent("onSelfAction", JSObject().put("event", "chat"))
    }
    this.chatButton = agChatButton
    agChatButton.setImageResource(R.drawable.chat)
    return agChatButton
}


@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.getParticipantsButton(): AgoraButtonBottom {
    this.participantsButton?.let {
        return it
    }
    val agParticipantsButton = AgoraButtonBottom(context = this.context)
    agParticipantsButton.clickAction = {
        this.getParticipantLayout().visibility = View.VISIBLE
        Log.d("Infor", "clicked")
    }
    this.participantsButton = agParticipantsButton
    agParticipantsButton.setImageResource(R.drawable.ic_people_outline)
    return agParticipantsButton
}

@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.getEndCallButton(): AgoraButtonBottom {
    this.endCallButton?.let {
        return it
    }
    val hangupButton = AgoraButtonBottom(this.context)
    hangupButton.clickAction = {
        // this.agkit.stopPreview()
        this.leaveChannel()
        (this.context as Activity).runOnUiThread{
          (this.context as Activity).finish()
        }
    }
    hangupButton.setImageResource(R.drawable.ic_baseline_call_end_24)
    hangupButton.background.setTint(ContextCompat.getColor(this.context, R.color.colorHungup))
    this.endCallButton = hangupButton
    return hangupButton
}

@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.getFlipButton(): AgoraButton {
    this.flipButton?.let {
        return it
    }
    val agFlipButton = AgoraButton(context = this.context)
    agFlipButton.clickAction = {
        this.agkit.switchCamera()
    }
    this.flipButton = agFlipButton
    agFlipButton.setImageResource(R.drawable.rotate_camera)
    agFlipButton.setBackgroundResource(R.drawable.top_button_background)
    return agFlipButton
}

@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.getLayoutButton(): AgoraButton {
    this.layoutButton?.let {
        return it
    }
    val agLayoutButton = AgoraButton(context = this.context)
    agLayoutButton.clickAction = {
        this.setStyleOfVideo(if (style == AgoraVideoViewer.Style.FLOATING) AgoraVideoViewer.Style.GRID else AgoraVideoViewer.Style.FLOATING)
    }
    this.layoutButton = agLayoutButton
    agLayoutButton.setImageResource(R.drawable.layout_btn)
    agLayoutButton.setBackgroundResource(R.drawable.top_button_background)
    return agLayoutButton
}

@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.getBluetoothButton(): AgoraButton {
    this.bluetoothButton?.let {
        return it
    }
    val agBluetoothButton = AgoraButton(context = this.context)
    agBluetoothButton.clickAction = {
//        it.isSelected = !it.isSelected
//        it.background.setTint(if (it.isSelected) Color.RED else Color.GRAY)
//        it.setImageResource(if (it.isSelected) R.drawable.ic_mic_off else R.drawable.ic_mic)
//        this.userVideoLookup[this.userID]?.audioMuted = it.isSelected
//        this.agkit.muteLocalAudioStream(it.isSelected)
        (this.context as Activity).runOnUiThread{
            it.isSelected = !it.isSelected
            val audioManager = this.context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if (audioManager.isSpeakerphoneOn && it.isSelected) {
                // Switch to earpiece
                audioManager.isSpeakerphoneOn = false
                audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
            } else {
                // Switch to speaker
                audioManager.isSpeakerphoneOn = true
                audioManager.mode = AudioManager.MODE_NORMAL
            }
        }
    }
    this.bluetoothButton = agBluetoothButton
    agBluetoothButton.setImageResource(R.drawable.bluetooth_btn)
    agBluetoothButton.setBackgroundResource(R.drawable.top_button_background)
    return agBluetoothButton
}

@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.getFlashButton(): AgoraButton {
    this.flashButton?.let {
        return it
    }
    val agFlashButton = AgoraButton(context = this.context)
    agFlashButton.clickAction = {
        (this.context as Activity).runOnUiThread {
            it.isSelected = !it.isSelected
            val cameraManager = this.context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            try {
                val cameraId = cameraManager.cameraIdList[0]
                cameraManager.setTorchMode(cameraId, it.isSelected)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
//        it.isSelected = !it.isSelected
//        it.background.setTint(if (it.isSelected) Color.RED else Color.GRAY)
//        it.setImageResource(if (it.isSelected) R.drawable.ic_mic_off else R.drawable.ic_mic)
//        this.userVideoLookup[this.userID]?.audioMuted = it.isSelected
//        this.agkit.muteLocalAudioStream(it.isSelected)
    }
    this.flashButton = agFlashButton
    agFlashButton.setImageResource(R.drawable.flash_btn)
    agFlashButton.setBackgroundResource(R.drawable.top_button_background)
    return agFlashButton
}

@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.getBackButton(): AgoraButton {
    this.backButton?.let {
        return it
    }
    val agBackButton = AgoraButton(context = this.context)
    agBackButton.clickAction = {
//        it.isSelected = !it.isSelected
//        it.background.setTint(if (it.isSelected) Color.RED else Color.GRAY)
//        it.setImageResource(if (it.isSelected) R.drawable.ic_mic_off else R.drawable.ic_mic)
//        this.userVideoLookup[this.userID]?.audioMuted = it.isSelected
//        this.agkit.muteLocalAudioStream(it.isSelected)
        val data = JSObject()
        data.put("event", "back")
        CapacitorPluginAgoraPlugin.sendEvent("onSelfAction", data)
    }
    this.backButton = agBackButton
    agBackButton.setImageResource(R.drawable.ic_arrow_back_ios_new)
    agBackButton.setBackgroundResource(R.drawable.top_button_background)
    return agBackButton
}

@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.getScreenShareButton(): AgoraButtonBottom? {
    return null
}

internal fun AgoraVideoViewer.builtinButtons(): MutableList<AgoraButtonBottom> {
    val rtnButtons = mutableListOf<AgoraButtonBottom>()
    for (button in this.agoraSettings.enabledButtons) {
        rtnButtons += when (button) {
            AgoraSettings.BuiltinButton.MIC -> this.getMicButton()
            AgoraSettings.BuiltinButton.CAMERA -> this.getCameraButton()
            // AgoraSettings.BuiltinButton.FLIP -> this.getFlipButton()
            AgoraSettings.BuiltinButton.PARTICIPANTS -> this.getParticipantsButton()
            AgoraSettings.BuiltinButton.END -> this.getEndCallButton()
            AgoraSettings.BuiltinButton.CHAT -> this.getChatButton()
            AgoraSettings.BuiltinButton.PARTICIPANTS -> this.getChatButton()
        }
    }
    return rtnButtons
}

internal fun AgoraVideoViewer.topButtons(): MutableList<AgoraButton> {
    val rtnButtons = mutableListOf<AgoraButton>()
    for (button in this.agoraSettings.topButtons) {
        rtnButtons += when (button) {
            AgoraSettings.TopButton.FLASH -> this.getFlashButton()
            AgoraSettings.TopButton.BLUETOOTH -> this.getBluetoothButton()
             AgoraSettings.TopButton.LAYOUT -> this.getLayoutButton()
            AgoraSettings.TopButton.FLIP -> this.getFlipButton()
        }
    }
    return rtnButtons
}

@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.addVideoButtons() {
    val container = this.getControlContainer()
    val topButtonContainer = this.getTopButtonContainer()
    val backButtonContainer = this.getBackButtonContainer()
    val participantContainer = this.getParticipantLayout()
    this.getControlContainerHandle()
    val buttonContainer = this.getHorizontalButtonContainer()
    val bottomButtons = this.builtinButtons() + this.agoraSettings.extraButtons
    val topButtons = this.topButtons()
    this.getTopLayoutView()
    this.getAlertLayout()
    container.visibility = if (bottomButtons.isEmpty()) View.INVISIBLE else View.VISIBLE
    var screenWidth = Resources.getSystem().displayMetrics.widthPixels
    Log.d("INFO", "screen width: " + screenWidth)
    val buttonSize = (screenWidth * 0.2 * 0.6).roundToInt()
    val buttonMargin = 0f

    val llayout = LinearLayout.LayoutParams(buttonSize, buttonSize)
    llayout.gravity = Gravity.CENTER
    backButtonContainer.addView(this.getBackButton(), llayout)

    bottomButtons.forEach { button ->
        val llayout = LinearLayout.LayoutParams(buttonSize, buttonSize)
        llayout.gravity = Gravity.CENTER
        buttonContainer.addView(button, llayout)
    }

    topButtons.forEach { topBtn ->
        val llayout = LinearLayout.LayoutParams(buttonSize, buttonSize)
        llayout.gravity = Gravity.CENTER
        topButtonContainer.addView(topBtn, llayout)
    }

//    val itemList = mutableListOf<IParticipant>()
//    itemList.add(IParticipant("test", "test", IAvatar("test"), ClientRole.HOST, "", true, ""))
//    itemList.add(IParticipant("test", "test", IAvatar("test"), ClientRole.HOST, "", true, ""))
//    itemList.add(IParticipant("test", "test", IAvatar("test"), ClientRole.HOST, "", true, ""))
//    itemList.add(IParticipant("test", "test", IAvatar("test"), ClientRole.HOST, "", true, ""))
//    itemList.add(IParticipant("test", "test", IAvatar("test"), ClientRole.HOST, "", true, ""))
//    itemList.add(IParticipant("test", "test", IAvatar("test"), ClientRole.HOST, "", true, ""))
//    itemList.add(IParticipant("test", "test", IAvatar("test"), ClientRole.HOST, "", true, ""))
//    itemList.add(IParticipant("test", "test", IAvatar("test"), ClientRole.HOST, "", true, ""))
//    itemList.add(IParticipant("test", "test", IAvatar("test"), ClientRole.HOST, "", true, ""))
    participantsAdapter = ItemAdapter(allParticipants)

    this.recyclerView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            500
    )
    (this.recyclerView.layoutParams as ViewGroup.MarginLayoutParams).apply {
        setMargins(48)
    }
    this.recyclerView.layoutManager = LinearLayoutManager(context)

    this.recyclerView.adapter = participantsAdapter

/*    // Create the ListView
    val listView = ListView(context)
    listView.layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
    listView.divider = ColorDrawable(ContextCompat.getColor(context, R.color.cardview_light_background))
    listView.dividerHeight = 1

// Set up the list view
    val items = listOf("Item 1", "Item 2", "Item 3")
    val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, items)
    listView.adapter = adapter*/
    participantContainer.addView(recyclerView)
    val contWidth = (bottomButtons.size.toFloat() + buttonMargin) * bottomButtons.count()
    container.setPadding(0, 20, 0, 20)
    this.positionButtonContainer(container, contWidth, buttonMargin)
}

@ExperimentalUnsignedTypes
private fun AgoraVideoViewer.positionButtonContainer(
        container: BottomSheetContainer,
        contWidth: Float,
        buttonMargin: Float
) {
    // TODO: Set container position and size
//    container.setBackgroundColor(this.agoraSettings.colors.buttonBackgroundColor)
//    container.background.alpha = this.agoraSettings.colors.buttonBackgroundAlpha
//    (container.subBtnContainer.layoutParams as? FrameLayout.LayoutParams)!!.width = contWidth.toInt()
//    (this.backgroundVideoHolder.layoutParams as? ViewGroup.MarginLayoutParams)
//            ?.bottomMargin = if (container.visibility == View.VISIBLE) container.measuredHeight else 0
//    this.addView(container)
}

class ItemAdapter(var itemList: List<IParticipant>) :
        RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.title_text_view)
        val subtitleTextView: TextView = itemView.findViewById(R.id.subtitle_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.participant_item, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.titleTextView.text = item.name
        holder.subtitleTextView.text = item.subtitle
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
