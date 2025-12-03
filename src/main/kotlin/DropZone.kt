package main


import kotlin.random.Random
import kotlin.system.exitProcess

// Типы блоков
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

// Игровое состояние
data class GameState(
    var score: Int = 0,
    var lives: Int = 3,
    var isGameOver: Boolean = false,
    var level: Int = 1
)

// Игровой объект - блок
data class GameObject(
    val x: Int,
    var y: Double, // Теперь дробная координата для плавного движения
    val isDangerous: Boolean,
    val speed: Double // Дробная скорость
)

class DropZoneGame {
    // Константы игры
    private val WIDTH = 20
    private val HEIGHT = 15
    private val PLAYER_WIDTH = 3

    // Игровое поле (матрица символов)
    private val gameField = Array(HEIGHT) { Array(WIDTH) { BlockTypes.EMPTY_SYMBOL } }

    // Состояние игры
    private val state = GameState()

    // Позиция игрока (центральная точка)
    private var playerX = WIDTH / 2

    // Список активных падающих объектов
    private val fallingObjects = mutableListOf<GameObject>()

    // Счетчик для управления скоростью игры
    private var gameTick = 0

    // Настройки скорости (можно легко менять)
    private val BASE_GAME_SPEED = 250L // Базовая задержка между кадрами (мс)
    private val BASE_BLOCK_SPEED = 0.3 // Базовая скорость падения блоков (клеток/кадр)
    private val SPAWN_INTERVAL = 45 // Интервал между появлением новых блоков (в кадрах)

    // Сброс игрового поля
    private fun clearField() {
        for (y in 0 until HEIGHT) {
            for (x in 0 until WIDTH) {
                gameField[y][x] = BlockTypes.EMPTY_SYMBOL
            }
        }
    }

    // Отрисовка игрового поля в консоли
    private fun render() {
        // Очистка консоли
        print("\u001B[H\u001B[2J")

        // Отображение счета и жизней
        println("Score: ${state.score} | Lives: ${state.lives} | Level: ${state.level}")
        println("Controls: A - left, D - right, Q - quit")
        println("${BlockTypes.USEFUL_COLOR}${BlockTypes.USEFUL_SYMBOL}${BlockTypes.RESET_COLOR} - +10 points | ${BlockTypes.DANGEROUS_COLOR}${BlockTypes.DANGEROUS_SYMBOL}${BlockTypes.RESET_COLOR} - -1 life")
        println("=".repeat(WIDTH + 2))

        // Отрисовка игрового поля
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

        // Отрисовка фигурки игрока внизу
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

    // Обновление игрового поля
    private fun updateField() {
        clearField()

        // Размещение падающих объектов (округляем координаты для отрисовки)
        for (obj in fallingObjects) {
            val drawY = obj.y.toInt()
            if (drawY in 0 until HEIGHT && obj.x in 0 until WIDTH) {
                gameField[drawY][obj.x] = if (obj.isDangerous) BlockTypes.DANGEROUS_SYMBOL else BlockTypes.USEFUL_SYMBOL
            }
        }
    }

    // Создание нового падающего объекта
    private fun spawnFallingObject() {
        val x = Random.nextInt(0, WIDTH)
        val isDangerous = Random.nextDouble() < 0.7
        // Скорость зависит от уровня, но медленная
        val speed = BASE_BLOCK_SPEED + (state.level * 0.1)

        fallingObjects.add(GameObject(x, 0.0, isDangerous, speed))
    }

    // Обновление позиций падающих объектов
    private fun updateFallingObjects() {
        val objectsToRemove = mutableListOf<GameObject>()

        for (obj in fallingObjects) {
            // Двигаем объект вниз с дробной скоростью
            obj.y += obj.speed

            // Проверка столкновения с игроком
            if (obj.y >= HEIGHT - 1 && obj.x in (playerX - 1)..(playerX + 1)) {
                if (obj.isDangerous) {
                    state.lives--
                    if (state.lives <= 0) {
                        state.isGameOver = true
                    }
                } else {
                    state.score += 10
                    // Повышение уровня каждые 50 очков
                    state.level = (state.score / 50) + 1
                }
                objectsToRemove.add(obj)
            }
            // Удаление объектов, упавших за пределы экрана
            else if (obj.y >= HEIGHT) {
                objectsToRemove.add(obj)
            }
        }

        fallingObjects.removeAll(objectsToRemove)
    }

    // Обработка ввода пользователя
    private fun handleInput(input: Char) {
        when (input.uppercaseChar()) {
            'A' -> if (playerX > 1) playerX--
            'D' -> if (playerX < WIDTH - 2) playerX++
            'Q' -> exitProcess(0)
        }
    }

    // Функция для неблокирующего чтения ввода
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

    // Основной игровой цикл
    fun startGame() {
        println("Добро пожаловать в игру Drop Zone")
        println("Уворачивайтесь от красных X (X) и собирайте зеленые O (O)")
        println("Ваша фигурка: ${BlockTypes.PLAYER_COLOR}/A\\${BlockTypes.RESET_COLOR}")
        println("Нажмите Enter чтобы начать...")
        readLine()

        // Игровой цикл
        while (!state.isGameOver) {
            gameTick++

            // Спавн новых объектов (реже)
            if (gameTick % SPAWN_INTERVAL == 0) {
                spawnFallingObject()
            }

            // Постоянное обновление объектов для плавности
            updateFallingObjects()

            updateField()
            render()

            // Обработка ввода
            val input = readInput()
            if (input != null) {
                handleInput(input)
            }

            // Большая задержка для медленной игры
            Thread.sleep(BASE_GAME_SPEED)
        }

        // Цикл после завершения игры
        render()
        while (true) {
            val input = readInput()
            if (input != null && input.uppercaseChar() == 'Q') {
                exitProcess(0)
            }
            Thread.sleep(10)
        }
    }
}

fun main() {
    System.setOut(java.io.PrintStream(System.out, true, "UTF-8"))
    val game = DropZoneGame()
    game.startGame()
}