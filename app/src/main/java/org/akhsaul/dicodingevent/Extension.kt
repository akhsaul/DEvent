package org.akhsaul.dicodingevent

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.akhsaul.core.toLocalDateTime
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.floor
import kotlin.time.Duration.Companion.seconds

fun Context?.showErrorWithToast(
    scope: LifecycleCoroutineScope,
    onShow: () -> Unit,
    onHidden: () -> Unit
) {
    if (this == null) return

    val toast = Toast.makeText(
        this,
        getString(R.string.toast_txt_no_internet),
        Toast.LENGTH_SHORT
    )

    if (Build.VERSION.SDK_INT >= 30) {
        toast.callBack(onShow, onHidden)
    } else {
        toast.show()
        onShow()
        scope.launch(Dispatchers.IO) {
            delay(2.seconds)
            onHidden()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
fun Toast.callBack(onShow: () -> Unit, onHidden: () -> Unit) {
    addCallback(object : Toast.Callback() {
        override fun onToastShown() {
            onShow()
        }

        override fun onToastHidden() {
            onHidden()
            removeCallback(this)
        }
    })
    show()
}

fun convertTime(time: String): ZonedDateTime {
    val wibZonedDateTime = ZonedDateTime.of(time.toLocalDateTime(), ZoneId.of("Asia/Jakarta"))
    return wibZonedDateTime.withZoneSameInstant(ZoneId.systemDefault())
}

fun Context.remainingTime(time: ZonedDateTime): String {
    val currentZonedDateTime = ZonedDateTime.now(ZoneId.systemDefault())
    val duration = Duration.between(currentZonedDateTime, time)
    val days = duration.toDays()
    val hours = duration.toHours() % 24
    val minutes = duration.toMinutes() % 60
    return when {
        duration.isNegative -> {
            getString(R.string.txt_finished)
        }

        days > 0 -> {
            getString(R.string.txt_days, days)
        }

        hours > 0 -> {
            getString(R.string.txt_hours, hours)
        }

        minutes > 0 -> {
            getString(R.string.txt_minutes, minutes)
        }

        else -> {
            getString(R.string.txt_minutes, 1)
        }
    }
}

fun Fragment.setupTopMenu(@IdRes navigationToSettings: Int, @IdRes navigationToAbout: Int) {
    requireActivity().addMenuProvider(object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.top_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.settingMenu -> {
                    findNavController().navigate(navigationToSettings)
                    true
                }

                R.id.about_page -> {
                    findNavController().navigate(navigationToAbout)
                    true
                }

                else -> false
            }
        }
    }, viewLifecycleOwner, Lifecycle.State.RESUMED)
}

fun isSystemInDarkMode(resources: Resources): Boolean {
    return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> true
        Configuration.UI_MODE_NIGHT_NO -> false
        else -> false
    }
}

fun setAppDarkMode(isDark: Boolean) {
    val compatDelegate = if (isDark) {
        AppCompatDelegate.MODE_NIGHT_YES
    } else {
        AppCompatDelegate.MODE_NIGHT_NO
    }
    AppCompatDelegate.setDefaultNightMode(compatDelegate)
}

fun roundNumber(number: Double): Int {
    val decimalPart = number - floor(number)
    return if (decimalPart >= 0.6) {
        floor(number) + 1
    } else {
        floor(number)
    }.toInt()
}

fun DisplayMetrics?.pxToDp(px: Int): Int {
    if (px == 0 || this == null) return 0
    return px / (densityDpi / DisplayMetrics.DENSITY_DEFAULT)
}

fun RecyclerView.LayoutManager?.adjustStaggeredGridSpanCount(
    widthParent: Int,
    heightParent: Int,
    widthContent: Double,
    displayMetrics: DisplayMetrics?,
) {
    val widthDp = displayMetrics.pxToDp(widthParent)
    val heightDp = displayMetrics.pxToDp(heightParent)
    if (widthDp != 0 && heightDp != 0) {
        if (this is StaggeredGridLayoutManager) {
            this.spanCount = roundNumber(widthDp.toDouble().div(widthContent))
        }
    }
}