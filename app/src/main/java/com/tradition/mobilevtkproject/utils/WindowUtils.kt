
import android.graphics.Color
import android.os.Build
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity

object WindowUtils {

    fun setStatusBarColor(
        activity: FragmentActivity,
        @ColorRes colorRes: Int,
        lightIcons: Boolean? = null
    ) {
        val window = activity.window
        val color = ContextCompat.getColor(activity, colorRes)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color

        // Android 11+

    }

    fun setNavigationBarColor(
        activity: FragmentActivity,
        @ColorRes colorRes: Int,
        lightIcons: Boolean? = null
    ) {
        val window = activity.window
        val color = ContextCompat.getColor(activity, colorRes)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.navigationBarColor = color

        // Android 11+
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.navigationBarColor = color
            WindowCompat.setDecorFitsSystemWindows(window, false)

            val controller = WindowInsetsControllerCompat(window, window.decorView)
            controller.isAppearanceLightNavigationBars = lightIcons ?: isColorLight(color)
        }
        // Android 5-10
        else {
            window.navigationBarColor = color

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = if (lightIcons ?: isColorLight(color)) {
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                } else {
                    0
                }
            }
        }*/
    }

    private fun isColorLight(color: Int): Boolean {
        return Color.luminance(color) > 0.5f
    }

    fun resetStatusBarToDefault(activity: FragmentActivity) {
        activity.window.statusBarColor = Color.TRANSPARENT
    }

    fun setDarkStatusBarIcons(activity: FragmentActivity) {
        val controller = WindowCompat.getInsetsController(activity.window, activity.window.decorView)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            controller.isAppearanceLightStatusBars = true
        }
    }

    fun setLightStatusBarIcons(activity: FragmentActivity) {
        val controller = WindowCompat.getInsetsController(activity.window, activity.window.decorView)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            controller.isAppearanceLightStatusBars = false
        }
    }


    fun resetNavigationBarToDefault(activity: FragmentActivity) {
        activity.window.navigationBarColor = Color.TRANSPARENT
    }

    fun setDarkNavigationBarIcons(activity: FragmentActivity) {
        val controller = WindowCompat.getInsetsController(activity.window, activity.window.decorView)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            controller.isAppearanceLightNavigationBars = true
        }
    }

    fun setLightNavigationBarIcons(activity: FragmentActivity) {
        val controller = WindowCompat.getInsetsController(activity.window, activity.window.decorView)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            controller.isAppearanceLightNavigationBars = false
        }
    }
}
