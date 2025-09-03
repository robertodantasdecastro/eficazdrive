package dev.eficazdrive.app.core

import android.content.Context
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import dev.eficazdrive.app.overlay.PopupOverlay
import java.util.regex.Pattern

/**
 * PT-BR: Motor de regras que identifica a plataforma, extrai campos via regex e avalia cálculos.
 * EN: Rule engine that identifies platform, extracts fields via regex and evaluates calculations.
 */
object RuleEngine {
    private val mapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    data class PlatformRule(
        val id: String,
        val markers: List<String>,
        val fields: Map<String, String> // name -> regex
    )

    data class CalculationsConfig(
        val formulas: Map<String, String> // name -> expression on extracted fields
    )

    private fun loadRules(context: Context): List<PlatformRule> {
        val json = context.assets.open("config/platform_rules.json").bufferedReader().use { it.readText() }
        return mapper.readValue(json)
    }

    private fun loadCalculations(context: Context): CalculationsConfig {
        val json = context.assets.open("config/calculations.json").bufferedReader().use { it.readText() }
        return mapper.readValue(json)
    }

    private fun loadAppConfig(context: Context): JsonNode {
        val json = context.assets.open("config/app.json").bufferedReader().use { it.readText() }
        return mapper.readTree(json)
    }

    fun onText(fullText: String, context: Context) {
        val rules = loadRules(context)
        val calc = loadCalculations(context)
        val appCfg = loadAppConfig(context)

        val platform = rules.firstOrNull { r -> r.markers.any { fullText.contains(it, ignoreCase = true) } } ?: return
        val extracted = mutableMapOf<String, Any>()
        platform.fields.forEach { (name, regex) ->
            val m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE).matcher(fullText)
            if (m.find()) {
                val value = m.group(1) ?: m.group()
                extracted[name] = value
            }
        }

        // Simple formula evaluator: supports + - * / and variables from extracted map
        val derived = mutableMapOf<String, Any>()
        calc.formulas.forEach { (name, expr) ->
            derived[name] = safeEval(expr, extracted)
        }

        val output = mapper.createObjectNode().apply {
            put("platform", platform.id)
            set<JsonNode>("data", mapper.valueToTree(extracted))
            set<JsonNode>("metrics", mapper.valueToTree(derived))
        }

        val displayMs = appCfg.path("popup_display_ms").asLong(4000)
        PopupOverlay.showJson(context, output.toPrettyString(), displayMs)
    }

    private fun safeEval(expr: String, vars: Map<String, Any>): Double {
        // Very small expression evaluator for numeric expressions like: (price - cost) / distance
        // Replace variables with numeric values when possible; non-resolvable become 0.
        var normalized = expr
        vars.forEach { (k, v) ->
            val num = v.toString().replace(",", ".").replace(Regex("[^0-9.]+"), "")
            normalized = normalized.replace(Regex("\\b$k\\b", RegexOption.IGNORE_CASE), num.ifEmpty { "0" })
        }
        return runCatching {
            SimpleMath.eval(normalized)
        }.getOrDefault(0.0)
    }
}

/**
 * PT-BR: Interpretador matemático mínimo (double) com + - * / e parênteses.
 * EN: Minimal math interpreter (double) with + - * / and parentheses.
 */
private object SimpleMath {
    fun eval(s: String): Double {
        return Parser(s).parse()
    }

    private class Parser(val s: String) {
        var i = 0
        fun parse(): Double = expr().also { skip() }
        private fun expr(): Double {
            var v = term()
            while (true) {
                skip()
                v = when (peek()) {
                    '+' -> { i++; v + term() }
                    '-' -> { i++; v - term() }
                    else -> return v
                }
            }
        }
        private fun term(): Double {
            var v = factor()
            while (true) {
                skip()
                v = when (peek()) {
                    '*' -> { i++; v * factor() }
                    '/' -> { i++; val d = factor(); if (d == 0.0) 0.0 else v / d }
                    else -> return v
                }
            }
        }
        private fun factor(): Double {
            skip()
            return when (peek()) {
                '(' -> { i++; val v = expr(); skip(); if (peek()==')') i++; v }
                '+', '-' -> { val sign = if (peek()== '-') -1 else 1; i++; sign * factor() }
                else -> number()
            }
        }
        private fun number(): Double {
            val start = i
            while (i < s.length && (s[i].isDigit() || s[i]=='.')) i++
            return s.substring(start, i).toDoubleOrNull() ?: 0.0
        }
        private fun peek(): Char = if (i < s.length) s[i] else '\u0000'
        private fun skip() { while (i < s.length && s[i].isWhitespace()) i++ }
    }
}




