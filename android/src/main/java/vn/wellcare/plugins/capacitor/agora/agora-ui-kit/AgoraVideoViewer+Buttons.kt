package vn.wellcare.plugins.capacitor.agora.`agora-ui-kit`

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import vn.wellcare.plugins.capacitor.agora.R
import vn.wellcare.plugins.capacitor.agora.util.ClientRole
import vn.wellcare.plugins.capacitor.agora.util.IAvatar
import vn.wellcare.plugins.capacitor.agora.util.IParticipant

internal class BottomSheetContainer(context: Context) : LinearLayout(context)

internal class ButtonContainer(context: Context) : LinearLayout(context)

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

@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.getControlContainer(): BottomSheetContainer {
    this.controlContainer?.let {
        return it
    }
    val bottomSheet = BottomSheetContainer(context)
    bottomSheet.layoutParams = ViewGroup.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT,
    )
    bottomSheet.orientation = LinearLayout.VERTICAL
    this.addView(bottomSheet)
// Set the bottom sheet behavior3
    val bottomSheetBehavior = BottomSheetBehavior<LinearLayout>()
    bottomSheetBehavior.isFitToContents = true
    bottomSheetBehavior.halfExpandedRatio = 0.5f
    bottomSheetBehavior.isHideable = false
    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    bottomSheetBehavior.peekHeight = resources.getDimensionPixelSize(R.dimen.bs_peek_height)
//    bottomSheetBehavior.
    bottomSheetBehavior.isDraggable = true
    bottomSheetBehavior.expandedOffset = 0
    bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    if (bottomSheetBehavior.peekHeight === resources.getDimensionPixelSize(R.dimen.bs_peek_height)) {
                        bottomSheetHandler.postDelayed({
                            bottomSheetBehavior.peekHeight =
                                resources.getDimensionPixelSize(R.dimen.bs_min_height)
                        }, 5000)
                    }
                }
                BottomSheetBehavior.STATE_EXPANDED -> {
                    bottomSheetHandler.removeCallbacksAndMessages(null)
                }
                BottomSheetBehavior.STATE_DRAGGING -> {
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
    bottomSheetHandler.postDelayed({
        bottomSheetBehavior.peekHeight = resources.getDimensionPixelSize(R.dimen.bs_min_height)
    }, 5000)
    this.controlContainer = bottomSheet
    return bottomSheet
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
    )
    backButtonContainer.setBackgroundColor(Color.TRANSPARENT)
    backButtonContainer.visibility = View.VISIBLE
//    topButtons.setPadding(16, 16, 16, 16)
    this.addView(backButtonContainer)

    this.backButtonContainer = backButtonContainer
    return backButtonContainer
}

@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.getCameraButton(): AgoraButton {
    this.camButton?.let {
        return it
    }
    val agCamButton = AgoraButton(context = this.context)
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
internal fun AgoraVideoViewer.getMicButton(): AgoraButton {
    this.micButton?.let {
        return it
    }
    val agMicButton = AgoraButton(context = this.context)
    agMicButton.clickAction = {
        it.isSelected = !it.isSelected
//        it.background.setTint(if (it.isSelected) Color.RED else Color.GRAY)
        it.setImageResource(if (it.isSelected) R.drawable.ic_mic_off else R.drawable.ic_mic)
        this.userVideoLookup[this.userID]?.audioMuted = it.isSelected
        this.agkit.muteLocalAudioStream(it.isSelected)
    }
    this.micButton = agMicButton
    agMicButton.setImageResource(R.drawable.ic_mic)
    return agMicButton
}

@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.getChatButton(): AgoraButton {
    this.chatButton?.let {
        return it
    }
    val agChatButton = AgoraButton(context = this.context)
    agChatButton.clickAction = {

    }
    this.chatButton = agChatButton
    agChatButton.setImageResource(R.drawable.chat)
    return agChatButton
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
    return agFlipButton
}

@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.getEndCallButton(): AgoraButton {
    this.endCallButton?.let {
        return it
    }
    val hangupButton = AgoraButton(this.context)
    hangupButton.clickAction = {
        this.agkit.stopPreview()
        this.leaveChannel()
    }
    hangupButton.setImageResource(R.drawable.ic_baseline_call_end_24)
    hangupButton.background.setTint(ContextCompat.getColor(this.context, R.color.colorHungup))
    this.endCallButton = hangupButton
    return hangupButton
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
    }
    this.backButton = agBackButton
    agBackButton.setImageResource(R.drawable.ic_arrow_back_ios_new)
    agBackButton.setBackgroundResource(R.drawable.top_button_background)
    return agBackButton
}

@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.getScreenShareButton(): AgoraButton? {
    return null
}

internal fun AgoraVideoViewer.builtinButtons(): MutableList<AgoraButton> {
    val rtnButtons = mutableListOf<AgoraButton>()
    for (button in this.agoraSettings.enabledButtons) {
        rtnButtons += when (button) {
            AgoraSettings.BuiltinButton.MIC -> this.getMicButton()
            AgoraSettings.BuiltinButton.CAMERA -> this.getCameraButton()
            AgoraSettings.BuiltinButton.FLIP -> this.getFlipButton()
            AgoraSettings.BuiltinButton.END -> this.getEndCallButton()
            AgoraSettings.BuiltinButton.CHAT -> this.getChatButton()
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
        }
    }
    return rtnButtons
}

@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.addVideoButtons() {
    val container = this.getControlContainer()
    val topButtonContainer = this.getTopButtonContainer()
    val backButtonContainer = this.getBackButtonContainer()
    this.getControlContainerHandle()
    val buttonContainer = this.getHorizontalButtonContainer()
    val buttons = this.builtinButtons() + this.agoraSettings.extraButtons
    val topButtons = this.topButtons()
    container.visibility = if (buttons.isEmpty()) View.INVISIBLE else View.VISIBLE

    val buttonSize = 120
    val buttonMargin = 0f

    val llayout = LinearLayout.LayoutParams(buttonSize, buttonSize)
    llayout.gravity = Gravity.CENTER
    backButtonContainer.addView(this.getBackButton(), llayout)

    buttons.forEach { button ->
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
    val recyclerView = RecyclerView(context)
    recyclerView.layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        500
    )
    recyclerView.layoutManager = LinearLayoutManager(context)
    recyclerView.adapter = participantsAdapter

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
    container.addView(recyclerView)
    val contWidth = (buttons.size.toFloat() + buttonMargin) * buttons.count()
    container.setPadding(0, 16, 0, 16)
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