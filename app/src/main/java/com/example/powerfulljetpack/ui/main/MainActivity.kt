package com.example.powerfulljetpack.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination

import com.example.powerfulljetpack.R
import com.example.powerfulljetpack.ui.BaseActivity
import com.example.powerfulljetpack.ui.auth.AuthActivity
import com.example.powerfulljetpack.ui.main.account.BaseAccountFragment
import com.example.powerfulljetpack.ui.main.account.ChangePasswordFragment
import com.example.powerfulljetpack.ui.main.account.UpdateAccountFragment
import com.example.powerfulljetpack.ui.main.blog.BaseBlogFragment
import com.example.powerfulljetpack.ui.main.blog.BlogFragment
import com.example.powerfulljetpack.ui.main.blog.UpdateBlogFragment
import com.example.powerfulljetpack.ui.main.blog.ViewBlogFragment
import com.example.powerfulljetpack.ui.main.create_blog.BaseCreateBlogFragment
import com.example.powerfulljetpack.ui.main.create_blog.CreateBlogFragment
import com.example.powerfulljetpack.util.BottomNavController
import com.example.powerfulljetpack.util.LastApiReponseResult
import com.example.powerfulljetpack.util.setUpNavigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity(),
    BottomNavController.NavGraphProvider,
    BottomNavController.OnNavigationGraphChanged,
    BottomNavController.OnNavigationReselectedListener {

    private lateinit var bottomNavigationView: BottomNavigationView

    private val bottomNavController by lazy(LazyThreadSafetyMode.NONE) {
        BottomNavController(
            this,
            R.id.main_nav_host_fragment,
            R.id.nav_blog,
            this,
            this
        )
    }

    override fun getNavGraphId(itemId: Int) = when (itemId) {
        R.id.nav_blog -> {
            R.navigation.nav_blog
        }
        R.id.nav_create_blog -> {
            R.navigation.nav_create_blog
        }
        R.id.nav_account -> {
            R.navigation.nav_account
        }
        else -> {
            R.navigation.nav_blog
        }
    }

    //when the graph changes
    override fun onGraphChange() {
        expandAppBar()
    }

    private fun expandAppBar() {

        cancellActiveJobs()
    }

    //cancell jobs when tab from one item to another in botton navigation
    private fun cancellActiveJobs() {

        val fragments = bottomNavController.fragmentManager
            .findFragmentById(bottomNavController.containerId)
            ?.childFragmentManager
            ?.fragments

        if (fragments != null) {
            for (fragment in fragments) {

                when (fragment) {
                    is BaseAccountFragment -> {
                        fragment.cancellActiveJobs()
                    }
                    is BaseBlogFragment -> {
                        fragment.cancellActiveJobs()
                    }

                    is BaseCreateBlogFragment -> {
                        fragment.cancellActiveJobs()
                    }
                }
            }

            displayProgressBar(false)
        }
    }

    override fun onReselectNavItem(
        navController: NavController,
        fragment: Fragment
    ) {
        when (fragment) {

            is ViewBlogFragment -> {
                navController.navigate(R.id.action_viewBlogFragment_to_home)
            }

            is UpdateBlogFragment -> {
                navController.navigate(R.id.action_updateBlogFragment_to_home)
            }

            is UpdateAccountFragment -> {
                navController.navigate(R.id.action_updateAccountFragment_to_accountFragment)
            }

            is ChangePasswordFragment -> {
                navController.navigate(R.id.action_changePasswordFragment_to_accountFragment)
            }

            else -> {
                // do nothing
            }
        }
    }

    override fun onBackPressed() = bottomNavController.onBackPressed()

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun hideSoftKeyboard() {

        if (currentFocus != null) {
            val inputMethodManager = getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            inputMethodManager
                .hideSoftInputFromWindow(currentFocus!!.windowToken, 0)


        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        setupActionBar()
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setUpNavigation(bottomNavController, this)
        if (savedInstanceState == null) {
            bottomNavController.onNavigationItemSelected()
        }

        subscribeObservers()
    }

    fun subscribeObservers() {
        sessionManager.cachedToken.observe(this, Observer { authToken ->
            if (authToken == null || authToken.account_pk == -1 || authToken.token == null) {
                navAuthActivity()
            }
        })
    }


    private fun setupActionBar() {
        setSupportActionBar(tool_bar)
    }

    private fun navAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }


    override fun displayProgressBar(bool: Boolean) {
        if (bool) {
            progress_bar.visibility = View.VISIBLE
        } else {
            progress_bar.visibility = View.GONE
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        TODO("Not yet implemented")
    }


}