package chess

enum class Player(val playerName: String, val char: Char) {
    WHITE("White", 'w'),
    BLACK("Black", 'b');

    fun opposite(): Player = if (this == WHITE) BLACK else WHITE
}