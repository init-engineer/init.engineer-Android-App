package engineer.kaobei.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE

object ClipBoardUtil {

    fun copy(context: Context, string: String) {
        val myClipboard: ClipboardManager? =
            context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
        val myClip: ClipData? = ClipData.newPlainText("text", string)
        myClipboard?.primaryClip = myClip;
    }
}