package vn.linhpv.miniblogapp.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import vn.linhpv.miniblogapp.R
import vn.linhpv.miniblogapp.databinding.WidgetBottomNavigationBarBinding

class BottomNavigationBar : LinearLayout {

    private var context: Context? = null
    lateinit var binding: WidgetBottomNavigationBarBinding

    var onSelected: (Int) -> Unit = {}

    private var currentCheckedId: Int = R.id.nav_discover

    private var isReverting: Boolean = false

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
        this.context = context
        binding = WidgetBottomNavigationBarBinding.inflate(LayoutInflater.from(context), this, true)

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

    fun setSelectedIndex(index: Int) {
        if (index < 0 || index > 3) return

        if (index == 3) {
            return
        }

        val targetId = when (index) {
            0 -> R.id.nav_discover
            1 -> R.id.nav_search
            2 -> R.id.nav_follow
            else -> -1
        }

        if (targetId != -1 && targetId != currentCheckedId) {
            isReverting = true
            binding.navBarItems.check(targetId)
            isReverting = false
            currentCheckedId = targetId
        }
    }
}