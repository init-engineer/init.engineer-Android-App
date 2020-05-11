package engineer.kaobei.Database

import android.content.Context
import android.content.res.AssetManager
import com.google.gson.Gson
import engineer.kaobei.Model.Themes.KaobeiThemes
import engineer.kaobei.Model.Themes.Theme
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicReference

class ThemeManager(context: Context) {

    private var themes : List<Theme>

    init {
        val fileInString: String =
            context.assets.open("themes.json").bufferedReader().use { it.readText() }
        val bean = Gson().fromJson(fileInString, KaobeiThemes::class.javaObjectType)
        themes = bean.themes
    }

    companion object{
        private val INSTANCE_REF =
            AtomicReference(
                WeakReference<ThemeManager>(null)
            )
        fun getInstance(context: Context): ThemeManager {
            var manager = INSTANCE_REF.get().get()
            if (manager == null) {
                manager = ThemeManager(context.applicationContext)
                INSTANCE_REF.set(WeakReference(manager))
            }
            return manager
        }
    }

    fun getThemes() : List<Theme>{
        return themes
    }

}