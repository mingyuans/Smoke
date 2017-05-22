import com.mingyuans.smoke.kotlin.Smoke
import org.junit.Test

/**
 * Created by yanxq on 17/5/18.
 */
class SmokeTest {
    @Test
    fun testSmoke() {
        Smoke.install("Smoke")

        Smoke.verbose(message = "Hello,Kotlin")

        var worldArray = arrayOf("Hello","Kotlin")
        Smoke.verbose(message = worldArray)

        Smoke.error(error = Throwable(),message = "There is a %s.",args = arrayOf("error"))

        Smoke.error(error = Throwable())
    }
}