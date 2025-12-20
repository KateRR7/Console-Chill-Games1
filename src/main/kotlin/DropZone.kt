package main


import kotlin.random.Random
import kotlin.system.exitProcess


object BlockTypes {
    const val DANGEROUS_SYMBOL = 'X'
    const val USEFUL_SYMBOL = 'O'
    const val EMPTY_SYMBOL = ' '
    const val PLAYER_SYMBOL = 'A'

    const val DANGEROUS_COLOR = "\u001B[31m"
    const val USEFUL_COLOR = "\u001B[32m"
    const val PLAYER_COLOR = "\u001B[36m"
    const val RESET_COLOR = "\u001B[0m"
}
object Difficulty {
    const val EASY = 1
    const val MEDIUM = 2
    const val HARD = 3
    const val EXTREME = 4

    fun getSpeedMultiplier(level: Int): Double {
        return when(level) {
            EASY -> 0.7
            MEDIUM -> 1.0
            HARD -> 1.3
            EXTREME -> 1.6
            else -> 1.0
        }
    }

    fun getDangerousChance(level: Int): Double {
        return when(level) {
            EASY -> 0.4
            MEDIUM -> 0.6
            HARD -> 0.75
            EXTREME -> 0.85
            else -> 0.6
        }
    }
    fun getSpawnRate(level: Int): Int { // ДОБАВЛЕНО: частота появления блоков
        return when (level) {
            EASY -> 60  // Медленное появление
            MEDIUM -> 45 // Среднее
            HARD -> 30   // Частое
            EXTREME -> 20 // Очень частое
            else -> 45
        }
    }

    fun getName(level: Int): String {
        return when(level) {
            EASY -> "ЛЕГКИЙ"
            MEDIUM -> "СРЕДНИЙ"
            HARD -> "СЛОЖНЫЙ"
            EXTREME -> "ЭКСТРЕМАЛЬНЫЙ"
            else -> "СРЕДНИЙ"
        }
    }
}

data class GameState(
    var score: Int = 0,
    var lives: Int = 3,
    var isGameOver: Boolean = false,
    var level: Int = 1,
    var difficulty: Int = Difficulty.MEDIUM
)


data class GameObject(
    val x: Int,
    var y: Double,
    val isDangerous: Boolean,
    val speed: Double
)

class DropZoneGame {

    private val WIDTH = 20
    private val HEIGHT = 15
    private val PLAYER_WIDTH = 3


    private val gameField = Array(HEIGHT) { Array(WIDTH) { BlockTypes.EMPTY_SYMBOL } }


    private val state = GameState()

    private var playerX = WIDTH / 2


    private val fallingObjects = mutableListOf<GameObject>()


    private var gameTick = 0


    private val BASE_GAME_SPEED = 250L
    private val BASE_BLOCK_SPEED = 0.3
    private var SPAWN_INTERVAL = 45

    private var usefulBlocks = 0
    private var dangerousBlocks = 0

    private fun clearField() {
        for (y in 0 until HEIGHT) {
            for (x in 0 until WIDTH) {
                gameField[y][x] = BlockTypes.EMPTY_SYMBOL
            }
        }
    }

    private fun render() {
        // Очистка консоли
        print("\u001B[H\u001B[2J")
        println("=".repeat(WIDTH + 2))

        for (y in 0 until HEIGHT) {
            print("|")
            for (x in 0 until WIDTH) {
                val cell = gameField[y][x]
                when (cell) {
                    BlockTypes.DANGEROUS_SYMBOL -> print("${BlockTypes.DANGEROUS_COLOR}$cell${BlockTypes.RESET_COLOR}")
                    BlockTypes.USEFUL_SYMBOL -> print("${BlockTypes.USEFUL_COLOR}$cell${BlockTypes.RESET_COLOR}")
                    else -> print(cell)
                }
            }
            println("|")
        }
        println("=".repeat(WIDTH + 2))

        print(" ")
        for (x in 0 until WIDTH) {
            if (x >= playerX - 1 && x <= playerX + 1) {
                when (x - playerX) {
                    -1 -> print("${BlockTypes.PLAYER_COLOR}/")  // Левая часть
                    0 -> print("${BlockTypes.PLAYER_COLOR}A")   // Центральная часть
                    1 -> print("${BlockTypes.PLAYER_COLOR}\\${BlockTypes.RESET_COLOR}") // Правая часть
                    else -> print(" ")
                }
            } else {
                print(" ")
            }
        }
        println()

        if (state.isGameOver) {
            println("GAME OVER! Final score: ${state.score}")
            println("Press Q to quit")
        }
    }

    private fun updateField() {
        clearField()

        for (obj in fallingObjects) {
            val drawY = obj.y.toInt()
            if (drawY in 0 until HEIGHT && obj.x in 0 until WIDTH) {
                gameField[drawY][obj.x] = if (obj.isDangerous) BlockTypes.DANGEROUS_SYMBOL else BlockTypes.USEFUL_SYMBOL
            }
        }
    }

    private fun spawnFallingObject() {
        val x = Random.nextInt(0, WIDTH)
        val isDangerous = Random.nextDouble() < Difficulty.getDangerousChance(state.difficulty)

        val speed = BASE_BLOCK_SPEED +  Difficulty.getSpeedMultiplier(state.difficulty) + (state.level * 0.1)

        fallingObjects.add(GameObject(x, 0.0, isDangerous, speed))
    }

    private fun updateFallingObjects() {
        val objectsToRemove = mutableListOf<GameObject>()

        for (obj in fallingObjects) {

            obj.y += obj.speed

            if (obj.y >= HEIGHT - 1 && obj.x in (playerX - 1)..(playerX + 1)) {
                if (obj.isDangerous) {
                    state.lives--
                    dangerousBlocks++
                    if (state.lives <= 0) {
                        state.isGameOver = true
                    }
                } else {
                    state.score += 10
                    usefulBlocks++
                    state.level = (state.score / 50) + 1
                }
                objectsToRemove.add(obj)
            }

            else if (obj.y >= HEIGHT) {
                objectsToRemove.add(obj)
            }
        }

        fallingObjects.removeAll(objectsToRemove)
    }

    private fun handleInput(input: Char) {
        when (input.uppercaseChar()) {
            'A' -> if (playerX > 1) playerX--
            'D' -> if (playerX < WIDTH - 2) playerX++
            'Q' -> state.isGameOver = true
        }
    }

    private fun readInput(): Char? {
        return try {
            if (System.`in`.available() > 0) {
                System.`in`.read().toChar()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    private fun selectDifficulty() {
        println("\n=== ВЫБЕРИТЕ УРОВЕНЬ СЛОЖНОСТИ ===")
        println("1. ЛЕГКИЙ (скорость x0.7, 40% опасных)")
        println("2. СРЕДНИЙ (скорость x1.0, 60% опасных) ← по умолчанию")
        println("3. СЛОЖНЫЙ (скорость x1.3, 75% опасных)")
        println("4. ЭКСТРЕМАЛЬНЫЙ (скорость x1.6, 85% опасных)")
        print("\nВаш выбор (1-4, Enter для среднего): ")

        val input = readLine()
        state.difficulty = when (input?.toIntOrNull()) {
            1 -> Difficulty.EASY
            2 -> Difficulty.MEDIUM
            3 -> Difficulty.HARD
            4 -> Difficulty.EXTREME
            else -> Difficulty.MEDIUM
        }
        SPAWN_INTERVAL = Difficulty.getSpawnRate(state.difficulty)
    }

    // Улучшаем showResults
    private fun showFinalResults() {
        print("\u001B[H\u001B[2J")
        println("╔══════════════════════════════════╗")
        println("║        РЕЗУЛЬТАТЫ ИГРЫ          ║")
        println("╠══════════════════════════════════╣")
        println("║                                  ║")
        println("║  Сложность: ${Difficulty.getName(state.difficulty).padEnd(20)}║")
        println("║  Финальный счет: ${state.score.toString().padEnd(15)}║")
        println("║  Достигнутый уровень: ${state.level.toString().padEnd(10)}║")
        println("║                                  ║")
        println("║  Статистика:                     ║")
        println("║  • Полезных блоков: ${usefulBlocks.toString().padEnd(12)}║")
        println("║  • Опасных блоков: ${dangerousBlocks.toString().padEnd(13)}║")
        println("║  • Всего блоков: ${(usefulBlocks + dangerousBlocks).toString().padEnd(15)}║")
        println("║                                  ║")
        println("║                                  ║")
        println("╠══════════════════════════════════╣")
        println("║                                  ║")
        println("║  Нажмите:                        ║")
        println("║  [R] - Играть снова              ║")
        println("║  [Q] - Выйти                     ║")
        println("║                                  ║")
        println("╚══════════════════════════════════╝")
    }

    fun startGame() {
        println("Добро пожаловать в игру Drop Zone")
        println("Уворачивайтесь от красных X (X) и собирайте зеленые O (O)")
        println("Ваша фигурка: ${BlockTypes.PLAYER_COLOR}/A\\${BlockTypes.RESET_COLOR}")
        println("🎮 Управление: ")
        println("[A] ← двигаться влево")
        println("[D] → двигаться вправо")
        println("[Q] выйти из игры")
        selectDifficulty()
        println("Нажмите Enter чтобы начать...")
        readLine()

        while (!state.isGameOver) {
            gameTick++

            if (gameTick % SPAWN_INTERVAL == 0) {
                spawnFallingObject()
            }

            updateFallingObjects()

            updateField()
            render()


            val input = readInput()
            if (input != null) {
                handleInput(input)
            }

            Thread.sleep(BASE_GAME_SPEED)
        }
        print("\u001B[H\u001B[2J")
        showFinalResults()
        while (true) {
            val input = readInput()
            when (input?.uppercaseChar()) {
                'R' -> {
                    state.score = 0
                    state.lives = 3
                    state.isGameOver = false
                    state.level = 1
                    playerX = WIDTH / 2
                    fallingObjects.clear()
                    gameTick = 0
                    usefulBlocks = 0
                    dangerousBlocks = 0

                    startGame()
                    return
                }
                'Q' -> {
                    println("\nСпасибо за игру!")
                    exitProcess(0)
                }
            }
            Thread.sleep(10)
        }


    }

}
