package engineer.kaobei.Fragment

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import com.google.android.material.transition.MaterialSharedAxis
import engineer.kaobei.Viewmodel.IndexViewModel

import engineer.kaobei.R
import engineer.kaobei.Util.CustomTabUtil

class IndexFragment : Fragment() {

    companion object {
        fun newInstance() = IndexFragment()
    }

    private lateinit var viewModel: IndexViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(
            R.layout.fragment_index, container,
            false
        )
        val card1 :CardView= view.findViewById(R.id.card1)
        val card2 :CardView= view.findViewById(R.id.card2)
        val card3 :CardView= view.findViewById(R.id.card3)
        val card4 :CardView= view.findViewById(R.id.card4)
        card1.setOnClickListener {
            CustomTabUtil.createCustomTab(view.context,"https://kaobei.engineer/animal/kohlrabi")
        }
        card2.setOnClickListener {
            CustomTabUtil.createCustomTab(view.context,"https://www.facebook.com/init.kobeengineer/")
        }
        card3.setOnClickListener {
            CustomTabUtil.createCustomTab(view.context,"https://plurk.com/kaobei_engineer/")
        }
        card4.setOnClickListener {
            CustomTabUtil.createCustomTab(view.context,"https://twitter.com/kaobei_engineer/")
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(IndexViewModel::class.java)
        val forward: MaterialSharedAxis = MaterialSharedAxis.create(MaterialSharedAxis.X, true)
        val backward: MaterialSharedAxis = MaterialSharedAxis.create(MaterialSharedAxis.X, false)
        enterTransition = forward
        exitTransition = backward
    }

}
