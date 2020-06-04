package engineer.kaobei.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import engineer.kaobei.BuildConfig
import engineer.kaobei.database.AuthStateManager
import engineer.kaobei.R
import engineer.kaobei.util.ext.viewLoading
import net.openid.appauth.*
import net.openid.appauth.AuthorizationService.TokenResponseCallback

/**
 * Class LoginActivity.
 */
class LoginActivity : AppCompatActivity() {

    private val redirectUrl: String = BuildConfig.OATH2REDIRECTURL
    private val oath2clientid: String = BuildConfig.OATH2CLIENTID
    private val oath2clientsecret: String = BuildConfig.OATH2CLIENTSECRET

    private lateinit var serviceConfig: AuthorizationServiceConfiguration
    private lateinit var authRequestBuilder: AuthorizationRequest.Builder

    companion object {
        private lateinit var service: AuthorizationService
        private lateinit var authStateManager: AuthStateManager
        const val RC_AUTH = 0
        const val AUTH_SUCCESS = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val container: ImageView = findViewById(R.id.container)
        container.viewLoading(ContextCompat.getDrawable(this, R.drawable.img_background_star))

        service = AuthorizationService(this)
        authStateManager = AuthStateManager.getInstance(this)
        serviceConfig = AuthorizationServiceConfiguration(
            Uri.parse("https://kaobei.engineer/oauth/authorize"),  // authorization endpoint
            Uri.parse("https://kaobei.engineer/oauth/token")
        )
        authRequestBuilder = AuthorizationRequest.Builder(
            serviceConfig,  // the authorization service configuration
            oath2clientid,  // the client ID, typically pre-registered and static
            ResponseTypeValues.CODE,  // the response_type value: we want a code
            Uri.parse(redirectUrl)
        )
        authRequestBuilder.setScope("*")
        val authState = AuthState(serviceConfig)
        authStateManager.replace(authState)
        val loginButton = findViewById<Button>(R.id.login_button)
        loginButton.setOnClickListener {
            login()
        }
    }

    private fun login() {
        doAuthorization(authRequestBuilder.build())
    }

    private fun doAuthorization(authRequest: AuthorizationRequest) {
        val authIntent = service.getAuthorizationRequestIntent(authRequest)
        startActivityForResult(authIntent, RC_AUTH)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_AUTH) {
            val resp = data?.let { AuthorizationResponse.fromIntent(it) }
            val ex = AuthorizationException.fromIntent(data)
            if (resp != null) {
                authStateManager.updateAfterAuthorization(resp, ex)
                val clientAuth: ClientAuthentication = ClientSecretBasic(oath2clientsecret)
                service.performTokenRequest(
                    resp.createTokenExchangeRequest(),
                    clientAuth,
                    TokenResponseCallback { r, e ->
                        if (r != null) {
                            val state2 = authStateManager.updateAfterTokenResponse(r, e)
                            Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show()
                            setResult(AUTH_SUCCESS)
                            finish()
                        } else {
                            Toast.makeText(this, "Resp failed.", Toast.LENGTH_SHORT).show()
                        }
                    })
            } else {
                Toast.makeText(this, "Login failed.", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Do something ...
        }
    }
}
