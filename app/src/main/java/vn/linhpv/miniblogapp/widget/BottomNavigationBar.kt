package vn.linhpv.miniblogapp.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import vn.linhpv.miniblogapp.R
import vn.linhpv.miniblogapp.databinding.WidgetBottomNavigationBarBinding

class BottomNavigationBar : LinearLayout {

    lateinit var binding: WidgetBottomNavigationBarBinding

    var onSelected: (Int) -> Unit = {}

    private var currentCheckedId: Int = R.id.nav_discover
    private var isReverting: Boolean = false
    private var originalBottomMargin: Int = 0

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        binding = WidgetBottomNavigationBarBinding.inflate(LayoutInflater.from(context), this, true)

        ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())

            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = originalBottomMargin + insets.bottom
            }

            WindowInsetsCompat.CONSUMED
        }

        val navGroup = binding.navBarItems
        navGroup.check(currentCheckedId)

        navGroup.setOnCheckedChangeListener { group, checkedId ->
            if (isReverting) {
                return@setOnCheckedChangeListener
            }

            if (checkedId == R.id.nav_profile) {
                isReverting = true
                group.check(currentCheckedId)
                onSelected(3)
                isReverting = false
            } else {
                currentCheckedId = checkedId
                val selectedIndex = when (checkedId) {
                    R.id.nav_discover -> 0
                    R.id.nav_search -> 1
                    R.id.nav_follow -> 2
                    else -> -1
                }
                if (selectedIndex != -1) {
                    onSelected(selectedIndex)
                }
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        ViewCompat.requestApplyInsets(this)
    }

    fun setSelectedIndex(index: Int) {
        if (index < 0 || index >= 3) {
            return
        }

        val targetId = when (index) {
            0 -> R.id.nav_discover
            1 -> R.id.nav_search
            2 -> R.id.nav_follow
            else -> -1
        }

        if (targetId != -1 && targetId != currentCheckedId) {
            currentCheckedId = targetId
            binding.navBarItems.check(targetId)
        }
    }
}