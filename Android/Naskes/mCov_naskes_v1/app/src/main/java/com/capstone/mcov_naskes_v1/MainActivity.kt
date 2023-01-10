package com.capstone.mcov_naskes_v1

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.capstone.mcov_naskes_v1.fragments.HomeFragment
import com.capstone.mcov_naskes_v1.fragments.ListPasienFragment
import com.capstone.mcov_naskes_v1.fragments.ListNotificationFragment
import com.capstone.mcov_naskes_v1.viewModels.KondisiViewModel
import com.capstone.mcov_naskes_v1.viewModels.NaskesViewModel
import com.capstone.mcov_naskes_v1.viewModels.PasienViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var fragmentManager: FragmentManager
    private val naskesViewModel: NaskesViewModel by viewModels()
    private val pasienViewModel: PasienViewModel by viewModels()
    private val kondisiViewModel: KondisiViewModel by viewModels()

//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (ContextCompat.checkSelfPermission(this@MainActivity,
                android.Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
                    android.Manifest.permission.POST_NOTIFICATIONS)) {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
            } else {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }
        auth = FirebaseAuth.getInstance()
        inits()
        findViewById<BottomNavigationView>(R.id.bottomNavBar).setOnItemSelectedListener {
            when(it.itemId){
                R.id.homeNav -> replaceFragments(HomeFragment())
                R.id.notifNav -> replaceFragments(ListNotificationFragment())
                R.id.pasienNav -> replaceFragments(ListPasienFragment())
            }
            true
        }
    }

    private fun inits() {
        auth = FirebaseAuth.getInstance()
        fragmentManager = supportFragmentManager
        naskesViewModel.refreshData(auth.currentUser?.uid!!)
        pasienViewModel.refreshData()
        kondisiViewModel.refreshData()
        replaceFragments(HomeFragment())
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener {
                if(naskesViewModel.getData()?.token != it.result) naskesViewModel.updateData(auth.currentUser?.uid!!,it.result)
            }
    }

    private fun replaceFragments(fragment: Fragment) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment,fragment).commit()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this@MainActivity,
                            android.Manifest.permission.POST_NOTIFICATIONS) ==
                                PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.logoutButton){
            auth.signOut()
            val logoutIntent = Intent(this,LoginActivity::class.java)
            startActivity(logoutIntent)
        }
        return true
    }
}