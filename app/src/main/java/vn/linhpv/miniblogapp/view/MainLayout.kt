package vn.linhpv.miniblogapp.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import vn.linhpv.miniblogapp.databinding.MainLayoutBinding
import vn.linhpv.miniblogapp.view.fragment.ExploreFragment
import vn.linhpv.miniblogapp.view.fragment.FollowingFragment
import vn.linhpv.miniblogapp.view.fragment.SearchFragment

@AndroidEntryPoint
class MainLayout : AppCompatActivity() {

    lateinit var binding: MainLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val exploreFragment = ExploreFragment()
        val followingFragment = FollowingFragment()
        val searchFragment = SearchFragment()
        changeFragment(exploreFragment)

        val bottomNavBar = binding.bottomNavBar
        bottomNavBar.onSelected = {
            when (it) {
                0 -> changeFragment(exploreFragment)
                1 -> changeFragment(searchFragment)
                2 -> changeFragment(followingFragment)
                3 -> {
                    bottomNavBar.binding.navDiscover.isSelected = true
                    bottomNavBar.binding.navProfile.isSelected = false

                    val intent = Intent(this, MyProfilePage::class.java)
                    startActivity(intent)
                }
            }
        }

        binding.createPost.setOnClickListener {
            val intent = Intent(this, PostCreatePage::class.java)
            startActivity(intent)
        }
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentPlaceholder.id, fragment)
            .commit()
    }

}