package engineer.kaobei.util

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import engineer.kaobei.R

object CustomTabUtil {
    fun createCustomTab(context: Context, url: String) {
        val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))
        builder.setShowTitle(true)
        val customTabsIntent: CustomTabsIntent = builder.build()
        customTabsIntent.launchUrl(
            context,
            Uri.parse(url)
        )
    }
}