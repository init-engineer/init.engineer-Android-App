package engineer.kaobei.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import engineer.kaobei.BuildConfig
import engineer.kaobei.manager.OpenSourceManager
import engineer.kaobei.model.opensources.OpenSource
import engineer.kaobei.R
import engineer.kaobei.util.CustomTabUtil
import engineer.kaobei.util.ViewUtil
import engineer.kaobei.view.AnimatedGap

/**
 * Class SettingsActivity.
 */
class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val appVersionPreference: Preference? =
                findPreference(resources.getText(R.string.pref_app_version))
            appVersionPreference?.summary = BuildConfig.VERSION_NAME
            val appVersionCodePreference: Preference? =
                findPreference(resources.getText(R.string.pref_app_versionCode))
            appVersionCodePreference?.summary = BuildConfig.VERSION_CODE.toString()


            val articleIDSearcherPreference:Preference? = findPreference(resources.getText(R.string.pref_app_id_search))
            articleIDSearcherPreference?.setOnPreferenceClickListener {
                val bt_sheet = BottomSheetDialog(it.context)
                val mView = LayoutInflater.from(it.context).inflate(R.layout.bottom_sheet_id_selecter, null)
                val textInputEditText : TextInputEditText = mView.findViewById(R.id.text_input)
                val confirm_button : Button = mView.findViewById(R.id.confirm_button)
                confirm_button.setOnClickListener {
                    val intent = Intent(context,ArticleActivity::class.java)
                    intent.putExtra(ArticleActivity.ID_KEY,textInputEditText.text.toString().toInt())
                    startActivity(intent)
                }
                bt_sheet.setContentView(mView)
                bt_sheet.show()
                false
            }

            val openSourcePreference: Preference? =
                findPreference(resources.getText(R.string.pref_openSource_key))
            openSourcePreference?.setOnPreferenceClickListener {
                val bt_sheet = BottomSheetDialog(it.context)
                val mView =
                    LayoutInflater.from(it.context).inflate(R.layout.bottom_sheet_open_source, null)
                val openSourceManager: OpenSourceManager = OpenSourceManager.getInstance(it.context)
                val rv_open_source: RecyclerView = mView.findViewById(R.id.rv_open_source)
                val gap = mView.findViewById<AnimatedGap>(R.id.gap)
                ViewUtil.addGapController(rv_open_source, gap)
                val adapter =
                    OpenSourceRecyclerViewAdapter(it.context, openSourceManager.getOpenSource())
                rv_open_source.layoutManager = LinearLayoutManager(it.context)
                adapter.setOnItemClickListener(object :
                    OpenSourceRecyclerViewAdapter.OnItemClickListener {
                    override fun onItemClick(opensource: OpenSource) {
                        val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
                        builder.setToolbarColor(ContextCompat.getColor(it.context, R.color.colorPrimary))
                        builder.setShowTitle(true)
                        val customTabsIntent: CustomTabsIntent = builder.build()
                        customTabsIntent.launchUrl(
                            it.context,
                            Uri.parse(opensource.url)
                        )
                    }
                })
                rv_open_source.adapter = adapter
                bt_sheet.setContentView(mView)
                bt_sheet.show()
                false
            }

            val projectonGithubPreference: Preference? =
                findPreference(resources.getText(R.string.pref_project_on_github))
            projectonGithubPreference?.setOnPreferenceClickListener {
                CustomTabUtil.createCustomTab(
                    it.context,
                    "https://github.com/init-engineer/init.engineer-Android-App"
                )
                false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

class OpenSourceRecyclerViewAdapter(
    private val context: Context,
    private val opensource: List<OpenSource>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mOnItemClickListener: OnItemClickListener? = null

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tv_name: TextView = itemView.findViewById(R.id.tv_name)
        val tv_author: TextView = itemView.findViewById(R.id.tv_author)
        val tv_url: TextView = itemView.findViewById(R.id.tv_url)

        fun bind(opensource: OpenSource) {
            tv_name.text = opensource.name
            tv_author.text = opensource.author
            tv_url.text = opensource.url
            itemView.setOnClickListener {
                mOnItemClickListener?.onItemClick(opensource)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview_open_source, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.opensource.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.bind(opensource[position])
        }
    }

    fun setOnItemClickListener(mOnItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(opensource: OpenSource)
    }
}