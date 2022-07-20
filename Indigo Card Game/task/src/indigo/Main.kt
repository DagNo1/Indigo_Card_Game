package indigo

import kotlin.system.exitProcess

val ALLCARDS = listOf(
    "A♠","2♠","3♠","4♠","5♠","6♠","7♠","8♠","9♠","10♠","J♠","Q♠","K♠",
    "A♥","2♥","3♥","4♥","5♥","6♥","7♥","8♥","9♥","10♥","J♥","Q♥","K♥",
    "A♦","2♦","3♦","4♦","5♦","6♦","7♦","8♦","9♦","10♦","J♦","Q♦","K♦",
    "A♣","2♣","3♣","4♣","5♣","6♣","7♣","8♣","9♣","10♣","J♣","Q♣","K♣")
open class Dec(var cards: MutableList<String>){
    init {
        cards.shuffle()
    }
    fun get(noOfCards: Int): MutableList<String>{
        val cardsGotten = mutableListOf<String>()
        for (i in 0 until noOfCards) cardsGotten.add(cards[i])
            repeat(noOfCards) {
                cards.removeAt(0)
            }
        return cardsGotten
    }
    fun add(newCards: MutableList<String>) = cards.addAll(newCards)
    fun count() = cards.lastIndex + 1
    fun drop(pickedCard: Int): String {
        val card = cards[pickedCard]
        cards.removeAt(pickedCard)
        return card
    }
    fun checkRange(number: Int,upperLimit: Int): Boolean = number in 1..upperLimit
}
class Table(cards: MutableList<String>): Dec(cards){
    fun onTop() = cards[cards.lastIndex]
    fun addCard(card: String) {
        cards.add(card)
    }
}
class Computer(cards: MutableList<String>): Dec(cards){
    fun tell(): Int {
        println("Computer plays ${cards[0]}")
        return 0
    }
}
class Player(cards: MutableList<String>, var turn: Int = 0): Dec(cards){
    fun pick(): Int{
        print("Cards in hand: ")
        for (i in 0 until this.count()) print("${i + 1})${cards[i]} ")
        println()
        do{
            println("Choose a card to play (1-${this.count()}):")
            val input = readln()
            if (input == "exit") {
                println("Game Over")
                exitProcess(0)
            }
            try {
                if (checkRange(input.toInt(),cards.count())) return input.toInt() - 1
            } catch (e: NumberFormatException){
                //do nada
            }
        }while(true)
    }
}
fun main() {
    println("Indigo Card Game")
    var binaryAnswer: String
    do {
        println("Play first?")
        binaryAnswer = readln().lowercase()
        if (binaryAnswer == "no" || binaryAnswer == "yes") break
    }while(true)
    val dec = Dec(ALLCARDS.toMutableList())
    val table = Table(dec.get(4))
    val player = Player(dec.get(6))
    val computer = Computer(dec.get(6))
    println("Initial cards on the table: ${table.cards.joinToString(separator = " ")}\n")
    do {
        if (binaryAnswer == "no") player.turn = 1
        for (i in 0..11) {
            println("${table.count()} cards on the table, and the top card is ${table.onTop()}")
            if (i % 2 == player.turn) table.addCard(player.drop(player.pick()))
            else table.addCard(computer.drop(computer.tell()))
            if (table.count() == 52) {
                println("${table.count()} cards on the table, and the top card is ${table.onTop()}")
                println("Game Over")
                return
            }
            if (i == 11) {
                player.add(dec.get(6))
                computer.add(dec.get(6))
            }
        }
    } while (true)
}
