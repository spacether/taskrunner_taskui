package io.taskrunner

import com.rabbitmq.client.ConnectionFactory
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.thymeleaf.Thymeleaf
import io.ktor.server.thymeleaf.ThymeleafContent
import io.taskdata.DbTaskRepository
import io.taskdata.TaskRepository
import io.taskmodels.Task
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

/**
 * Main entrypoint of the executable that starts a Netty webserver at port 8080
 * and registers the [module].
 *
 */
const val QUEUE_NAME = "tasks"

fun main() {
    val port = System.getenv("PORT") ?: "8080"
    embeddedServer(Netty, port = port.toInt()) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    val repository = DbTaskRepository()
    configureRouting(repository)
    configureDatabases()
    configureTemplatingAndContentNegotiation()
}

fun configureDatabases() {
    val url = System.getenv("JDBC_DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/taskrunner_db?user=taskrunner_readwriter&password=xfdz8t-mds-V"
    Database.connect(url)
}

fun Application.configureTemplatingAndContentNegotiation() {
    install(ContentNegotiation) {
        json()
    }
    install(Thymeleaf) {
        setTemplateResolver(ClassLoaderTemplateResolver().apply {
            prefix = "templates/"
            suffix = ".html"
            characterEncoding = "utf-8"
        })
    }
}

@Serializable
data class QueueInfo(
    val messageCount: Int
)

fun getQueueMessageCount(): Int {
    val factory = ConnectionFactory()
    val uri = System.getenv("CLOUDAMQP_URL") ?: "amqp://guest:guest@localhost"
    factory.setUri(uri);
    val connection = factory.newConnection()
    val channel = connection.createChannel()
    val declareOk = channel.queueDeclare(QUEUE_NAME, true, false, false, null)
    val messageCount = declareOk.messageCount
    channel.close()
    connection.close()
    return messageCount
}

fun Application.configureRouting(repository: TaskRepository) {
    routing {
        get("/queue_info") {
            val messageCount = getQueueMessageCount()
            println("messageCount=$messageCount")
            call.respond(QueueInfo(messageCount))

        }
        delete("/tasks/{taskName}") {
            val name = call.parameters["taskName"]
            if (name == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            if (repository.removeTask(name)) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        post("/tasks") {
            val formContent = call.receiveParameters()
            val name = formContent["name"].toString()
            val fileName = formContent["fileName"].toString()
            val minute = formContent["minute"]?.toIntOrNull()
            val hour = formContent["hour"]?.toIntOrNull()

            val task = Task(
                name, fileName, minute, hour
            )
            try {
                repository.addTask(task)
                val templateInfo = mapOf(
                    "tasks" to repository.allTasks(),
                    "queueMessageCount" to getQueueMessageCount()
                )
                call.respond(
                    ThymeleafContent("tasks", templateInfo)
                )
            } catch (ex: Throwable) {
                call.respond(HttpStatusCode.BadRequest, message=ex.message!!)
            } catch (ex: IllegalStateException) {
                call.respond(HttpStatusCode.BadRequest, message=ex.message!!)
            }
        }
        get("/tasks") {
            val templateInfo = mapOf(
                "tasks" to repository.allTasks(),
                "queueMessageCount" to getQueueMessageCount()
            )
            call.respond(ThymeleafContent("tasks", templateInfo))
        }
    }
}