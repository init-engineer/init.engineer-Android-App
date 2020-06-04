package engineer.kaobei.Database

import android.content.Context
import com.google.gson.Gson
import engineer.kaobei.Model.Fonts.Font
import engineer.kaobei.Model.Fonts.KaobeiFonts

class FontManager(context: Context) {

    private var fonts: List<Font>

    init {
        val fileInString: String =
            context.assets.open("fonts.json").bufferedReader().use { it.readText() }
        val bean = Gson().fromJson(fileInString, KaobeiFonts::class.javaObjectType)
        fonts = bean.options
    }

    companion object {
        @Volatile
        private var INSTANCE: FontManager? = null

        fun getInstance(context: Context): FontManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FontManager(context).also { INSTANCE = it }
            }

        }
    }

    fun getFonts(): List<Font> {
        return fonts
    }


}