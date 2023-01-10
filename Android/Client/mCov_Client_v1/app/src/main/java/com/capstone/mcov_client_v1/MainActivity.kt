package com.capstone.mcov_client_v1

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
import com.capstone.mcov_client_v1.fragments.HomeFragment
import com.capstone.mcov_client_v1.fragments.NotifFragment
import com.capstone.mcov_client_v1.fragments.UkurFragment
import com.capstone.mcov_client_v1.viewModels.NotifactionVM
import com.capstone.mcov_client_v1.viewModels.PasienVM
import com.capstone.mcov_client_v1.viewModels.KondisiVM
import com.capstone.mcov_client_v1.viewModels.NaskesVM
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var fragmentManager: FragmentManager
    private val pasienViewModel: PasienVM by viewModels()
    private val kondisiViewModel: KondisiVM by viewModels()
    private val notificationViewModel: NotifactionVM by viewModels()
    private val naskesViewModel: NaskesVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inits()

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

//        retrieveData()
        findViewById<BottomNavigationView>(R.id.bottomNavBar).setOnItemSelectedListener {
            when(it.itemId){
                R.id.homeNav -> replaceFragments(HomeFragment())
                R.id.notifNav -> replaceFragments(NotifFragment())
                R.id.ukurNav -> replaceFragments(UkurFragment())
            }
            true
        }
    }

//    private fun retrieveData() {
//        val currentUserUID = auth.currentUser?.uid
//        getPasien(currentUserUID)
//        getSensor(currentUserUID)
//        getCondition(currentUserUID)
//        getNotification(currentUserUID)
//    }

//    private fun getNotification(currentUserUID: String?) {
//        firestoreDatabase.collection("notifications")
//            .whereEqualTo("UID", currentUserUID )
//            .get()
//            .addOnSuccessListener {
//                if (it.isEmpty) {
//                    Toast.makeText(this, "tidak ada data notifikasi", Toast.LENGTH_SHORT).show()
//                } else {
//                        notificationViewModel.setData(it.toObjects(Notification::class.java))
//                }
//            }
//    }

//    private fun getCondition(currentUserUID: String?) {
//        firestoreDatabase.collection("conditions")
//            .whereEqualTo("UID", currentUserUID )
//            .get()
//            .addOnSuccessListener {
//                if(it.isEmpty){
//                    Toast.makeText(this,"tidak ada data kondisi",Toast.LENGTH_SHORT).show()
//                }else{
//                    conditionViewModel.setData(it?.toObjects(Condition::class.java)?.component1()!!)
//                }
//            }
//    }

//    private fun getSensor(currentUserUID: String?) {
//        firestoreDatabase.collection("sensors")
//            .whereEqualTo("UID", currentUserUID )
//            .get()
//            .addOnSuccessListener {
//                if(it.isEmpty){
//                    Toast.makeText(this,"tidak ada data sensor",Toast.LENGTH_SHORT).show()
//                }else{
//                    sensorViewModel.setData(it?.toObjects(Sensor::class.java)?.component1()!!)
//                }
//            }
//    }


    private fun replaceFragments(fragment: Fragment) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment,fragment).commit()
    }

    private fun inits(){
        auth = FirebaseAuth.getInstance()
        val currentUserId = auth.currentUser?.uid.toString()
        pasienViewModel.refreshData(currentUserId)
        kondisiViewModel.updateData(currentUserId)
        notificationViewModel.updateData(currentUserId)
        naskesViewModel.updateData()
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener {
                if(pasienViewModel.getData()?.token != it.result) pasienViewModel.updateData(currentUserId,it.result)
            }

        fragmentManager = supportFragmentManager
        replaceFragments(HomeFragment())
    }

//    private fun getPasien(currentUserUID: String?) {
//        firestoreDatabase.collection("pasiens")
//            .whereEqualTo("UID", currentUserUID )
//            .get()
//            .addOnSuccessListener {
//                if(it.isEmpty){
//                    Toast.makeText(this,"User tidak terdaftar",Toast.LENGTH_SHORT).show()
//                }else{
//                    pasienViewModel.setData(it.documents[0].toObject(Pasien::class.java)!!)
////                    pasienViewModel.setData(it?.toObjects(Pasien::class.java)?.component1()!!)
//                }
//            }
//    }

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

//    override fun onDestroy() {
//        super.onDestroy()
//        auth.signOut()
//    }
}