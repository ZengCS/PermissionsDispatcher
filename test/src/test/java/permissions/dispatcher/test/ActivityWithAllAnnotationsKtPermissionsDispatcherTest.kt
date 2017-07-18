package permissions.dispatcher.test


import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.PermissionChecker
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers.any
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import permissions.dispatcher.PermissionRequest


@Suppress("IllegalIdentifier")
@RunWith(PowerMockRunner::class)
@PrepareForTest(ActivityCompat::class, PermissionChecker::class)
class ActivityWithAllAnnotationsKtPermissionsDispatcherTest {

    private lateinit var activity: ActivityWithAllAnnotationsKt

    companion object {
        private var requestCode = 0
        lateinit private var requestPermissions: Array<String>

        @BeforeClass
        @JvmStatic
        fun setUpForClass() {
            requestCode = getRequestCameraConstant(ActivityWithAllAnnotationsKtPermissionsDispatcher::class.java)
            requestPermissions = getPermissionRequestConstant(ActivityWithAllAnnotationsKtPermissionsDispatcher::class.java)
        }
    }

    @Before
    fun setUp() {
        activity = Mockito.mock(ActivityWithAllAnnotationsKt::class.java)
        PowerMockito.mockStatic(ActivityCompat::class.java)
        PowerMockito.mockStatic(PermissionChecker::class.java)
    }

    @Test
    fun `already granted call the method`() {
        mockCheckSelfPermission(true)

        ActivityWithAllAnnotationsKtPermissionsDispatcher.showCameraWithCheck(activity)

        Mockito.verify(activity, Mockito.times(1)).showCamera()
    }

    @Test
    fun `not granted does not call the method`() {
        mockCheckSelfPermission(false)
        mockShouldShowRequestPermissionRationaleActivity(true)

        ActivityWithAllAnnotationsKtPermissionsDispatcher.showCameraWithCheck(activity)

        Mockito.verify(activity, Mockito.times(0)).showCamera()
    }

    @Test
    fun `not granted permission and show rationale is true then call the rationale method`() {
        mockCheckSelfPermission(false)
        mockShouldShowRequestPermissionRationaleActivity(true)

        ActivityWithAllAnnotationsKtPermissionsDispatcher.showCameraWithCheck(activity)

        Mockito.verify(activity, Mockito.times(1)).showRationaleForCamera(any(PermissionRequest::class.java))
    }

    @Test
    fun `not granted permission and show rationale is false then does not call the rationale method`() {
        mockCheckSelfPermission(false)
        mockShouldShowRequestPermissionRationaleActivity(false)

        ActivityWithAllAnnotationsKtPermissionsDispatcher.showCameraWithCheck(activity)

        Mockito.verify(activity, Mockito.times(0)).showRationaleForCamera(any(PermissionRequest::class.java))
    }

    @Test
    fun `the method is called if verifyPermission is true`() {
        ActivityWithAllAnnotationsKtPermissionsDispatcher.onRequestPermissionsResult(activity, requestCode, intArrayOf(PackageManager.PERMISSION_GRANTED))

        Mockito.verify(activity, Mockito.times(1)).showCamera()
    }

    @Test
    fun `the method is not called if verifyPermission is false`() {
        ActivityWithAllAnnotationsKtPermissionsDispatcher.onRequestPermissionsResult(activity, requestCode, intArrayOf(PackageManager.PERMISSION_DENIED))

        Mockito.verify(activity, Mockito.times(0)).showCamera()
    }

    @Test
    fun `show never ask method is call if verifyPermission is false and shouldShowRequestPermissionRationale is false`() {
        mockShouldShowRequestPermissionRationaleActivity(false)

        ActivityWithAllAnnotationsKtPermissionsDispatcher.onRequestPermissionsResult(activity, requestCode, intArrayOf(PackageManager.PERMISSION_DENIED))

        Mockito.verify(activity, Mockito.times(1)).showNeverAskForCamera()
    }

    @Test
    fun `show deny method is call if verifyPermission is false and shouldShowRequestPermissionRationale is true`() {
        mockShouldShowRequestPermissionRationaleActivity(true)

        ActivityWithAllAnnotationsKtPermissionsDispatcher.onRequestPermissionsResult(activity, requestCode, intArrayOf(PackageManager.PERMISSION_DENIED))

        Mockito.verify(activity, Mockito.times(1)).showDeniedForCamera()
    }

    @Test
    fun `no the method call if request code is not related to the library`() {
        ActivityWithAllAnnotationsKtPermissionsDispatcher.onRequestPermissionsResult(activity, requestCode + 1000, null)

        Mockito.verify(activity, Mockito.times(0)).showCamera()
    }

    @Test
    fun `no denied method call if request code is not related to the library`() {
        ActivityWithAllAnnotationsKtPermissionsDispatcher.onRequestPermissionsResult(activity, requestCode + 1000, null)

        Mockito.verify(activity, Mockito.times(0)).showDeniedForCamera()
    }

    @Test
    fun `no never ask method call if request code is not related to the library`() {
        ActivityWithAllAnnotationsKtPermissionsDispatcher.onRequestPermissionsResult(activity, requestCode + 1000, null)

        Mockito.verify(activity, Mockito.times(0)).showNeverAskForCamera()
    }
}