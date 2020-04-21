package engineer.kaobei.Fragment

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

import engineer.kaobei.R

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MenuFragment.OnFragmentChangeListener] interface
 * to handle interaction events.
 * Use the [MenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MenuFragment : Fragment() {

    val ARG_MENU_POSITION : String = "ARG_MENU_POSITION"

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(menuPosition: Int) =
            MenuFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_MENU_POSITION, menuPosition)
                }
            }
    }

    private var menuPosition: Int? = null
    private var mOnFragmentChangeListener: OnFragmentChangeListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            menuPosition = it.getInt(ARG_MENU_POSITION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(
            R.layout.fragment_menu, container,
            false
        )

        val background = view.findViewById<ImageView>(R.id.background_image_view)
        //Glide.with(view).asGif().load(R.drawable.background_star).into(background)

        val item1 = view.findViewById<TextView>(R.id.menu_item_1)
        val item2 = view.findViewById<TextView>(R.id.menu_item_2)
        val item3 = view.findViewById<TextView>(R.id.menu_item_3)
        val item4 = view.findViewById<TextView>(R.id.menu_item_4)

        when(menuPosition){
            0 -> item1.background = resources.getDrawable(R.drawable.text_bottom_line)
            1 -> item2.background = resources.getDrawable(R.drawable.text_bottom_line)
            2 -> item3.background = resources.getDrawable(R.drawable.text_bottom_line)
            3-> item4.background = resources.getDrawable(R.drawable.text_bottom_line)
        }

        item1.setOnClickListener{
            onChangeIndex(0)
        }
        item2.setOnClickListener{
            onChangeIndex(1)
        }
        item3.setOnClickListener{
            onChangeIndex(2)
        }
        item4.setOnClickListener{
            onChangeIndex(3)
        }


        return view
    }

    fun setOnFragmentInteractionListener(listener : OnFragmentChangeListener){
        mOnFragmentChangeListener= listener
    }

    interface OnFragmentChangeListener {
        // TODO: Update argument type and name
        fun onFragmentChange(index: Int)
    }

    fun onChangeIndex(index : Int) {
        mOnFragmentChangeListener?.onFragmentChange(index)
    }

}
