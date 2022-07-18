package indigo

val CARDS = listOf(
    "A♠","2♠","3♠","4♠","5♠","6♠","7♠","8♠","9♠","10♠","J♠","Q♠","K♠",
    "A♥","2♥","3♥","4♥","5♥","6♥","7♥","8♥","9♥","10♥","J♥","Q♥","K♥",
    "A♦","2♦","3♦","4♦","5♦","6♦","7♦","8♦","9♦","10♦","J♦","Q♦","K♦",
    "A♣","2♣","3♣","4♣","5♣","6♣","7♣","8♣","9♣","10♣","J♣","Q♣","K♣")
var playingCards = CARDS.toMutableList()
fun main() {
    do {
        println("Choose an action (reset, shuffle, get, exit):")
        when (readln()) {
            "reset" -> reset()
            "shuffle" -> shuffle()
            "get" -> get()
            "exit"-> {
                println("Bye")
                return
            }
            else -> println("Wrong action.")
        }
    }while (true)
}
fun reset() {
    playingCards = CARDS.toMutableList()
    println("Card deck is reset.")
}
fun shuffle(){
    playingCards.shuffle()
    println("Card deck is shuffled.")
}
fun get(){
    println("Number of cards:")
    val noOfCards = readln()
    if (!noOfCards[0].isDigit()) {
        println("Invalid number of cards.")
    } else if (noOfCards.toInt() !in 1..52){
        println("Invalid number of cards.")
    } else if (noOfCards.toInt() > playingCards.lastIndex + 1) {
        println("The remaining cards are insufficient to meet the request.")
    } else {
        for (i in 0 until noOfCards.toInt()) print(playingCards[i] + " ")
        println()
        for (i in 0 until noOfCards.toInt()) if (i != playingCards.lastIndex ) playingCards[i] = playingCards[i + 1] // loop for scotching
        for (i in 0 until noOfCards.toInt()) playingCards.removeAt(playingCards.lastIndex) // removing the cards that are double
    }
}