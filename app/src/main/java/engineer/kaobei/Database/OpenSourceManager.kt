package engineer.kaobei.Database

import android.content.Context
import com.google.gson.Gson
import engineer.kaobei.Model.OpenSources.OpenSource
import engineer.kaobei.Model.OpenSources.OpenSources

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