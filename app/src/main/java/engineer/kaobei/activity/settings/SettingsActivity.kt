package engineer.kaobei.activity.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Button
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
import engineer.kaobei.R
import engineer.kaobei.activity.ArticleActivity
import engineer.kaobei.database.OpenSourceManager
import engineer.kaobei.util.CustomTabUtil
import engineer.kaobei.util.ViewUtil
import engineer.kaobei.view.AnimatedGap

/**
 * Class SettingsActivity.
 */
class SettingsActivity : AppCompatActivity() {

    companion object {
        val URL_PROJECT_ON_GITHUB = "https://github.com/init-engineer/init.engineer-Android-App"
    }

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

                val adapter = OpenSourceAdapter(openSourceManager.getOpenSource()) { source ->
                    val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
                        .setToolbarColor(ContextCompat.getColor(it.context, R.color.colorPrimary))
                        .setShowTitle(true)

                    val customTabsIntent: CustomTabsIntent = builder.build()
                    customTabsIntent.launchUrl(it.context, Uri.parse(source.url))
                }

                rv_open_source.layoutManager = LinearLayoutManager(it.context)
                rv_open_source.adapter = adapter
                bt_sheet.setContentView(mView)
                bt_sheet.show()
                false
            }

            val projectOnGithubPreference: Preference? = findPreference(resources.getText(R.string.pref_project_on_github))
            projectOnGithubPreference?.setOnPreferenceClickListener {
                CustomTabUtil.createCustomTab(it.context, URL_PROJECT_ON_GITHUB)
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
