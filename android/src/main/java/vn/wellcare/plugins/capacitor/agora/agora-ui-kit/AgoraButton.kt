package vn.wellcare.plugins.capacitor.agora.`agora-ui-kit`

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DimenRes
import androidx.core.view.setPadding
import vn.wellcare.plugins.capacitor.agora.R

/**
 * A button to fit the style of builtin Agora VideoUIKit Buttons
 *
 * @param context the context for the application.
 * @param attrs the attribute set for the button.
 * @param defStyleAttr An attribute in the current theme that contains a reference to a style resource that supplies defaults values for the StyledAttributes. Can be 0 to not look for defaults.
 * @property clickAction The action to be conducted when the button is tapped.
 * @constructor Creates a new button.
 */
public class AgoraButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr) {

    public var clickAction: ((button: AgoraButton) -> Any?)? = null

    init {
//        setBackgroundColor(Color.BLUE)
        background = context.getDrawable(R.drawable.button_background)
        scaleType = ScaleType.FIT_XY
        this.background.setTint(Color.TRANSPARENT)
        setPadding(DPToPx(context, 10))
//        setMargin(R.dimen.zero_margin,R.dimen.top_margin,R.dimen.zero_margin,R.dimen.zero_margin)
        setOnClickListener {
            this.clickAction?.let { it(this) }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setMargin(R.dimen.button_margin, R.dimen.top_margin_15, R.dimen.button_margin, R.dimen.button_margin)
    }

      fun View?.setMargin(
            @DimenRes marginStart: Int? = null,
            @DimenRes marginTop: Int? = null,
            @DimenRes marginEnd: Int? = null,
            @DimenRes marginBottom: Int? = null
    ) {
        setMarginPixelOffset(
                marginStart?.let {
                    this?.resources?.getDimensionPixelOffset(it)
                },
                marginTop?.let {
                    this?.resources?.getDimensionPixelOffset(it)
                },
                marginEnd?.let {
                    this?.resources?.getDimensionPixelOffset(it)
                },
                marginBottom?.let {
                    this?.resources?.getDimensionPixelOffset(it)
                }
        )
    }

    private fun View?.setMarginPixelOffset(
            marginStart: Int? = null,
            marginTop: Int? = null,
            marginEnd: Int? = null,
            marginBottom: Int? = null
    ) {

        (this?.layoutParams as? ViewGroup.MarginLayoutParams)?.let { mlp ->
            mlp.setMargins(
                    marginStart ?: mlp.marginStart,
                    marginTop ?: mlp.topMargin,
                    marginEnd ?: mlp.marginEnd,
                    marginBottom ?: mlp.bottomMargin
            )
        }
    }
}

public class AgoraButtonBottom @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr) {

    public var clickAction: ((button: AgoraButtonBottom) -> Any?)? = null

    init {
//        setBackgroundColor(Color.BLUE)
        background = context.getDrawable(R.drawable.button_background)
        scaleType = ScaleType.FIT_XY
        this.background.setTint(Color.TRANSPARENT)
        setPadding(DPToPx(context, 10))
//        setMargin(R.dimen.zero_margin,R.dimen.top_margin,R.dimen.zero_margin,R.dimen.zero_margin)
        setOnClickListener {
            this.clickAction?.let { it(this) }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setMargin(R.dimen.top_margin_15, R.dimen.top_margin_15, R.dimen.top_margin_15, R.dimen.top_margin_15)
    }

    fun View?.setMargin(
        @DimenRes marginStart: Int? = null,
        @DimenRes marginTop: Int? = null,
        @DimenRes marginEnd: Int? = null,
        @DimenRes marginBottom: Int? = null
    ) {
        setMarginPixelOffset(
            marginStart?.let {
                this?.resources?.getDimensionPixelOffset(it)
            },
            marginTop?.let {
                this?.resources?.getDimensionPixelOffset(it)
            },
            marginEnd?.let {
                this?.resources?.getDimensionPixelOffset(it)
            },
            marginBottom?.let {
                this?.resources?.getDimensionPixelOffset(it)
            }
        )
    }

    private fun View?.setMarginPixelOffset(
        marginStart: Int? = null,
        marginTop: Int? = null,
        marginEnd: Int? = null,
        marginBottom: Int? = null
    ) {

        (this?.layoutParams as? ViewGroup.MarginLayoutParams)?.let { mlp ->
            mlp.setMargins(
                marginStart ?: mlp.marginStart,
                marginTop ?: mlp.topMargin,
                marginEnd ?: mlp.marginEnd,
                marginBottom ?: mlp.bottomMargin
            )
        }
    }
}
