//Сделать хай скор, сделать змейку
//import SnakeStats.SnakePosition
import java.io.File
import kotlin.system.exitProcess
import kotlin.random.Random

fun welcomeMessage() {
    println("╔═══════════════════════════════════════════════════════════╗")
    println("║                   ДОБРО ПОЖАЛОВАТЬ В ЗМЕЙКУ!              ║")
    println("╠═══════════════════════════════════════════════════════════╣")
    println("║ Вы попали в классическую игру 'Змейка' с модификациями!   ║")
    println("║                                                           ║")
    println("║ ЦЕЛЬ ИГРЫ:                                                ║")
    println("║ - Собирайте яблоки (0) для увеличения счета               ║")
    println("║ - Избегайте столкновений со стенами и своим хвостом       ║")
    println("║ - Остерегайтесь плохих яблок (X) - они отнимают жизнь!    ║")
    println("║                                                           ║")
    println("║ УПРАВЛЕНИЕ:                                               ║")
    println("║   W - движение вверх                                      ║")
    println("║   A - движение влево                                      ║")
    println("║   S - движение вниз                                       ║")
    println("║   D - движение вправо                                     ║")
    println("║   X - выход из игры                                       ║")
    println("║                                                           ║")
    println("║ ИГРОВАЯ МЕХАНИКА:                                         ║")
    println("║ • Начальное количество жизней: 3                          ║")
    println("║ • Каждое яблоко: +10 очков                                ║")
    println("║ • С каждым съеденным яблоком скорость увеличивается       ║")
    println("║ • Столкновение или плохое яблоко: -1 жизнь                ║")
    println("║                                                           ║")
    println("╚═══════════════════════════════════════════════════════════╝")
    println()
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

//Заполнение карты
data object FieldSymbols {
    const val horizontal =  "═"
    const val vertical = "║"
    const val northwest = "╝"
    const val southwest = "╗"
    const val northeast = "╚"
    const val southeast = "╔"
    const val barrier = "▒"
    const val emptyspace = " "
    const val apple = "0"
    const val bad_apple = "X"
}

data object SnakeGameConst {
    const val height = 14
    const val width = 50
}

data object SnakeStats {
    var currentscore: Int = 0
    var IsGameOver: Boolean = false
    var SnakePosition = mutableListOf(
        arrayOf(SnakeGameConst.width / 2, 4),
        arrayOf(SnakeGameConst.width / 2, 3),
        arrayOf(SnakeGameConst.width / 2, 2),
        arrayOf(SnakeGameConst.width / 2, 1)
    )
    var ApplePositionX = Random.nextInt(5, SnakeGameConst.width - 5)
    var ApplePositionY = Random.nextInt(2, SnakeGameConst.height - 2)
    var BadApplePositionX = Random.nextInt(5, SnakeGameConst.width - 5)
    var BadApplePositionY = Random.nextInt(2, SnakeGameConst.height - 2)
    var currentlife = 3
    var direction = 'S'
    var gameSpeed = 500L

    fun reset() {
        currentscore = 0
        IsGameOver = false
        SnakePosition = mutableListOf(
            arrayOf(SnakeGameConst.width / 2, 4),
            arrayOf(SnakeGameConst.width / 2, 3),
            arrayOf(SnakeGameConst.width / 2, 2),
            arrayOf(SnakeGameConst.width / 2, 1)
        )
        currentlife = 3
        direction = 'S'
        NewApple()
        NewBadApple()
    }
}
fun NewApple() {
    do {
        SnakeStats.ApplePositionX = Random.nextInt(0, SnakeGameConst.width)
        SnakeStats.ApplePositionY = Random.nextInt(0, SnakeGameConst.height)
    } while (isPositionOnSnake(SnakeStats.ApplePositionX, SnakeStats.ApplePositionY))
}

fun NewBadApple(){
    do {
        SnakeStats.BadApplePositionX = Random.nextInt(0, SnakeGameConst.width)
        SnakeStats.BadApplePositionY = Random.nextInt(0, SnakeGameConst.height)
    } while (isPositionOnSnake(SnakeStats.BadApplePositionX, SnakeStats.BadApplePositionY) ||
        (SnakeStats.BadApplePositionX == SnakeStats.ApplePositionX &&
                SnakeStats.BadApplePositionY == SnakeStats.ApplePositionY))
}

fun isPositionOnSnake(x: Int, y: Int): Boolean {
    return SnakeStats.SnakePosition.any { it[0] == x && it[1] == y }
}

fun SnakeGameRender() {
    println("Current score: ${SnakeStats.currentscore} | Remaining Lives: ${"H".repeat(SnakeStats.currentlife)}")
    println("${FieldSymbols.southeast}${(FieldSymbols.horizontal).repeat(SnakeGameConst.width)}${FieldSymbols.southwest}")

    for (y in 0 until SnakeGameConst.height) {
        print("${FieldSymbols.vertical}")
        for (x in 0 until SnakeGameConst.width) {
            when {
                isPositionOnSnake(x, y) -> print(FieldSymbols.barrier)
                x == SnakeStats.ApplePositionX && y == SnakeStats.ApplePositionY -> print(FieldSymbols.apple)
                x == SnakeStats.BadApplePositionX && y == SnakeStats.BadApplePositionY -> print(FieldSymbols.bad_apple)
                else -> print(FieldSymbols.emptyspace)
            }
        }
        println("${FieldSymbols.vertical}")
    }

    println("${FieldSymbols.northeast}${(FieldSymbols.horizontal).repeat(SnakeGameConst.width)}${FieldSymbols.northwest}")
}

fun ClearField() {
    print("\u001B[H\u001B[2J")
}

fun highscoresave(file: File) {
    file.writeText("A")
}

fun Keypress(input: Char) {
    when (input.uppercaseChar()) {
        'A' -> if (SnakeStats.direction != 'D') SnakeStats.direction = 'A'
        'S' -> if (SnakeStats.direction != 'W') SnakeStats.direction = 'S'
        'W' -> if (SnakeStats.direction != 'S') SnakeStats.direction = 'W'
        'D' -> if (SnakeStats.direction != 'A') SnakeStats.direction = 'D'
        'X' -> SnakeStats.IsGameOver = true
    }
}


fun moveSnake(): Boolean {
    val head = SnakeStats.SnakePosition[0]
    val newHead = when (SnakeStats.direction) {
        'A' -> arrayOf(head[0] - 1, head[1])
        'D' -> arrayOf(head[0] + 1, head[1])
        'W' -> arrayOf(head[0], head[1] - 1)
        'S' -> arrayOf(head[0], head[1] + 1)
        else -> head
    }

    if (newHead[0] < 0 || newHead[0] >= SnakeGameConst.width ||
        newHead[1] < 0 || newHead[1] >= SnakeGameConst.height) {
        return false
    }

    if (isPositionOnSnake(newHead[0], newHead[1])) {
        return false
    }

    var ateApple = false
    var ateBadApple = false

    if (newHead[0] == SnakeStats.ApplePositionX && newHead[1] == SnakeStats.ApplePositionY) {
        SnakeStats.currentscore += 10
        NewApple()
        ateApple = true
        if (SnakeStats.gameSpeed > 50) {
            SnakeStats.gameSpeed -= 2
        }
    }

    if (newHead[0] == SnakeStats.BadApplePositionX && newHead[1] == SnakeStats.BadApplePositionY) {
        SnakeStats.currentlife -= 1
        NewBadApple()
        ateBadApple = true
    }

    SnakeStats.SnakePosition.add(0, newHead)

    if (!ateApple) {
        SnakeStats.SnakePosition.removeAt(SnakeStats.SnakePosition.size - 1)
    }

    return true
}

fun mainMenu() {
    val file = File("Snakehighscore.txt")

    println("Выберите что хотите сделать:")
    println("1. Играть \n 2. Выйти"
    )

    val MenuPlayerInput = readLine()
    val MenuPlayerInputInt = MenuPlayerInput?.toIntOrNull()
    if (MenuPlayerInputInt == 1) {
        SnakeStats.reset()
        ClearField()
        SnakeGame()
    }
    else if (MenuPlayerInputInt == 2) {
        exitProcess(status = 0)
    }
    else {
        println("Вы ввели неправильное значение. Пожалуйста выберите значение из списка.")
        mainMenu()
    }
}

fun SnakeGame() {
    var lastMoveTime = System.currentTimeMillis()

    while (!SnakeStats.IsGameOver && SnakeStats.currentlife > 0) {
        val currentTime = System.currentTimeMillis()

        val input = readInput()
        if (input != null) {
            Keypress(input)
        }

        if (currentTime - lastMoveTime >= SnakeStats.gameSpeed) {
            val validMove = moveSnake()

            if (!validMove) {
                SnakeStats.currentlife -= 1
                if (SnakeStats.currentlife > 0) {
                    Thread.sleep(500)
                }
            }

            ClearField()
            SnakeGameRender()
            lastMoveTime = currentTime
        }
        Thread.sleep(10)
    }
    ClearField()
    println("Game Over")
    println("Score: ${SnakeStats.currentscore}")

}