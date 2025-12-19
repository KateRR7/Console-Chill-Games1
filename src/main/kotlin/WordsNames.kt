import java.io.File
import java.util.Locale

class WordGame(
    private val wordsPath: String,
    private val namesPath: String
) {

    enum class Category(val displayName: String) {
        WORD("Слово"),
        NAME("Имя")
    }

    private val used = mutableSetOf<String>()
    private var lastChar: Char? = null
    private var turnHuman = true
    private var round = 1

    private val words: List<String> by lazy { loadFile(wordsPath) }
    private val names: List<String> by lazy { loadFile(namesPath) }

    fun start() {
        println("=== Игра: Слова / Имена ===")
        println("Обращаю внимание, что имена должны быть написаны с заглавной буквы, а слова с маленькой.")

        print("Введите ваше имя: ")
        val human = readLine()?.trim().takeUnless { it.isNullOrBlank() } ?: "Игрок"
        val computer = "Компьютер"

        if (words.isEmpty() || names.isEmpty()) {
            println("Один или несколько файлов пусты или не найдены.")
            return
        }

        println("\nВыберите режим:")
        println("1 - слова")
        println("2 - имена")
        print("Ваш выбор: ")

        val category = when (readLine()?.toIntOrNull()) {
            1 -> Category.WORD
            else -> Category.NAME
        }

        println("\nИгра началась!\n")

        while (true) {
            println("Раунд $round | Категория: ${category.displayName}")

            val ok = if (turnHuman) {
                humanTurn(human, category)
            } else {
                computerTurn(computer, category)
            }

            if (!ok) break

            turnHuman = !turnHuman
            round++
            println()
        }

        println("Игра окончена.")
        println("Результат: ${round - 1}")
    }


    private fun humanTurn(name: String, category: Category): Boolean {
        lastChar?.let { println("Введите на букву '${it.uppercaseChar()}'") }
        print("$name: ")

        val input = readLine()?.trim() ?: return false
        if (input.equals("пас", true)) {
            println("$name сдался. Компьютер победил.")
            return false
        }

        val word = normalize(input)

        if (!isValid(word, category)) {
            println("Ошибка. Компьютер победил.")
            return false
        }

        used.add(word)
        lastChar = lastSignificantChar(word)
        return true
    }


    private fun computerTurn(name: String, category: Category): Boolean {
        println("Ход компьютера...")

        val dict = getDictionary(category)
        val word = dict
            .filter { it !in used }
            .filter { lastChar == null || it.startsWith(lastChar!!) }
            .randomOrNull()

        if (word == null) {
            println("Компьютер не знает слов. Вы победили! 🎉")
            return false
        }

        println("$name: $word")
        used.add(word)
        lastChar = lastSignificantChar(word)
        return true
    }


    private fun isValid(word: String, category: Category): Boolean {
        if (word.isEmpty()) return false
        if (word in used) return false
        if (lastChar != null && word.first() != lastChar) return false

        val dictionary = getDictionary(category)

        if (word !in dictionary) {
            println("Такое слово не существует.")
            return false
        }

        return when (category) {
            Category.WORD -> true
            Category.NAME -> !word.contains(' ')
        }
    }



    private fun getDictionary(category: Category): List<String> =
        when (category) {
            Category.WORD -> words
            Category.NAME -> names
        }

    private fun loadFile(path: String): List<String> {
        val file = File(path)
        if (!file.exists()) {
            println("Файл не найден: $path")
            return emptyList()
        }

        return file.readLines()
            .map { normalize(it) }
            .filter { it.isNotBlank() }
            .distinct()
    }


    private fun normalize(s: String): String {
        var t = s.trim().lowercase(Locale.getDefault())
        t = t.replace(Regex("\\s*-\\s*"), "-")
        t = t.replace(Regex("[^\\p{L}-]"), "")
        return t.trim('-')
    }

    private fun lastSignificantChar(s: String): Char? {
        val ignore = setOf('ь', 'ъ', '\'', '’')
        for (i in s.length - 1 downTo 0) {
            if (s[i] !in ignore) return s[i]
        }
        return null
    }
}