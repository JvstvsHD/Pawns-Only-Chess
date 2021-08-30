package chess

class Pawn(val row: Int, val column: Int, val player: Player) {

    var enPassant: Int = 0
    override fun toString(): String {
        return "Pawn(row=$row, column=$column, player=$player, enPassantPossible=$enPassant)"
    }


}