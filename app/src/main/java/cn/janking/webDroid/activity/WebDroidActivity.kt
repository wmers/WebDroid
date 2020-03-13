package cn.janking.webDroid.activity

import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import cn.janking.webDroid.R
import cn.janking.webDroid.model.Config
import cn.janking.webDroid.widget.WebDroidItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_webdroid.*

class WebDroidActivity : BaseActivity() {
    /**
     * 缓存页面
     */
    private val pageMap: MutableMap<Int, WebDroidItem> = HashMap()

    /**
     * 滑动页面的适配器
     */
    private val mPagerAdapter: PagerAdapter = object : PagerAdapter() {
        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun getCount(): Int {
            return Config.instance.tabCount
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            if (pageMap[position] == null) {
                pageMap[position] = WebDroidItem(
                    this@WebDroidActivity,
                    viewPager,
                    Config.instance.tabUrls[position]
                )
            }
            val view = pageMap[position]!!.agentWeb.webCreator.webParentLayout
            //如果已经被回收了，需要手动添加进去
            if (viewPager.indexOfChild(view) == -1) {
                viewPager.addView(
                    view,
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
            }
            return view
        }

        override fun destroyItem(
            container: ViewGroup,
            position: Int,
            `object`: Any
        ) {
            container.removeView(`object` as View)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return Config.instance.tabTitles[position]
        }
    }

    /**
     * 底部的监听器
     */
    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            viewPager.currentItem = item.itemId
            return@OnNavigationItemSelectedListener true
        }

    /**
     * 适用于底部导航栏的对viewPager的监听器
     */
    private val pageChangeListener: OnPageChangeListener = object : OnPageChangeListener {

        override fun onPageSelected(position: Int) {
            if (bottomNavigation.selectedItemId != position) {
                bottomNavigation.selectedItemId = position
            }
        }

        override fun onPageScrollStateChanged(state: Int) {}
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webdroid)
        initToolBar()
        initViews()
    }

    private fun initToolBar() {
        toolbar.title = Config.instance.appName
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        drawerNavigation.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_settings -> {

                }
                R.id.nav_about -> {

                }
            }
            drawer.closeDrawer(GravityCompat.START)
            true
        }
        drawerNavigation.getHeaderView(0).findViewById<LinearLayout>(R.id.navHeader)
            .setOnClickListener {

            }
    }

    private fun initViews() {
        viewPager.adapter = mPagerAdapter
        if (Config.instance.tabCount <= 1) {
            topNavigation.visibility = View.GONE
            bottomNavigation.visibility = View.GONE
        } else {
            //如果是设置顶部tab
            if (Config.instance.tabStyle == 0) {
                topNavigation.visibility = View.VISIBLE
                bottomNavigation.visibility = View.GONE
                topNavigation.setupWithViewPager(viewPager)
            } else {
                //设置底部tab @todo 添加底部tab icon
                topNavigation.visibility = View.GONE
                bottomNavigation.visibility = View.VISIBLE
                viewPager.addOnPageChangeListener(pageChangeListener)
                for (i in 0 until Config.instance.tabCount) {
                    bottomNavigation.menu.add(Menu.NONE, i, i, Config.instance.tabTitles[i])
                }
                bottomNavigation.setOnNavigationItemSelectedListener(
                    mOnNavigationItemSelectedListener
                )
            }
        }
    }

    /**
     * 监听返回键
     */
    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else if (pageMap[viewPager.currentItem]!!.handleKeyDown(KeyEvent.KEYCODE_BACK, null)) {
        } else {
            super.onBackPressed()
        }
    }

}