package engineer.kaobei.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import engineer.kaobei.R
import engineer.kaobei.fragment.CreateArticleFragment

class CreateArticleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_article_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, CreateArticleFragment.newInstance())
                .commitNow()
        }
    }
}
