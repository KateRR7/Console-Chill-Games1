import java.io.File

class WordlyGame {
    private var attempt = 6
    private var success = false
    private lateinit var word: String
    private val russianWords = File("C:\\Users\\Admin\\Console-Chill-Games1\\src\\main\\resources\\Файл с словами.txt").readLines()

    fun start() {
        try {
            val file = File("C:\\Users\\Admin\\Console-Chill-Games1\\src\\main\\resources\\Файл с словами.txt").readLines().map { it.trim() }
            word = file.random()
            while (word.length != 5) {
                word = file.random()
            }
        } catch (e : Exception) {
            println("Error opening file: $e")
            return
        }
        println("Привет, это игра wordly, смысл которой - угадать загаданное слово из 5 букв за 6 попыток")
        println("Напоминаю про обозначения:")
        println("✅ — эта буква стоит на этом месте\n" +
                "❔ — эта буква есть в слове, но не на этом месте\n" +
                "❌ — этой буквы нет в слове")
        println("Начинаем игру!")
        playersGuess()
    }

    private fun playersGuess() {
        while (attempt > 0 && !success) {
            val guess = readLine()?.trim()?.lowercase()
            if (guess.isNullOrEmpty()) {
                println("Введите слово")
                continue
            } else if (guess.length != 5) {
                println("Слово должно состоять из 5 букв. Пожалуйста, введите другое.")
                continue
            } else if (guess.firstOrNull { !it.isLetter() } != null) {
                println("То, что ввели вы, словом не является. Введите слово из 5 букв.")
                continue
            }  else if (guess !in russianWords) {
                println("Такого слова не существует. Пожалуйста, введите другое.")
                continue
            }
            game(guess, word)
        }
    }

    private fun game(guess: String, word: String) {
        var counter: Int = 0
        var res = CharArray(word.length) { '❌' }
        var used = BooleanArray(word.length) { false }
        for (i in 0..4) {
            if (guess[i] == word[i]) {
                res[i] = '✅'
                counter++
                used[i] = true
            }
        }

        for (i in 0..4) {
            if (res[i] != '✅') {
                for (j in 0..4) {
                    if (word[j] == guess[i] && !used[j]) {
                        res[i] = '❔'
                        used[j] = true
                        break
                    }
                }
            }
        }
        println(res.joinToString(""))

        if (counter == 5) {
            success = true
            println("Поздравляем, вы угадали слово \"$word\"!")

            val record: String = File("C:\\Users\\Admin\\Console-Chill-Games1\\src\\main\\resources\\input.txt").readText()
            var record1: Int = record.toInt()
            if (6 - attempt < record1) {
                println("Вы установили новый личный рекорд! " +
                        "За ${6 - attempt} попыток вы смогли догадаться до данного слова=)")
                record1 = 6 - attempt
                File("C:\\Users\\Admin\\Console-Chill-Games1\\src\\main\\resources\\input.txt").writeText(record1.toString())
            } else {
                println("Ваш рекорд по этой игре: $record")
            }
            asking()
        }

        attempt--
        if (attempt == 0) {
            println("\nПопыток больше не осталось=( \nЗагаданное слово было \"$word\"")
            asking()
        } else {
            if (attempt > 4) {
                println("У вас осталось $attempt попыток \n")
            } else if (attempt > 1) {
                println("У вас осталось $attempt попытки\n")
            } else {
                println("У вас осталось $attempt попытка\n")
            }
        }
    }

    private fun asking() {
        println("\n Хотите сыграть еще раз и побить свой рекорд?")
        println("Напишите «да» или «нет» (без кавычек)")

        val ans = readLine()?.trim()?.lowercase()
        if (ans == "да") {
            attempt = 6
            success = false
            println("\n ...Запускаем новую игру...\n")
            start()
        } else if (ans == "нет") {
            println("\n ...Выход в главное меню...\n")
        }
    }
}



