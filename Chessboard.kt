package chess

import java.util.*

class Chessboard {
    private val lineSeparator: String = "  +---+---+---+---+---+---+---+---+"
    private val pawns = mutableListOf<Pawn>()
    var winner: Player? = null


    init {
        for (i in 0..7) {
            pawns.add(Pawn(1, i, Player.WHITE))
            pawns.add(Pawn(6, i, Player.BLACK))
        }
    }

    fun doTurn(turn: Turn): Turn.Result {
        removeEnPassantEntries()
        val type = checkTurnValidity(turn)
        val toPawn = Pawn(
            turn.toRow - 1, turn.toColumn - 1, turn.player
        )
        @Suppress("NON_EXHAUSTIVE_WHEN")
        when (type) {
            Turn.Type.INVALID -> return Turn.Result.INVALID
            Turn.Type.TWO_RANKS_MOVE -> toPawn.enPassant = 2
            Turn.Type.CAPTURE_NORMAL -> pawns.removeIf { pawn -> pawn.row == turn.toRow - 1 && pawn.column == turn.toColumn - 1 }
        }
        pawns.removeIf { pawn -> pawn.row == turn.fromRow - 1 && pawn.column == turn.fromColumn - 1 }
        pawns.add(toPawn)
        val winner = checkWin(toPawn)
        if (winner.player.isPresent) {
            this.winner = winner.player.get()
        }
        return winner.result
    }

    fun print() {
        println(lineSeparator)
        for (i in 7 downTo 0) {
            print("${i + 1} |")
            for (j in 0..7) {
                print(" ${pawnCharAt(j, i).uppercaseChar()} |")
            }
            println()
            println(lineSeparator)
        }
        println("    a   b   c   d   e   f   g   h")
    }

    private fun pawnCharAt(column: Int, row: Int): Char {
        for (pawn in pawns) {
            if (pawn.row == row && pawn.column == column) {
                return pawn.player.char
            }
        }
        return ' '
    }

    private fun removeEnPassantEntries() {
        for (pawn in pawns) {
            pawn.enPassant--
        }
    }

    private fun getPawnAt(row: Int, column: Int): Optional<Pawn> {
        if (isOutOfBoard(row, column)) return Optional.empty()
        for (pawn in pawns) {
            if (pawn.row == row && pawn.column == column) return Optional.of(pawn)
        }
        return Optional.empty()
    }

    private fun isOutOfBoard(row: Int, column: Int): Boolean {
        return (row !in 0..7 || column !in 0..7)
    }

    private fun checkTurnValidity(turn: Turn): Turn.Type {
        if (!turn.matchesRegex()) {
            println("Invalid input")
            return Turn.Type.INVALID
        }
        if (!isPlayerAllowed(turn)) {
            println("No ${turn.player.playerName.lowercase()} pawn at ${turn.from}")
            return Turn.Type.INVALID
        }
        val type = checkMovementValidity(turn)
        if (type == Turn.Type.INVALID) {
            println("Invalid input")
            return Turn.Type.INVALID
        }
        return type
    }

    private fun isPlayerAllowed(turn: Turn): Boolean {
        val char = pawnCharAt(turn.fromColumn - 1, turn.fromRow - 1)
        return char == turn.player.char
    }

    private fun checkMovementValidity(turn: Turn): Turn.Type {
        val columnDifference = turn.fromColumn - turn.toColumn
        val rowDifference: Int = if (turn.player == Player.BLACK)
            turn.fromRow - turn.toRow else turn.toRow - turn.fromRow
        if ((columnDifference == 1 || columnDifference == -1) && rowDifference == 1) {
            return checkCapture(turn)
        }
        if (columnDifference != 0) return Turn.Type.INVALID
        if (turn.fromRow == turn.toRow) return Turn.Type.INVALID
        val char = pawnCharAt(turn.toColumn - 1, turn.toRow - 1)
        if (char != ' ') {
            return Turn.Type.INVALID
        }
        val maxVerticalMove = if (turn.fromRow == 2 || turn.fromRow == 7) 2 else 1
        val moveType: Turn.Type = if (maxVerticalMove == 2) Turn.Type.TWO_RANKS_MOVE else Turn.Type.MOVE
        return if (rowDifference <= 0 || rowDifference > maxVerticalMove) Turn.Type.INVALID else moveType
    }

    private fun checkCapture(turn: Turn): Turn.Type {
        val optPawn = getPawnAt(turn.toRow - 1, turn.toColumn - 1)
        if (optPawn.isEmpty) {
            val enPassantRow = turn.fromRow - 1
            val enPassantColumn = if (turn.player == Player.WHITE) turn.fromColumn - 2 else turn.fromColumn
            val optPawnEnPassant = getPawnAt(enPassantRow, enPassantColumn)
            if (optPawnEnPassant.isPresent && optPawnEnPassant.get().enPassant > 0 && optPawnEnPassant.get().player.opposite() == turn.player) {
                pawns.removeIf { pawn -> pawn.row == enPassantRow && pawn.column == enPassantColumn }
                return Turn.Type.CAPTURE_EN_PASSANT
            }
            return Turn.Type.INVALID
        }
        return if (optPawn.get().player.opposite() != turn.player) Turn.Type.INVALID else Turn.Type.CAPTURE_NORMAL
    }

    private fun checkWin(pawn: Pawn): Winner {
        return when {
            pawn.player == Player.BLACK && pawn.row == 0 || hasNoMorePawns(Player.WHITE) -> Winner(
                Optional.of(Player.BLACK),
                Turn.Result.WIN
            )
            pawn.player == Player.WHITE && pawn.row == 7 || hasNoMorePawns(Player.BLACK) -> Winner(
                Optional.of(Player.WHITE),
                Turn.Result.WIN
            )
            else -> checkStalemateWin()
        }
    }

    private fun checkStalemateWin(): Winner {
        return if (isStalemate()) Winner(result = Turn.Result.STALEMATE) else Winner(result = Turn.Result.NORMAL)
    }

    private fun isStalemate(player: Player): Boolean {
        pawns.filter { pawn -> pawn.player == player }
            .forEach { pawn ->
                if (canMove(pawn))
                    return false
            }
        return true
    }

    private fun isStalemate(): Boolean {
        return isStalemate(Player.WHITE) || isStalemate(Player.BLACK)
    }

    private fun canMove(pawn: Pawn): Boolean {
        return (when (pawn.player) {
            Player.WHITE -> {
                getPawnAt(pawn.row, pawn.column - 1).isEmpty || getPawnAt(
                    pawn.row,
                    pawn.column
                ).isPresent || getPawnAt(pawn.row, pawn.column - 2).isPresent
            }
            Player.BLACK -> {
//                println(pawn)
                val one = getPawnAt(pawn.row - 1, pawn.column)
                val two = getPawnAt(pawn.row - 1, pawn.column - 1)
                val three = getPawnAt(pawn.row - 1, pawn.column + 1)
//                println("$one\n$two\n$three")
                one.isEmpty || two.isPresent || three.isPresent
            }
        })
    }

    private fun hasNoMorePawns(player: Player): Boolean {
        return pawns.none { pawn -> pawn.player == player }
    }

    private data class Winner(val player: Optional<Player> = Optional.empty(), val result: Turn.Result)
}