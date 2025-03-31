package vn.linhpv.miniblogapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import vn.linhpv.miniblogapp.R
import vn.linhpv.miniblogapp.databinding.MainLayoutBinding
import vn.linhpv.miniblogapp.view.fragment.ListPostFragment
import vn.linhpv.miniblogapp.view.fragment.ListUserFragment

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var listUserFragment: ListUserFragment
    private lateinit var listPostFragment: ListPostFragment
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = MainLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listUserFragment = ListUserFragment()
        listPostFragment = ListPostFragment()

        if (savedInstanceState == null) {
            setCurrentFragment(listUserFragment)
        } else {
            currentFragment = supportFragmentManager.findFragmentByTag(ListUserFragment::class.java.simpleName)
            if (currentFragment == null) {
                setCurrentFragment(listUserFragment)
            }
        }

        val bottomNavigationView = binding.bottomNavBar
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.userMenuItem -> setCurrentFragment(listUserFragment)
                R.id.postMenuItem -> setCurrentFragment(listPostFragment)
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        if (currentFragment != fragment) {
            val transaction = supportFragmentManager.beginTransaction()
            if (currentFragment != null) {
                transaction.hide(currentFragment!!)
            }
            if (fragment.isAdded) {
                transaction.show(fragment)
            } else {
                transaction.add(R.id.fragmentPlaceholder, fragment, fragment::class.java.simpleName)
            }
            transaction.commit()
            currentFragment = fragment
        }
    }
}
