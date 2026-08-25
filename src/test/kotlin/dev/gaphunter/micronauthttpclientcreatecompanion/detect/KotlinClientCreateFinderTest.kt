package dev.gaphunter.micronauthttpclientcreatecompanion.detect

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class KotlinClientCreateFinderTest : BasePlatformTestCase() {

    fun `test HttpClient create is flagged`() {
        val file = myFixture.configureByText(
            "OrderService.kt",
            """
            class OrderService {
                fun fetch() {
                    val client = HttpClient.create(url)
                }
            }
            """.trimIndent(),
        )
        assertEquals(1, KotlinClientCreateFinder.findAll(file).size)
    }

    fun `test an unrelated create call on a different class is not flagged`() {
        val file = myFixture.configureByText(
            "OrderService.kt",
            """
            class OrderService {
                fun fetch() {
                    val order = Order.create(id)
                }
            }
            """.trimIndent(),
        )
        assertTrue(KotlinClientCreateFinder.findAll(file).isEmpty())
    }
}
