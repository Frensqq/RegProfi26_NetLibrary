package com.example.netlibrary

import com.example.netlibrary.data.remote.PBApi
import com.example.netlibrary.data.remote.PBApiServis
import com.example.netlibrary.domain.model.RequestAuth
import com.example.netlibrary.domain.model.RequestCart
import com.example.netlibrary.domain.model.RequestOrder
import com.example.netlibrary.domain.model.RequestProject
import com.example.netlibrary.domain.model.RequestRegister
import com.example.netlibrary.domain.model.RequestUser
import com.example.netlibrary.domain.model.ResponseAuth
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test

class PBApiTest {

    private val api: PBApi = PBApiServis.instance

    private val password = "12345678"

    /*
     * Отдельный клиент используется только для подготовки тестовых данных,
     * которых нельзя создать через PBApi.
     */
    private val testClient = HttpClient(OkHttp) {
        expectSuccess = true

        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                }
            )
        }

        defaultRequest {
            url(PBApiServis.BASE_URL)
            contentType(ContentType.Application.Json)
        }
    }

    // ==================================================
    // USERS
    // ==================================================

    @Test
    fun postUser_createsUser() = runBlocking {
        val email = uniqueEmail()

        val user = api.postUser(
            RequestRegister(
                email = email,
                password = password,
                passwordConfirm = password
            )
        )

        assertTrue(user.id.isNotBlank())
        assertEquals("users", user.collectionName)
    }

    @Test
    fun authUser_returnsToken() = runBlocking {
        val credentials = createUser()

        val auth = api.authUser(
            RequestAuth(
                identity = credentials.email,
                password = credentials.password
            )
        )

        assertTrue(auth.token.isNotBlank())
        assertEquals(credentials.userId, auth.record.id)
    }

    @Test
    fun getUser_returnsCreatedUser() = runBlocking {
        val testUser = createAndAuthorize()

        val user = api.getUser(testUser.auth.record.id)

        assertEquals(testUser.auth.record.id, user.id)
        assertEquals("users", user.collectionName)
    }

    @Test
    fun patchUser_changesUserData() = runBlocking {
        val testUser = createAndAuthorize()

        val user = api.patchUser(
            id = testUser.auth.record.id,
            data = RequestUser(
                /*
                 * Передаём исходный email.
                 * Новый email PocketBase воспринимает как смену email
                 * и требует отдельного подтверждения.
                 */
                email = testUser.email,
                emailVisibility = true,
                firstname = "JUnit",
                lastname = "Test",
                secondname = "User",
                datebirthday = "2000-01-01",
                gender = "male"
            )
        )

        assertEquals(testUser.auth.record.id, user.id)
        assertEquals("JUnit", user.firstname)
        assertEquals("Test", user.lastname)
        assertEquals("User", user.secondname)
        assertEquals("2000-01-01", user.datebirthday)
        assertEquals("male", user.gender)
    }

    // ==================================================
    // NEWS
    // ==================================================

    @Test
    fun getNews_returnsPage() = runBlocking {
        val response = api.getNews()

        assertTrue(response.page >= 1)
        assertTrue(response.totalItems >= 0)
        assertNotNull(response.items)
    }

    // ==================================================
    // PRODUCTS
    // ==================================================

    @Test
    fun getProducts_returnsPage() = runBlocking {
        /*
         * Создаём товар, чтобы тест проверял не только пустую страницу.
         */
        val createdProduct = createProduct()

        val response = api.getProducts(null)

        assertTrue(response.page >= 1)
        assertTrue(
            response.items.any { product ->
                product.id == createdProduct.id
            }
        )
    }

    @Test
    fun getProductsWithFilter_returnsCreatedProduct() = runBlocking {
        val createdProduct = createProduct()

        val response = api.getProducts(
            filter = "id = '${createdProduct.id}'"
        )

        assertEquals(1, response.items.size)
        assertEquals(createdProduct.id, response.items.first().id)
        assertEquals(createdProduct.title, response.items.first().title)
    }

    @Test
    fun getProduct_returnsCreatedProduct() = runBlocking {
        val createdProduct = createProduct()

        val product = api.getProduct(createdProduct.id)

        assertEquals(createdProduct.id, product.id)
        assertEquals(createdProduct.title, product.title)
        assertEquals(createdProduct.price, product.price)
    }

    // ==================================================
    // PROJECTS
    // ==================================================

    @Test
    fun getProject_returnsPage() = runBlocking {
        val testUser = createAndAuthorize()
        val createdProject = createProject(testUser)

        val response = api.getProject()

        assertTrue(response.page >= 1)
        assertTrue(
            response.items.any { project ->
                project.id == createdProject.id
            }
        )
    }

    @Test
    fun postProject_createsProject() = runBlocking {
        val testUser = createAndAuthorize()

        val title = "JUnit project ${System.nanoTime()}"

        val project = api.postProject(
            token = testUser.auth.token,
            data = RequestProject(
                title = title,
                typeProject = "Web",
                user_id = testUser.auth.record.id,
                dateStart = "2026-08-01 00:00:00.000Z",
                dateEnd = "2026-08-02 00:00:00.000Z",
                gender = "male",
                description_source = "JUnit integration test",
                category = "test",
                image = null
            )
        )

        assertTrue(project.id.isNotBlank())
        assertEquals(title, project.title)
        assertEquals(testUser.auth.record.id, project.user_id)
    }

    // ==================================================
    // CART
    // ==================================================

    @Test
    fun postBucket_createsCartRecord() = runBlocking {
        val testUser = createAndAuthorize()
        val product = createProduct()

        val cart = api.postBucket(
            RequestCart(
                user_id = testUser.auth.record.id,
                product_id = product.id,
                count = 1
            )
        )

        assertTrue(cart.id.isNotBlank())
        assertEquals(testUser.auth.record.id, cart.user_id)
        assertEquals(product.id, cart.product_id)
        assertEquals(1, cart.count)
    }

    @Test
    fun patchBucket_changesCount() = runBlocking {
        val testUser = createAndAuthorize()
        val product = createProduct()

        /*
         * Этот тест сам создаёт корзину, которую затем изменяет.
         */
        val createdCart = api.postBucket(
            RequestCart(
                user_id = testUser.auth.record.id,
                product_id = product.id,
                count = 1
            )
        )

        val updatedCart = api.patchBucket(
            id = createdCart.id,
            data = RequestCart(
                user_id = testUser.auth.record.id,
                product_id = product.id,
                count = 5
            )
        )

        assertEquals(createdCart.id, updatedCart.id)
        assertEquals(product.id, updatedCart.product_id)
        assertEquals(5, updatedCart.count)
    }

    // ==================================================
    // ORDERS
    // ==================================================

    @Test
    fun postOrder_createsOrder() = runBlocking {
        val testUser = createAndAuthorize()
        val product = createProduct()

        val order = api.postOrder(
            RequestOrder(
                user_id = testUser.auth.record.id,
                product_id = product.id,
                count = 2
            )
        )

        assertTrue(order.id.isNotBlank())
        assertEquals(testUser.auth.record.id, order.user_id)
        assertEquals(product.id, order.product_id)
        assertEquals(2, order.count)
    }

//    @Test
//    fun getOrders_returnsCreatedOrder() = runBlocking {
//        val testUser = createAndAuthorize()
//        val product = createProduct()
//
//        /*
//         * Сначала создаём заказ.
//         */
//        val createdOrder = api.postOrder(
//            RequestOrder(
//                user_id = testUser.auth.record.id,
//                product_id = product.id,
//                count = 1
//            )
//        )
//
//        /*
//         * Затем получаем только заказы созданного пользователя.
//         */
//        val response = api.getOrders(
//            filter = "user_id = '${testUser.auth.record.id}'"
//        )
//
//        assertTrue(response.page >= 1)
//        assertTrue(
//            response.items.any { order ->
//                order.id == createdOrder.id
//            }
//        )
//
//        assertTrue(
//            response.items.all { order ->
//                order.user_id == testUser.auth.record.id
//            }
//        )
//    }

    // ==================================================
    // AUTH ORIGINS
    // ==================================================

    @Test
    fun getToken_returnsPage() = runBlocking {
        createAndAuthorize()

        val response = api.getToken()

        assertTrue(response.page >= 1)
        assertTrue(response.totalItems >= 0)
        assertNotNull(response.items)
    }

    @Test
    fun deleteToken_removesExistingOrigin() = runBlocking {
        createAndAuthorize()

        val before = api.getToken()

        /*
         * Обычная password-авторизация может не создавать _authOrigins.
         * В таком случае корректно пропускаем только этот тест.
         */
        assumeTrue(
            "PocketBase не создал запись в _authOrigins",
            before.items.isNotEmpty()
        )

        val originId = before.items.first().id

        api.deleteToken(originId)

        val after = api.getToken()

        assertFalse(
            after.items.any { origin ->
                origin.id == originId
            }
        )
    }

    // ==================================================
    // TEST DATA HELPERS
    // ==================================================

    private suspend fun createUser(): TestCredentials {
        val email = uniqueEmail()

        val registered = api.postUser(
            RequestRegister(
                email = email,
                password = password,
                passwordConfirm = password
            )
        )

        assertTrue(
            "PocketBase не создал тестового пользователя",
            registered.id.isNotBlank()
        )

        return TestCredentials(
            userId = registered.id,
            email = email,
            password = password
        )
    }

    private suspend fun createAndAuthorize(): TestUser {
        /*
         * Сбрасываем токен предыдущего теста.
         */
        PBApiServis.token = null

        val credentials = createUser()

        val auth = api.authUser(
            RequestAuth(
                identity = credentials.email,
                password = credentials.password
            )
        )

        assertTrue(
            "PocketBase не вернул auth token",
            auth.token.isNotBlank()
        )

        assertEquals(
            credentials.userId,
            auth.record.id
        )

        PBApiServis.token = auth.token

        return TestUser(
            auth = auth,
            email = credentials.email
        )
    }

    private suspend fun createProduct(): TestProductResponse {
        val suffix = System.nanoTime()

        val request = TestProductRequest(
            title = "JUnit product $suffix",
            description = "Created by integration test",
            price = 1000,
            type = "test",
            typeCloses = "test",
            approximateCost = "1000"
        )

        val product = testClient.post(
            "collections/products/records"
        ) {
            setBody(request)
        }.body<TestProductResponse>()

        assertTrue(
            "PocketBase не создал тестовый товар",
            product.id.isNotBlank()
        )

        return product
    }

    private suspend fun createProject(
        testUser: TestUser
    ) = api.postProject(
        token = testUser.auth.token,
        data = RequestProject(
            title = "JUnit project ${System.nanoTime()}",
            typeProject = "Web",
            user_id = testUser.auth.record.id,
            dateStart = "2026-08-01 00:00:00.000Z",
            dateEnd = "2026-08-02 00:00:00.000Z",
            gender = "male",
            description_source = "JUnit integration test",
            category = "test",
            image = null
        )
    )

    private fun uniqueEmail(): String {
        return "junit_${System.nanoTime()}@test.com"
    }

    // ==================================================
    // TEST-ONLY MODELS
    // ==================================================

    private data class TestCredentials(
        val userId: String,
        val email: String,
        val password: String
    )

    private data class TestUser(
        val auth: ResponseAuth,
        val email: String
    )

    @Serializable
    private data class TestProductRequest(
        val title: String,
        val description: String,
        val price: Int,
        val type: String,
        val typeCloses: String,
        val approximateCost: String
    )

    @Serializable
    private data class TestProductResponse(
        val id: String,
        val collectionId: String,
        val collectionName: String,
        val created: String,
        val updated: String,
        val title: String,
        val description: String,
        val price: Int,
        val type: String,
        val typeCloses: String,
        val approximateCost: String
    )
}