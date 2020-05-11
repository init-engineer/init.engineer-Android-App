package engineer.kaobei.Activity

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import engineer.kaobei.BuildConfig
import engineer.kaobei.Database.AuthStateManager
import engineer.kaobei.R
import net.openid.appauth.*
import net.openid.appauth.AuthorizationService.TokenResponseCallback


class LoginActivity : AppCompatActivity() {

    val redirectUrl: String = BuildConfig.OATH2REDIRECTURL
    val oath2clientid: String = BuildConfig.OATH2CLIENTID
    val oath2clientsecret: String = BuildConfig.OATH2CLIENTSECRET
    val website = "https://kaobei.engineer/oauth/authorize"

    lateinit var serviceConfig: AuthorizationServiceConfiguration
    lateinit var authRequestBuilder: AuthorizationRequest.Builder

    companion object {
        lateinit var service : AuthorizationService
        private lateinit var authStateManager: AuthStateManager
        lateinit var authState: AuthState
        const val RC_AUTH = 0
        const val AUTH_SUCCESS = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        service = AuthorizationService(this)
        authStateManager = AuthStateManager.getInstance(this)
        serviceConfig =
            AuthorizationServiceConfiguration(
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

    fun login() {
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
                            Toast.makeText(this, "Login success!", Toast.LENGTH_SHORT).show()
                            setResult(AUTH_SUCCESS)
                            finish()
                        } else {
                            Toast.makeText(this, "resp failed", Toast.LENGTH_SHORT).show()
                        }
                    })
            } else {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            }
        } else {
            // ...
        }
    }
}
