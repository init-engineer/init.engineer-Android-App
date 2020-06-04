package engineer.kaobei.util

import android.view.Gravity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar

object SnackbarUtil {
    @JvmStatic
    fun makeAnchorSnackbar(coorView: CoordinatorLayout, text: String, anchorID: Int) {
        val snackbar = Snackbar.make(coorView, text, Snackbar.LENGTH_SHORT)
        val layoutParams = snackbar.view.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.anchorId = anchorID
        layoutParams.gravity = Gravity.TOP
        snackbar.view.layoutParams = layoutParams
        snackbar.show()
    }
}