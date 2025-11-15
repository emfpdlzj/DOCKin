package com.project.dockin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.dockin.data.pref.SessionStore
import com.project.dockin.data.pref.UserRole
import com.project.dockin.ui.fragment.ChatFragment
import com.project.dockin.ui.fragment.ManagerHomeFragment
import com.project.dockin.ui.fragment.NavigationFragment
import com.project.dockin.ui.fragment.SafetyAdminFragment
import com.project.dockin.ui.fragment.SafetyLearnFragment
import com.project.dockin.ui.fragment.WorkLogFragment
import com.project.dockin.ui.fragment.WorkerHomeFragment
import com.project.dockin.ui.settings.SettingsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var sessionStore: SessionStore
    private var isManager: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionStore = SessionStore(this)
        isManager = (sessionStore.role == UserRole.MANAGER)

        bottomNav = findViewById(R.id.bottomNav)

        // 권한에 따라 다른 메뉴 로드
        bottomNav.menu.clear()
        bottomNav.inflateMenu(
            if (isManager) R.menu.menu_bottom_manager
            else R.menu.menu_bottom_worker
        )

        // 처음 진입 화면
        if (savedInstanceState == null) {
            val startFragment: Fragment =
                if (isManager) ManagerHomeFragment() else WorkerHomeFragment()
            replaceFragment(startFragment)
        }

        // 탭 클릭 시 프래그먼트 교체
        bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment? = when (item.itemId) {
                R.id.menu_tab_home ->
                    if (isManager) ManagerHomeFragment() else WorkerHomeFragment()

                R.id.menu_tab_chat ->
                    ChatFragment()

                R.id.menu_tab_worklog ->
                    WorkLogFragment()

                R.id.menu_tab_nav ->
                    NavigationFragment()

                R.id.menu_tab_safety ->
                    if (isManager) SafetyAdminFragment()
                    else SafetyLearnFragment()

                else -> null
            }

            if (fragment != null) {
                replaceFragment(fragment)
                true
            } else {
                false
            }
        }

        // 오른쪽 위 환경설정 버튼
        findViewById<android.view.View>(R.id.btnSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}