package org.akhsaul.dicodingevent.ui

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import org.akhsaul.core.worker.DailyReminderWorker
import org.akhsaul.dicodingevent.R
import org.akhsaul.dicodingevent.setAppDarkMode
import org.akhsaul.dicodingevent.util.SettingPreferences
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {
    @Inject
    lateinit var settingPreferences: SettingPreferences
    private var switchReminder: SwitchPreferenceCompat? = null
    private lateinit var workManager: WorkManager

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            switchReminder?.isChecked = true
            Toast.makeText(
                requireContext(),
                getString(R.string.txt_permission_granted),
                Toast.LENGTH_SHORT
            ).show()
            setWork()
        } else {
            if (shouldShowRequestPermissionRationale(POST_NOTIFICATIONS)) {
                showPermissionRationaleDialog()
            } else {
                showPermissionDeniedForeverDialog()
            }
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        workManager = WorkManager.getInstance(requireContext())

        preferenceManager.preferenceDataStore = settingPreferences
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val switchMode = findPreference<SwitchPreferenceCompat>(getString(R.string.key_dark_mode))
        switchMode?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                setAppDarkMode(newValue as Boolean)
                return@OnPreferenceChangeListener true
            }

        switchReminder = findPreference(getString(R.string.key_daily_reminder))
        switchReminder?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                val newBoolean = newValue as Boolean
                if (newBoolean) {
                    if (Build.VERSION.SDK_INT >= 33) {
                        if (isNotificationGranted().not()) {
                            requestPermissionLauncher.launch(POST_NOTIFICATIONS)
                            return@OnPreferenceChangeListener false
                        }
                    }
                    setWork()
                } else {
                    workManager.cancelUniqueWork(getString(R.string.key_daily_reminder))
                }
                return@OnPreferenceChangeListener true
            }

    }

    private fun setWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val periodicWork = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        val key = getString(R.string.key_daily_reminder)
        workManager.enqueueUniquePeriodicWork(
            key,
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicWork
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun isNotificationGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showPermissionRationaleDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.title_notif_req))
            .setMessage(getString(R.string.msg_notif_req))
            .setPositiveButton(getString(R.string.btn_grant)) { _, _ ->
                requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
            .setNegativeButton(getString(R.string.btn_cancel)) { dialog, _ ->
                dialog.dismiss()
                switchReminder?.isChecked = false
            }
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showPermissionDeniedForeverDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.title_notif_denied))
            .setMessage(getString(R.string.msg_notif_denied))
            .setPositiveButton(getString(R.string.btn_go_setting)) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
                switchReminder?.isChecked = false
            }
            .setNegativeButton(getString(R.string.btn_cancel)) { dialog, _ ->
                dialog.dismiss()
                switchReminder?.isChecked = false
            }
            .show()
    }
}