package engineer.kaobei.Database

import android.content.Context
import com.google.gson.Gson
import engineer.kaobei.Model.OpenSources.OpenSource
import engineer.kaobei.Model.OpenSources.OpenSources
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicReference

class OpenSourceManager(context: Context) {

    private var openSourceList: List<OpenSource>

    init {
        val fileInString: String =
            context.assets.open("open_source.json").bufferedReader().use { it.readText() }
        val bean = Gson().fromJson(fileInString, OpenSources::class.javaObjectType)
        openSourceList = bean.data
    }

    companion object {
        private val INSTANCE_REF =
            AtomicReference(
                WeakReference<OpenSourceManager>(null)
            )

        fun getInstance(context: Context): OpenSourceManager {
            var manager = INSTANCE_REF.get().get()
            if (manager == null) {
                manager = OpenSourceManager(context.applicationContext)
                INSTANCE_REF.set(WeakReference(manager))
            }
            return manager
        }
    }

    fun getOpenSource(): List<OpenSource> {
        return openSourceList
    }
}