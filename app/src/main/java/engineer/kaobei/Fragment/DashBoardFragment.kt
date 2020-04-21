package engineer.kaobei.Fragment

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import engineer.kaobei.R

class DashBoardFragment : Fragment() {

    companion object {
        fun newInstance() = DashBoardFragment()
    }

    private lateinit var viewModel: DashBoardViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dash_board, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DashBoardViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
