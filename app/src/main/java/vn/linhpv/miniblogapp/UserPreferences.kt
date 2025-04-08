package vn.linhpv.miniblogapp

import android.content.Context
import android.content.SharedPreferences
import vn.linhpv.miniblogapp.model.User

object UserPreferences {

    private const val PREFS_NAME = "user_prefs"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_AVATAR = "user_avatar"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_PASSWORD = "user_password"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_USER_FOLLOWING = "user_following"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveUser(context: Context, user: User) {
        val editor = getPreferences(context).edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putString(KEY_USER_ID, user.id)
        editor.putString(KEY_USER_AVATAR, user.avatar)
        editor.putString(KEY_USER_NAME, user.name)
        editor.putString(KEY_USER_EMAIL, user.email)
        editor.putString(KEY_USER_PASSWORD, user.password)
        editor.putString(KEY_USER_ROLE, user.role)
        editor.putStringSet(KEY_USER_FOLLOWING, user.following.toSet())
        editor.apply()
    }

    fun loadUser(context: Context): User? {
        val prefs = getPreferences(context)
        if (!prefs.getBoolean(KEY_IS_LOGGED_IN, false)) {
            return null
        }

        val id = prefs.getString(KEY_USER_ID, "") ?: ""
        if (id.isEmpty()) {
            return null
        }

        val avatar = prefs.getString(KEY_USER_AVATAR, "") ?: ""
        val name = prefs.getString(KEY_USER_NAME, "") ?: ""
        val email = prefs.getString(KEY_USER_EMAIL, "") ?: ""
        val password = prefs.getString(KEY_USER_PASSWORD, "") ?: ""
        val role = prefs.getString(KEY_USER_ROLE, "") ?: ""
        val followingSet = prefs.getStringSet(KEY_USER_FOLLOWING, emptySet()) ?: emptySet()
        val followingList = followingSet.toMutableList()

        return User(id, avatar, name, email, password, role, followingList)
    }

    fun clearUser(context: Context) {
        val editor = getPreferences(context).edit()
        editor.remove(KEY_IS_LOGGED_IN)
        editor.remove(KEY_USER_ID)
        editor.remove(KEY_USER_AVATAR)
        editor.remove(KEY_USER_NAME)
        editor.remove(KEY_USER_EMAIL)
        editor.remove(KEY_USER_PASSWORD)
        editor.remove(KEY_USER_ROLE)
        editor.remove(KEY_USER_FOLLOWING)
        editor.apply()
    }

     fun isLoggedIn(context: Context): Boolean {
         return getPreferences(context).getBoolean(KEY_IS_LOGGED_IN, false)
     }
}