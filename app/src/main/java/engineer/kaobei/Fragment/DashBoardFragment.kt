package engineer.kaobei.Fragment

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.button.MaterialButton
import engineer.kaobei.Activity.ui.login.LoginActivity
import engineer.kaobei.Viewmodel.DashBoardViewModel

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
        val view: View = inflater.inflate(
            R.layout.fragment_dash_board, container,
            false
        )
        val btn = view.findViewById<MaterialButton>(R.id.btn_login)
        btn.setOnClickListener{
            var intent = Intent(context, LoginActivity::class.java)
            context?.startActivity(intent)
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DashBoardViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
