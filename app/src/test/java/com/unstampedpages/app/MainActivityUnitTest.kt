package com.unstampedpages.app

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * JVM unit tests for [MainActivity.onCreate] using Robolectric.
 *
 * These tests provide line-level coverage of every statement executed during
 * [MainActivity.onCreate]:
 *   1. NewRelic.withApplicationToken(...).start(applicationContext)
 *   2. super.onCreate(savedInstanceState)
 *   3. CountryGeometryData.initializeAsync(this)
 *   4. enableEdgeToEdge()
 *   5. setContent { UnstampedPagesTheme { Surface { UnstampedPagesApp() } } }
 *
 * Robolectric simulates the Android framework on the JVM, so these tests run
 * without an emulator and are included in the JaCoCo unit-test coverage report.
 *
 * End-to-end UI behaviour on a real device/emulator is covered by
 * [MainActivityTest] in src/androidTest.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MainActivityUnitTest {

    private var scenario: ActivityScenario<MainActivity>? = null

    @After
    fun tearDown() {
        scenario?.close()
    }

    // -------------------------------------------------------------------------
    // Fresh launch — covers onCreate(savedInstanceState = null)
    // -------------------------------------------------------------------------

    /**
     * Launching [MainActivity] with no saved state must complete without throwing.
     * A RESUMED lifecycle state confirms that [onCreate], [onStart], and [onResume]
     * all executed successfully, including every statement inside [onCreate].
     */
    @Test
    fun onCreateWithNullBundleActivityReachesResumedState() {
        scenario = ActivityScenario.launch(MainActivity::class.java)

        assertEquals(Lifecycle.State.RESUMED, scenario!!.state)
    }

    /**
     * The activity window must not be null after [onCreate], confirming that
     * [enableEdgeToEdge] and [setContent] ran against a properly initialised window.
     */
    @Test
    fun onCreateWithNullBundleWindowIsNotNull() {
        scenario = ActivityScenario.launch(MainActivity::class.java)

        scenario!!.onActivity { activity ->
            assertNotNull(activity.window)
        }
    }

    // -------------------------------------------------------------------------
    // Recreation — covers onCreate(savedInstanceState != null)
    // -------------------------------------------------------------------------

    /**
     * [ActivityScenario.recreate] triggers onSaveInstanceState → onDestroy →
     * onCreate(bundle), exercising [onCreate] a second time with a non-null
     * [savedInstanceState].  The activity must reach RESUMED state again, proving
     * that re-invoking New Relic, CountryGeometryData.initializeAsync, and setContent
     * on recreation does not throw.
     */
    @Test
    fun onCreateAfterRecreationActivityReachesResumedState() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario!!.recreate()

        assertEquals(Lifecycle.State.RESUMED, scenario!!.state)
    }

    @Test
    fun onCreateAfterRecreationWindowIsNotNull() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario!!.recreate()

        scenario!!.onActivity { activity ->
            assertNotNull(activity.window)
        }
    }

    // -------------------------------------------------------------------------
    // CountryGeometryData guard — initializeAsync no-op on second call
    // -------------------------------------------------------------------------

    /**
     * [CountryGeometryData.initializeAsync] is guarded by [CountryGeometryData.isLoaded]:
     * once loading has started a second call is a no-op.  Launching and immediately
     * recreating the activity must not throw even if the guard fires on the second
     * [onCreate] invocation.
     */
    @Test
    fun onCreateCalledTwiceViaRecreationDoesNotThrow() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        assertEquals(Lifecycle.State.RESUMED, scenario!!.state)

        scenario!!.recreate()
        assertEquals(Lifecycle.State.RESUMED, scenario!!.state)
    }
}
