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
import android.util.Log
import com.newrelic.agent.android.NewRelic
import com.newrelic.agent.android.logging.AgentLog

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        if (!NewRelic.isStarted()) {
            NewRelic.withApplicationToken(BuildConfig.NEW_RELIC_TOKEN)
                .withLogLevel(AgentLog.DEBUG)
                .start(this.applicationContext)
            Log.d("NewRelic", "Started — token empty: ${BuildConfig.NEW_RELIC_TOKEN.isEmpty()}")
        }

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
