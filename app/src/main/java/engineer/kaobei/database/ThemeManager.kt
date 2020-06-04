package engineer.kaobei.database

import android.content.Context
import com.google.gson.Gson
import engineer.kaobei.model.themes.KaobeiThemes
import engineer.kaobei.model.themes.Theme

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