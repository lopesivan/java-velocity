package com.exemplo

import java.io.StringWriter
import java.util.*
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.runtime.RuntimeConstants
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader

// ========== CONFIGURAÇÃO DO ENGINE ==========

private val velocityEngine by lazy {
    VelocityEngine(
                    Properties().apply {
                        setProperty(RuntimeConstants.RESOURCE_LOADERS, "class")
                        setProperty(
                                "resource.loader.class.class",
                                ClasspathResourceLoader::class.java.name
                        )
                    }
            )
            .apply { init() }
}

// ========== EXTENSION FUNCTIONS ==========

/** Renderiza template com Map */
fun String.renderTemplate(context: Map<String, Any> = emptyMap()): String =
        StringWriter().use { writer ->
            val template = velocityEngine.getTemplate(this, "UTF-8")
            template.merge(VelocityContext(context), writer)
            writer.toString()
        }

/** Avalia template inline */
fun String.evalTemplate(context: Map<String, Any> = emptyMap()): String =
        StringWriter().use { writer ->
            velocityEngine.evaluate(VelocityContext(context), writer, "inline", this)
            writer.toString()
        }

/** DSL para criar contexto Velocity */
fun velocity(block: VelocityContextBuilder.() -> Unit): String =
        VelocityContextBuilder().apply(block).render()

// ========== DSL BUILDER ==========

class VelocityContextBuilder {
    private val context = mutableMapOf<String, Any>()
    private var templatePath: String? = null
    private var templateString: String? = null

    infix fun String.to(value: Any) {
        context[this] = value
    }

    fun template(path: String) {
        templatePath = path
    }

    fun inline(template: String) {
        templateString = template
    }

    fun render(): String =
            when {
                templatePath != null -> templatePath!!.renderTemplate(context)
                templateString != null -> templateString!!.evalTemplate(context)
                else -> throw IllegalStateException("Defina um template")
            }
}

// ========== INFIX FUNCTIONS ==========

/** Template com contexto inline */
infix fun String.with(context: Map<String, Any>): String = this.renderTemplate(context)

/** Template inline com contexto */
infix fun String.eval(context: Map<String, Any>): String = this.evalTemplate(context)

// ========== EXEMPLOS DE USO ==========
fun main() {
    try {
        exemplo1_ExtensionFunction()
        exemplo2_InfixOperator()
        exemplo3_DSL()
        exemplo4_SuperConciso()
    } catch (e: Exception) {
        e.printStackTrace() // <<< aqui aparece a causa real
    }
}

/** Exemplo 1: Extension Function */
fun exemplo1_ExtensionFunction() {
    println("=== Exemplo 1: Extension Function ===")

    val resultado =
            "template.vm".renderTemplate(
                    mapOf("nome" to "Ivan", "mensagem" to "Usando Kotlin extension functions!")
            )

    println(resultado)
}

/** Exemplo 2: Infix Operator (super legível!) */
fun exemplo2_InfixOperator() {
    println("\n=== Exemplo 2: Infix Operator ===")

    // Template de arquivo
    val resultado1 =
            "template.vm" with
                    mapOf("nome" to "Ivan", "mensagem" to "Infix operator = código limpo!")
    println(resultado1)

    // Template inline
    val resultado2 =
            "Olá, \$nome! Você tem \$idade anos." eval mapOf("nome" to "Ivan", "idade" to 30)
    println(resultado2)
}

/** Exemplo 3: DSL Kotlin (mais elegante!) */
fun exemplo3_DSL() {
    println("\n=== Exemplo 3: DSL Kotlin ===")

    val resultado = velocity {
        "nome" to "Ivan"
        "mensagem" to "DSL super elegante!"
        "itens" to listOf("A", "B", "C")
        template("template.vm")
    }

    println(resultado)
}

/** Exemplo 4: Super conciso */
fun exemplo4_SuperConciso() {
    println("\n=== Exemplo 4: Ultra Conciso ===")

    // Uma linha!
    println("Olá, \$nome!" eval mapOf("nome" to "Ivan"))

    // Com template file
    println("template.vm" with mapOf("nome" to "Ivan", "mensagem" to "One-liner!"))

    // DSL inline
    println(
            velocity {
                "nome" to "Ivan"
                "data" to java.time.LocalDate.now()
                inline("Hoje é \$data, \$nome!")
            }
    )
}

// ========== ADVANCED: TEMPLATE CACHING ==========

object TemplateCache {
    private val cache = mutableMapOf<String, org.apache.velocity.Template>()

    fun get(path: String): org.apache.velocity.Template =
            cache.getOrPut(path) { velocityEngine.getTemplate(path, "UTF-8") }

    fun render(path: String, context: Map<String, Any>): String =
            StringWriter().use { writer ->
                get(path).merge(VelocityContext(context), writer)
                writer.toString()
            }
}

// ========== TYPEALIAS PARA CONTEXTO ==========

typealias TemplateContext = Map<String, Any>

fun renderCached(template: String, context: TemplateContext): String =
        TemplateCache.render(template, context)

// ========== USO AVANÇADO ==========

fun exemplosAvancados() {
    // Com cache
    val resultado1 = renderCached("template.vm", mapOf("nome" to "Ivan"))

    // Destructuring
    val (nome, idade) = listOf("Ivan", 30)
    println("Olá, \$nome!" eval mapOf("nome" to nome))

    // Com data classes
    data class Usuario(val nome: String, val email: String)
    val user = Usuario("Ivan", "ivan@exemplo.com")

    println(
            velocity {
                "usuario" to user
                inline("Nome: \$usuario.nome, Email: \$usuario.email")
            }
    )

    // Pipeline style
    "template.vm".renderTemplate(mapOf("nome" to "Ivan")).also(::println)
}

// ========== OPERADORES PERSONALIZADOS ==========

operator fun String.invoke(vararg pairs: Pair<String, Any>): String =
        this.renderTemplate(pairs.toMap())

fun exemploOperadorInvoke() {
    // Chamar template como função!
    println("template.vm"("nome" to "Ivan", "idade" to 30))

    // Template inline
    println("\$nome tem \$idade anos"("nome" to "Ivan", "idade" to 30))
}

