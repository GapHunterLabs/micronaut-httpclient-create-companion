package dev.gaphunter.micronauthttpclientcreatecompanion.detect

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class JavaClientCreateFinderTest : BasePlatformTestCase() {

    fun `test HttpClient create is flagged`() {
        val file = myFixture.configureByText(
            "OrderService.java",
            """
            class OrderService {
                void fetch() {
                    HttpClient client = HttpClient.create(url);
                }
            }
            """.trimIndent(),
        )
        assertEquals(1, JavaClientCreateFinder.findAll(file).size)
    }

    fun `test an unrelated create call on a different class is not flagged`() {
        val file = myFixture.configureByText(
            "OrderService.java",
            """
            class OrderService {
                void fetch() {
                    Order order = Order.create(id);
                }
            }
            """.trimIndent(),
        )
        assertTrue(JavaClientCreateFinder.findAll(file).isEmpty())
    }

    fun `test a different HttpClient method is not flagged`() {
        val file = myFixture.configureByText(
            "OrderService.java",
            """
            class OrderService {
                void fetch() {
                    HttpClient.builder(url);
                }
            }
            """.trimIndent(),
        )
        assertTrue(JavaClientCreateFinder.findAll(file).isEmpty())
    }
}
