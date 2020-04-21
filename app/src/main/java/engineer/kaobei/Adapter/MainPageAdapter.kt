package engineer.kaobei.Adapter

import android.os.Bundle
import androidx.fragment.app.*
import androidx.viewpager.widget.ViewPager
import engineer.kaobei.Fragment.*

/**
A adapter controlling the main layout
 */
class MainPageAdapter(val viewPager: ViewPager, val fragmentManager: FragmentManager ,var menuPosition : Int) :
    FragmentStatePagerAdapter(
        fragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {

    private val viewPagerCount = 2

    /**
     * The first page of viewpager is menu
     * THe second page of viewpager is content
     */
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> getMenuFragment()
            1 -> getFragment()
            else -> getMenuFragment()
        }
    }

    /**
     * When the items in the menu is clicked by user
     * switch to page 2 and change contents
     */
    private fun getMenuFragment(): Fragment {
        var menuFragment = MenuFragment.newInstance(menuPosition)
        menuFragment.setOnFragmentInteractionListener(object :
            MenuFragment.OnFragmentChangeListener {
            override fun onFragmentChange(index: Int) {
                menuPosition = index
                notifyDataSetChanged()
                viewPager.setCurrentItem(1, true)
            }
        })
        return menuFragment
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    private fun getFragment(): Fragment {
        return when (menuPosition) {
            0 -> IndexFragment.newInstance()
            1 -> ArticleListFragment.newInstance()
            2 -> DashBoardFragment.newInstance()
            3-> AboutFragment.newInstance()
            else -> getMenuFragment()
        }
    }

    override fun getCount(): Int {
        return viewPagerCount
    }

}
