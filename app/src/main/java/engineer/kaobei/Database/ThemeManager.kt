package engineer.kaobei.Database

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import com.google.gson.Gson
import engineer.kaobei.Model.Themes.KaobeiThemes
import engineer.kaobei.Model.Themes.Theme
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicReference

class ThemeManager(context: Context) {

    private var themes: List<Theme>

    init {
        val fileInString: String =
            context.assets.open("themes.json").bufferedReader().use { it.readText() }
        val bean = Gson().fromJson(fileInString, KaobeiThemes::class.javaObjectType)
        themes = bean.themes
    }

    companion object {

        private var INSTANCE: ThemeManager? = null

        fun getInstance(context: Context): ThemeManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ThemeManager(context).also { INSTANCE = it }
            }
        }
    }

    fun getThemes(): List<Theme> {
        return themes
    }

}