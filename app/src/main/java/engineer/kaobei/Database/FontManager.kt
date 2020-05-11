package engineer.kaobei.Database

import android.content.Context
import com.google.gson.Gson
import engineer.kaobei.Model.Fonts.Font
import engineer.kaobei.Model.Fonts.KaobeiFonts
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicReference

class FontManager(context: Context) {

    private var fonts : List<Font>

    init {
        val fileInString: String =
            context.assets.open("fonts.json").bufferedReader().use { it.readText() }
        val bean = Gson().fromJson(fileInString, KaobeiFonts::class.javaObjectType)
        fonts = bean.options
    }

    companion object{
        private val INSTANCE_REF =
            AtomicReference(
                WeakReference<FontManager>(null)
            )
        fun getInstance(context: Context): FontManager {
            var manager = INSTANCE_REF.get().get()
            if (manager == null) {
                manager = FontManager(context.applicationContext)
                INSTANCE_REF.set(WeakReference(manager))
            }
            return manager
        }
    }

    fun getFonts() : List<Font>{
        return fonts
    }


}