package chess

var turns: Int = 0

fun main() {
    val chessboard = Chessboard()
    println("Pawns-Only Chess")
    val firstPlayerName = getPlayerName("First")
    val secondPlayerName = getPlayerName("Second")
    chessboard.print()
    turns = 0
    var input: String
    while (true) {
        println(if (turns % 2 == 0) "$firstPlayerName's turn:" else "$secondPlayerName's turn:")
        input = readLine()!!
        val turn = Turn(input, getPlayer())
        if (input == "exit")
            break
        val result = chessboard.doTurn(turn)
        when (result) {
            Turn.Result.NORMAL -> chessboard.print()
            Turn.Result.WIN -> {
                chessboard.print()
                var winner = chessboard.winner!!.name.lowercase()
                winner = winner.replaceFirstChar { c: Char -> c.uppercaseChar() }
                println()
                println("$winner Wins!")
                break
            }
            Turn.Result.INVALID -> {
                turns--
            }
            Turn.Result.STALEMATE -> {
                chessboard.print()
                println("Stalemate!")
                break
            }
        }
        turns++
    }
    println("Bye!")
}

private fun getPlayer() = if (turns % 2 == 0) Player.WHITE else Player.BLACK

private fun getPlayerName(prefix: String): String {
    println("$prefix Player's name:")
    return readLine()!!
}
