package indigo

import kotlin.system.exitProcess
val Suits = listOf('♠', '♥', '♦', '♣')
val Ranks = listOf('A','2','3','4','5','6','7','8','9','1','J','Q','K')
val ALL_CARDS = listOf(
    "A♠","2♠","3♠","4♠","5♠","6♠","7♠","8♠","9♠","10♠","J♠","Q♠","K♠",
    "A♥","2♥","3♥","4♥","5♥","6♥","7♥","8♥","9♥","10♥","J♥","Q♥","K♥",
    "A♦","2♦","3♦","4♦","5♦","6♦","7♦","8♦","9♦","10♦","J♦","Q♦","K♦",
    "A♣","2♣","3♣","4♣","5♣","6♣","7♣","8♣","9♣","10♣","J♣","Q♣","K♣")
val SPECIAL_CARDS = listOf(Regex("A."), Regex("10."),Regex("J."), Regex("Q."), Regex("K."))
open class DecType(var cards: MutableList<String>, var score: Int = 0, var noOfWonCards: Int = 0){
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
    fun count() = cards.size
    fun drop(pickedCard: Int): String {
        val card = cards[pickedCard]
        cards.removeAt(pickedCard)
        return card
    }
    fun checkRange(number: Int,upperLimit: Int): Boolean = number in 1..upperLimit
    fun addScore(cardsOnTable: MutableList<String>){
        for (i in 0 until cardsOnTable.size)
            for (j in 0.. 4)
                if (SPECIAL_CARDS[j].matches(cardsOnTable[i])) this.score++
        this.noOfWonCards += cardsOnTable.size
    }
}
class Table(cards: MutableList<String>): DecType(cards){
    fun onTop() = cards[cards.lastIndex]
    fun addCard(card: String) = cards.add(card)
    fun showCards() {
        if (this.count() != 0) println("${this.count()} cards on the table, and the top card is ${this.onTop()}")
        else println("No cards on the table")
    }
}
class Computer(cards: MutableList<String>): DecType(cards){
    fun choose(table: Table): Int {
        for (i in 0 until this.count()) print("${cards[i]} ")
        println()
        if (cards.size == 1) return 0
        if (table.count() == 0) return chosen(cards)
        val candidateCards = mutableListOf<String>()
        for (i in 0 until cards.size) if (checkIfSame(cards[i],table.onTop())) candidateCards.add(cards[i])
        return if (candidateCards.isNotEmpty()) cards.indexOf(candidateCards[chosen(candidateCards)]) else chosen(cards)
    }
    private fun chosen(cards: MutableList<String>): Int{
        val cardsInSuits = MutableList(4){ mutableListOf<Int>() }
        for (i in Suits.indices) {
            for (j in cards.indices) if (cards[j].last() == Suits[i]) cardsInSuits[i].add(j)
        }
        var maxNoOfSuits = 0
        var biggestSuit = -1
        for (i in cardsInSuits.indices) if (maxNoOfSuits < cardsInSuits[i].size) {
            maxNoOfSuits = cardsInSuits[i].size
            biggestSuit = i
        }
        if (maxNoOfSuits != 1) return cardsInSuits[biggestSuit][0]
        val cardsInRanks = MutableList(13){ mutableListOf<Int>() }
        for (i in Ranks.indices) {
            for (j in cards.indices) if (cards[j][0] == Ranks[i]) cardsInRanks[i].add(j)
        }
        var maxNoOfRanks = 0
        var biggestRank = -1
        for (i in cardsInRanks.indices) if (maxNoOfRanks < cardsInRanks[i].size) {
            maxNoOfRanks = cardsInRanks[i].size
            biggestRank = i
        }
        return if (maxNoOfRanks != 1) cardsInRanks[biggestRank][0] else 0
    }
}
class Player(cards: MutableList<String>, var turn: Int = 0): DecType(cards){
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
    val decType = DecType(ALL_CARDS.toMutableList())
    val table = Table(decType.get(4))
    val player = Player(decType.get(6))
    val computer = Computer(decType.get(6))
    println("Initial cards on the table: ${table.cards.joinToString(separator = " ")}\n")
    if (binaryAnswer == "no") player.turn = 1
    var i = 0
    var whoWonLast = -1
    do {
        table.showCards()
        var matches = false
        if (i % 2 == player.turn) {
            val droppingCard = player.drop(player.pick()) // picking card
            if(table.count() != 0) matches = checkIfSame(table.onTop(), droppingCard)
            table.addCard(droppingCard)
            if (matches) {
                player.addScore(table.get(table.count()))
                println("Player wins cards")
                whoWonLast = 0
                showScore(player, computer)
                println()
            }
        } else {
            val droppingCard = computer.drop(computer.choose(table))
            println("Computer plays $droppingCard")
            if(table.count() != 0) matches = checkIfSame(table.onTop(), droppingCard)
            table.addCard(droppingCard)
            if (matches) {
                computer.addScore(table.get(table.count()))
                println("Computer wins cards")
                whoWonLast = 1
                showScore(player, computer)
                println()
            }
        }
        gameOver(table, player, computer, whoWonLast)
        if (i == 11) {
            player.add(decType.get(6))
            computer.add(decType.get(6))
            i = -1
        }
        i++
    } while (true)
}
fun checkIfSame(card1: String, card2: String): Boolean {
    val suit  = "..?${card1.last()}".toRegex()
    val rank = "${card1[0]}.?.?".toRegex()
    return suit.matches(card2) || rank.matches(card2)
}
fun showScore(player: Player, computer: Computer) {
    println("Score: Player ${player.score} - Computer ${computer.score}")
    println("Cards: Player ${player.noOfWonCards} - Computer ${computer.noOfWonCards}\n")
}
fun gameOver(table: Table, player: Player, computer: Computer, wonLast: Int){
    if (table.count() + player.noOfWonCards + computer.noOfWonCards != 52) return
    println()
    table.showCards()
    if (wonLast == 0 && table.count() != 0)  player.addScore(table.get(table.count()))
    else if (wonLast == 1 && table.count() != 0) computer.addScore(table.get(table.count()))
    else if (wonLast == -1) if (player.turn == 0) player.addScore(table.get(table.count())) else computer.addScore(table.get(table.count()))
    when {
        player.noOfWonCards > computer.noOfWonCards -> player.score += 3
        player.noOfWonCards < computer.noOfWonCards -> computer.score += 3
        player.noOfWonCards == computer.noOfWonCards -> if (player.turn == 0) player.score += 3 else computer.score += 3
    }
    showScore(player, computer)
    println("Game Over")
    exitProcess(0)
}
