package com.unstampedpages.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.unstampedpages.app.data.repository.CountryGeometryData
import com.unstampedpages.app.ui.theme.UnstampedPagesTheme
import com.newrelic.agent.android.NewRelic
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        NewRelic.withApplicationToken(
            BuildConfig.NEW_RELIC_TOKEN
        ).start(this.applicationContext)

        super.onCreate(savedInstanceState)

        // Kick off async load of country geometry data (high-resolution 10m dataset)
        CountryGeometryData.initializeAsync(this)

        enableEdgeToEdge()
        setContent {
            UnstampedPagesTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    UnstampedPagesApp()
                }
            }
        }
    }
}
