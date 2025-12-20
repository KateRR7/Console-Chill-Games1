package main

import WordGame
import WordlyGame
import mainMenu
import welcomeMessage

fun cyan(text: String) = "\u001B[36m$text\u001B[0m"
fun purple(text: String) = "\u001B[35m$text\u001B[0m"
fun main() {
    while (true) {
        System.setOut(java.io.PrintStream(System.out, true, "UTF-8"))
        println(cyan("Привет!Ты зашёл в консольное мини приложение Console-Chill-Games."))
        println(cyan("Здесь ты можешь отдохнуть и поиграть в наши мини игры."))
        println(cyan("Советую сделать окно консоли побольше."))
        println("                                                  ")
        Thread.sleep(7000L)

        println(purple("Если хочешь поиграть в DropZoneGame - введи 1."))
        println(purple("Если хочешь поиграть в WordlyGame - введи 2."))
        println(purple("Если хочешь поиграть в WordsNames - введи 3."))
        println(purple("Если хочешь поиграть в Snake - введи 4."))
        println(purple("Если хочешь выйти из приложения - введи 5."))
        println(purple("Выбирай игру!"))
        val input = readln().toInt()
        if (input == 1){
            val game = DropZoneGame()
            game.startGame()
        }
        if (input == 2) {
            val game = WordlyGame()
            game.start()
        }
        if (input == 3){

            val wordspath = "C:\\Users\\timur\\Documents\\Console-Chill-Games1\\src\\main\\resources\\Файл с словами.txt"
            val words = wordspath.trim()
            val namespath = "C:\\Users\\timur\\Documents\\Console-Chill-Games1\\src\\main\\resources\\Файл с именами.txt"
            val names = namespath.trim()

            val game = WordGame(words, names)
            game.start()
        }
        if (input == 5) {
            break
        }
        if (input == 4) {
            welcomeMessage()
            mainMenu()
        }

    }
}





