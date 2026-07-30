package com.example.netlibrary

import com.example.netlibrary.data.remote.PBApiServis
import com.example.netlibrary.domain.model.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class PBApiRealTest {

    private val api = PBApiServis.instance
    private var authToken: String? = null
    private var userId: String? = null
    private val testEmail = "test_${System.currentTimeMillis()}@test.com"
    private val testPassword = "12345678"

    @Before
    fun setup() = runBlocking {
        // 1. Сначала регистрируем пользователя
        val registerData = RequestRegister(
            email = testEmail,
            password = testPassword,
            passwordConfirm = testPassword
        )

        try {
            val registerResult = api.postUser(registerData)
            println("User registered: ${registerResult.id}")
        } catch (e: Exception) {
            println("Registration failed (maybe user exists): ${e.message}")
            // Если пользователь уже существует, пробуем войти
        }

        // 2. Авторизуемся
        val authData = RequestAuth(
            identity = testEmail,
            password = testPassword
        )

        try {
            val result = api.authUser(authData)
            authToken = result.token
            userId = result.record.id
            PBApiServis.setToken(authToken)
            println("Auth success: token=$authToken, userId=$userId")
        } catch (e: Exception) {
            println("Auth failed: ${e.message}")
            throw e
        }
    }

    // ============ USERS ============
    @Test
    fun testPostUser() = runBlocking {
        val data = RequestRegister(
            email = "test_${System.currentTimeMillis()}@test.com",
            password = "12345678",
            passwordConfirm = "12345678"
        )
        val result = api.postUser(data)

        assertNotNull(result)
        assertNotNull(result.id)
        assertNotNull(result.created)
        assertTrue(result.emailVisibility)
        println("User created: id=${result.id}")
    }

    @Test
    fun testGetUser() = runBlocking {
        val id = userId ?: return@runBlocking
        val result = api.getUser(id)

        assertNotNull(result)
        assertNotNull(result.id)
        assertNotNull(result.firstname)
        assertTrue(result.emailVisibility)
        println("User found: ${result.firstname} ${result.lastname}")
    }

    @Test
    fun testPatchUser() = runBlocking {
        val id = userId ?: return@runBlocking
        val newName = "Updated_${System.currentTimeMillis()}"
        val data = RequestUser(
            email = testEmail,
            emailVisibility = true,
            firstname = newName,
            lastname = "Test",
            secondname = "User",
            datebirthday = "1990-01-01",
            gender = "male"
        )
        val result = api.patchUser(id, data)

        assertNotNull(result)
        assertEquals(newName, result.firstname)
        println("User updated: firstname=$newName")
    }

    // ============ AUTH ============
    @Test
    fun testAuthUser() = runBlocking {
        val data = RequestAuth(testEmail, testPassword)
        val result = api.authUser(data)

        assertNotNull(result)
        assertNotNull(result.token)
        assertNotNull(result.record)
        println("Auth success: token=${result.token.take(20)}...")
    }

    @Test
    fun testGetToken() = runBlocking {
        val result = api.getToken()

        assertNotNull(result)
        assertTrue(result.item.isNotEmpty())
        result.item.forEach { auth ->
            assertNotNull(auth.id)
            assertNotNull(auth.fingerprint)
            assertNotNull(auth.recordRef)
        }
        println("Tokens count: ${result.item.size}")
    }

    @Test
    fun testDeleteToken() = runBlocking {
        val tokens = api.getToken()
        if (tokens.item.isNotEmpty()) {
            val tokenId = tokens.item.first().id
            api.deleteToken(tokenId)
            val afterDelete = api.getToken()
            assertTrue(afterDelete.item.none { it.id == tokenId })
            println("Token deleted: $tokenId")
        } else {
            println("No tokens to delete")
        }
    }

    // ============ NEWS ============
    @Test
    fun testGetNews() = runBlocking {
        val result = api.getNews()

        assertNotNull(result)
        assertTrue(result.items.isNotEmpty())
        assertTrue(result.totalItems > 0)
        result.items.forEach { news ->
            assertNotNull(news.id)
            assertNotNull(news.newsImage)
            assertNotNull(news.created)
        }
        println("News count: ${result.items.size}")
    }

    // ============ PRODUCTS ============
    @Test
    fun testGetProducts() = runBlocking {
        val result = api.getProducts()

        assertNotNull(result)
        assertTrue(result.items.isNotEmpty())
        assertTrue(result.totalItems > 0)
        result.items.forEach { product ->
            assertNotNull(product.id)
            assertNotNull(product.title)
            assertTrue(product.price >= 0)
            assertNotNull(product.type)
            assertNotNull(product.typeCloses)
        }
        println("Products count: ${result.items.size}")
    }

    @Test
    fun testGetProductsWithFilter() = runBlocking {
        val result = api.getProducts("type='clothes'")

        assertNotNull(result)
        result.items.forEach { product ->
            assertEquals("clothes", product.type)
        }
        println("Filtered products: ${result.items.size}")
    }

    @Test
    fun testGetProduct() = runBlocking {
        val products = api.getProducts()
        if (products.items.isNotEmpty()) {
            val productId = products.items.first().id
            val result = api.getProduct(productId)

            assertNotNull(result)
            assertEquals(productId, result.id)
            assertNotNull(result.title)
            assertNotNull(result.description)
            assertTrue(result.price >= 0)
            assertNotNull(result.type)
            println("Product: ${result.title}, price=${result.price}")
        } else {
            println("No products to test")
        }
    }

    // ============ PROJECTS ============
    @Test
    fun testGetProject() = runBlocking {
        val result = api.getProject()

        assertNotNull(result)
        assertTrue(result.items.isNotEmpty())
        result.items.forEach { project ->
            assertNotNull(project.id)
            assertNotNull(project.title)
            assertNotNull(project.user_id)
            assertNotNull(project.image)
        }
        println("Projects count: ${result.items.size}")
    }

    @Test
    fun testPostProject() = runBlocking {
        val token = authToken ?: return@runBlocking
        val timestamp = System.currentTimeMillis()
        val data = RequestProject(
            title = "Test Project $timestamp",
            typeProject = "development",
            user_id = userId ?: "test_user",
            dateStart = "2026-01-01",
            dateEnd = "2026-12-31",
            gender = "unisex",
            description_source = "Test description created at $timestamp",
            category = "tech",
            image = null
        )
        val result = api.postProject(token, data)

        assertNotNull(result)
        assertNotNull(result.id)
        assertEquals(data.title, result.title)
        assertEquals(data.user_id, result.user_id)
        println("Project created: id=${result.id}, title=${result.title}")
    }

    // ============ CART ============
    @Test
    fun testPostBucket() = runBlocking {
        val products = api.getProducts()
        if (products.items.isNotEmpty()) {
            val productId = products.items.first().id
            val data = RequestCart(
                user_id = userId ?: "test_user",
                product_id = productId,
                count = 1
            )
            val result = api.postBucket(data)

            assertNotNull(result)
            assertNotNull(result.id)
            assertEquals(1, result.count)
            assertEquals(productId, result.product_id)
            println("Cart created: id=${result.id}, product=$productId")
        } else {
            println("No products to add to cart")
        }
    }

    @Test
    fun testPatchBucket() = runBlocking {
        val products = api.getProducts()
        if (products.items.isNotEmpty()) {
            val productId = products.items.first().id
            val createData = RequestCart(userId ?: "test_user", productId, 1)
            val created = api.postBucket(createData)
            val cartId = created.id

            val updateData = RequestCart(userId ?: "test_user", productId, 5)
            val result = api.patchBucket(cartId, updateData)

            assertNotNull(result)
            assertEquals(5, result.count)
            assertEquals(cartId, result.id)
            println("Cart updated: id=$cartId, count=5")
        } else {
            println("No products to test cart update")
        }
    }

    // ============ ORDERS ============
    @Test
    fun testPostOrder() = runBlocking {
        val products = api.getProducts()
        if (products.items.isNotEmpty()) {
            val productId = products.items.first().id
            val data = RequestOrder(
                user_id = userId ?: "test_user",
                product_id = productId,
                count = 2
            )
            val result = api.postOrder(data)

            assertNotNull(result)
            assertNotNull(result.id)
            assertEquals(2, result.count)
            assertEquals(productId, result.product_id)
            println("Order created: id=${result.id}")
        } else {
            println("No products to create order")
        }
    }

    @Test
    fun testGetOrders() = runBlocking {
        val result = api.getOrders()

        assertNotNull(result)
        assertNotNull(result.id)
        assertNotNull(result.user_id)
        assertTrue(result.count >= 0)
        println("Order: id=${result.id}, count=${result.count}")
    }

    @Test
    fun testGetOrdersWithFilter() = runBlocking {
        val id = userId ?: return@runBlocking
        val result = api.getOrders("user_id='$id'")

        assertNotNull(result)
        assertEquals(id, result.user_id)
        println("Filtered order found for user $id")
    }

    // ============ COMPLEX TEST ============
    @Test
    fun testFullFlow() = runBlocking {
        val products = api.getProducts()
        assertTrue(products.items.isNotEmpty())
        val productId = products.items.first().id
        println("Got product: ${products.items.first().title}")

        val cartData = RequestCart(userId ?: "test_user", productId, 1)
        val cart = api.postBucket(cartData)
        assertNotNull(cart.id)
        println("Added to cart: ${cart.id}")

        val orderData = RequestOrder(userId ?: "test_user", productId, 1)
        val order = api.postOrder(orderData)
        assertNotNull(order.id)
        println("Order created: ${order.id}")

        val orders = api.getOrders()
        assertNotNull(orders)
        println("Orders retrieved")
    }

//    @Test
//    fun testErrorHandling() = runBlocking {
//        try {
//            api.getUser("non_existent_id_12345")
//        } catch (e: Exception) {
//            assertTrue(e.message?.contains("404") == true || e is io.ktor.client.plugins.ClientRequestException)
//            println("Error handled: ${e.message}")
//        }
//    }
}