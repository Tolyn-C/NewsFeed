package com.tolyn.newsfeed

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.tolyn.database.News_info
import com.tolyn.newsfeed.data.NewsInfo
import com.tolyn.newsfeed.data.UserPreferencesRepository
import com.tolyn.newsfeed.data.dateCalendarAdapter
import com.tolyn.newsfeed.databinding.ActivityMainBinding
import com.tolyn.newsfeed.service.NewsFeedProviderService
import com.tolyn.newsfeed.service.NewsFeedService
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

class MainActivity : AppCompatActivity(), ServiceConnection {

    private lateinit var binding: ActivityMainBinding
    private var serviceBound = false

    override fun onStart() {
        super.onStart()
        Intent(this, NewsFeedProviderService::class.java).also { intent ->
            startService(intent)
            bindService(intent, this, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (serviceBound) {
            unbindService(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_dashboard)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
        val binder = service as NewsFeedProviderService.LocalBinder

        val driver = AndroidSqliteDriver(
            Database.Schema,
            applicationContext,
            "news.db"
        )
        Database.Schema.create(driver)
        val database = Database(
            driver = driver, news_infoAdapter = News_info.Adapter(
                timeAdapter = dateCalendarAdapter
            )
        )
        val databaseDiModule = DI.Module("Database") {
            bind<Database>() with instance(database)
        }
        val protoDiModule = DI.Module("proto") {
            val userPreferencesRepository = UserPreferencesRepository.getInstance(this@MainActivity)
            bind<UserPreferencesRepository>() with singleton {
                userPreferencesRepository
            }
        }
        val providerService = DI.Module("NewsFeedProviderService") {
            bind<NewsFeedService>() with singleton {
                binder.getNewsFeedService().provide()
            }
        }
        App.setUpDi {
            listOf(databaseDiModule, protoDiModule, providerService, newsInfo)
        }
        serviceBound = true

    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        serviceBound = false
    }

    private val newsInfo = DI.Module("newsInfo") {
        bind<NewsInfo.NewsList>() with singleton {
            loadNewsJSONFromAsset()?.let {
                NewsInfo(it).getNewsList()
            } ?: NewsInfo("").getNewsList()
        }
    }

    private fun loadNewsJSONFromAsset(): String? {
        var json: String? = null
        json = try {
            val `is`: InputStream = this.assets.open("news.json")
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer, Charset.defaultCharset())
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }
}