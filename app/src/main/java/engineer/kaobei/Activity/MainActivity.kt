package engineer.kaobei.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import engineer.kaobei.Adapter.MainPageAdapter
import engineer.kaobei.R

/**
 * This is the base activity of the app
 */
class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var fab: ExtendedFloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewpager()
    }

    private fun initViewpager(){
        viewPager = findViewById(R.id.main_viewpager)
        fab=findViewById(R.id.fab)

        //Adapter
        val adapter = MainPageAdapter(viewPager,supportFragmentManager,0)
        viewPager.adapter = adapter
        viewPager.setCurrentItem(1, true)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }
            override fun onPageSelected(position: Int) {
                if(position==0) fab.hide() else fab.show()

            }
        })
        fab.setOnClickListener{
            viewPager.currentItem = 0
        }
    }
}
