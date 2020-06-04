package engineer.kaobei.database

import android.content.Context
import com.google.gson.Gson
import engineer.kaobei.model.opensources.OpenSource
import engineer.kaobei.model.opensources.OpenSources

class OpenSourceManager(context: Context) {

    private var openSourceList: List<OpenSource>

    init {
        val fileInString: String =
            context.assets.open("open_source.json").bufferedReader().use { it.readText() }
        val bean = Gson().fromJson(fileInString, OpenSources::class.javaObjectType)
        openSourceList = bean.data
    }

    companion object {

        @Volatile
        private var INSTANCE: OpenSourceManager? = null

        fun getInstance(context: Context): OpenSourceManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: OpenSourceManager(context).also { INSTANCE = it }
            }
        }
    }

    fun getOpenSource(): List<OpenSource> {
        return openSourceList
    }
}