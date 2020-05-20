package engineer.kaobei.Util

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import engineer.kaobei.R

object CustomTabUtil {
    fun createCustomTab(context: Context, url: String) {
        val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(context.resources.getColor(R.color.colorPrimary))
        builder.setShowTitle(true)
        val customTabsIntent: CustomTabsIntent = builder.build()
        customTabsIntent.launchUrl(
            context,
            Uri.parse(url)
        )
    }
}