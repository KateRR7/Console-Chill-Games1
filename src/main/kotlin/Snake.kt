//Сделать хай скор, сделать змейку
//import SnakeStats.SnakePosition
import java.io.File
import kotlin.system.exitProcess
import kotlin.random.Random

fun main() { //начало игры
    println("Добро пожаловать в игру 'Змейка!'")
    mainMenu()
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
    val gameField = Array(height) { Array(width) { FieldSymbols.emptyspace } }

}

data object SnakeStats {
    var currentscore: Int = 0
    var IsGameOver: Boolean = false
    var SnakePosition = arrayOf(arrayOf(SnakeGameConst.width/2,1), arrayOf(SnakeGameConst.width/2,2),arrayOf(SnakeGameConst.width/2,3),arrayOf(SnakeGameConst.width/2,4))
    var ApplePositionX = Random.nextInt(0, SnakeGameConst.width)
    var ApplePositionY = Random.nextInt(0, SnakeGameConst.height)
    var BadApplePositionX = Random.nextInt(0, SnakeGameConst.width)
    var BadApplePositionY = Random.nextInt(0, SnakeGameConst.height)
    var currentlife = 3
}
fun NewApple() {
    SnakeStats.ApplePositionX = Random.nextInt(0, SnakeGameConst.width)
    SnakeStats.ApplePositionY = Random.nextInt(0, SnakeGameConst.height)
}

fun NewBadApple(){
    SnakeStats.BadApplePositionX = Random.nextInt(0, SnakeGameConst.width)
    SnakeStats.BadApplePositionY = Random.nextInt(0, SnakeGameConst.height)
}

fun SnakeGameRender() {

    println("Current score: ${SnakeStats.currentscore} ")
    println("${FieldSymbols.southeast}${(FieldSymbols.horizontal).repeat(SnakeGameConst.width)}${FieldSymbols.southwest}")
    for (y in 0 until SnakeGameConst.height) {
        print("${FieldSymbols.vertical}")
        for (x in 0 until SnakeGameConst.width) {
            val block = SnakeGameConst.gameField[y][x]
            for (i in SnakeStats.SnakePosition)
            {        if ((i[0] ==x) and (i[1] == y)) {
                print(FieldSymbols.barrier)
                }
            }
            if ((x == SnakeStats.ApplePositionX) and (y == SnakeStats.ApplePositionY)) {
                print(FieldSymbols.apple)
            }

            else {
                print(block)
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

private fun Keypress(input: Char) {
    when (input.uppercaseChar()) {
        'A' -> if ( SnakeStats.SnakePosition[0][0] > 0)  SnakeStats.SnakePosition[0][0]--
        'S' -> if ( SnakeStats.SnakePosition[0][1] < SnakeGameConst.height-1)  SnakeStats.SnakePosition[0][1]++
        'W' -> if (SnakeStats.SnakePosition[0][1] > 0) SnakeStats.SnakePosition[0][1]--
        'D' -> if ( SnakeStats.SnakePosition[0][0] < SnakeGameConst.width-1)  SnakeStats.SnakePosition[0][0]++
        'X' -> SnakeStats.currentlife = 0
    }
}

fun mainMenu() {
    val file = File("Snakehighscore.txt")

    println("Выберите что хотите сделать:")
    println("1. Играть \n 2. Выйти"
    )

    val MenuPlayerInput = readLine()
    val MenuPlayerInputInt = MenuPlayerInput!!?.toIntOrNull()
    if (MenuPlayerInputInt == 1) {
        ClearField()
        //SnakeGameRender()
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
    while (SnakeStats.IsGameOver == false){
        SnakeGameRender()
        val input = readInput()
        if (input != null) {
            Keypress(input)
        }
        if ((SnakeStats.SnakePosition[0][0] == SnakeStats.ApplePositionX) and (SnakeStats.SnakePosition[0][1] == SnakeStats.ApplePositionY)) {
            SnakeStats.currentscore += 10
            NewApple()
        }
        if ((SnakeStats.SnakePosition[0][0] == SnakeStats.BadApplePositionX) and (SnakeStats.SnakePosition[0][1] == SnakeStats.BadApplePositionY)) {
            SnakeStats.currentlife -= 1
            NewApple()
        }
        if (SnakeStats.currentlife == 0) {
            break
        }
    }
    println("Game Over")
    println("Score: ${SnakeStats.currentscore}")
}



