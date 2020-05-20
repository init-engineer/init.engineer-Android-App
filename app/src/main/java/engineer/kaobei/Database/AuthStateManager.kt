package engineer.kaobei.Database

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.annotation.AnyThread
import net.openid.appauth.*
import org.json.JSONException
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock


class AuthStateManager() {

    private val TAG = "AuthStateManager"

    private val STORE_NAME = "AuthState"
    private val KEY_STATE = "state"

    private lateinit var mPrefs: SharedPreferences
    private lateinit var mPrefsLock: ReentrantLock
    private lateinit var mCurrentAuthState: AtomicReference<AuthState>

    constructor(context: Context) : this() {
        mPrefs = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)
        mPrefsLock = ReentrantLock()
        mCurrentAuthState = AtomicReference()
    }

    companion object {
        private val INSTANCE_REF =
            AtomicReference(
                WeakReference<AuthStateManager>(null)
            )

        fun getInstance(context: Context): AuthStateManager {
            var manager = INSTANCE_REF.get().get()
            if (manager == null) {
                manager = AuthStateManager(context.applicationContext)
                INSTANCE_REF.set(WeakReference(manager))
            }
            return manager
        }
    }


    @AnyThread
    fun getCurrent(): AuthState {
        if (mCurrentAuthState.get() != null) {
            return mCurrentAuthState.get()
        }
        val state = readState()
        return if (mCurrentAuthState.compareAndSet(null, state)) {
            state
        } else {
            mCurrentAuthState.get()
        }
    }

    @AnyThread
    fun replace(state: AuthState): AuthState {
        writeState(state)
        mCurrentAuthState.set(state)
        return state
    }

    @AnyThread
    fun updateConfig(
        config: AuthorizationServiceConfiguration
    ): AuthState {
        val current = getCurrent()

        return replace(current)
    }


    @AnyThread
    fun updateAfterAuthorization(
        response: AuthorizationResponse,
        ex: AuthorizationException?
    ): AuthState {
        val current = getCurrent()
        current.update(response, ex)
        return replace(current)
    }

    @AnyThread
    fun updateAfterTokenResponse(
        response: TokenResponse,
        ex: AuthorizationException?
    ): AuthState {
        val current = getCurrent()
        if (ex != null) {
            return current
        }
        current.update(response, ex)
        return replace(current)
    }

    @AnyThread
    fun updateAfterRegistration(
        response: RegistrationResponse,
        ex: AuthorizationException?
    ): AuthState {
        val current = getCurrent()
        if (ex != null) {
            return current
        }
        current.update(response)
        return replace(current)
    }

    @AnyThread
    private fun readState(): AuthState {
        mPrefsLock.lock()
        return try {
            val currentState = mPrefs.getString(KEY_STATE, null) ?: return AuthState()
            try {
                AuthState.jsonDeserialize(currentState)
            } catch (ex: JSONException) {
                Log.w(TAG, "Failed to deserialize stored auth state - discarding")
                AuthState()
            }
        } finally {
            mPrefsLock.unlock()
        }
    }

    @AnyThread
    private fun writeState(state: AuthState) {
        mPrefsLock.lock()
        try {
            val editor = mPrefs.edit()
            editor.putString(KEY_STATE, state.jsonSerializeString())
            check(editor.commit()) { "Failed to write state to shared prefs" }
        } finally {
            mPrefsLock.unlock()
        }
    }

}