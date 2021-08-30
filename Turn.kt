package chess

class Turn(private val turn: String, val player: Player) {
    private val regex = Regex("([a-hA-H][1-8]){2}")
    val from = turn.substring(0, 2)
    val to = turn.substring(2, 4)
    val fromRow = Character.getNumericValue(from[1])
    val fromColumn = from[0].minus(96).code
    val toRow = Character.getNumericValue(to[1])
    val toColumn = to[0].minus(96).code

    fun matchesRegex() = turn.matches(regex)

    enum class Type {
        CAPTURE_NORMAL,
        CAPTURE_EN_PASSANT,
        INVALID,
        MOVE,
        TWO_RANKS_MOVE;
    }

    enum class Result {
        NORMAL,
        WIN,
        INVALID,
        STALEMATE;
    }
}