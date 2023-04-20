package com.example.mediaplayer.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import com.example.mediaplayer.R
import com.example.mediaplayer.eventcontroller.PermissionsHandler
import com.example.mediaplayer.fragments.playerfragments.MenuFragment
import com.example.mediaplayer.interfaces.ActivityNavigator
import com.example.mediaplayer.interfaces.navigation.FragmentBackPressed

class PermissionsFragment : BaseFragment(R.layout.fragment_permissions), FragmentBackPressed {
    private lateinit var tryPermission: Button
    private var activityNavigator: ActivityNavigator? = null
    private var counter = 0
    private val permissionsHandler = PermissionsHandler()
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            checkAndNavigate()
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityNavigator = context as ActivityNavigator
    }

    override fun onDetach() {
        super.onDetach()
        activityNavigator = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tryPermission = view.findViewById(R.id.b_try_permission)
        tryPermission.setOnClickListener {
            when (counter) {
                0 -> {
                    permissionsHandler.requestPermissions(requireContext())
                    counter++
                }

                1 -> {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + context?.packageName)
                    )
                    resultLauncher.launch(intent)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkAndNavigate()
    }

    private fun checkAndNavigate() {
        if (permissionsHandler.isReadExternalStorageGranted(requireContext())) {
            activityNavigator?.navigate(MenuFragment())
        }
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}