package engineer.kaobei.Activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.transition.TransitionManager
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import engineer.kaobei.R


/**
 * This is the base activity of the app
 */
class MainActivity : AppCompatActivity() {

    //Bottom sheet status
    var titleClicked = false

    lateinit var title: TextView
    lateinit var sheet: CardView
    lateinit var big_card: CardView

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==LoginActivity.AUTH_SUCCESS){
            val intent = intent
            finish()
            startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showSplashScreen()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navView.setupWithNavController(navController)
        /*navView.setOnNavigationItemSelectedListener {it ->
             onNavDestinationSelected(it,navController)
        }*/

        title = findViewById<TextView>(R.id.tv_title)
        sheet = findViewById<CardView>(R.id.sheet)
        big_card = findViewById<CardView>(R.id.big_card)
        title.setOnClickListener {
            enterView()
        }

    }

    fun onNavDestinationSelected(it:MenuItem,navController: NavController) : Boolean{
        when(it.itemId){
            R.id.navigation_home ->{

            }
            R.id.navigation_article_list ->{

            }
            R.id.navigation_create_article ->{

            }
            R.id.navigation_dashboard ->{

            }
            else ->{
                return false
            }
        }
        return true
    }

    fun showSplashScreen() {
        Handler().postDelayed({
            exitView()
        }, 1000)
    }

    //show splash screen and hide sheet
    fun enterView() {
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

    //hide splash screen and show sheet
    fun exitView() {
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
