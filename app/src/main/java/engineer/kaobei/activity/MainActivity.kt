package engineer.kaobei.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.transition.TransitionManager
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import engineer.kaobei.database.AuthStateManager
import engineer.kaobei.R
import engineer.kaobei.fragment.ArticleListFragment
import engineer.kaobei.fragment.DashBoardFragment
import engineer.kaobei.fragment.IndexFragment
import engineer.kaobei.fragment.ReviewFragment
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Class MainActivity.
 *
 * This is the base activity of the app
 */
class MainActivity : AppCompatActivity() {

    /**
     * Bottom sheet status
     */
    private var titleClicked: Boolean = false
    private lateinit var authStateManager: AuthStateManager

    private lateinit var title: TextView
    private lateinit var sheet: CardView
    private lateinit var big_card: CardView
    private lateinit var fab_main : ExtendedFloatingActionButton


    private var indexFragment: Fragment =  IndexFragment()
    private var articleListFragment : Fragment = ArticleListFragment()
    private var reviewFragment : Fragment = ReviewFragment()
    private var dashBoardFragment : Fragment = DashBoardFragment()
    private var currentFragment : Fragment = Fragment()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == LoginActivity.AUTH_SUCCESS) {
            val intent = intent
            finish()
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        savedInstanceState?.remove("android:support:fragments")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this) {}


        authStateManager = AuthStateManager.getInstance(this)

        title = findViewById(R.id.tv_title)
        sheet = findViewById(R.id.sheet)
        big_card = findViewById(R.id.big_card)
        title.setOnClickListener {
            enterView()
        }

        if(savedInstanceState!=null){
            exitView()
        }else{
            showSplashScreen()
        }

        fab_main = findViewById(R.id.fab_main)
        fab_main.hide()
        fab_main.setOnClickListener{
            when(nav_view.selectedItemId){
                R.id.navigation_home ->{

                }
                R.id.navigation_article_list ->{
                   val intent = Intent(this,CreateArticleActivity::class.java)
                    startActivity(intent)
                }
                R.id.navigation_review ->{

                }
                R.id.navigation_dashboard ->{
                }
            }
        }

        currentFragment = indexFragment
        supportFragmentManager.beginTransaction().apply {
            add(R.id.nav_host_fragment, articleListFragment, getString(R.string.menu_2A)).hide(articleListFragment)
            add(R.id.nav_host_fragment, reviewFragment, getString(R.string.menu_3A)).hide(reviewFragment)
            add(R.id.nav_host_fragment, dashBoardFragment, getString(R.string.menu_4A)).hide(dashBoardFragment)
            add(R.id.nav_host_fragment, indexFragment, getString(R.string.menu_1A))
        }.commit()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.selectedItemId = R.id.navigation_home
        navView.setOnNavigationItemSelectedListener  {
            when(it.itemId){
                R.id.navigation_home ->{
                    fab_main.hide()
                    supportFragmentManager.beginTransaction().hide(currentFragment).show(indexFragment).commit()
                    currentFragment = indexFragment
                    true
                }
                R.id.navigation_article_list ->{
                    if(authStateManager.getCurrent().isAuthorized){
                        fab_main.show()
                    }
                    supportFragmentManager.beginTransaction().hide(currentFragment).show(articleListFragment).commit()
                    currentFragment = articleListFragment
                    true
                }
                R.id.navigation_review ->{
                    fab_main.hide()
                    supportFragmentManager.beginTransaction().hide(currentFragment).show(reviewFragment).commit()
                    currentFragment = reviewFragment
                    true
                }
                R.id.navigation_dashboard ->{
                    fab_main.hide()
                    supportFragmentManager.beginTransaction().hide(currentFragment).show(dashBoardFragment).commit()
                    currentFragment = dashBoardFragment
                    true
                }else -> false
            }
        }


    }

    private fun showSplashScreen() {
        Handler().postDelayed({
            exitView()
        }, 1000)
    }

    /**
     * show splash screen and hide sheet
     */
    private fun enterView() {
        val transform = MaterialContainerTransform().apply {
            // Manually tell the container transform which Views to transform between.
            startView = sheet
            endView = big_card

            // Optionally add a curved path to the transform
            pathMotion = MaterialArcMotion()

            // Since View to View transforms often are not transforming into full screens,
            // remove the transition's scrim.
            scrimColor = Color.TRANSPARENT
        }

        TransitionManager.beginDelayedTransition(
            window.decorView.findViewById(android.R.id.content),
            transform
        )

        sheet.visibility = View.GONE
        big_card.visibility = View.VISIBLE
        titleClicked = true
    }

    /**
     * hide splash screen and show sheet
     */
    private fun exitView() {
        val transform = MaterialContainerTransform().apply {
            // Manually tell the container transform which Views to transform between.
            startView = big_card
            endView = sheet

            // Optionally add a curved path to the transform
            pathMotion = MaterialArcMotion()

            // Since View to View transforms often are not transforming into full screens,
            // remove the transition's scrim.
            scrimColor = Color.TRANSPARENT
        }
        TransitionManager.beginDelayedTransition(
            window.decorView.findViewById(android.R.id.content),
            transform
        )
        sheet.visibility = View.VISIBLE
        big_card.visibility = View.GONE
        titleClicked = false
    }

    override fun onBackPressed() {
        if (titleClicked) {
            exitView()
        } else {
            super.onBackPressed()
        }
    }
}
