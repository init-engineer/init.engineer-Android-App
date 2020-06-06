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
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import engineer.kaobei.database.AuthStateManager
import engineer.kaobei.R

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

    private lateinit var title: TextView
    private lateinit var sheet: CardView
    private lateinit var big_card: CardView
    private lateinit var authStateManager: AuthStateManager

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == LoginActivity.AUTH_SUCCESS) {
            val intent = intent
            finish()
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this) {}

        showSplashScreen()
        authStateManager = AuthStateManager.getInstance(this)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
        title = findViewById(R.id.tv_title)
        sheet = findViewById(R.id.sheet)
        big_card = findViewById(R.id.big_card)
        title.setOnClickListener {
            enterView()
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
