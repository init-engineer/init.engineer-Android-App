package engineer.kaobei.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import engineer.kaobei.Activity.LoginActivity
import engineer.kaobei.Database.AuthStateManager
import engineer.kaobei.Model.KaobelUser.BeanKaobeiUser
import engineer.kaobei.R
import engineer.kaobei.View.UserViewer
import engineer.kaobei.Viewmodel.DashBoardViewModel
import kotlinx.android.synthetic.main.fragment_dash_board.*
import net.openid.appauth.AuthState
import okhttp3.*
import java.io.IOException


class DashBoardFragment : Fragment() {

    companion object {
        private lateinit var authStateManager : AuthStateManager
        private var isAuthorized = false
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

        val userviewer = view.findViewById<UserViewer>(R.id.userviewer)

        userviewer.setOnLongClickListener(object :View.OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                Toast.makeText(v?.context,"AAAAAAAAA",Toast.LENGTH_LONG).show()
                return true
            }
        })

        authStateManager = AuthStateManager.getInstance(view.context)
        if(authStateManager.getCurrent().isAuthorized){
            isAuthorized = true
            userviewer.initView(true)
            userviewer.setOnClickListener {
                val bt_sheet = BottomSheetDialog(view.context)
                val mView = LayoutInflater.from(view.context).inflate(R.layout.bottom_sheet_authorized, null)
                val cardview_logout : CardView = mView.findViewById(R.id.cardview_logout)
                cardview_logout.setOnClickListener{
                    logout()
                }
                bt_sheet.setContentView(mView)
                bt_sheet.show()
            }
        }else{
            isAuthorized = false
            userviewer.initView(false)
            userviewer.setOnClickListener {
                val bt_sheet = BottomSheetDialog(view.context)
                val mView = LayoutInflater.from(view.context).inflate(R.layout.bottom_sheet_not_authorized, null)
                val cardview_login : CardView = mView.findViewById(R.id.cardview_login)
                cardview_login.setOnClickListener {
                    login()
                }
                bt_sheet.setContentView(mView)
                bt_sheet.show()
            }
        }

        return view
    }

    fun login(){
        val intent = Intent(context, LoginActivity::class.java)
        activity?.startActivityForResult(intent,LoginActivity.RC_AUTH)
    }

    fun logout() {
        val authState = AuthState()
        authStateManager.replace(authState)
        activity?.finish()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(isAuthorized){
            viewModel = ViewModelProviders.of(this).get(DashBoardViewModel::class.java)
            authStateManager.getCurrent().accessToken?.let { loadDashBoard(it) }
            authStateManager.getCurrent().accessToken?.let { loadProfile(it) }
        }
        // TODO: Use the ViewModel
    }

    fun loadDashBoard(accessToken:String) {
        // Do an asynchronous operation .
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://kaobei.engineer/api/frontend/social/cards/api/dashboard")
            .addHeader("Authorization","Bearer "+accessToken)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {

            }

        })
    }

    fun loadProfile(accessToken:String) {
        // Do an asynchronous operation .
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://kaobei.engineer/api/frontend/user/profile")
            .addHeader("Authorization","Bearer "+accessToken)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response?.body?.string()
                if(response?.code !=200){
                    return
                }
                val beawn = Gson().fromJson(responseData,BeanKaobeiUser::class.javaObjectType)
                activity?.runOnUiThread {
                    userviewer?.setProfile(beawn.data)
                }
            }

        })
    }

}
