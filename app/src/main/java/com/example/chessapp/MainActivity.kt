package com.example.chessapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChessBoard()
        }
    }
}

fun isValidMove(
    piece: String,
    startRow: Int,
    startCol: Int,
    endRow: Int,
    endCol: Int,
    board: Array<Array<String?>>
): Boolean {
    return when (piece) {
        "♙", "♟" -> isValidPawnMove(piece, startRow, startCol, endRow, endCol, board)
       "♘", "♞" -> isValidKnightMove(piece, startRow, startCol, endRow, endCol, board)
       "♖", "♜" -> isValidRookMove(piece, startRow, startCol, endRow, endCol, board)
       "♗", "♝" -> isValidBishopMove(piece, startRow, startCol, endRow, endCol, board)
       "♕", "♛" -> isValidQueenMove(piece, startRow, startCol, endRow, endCol, board)
        "♔", "♚" -> isValidKingMove(piece, startRow, startCol, endRow, endCol, board)
        else -> false
    }
}



fun isValidPawnMove(
    piece: String,                      // current pawn (♙ = white, ♟ = black)
    fromRow: Int, fromCol: Int,         // where pawn is right now
    toRow: Int, toCol: Int,             // where user wants to move pawn
    board: Array<Array<String?>>        // chessboard state
): Boolean {

    // STEP 1: decide which way pawn moves
    // White pawns (♙) move UP (row -1), black pawns (♟) move DOWN (row +1)
    val direction = if (piece == "♙") -1 else 1

    // STEP 2: single forward move
    // Same column, one step forward, and destination is empty
    if (fromCol == toCol &&
        toRow == fromRow + direction &&
        board[toRow][toCol] == null
    ) {
        return true
    }

    // STEP 3: double forward move (only at starting row)
    // White starts at row 6, black starts at row 1
    // Move two steps if both squares in front are empty
    if (fromCol == toCol &&
        ((piece == "♙" && fromRow == 6) || (piece == "♟" && fromRow == 1)) &&
        toRow == fromRow + 2 * direction &&
        board[fromRow + direction][toCol] == null &&   // square just in front must be empty
        board[toRow][toCol] == null                    // destination must also be empty
    ) {
        return true
    }

    // STEP 4: diagonal capture
    // Move 1 square diagonally forward AND destination must contain opponent piece
    if (kotlin.math.abs(toCol - fromCol) == 1 &&       // column difference = 1
        toRow == fromRow + direction &&               // row difference = 1 step forward
        board[toRow][toCol] != null                   // must capture something
    ) {
        return true
    }

    // STEP 5: none of the above → invalid move
    return false
}



// ✅ Rook (straight lines only)
fun isValidRookMove(
    piece: String,
    fromRow: Int, fromCol: Int,
    toRow: Int, toCol: Int,
    board: Array<Array<String?>>
): Boolean {
    // ❌ If not same row or same col → invalid
    if (fromRow != toRow && fromCol != toCol) return false

    // ✅ Moving horizontally
    if (fromRow == toRow) {
        var c = fromCol + if (toCol > fromCol) 1 else -1
        while (c != toCol) {
            if (board[fromRow][c] != null) return false // blocked
            c += if (toCol > fromCol) 1 else -1
        }
    }

    // ✅ Moving vertically
    if (fromCol == toCol) {
        var r = fromRow + if (toRow > fromRow) 1 else -1
        while (r != toRow) {
            if (board[r][fromCol] != null) return false // blocked
            r += if (toRow > fromRow) 1 else -1
        }
    }

    // ✅ Capture check
    val targetPiece = board[toRow][toCol]
    if (targetPiece != null) {
        val isWhite = piece in listOf("♙","♖","♘","♗","♕","♔")
        val isTargetWhite = targetPiece in listOf("♙","♖","♘","♗","♕","♔")
        if (isWhite == isTargetWhite) return false // cannot capture own piece
    }

    return true
}





// ✅ Knight (L-shape move)
fun isValidKnightMove(
    piece: String,                      // which knight is moving (♘ = white, ♞ = black)
    fromRow: Int, fromCol: Int,         // current position of the knight
    toRow: Int, toCol: Int,             // destination square
    board: Array<Array<String?>>        // current chessboard state
): Boolean {
    // STEP 1: calculate row and column difference
    val rowDiff = kotlin.math.abs(toRow - fromRow)
    val colDiff = kotlin.math.abs(toCol - fromCol)

    // STEP 2: Knight must move in an "L" shape:
    //   → either 2 steps in one direction and 1 in the other
    //   → valid combos: (row=2, col=1) OR (row=1, col=2)
    if (!((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2))) {
        return false
    }

    // STEP 3: Check if destination square has a piece
    val targetPiece = board[toRow][toCol]

    if (targetPiece != null) {
        // Identify piece colors
        val isWhite = piece in listOf("♙","♖","♘","♗","♕","♔")      // current knight color
        val isTargetWhite = targetPiece in listOf("♙","♖","♘","♗","♕","♔") // target piece color

        // ❌ Cannot capture your own piece
        if (isWhite == isTargetWhite) return false
    }

    // STEP 4: If it passed all checks → valid move
    return true
}



// ✅ Bishop (moves diagonally any distance, no jumping)
fun isValidBishopMove(
    piece: String,                      // which bishop is moving (♗ = white, ♝ = black)
    fromRow: Int, fromCol: Int,         // current position
    toRow: Int, toCol: Int,             // destination
    board: Array<Array<String?>>
): Boolean {
    // STEP 1: must move diagonally → |Δrow| == |Δcol| and must actually move
    val rowDiff = kotlin.math.abs(toRow - fromRow)
    val colDiff = kotlin.math.abs(toCol - fromCol)
    if (rowDiff == 0 || rowDiff != colDiff) return false

    // STEP 2: figure out the direction to walk (one step at a time)
    val rowStep = if (toRow > fromRow) 1 else -1
    val colStep = if (toCol > fromCol) 1 else -1

    // STEP 3: walk along the diagonal path and ensure all intermediate squares are empty
    var r = fromRow + rowStep
    var c = fromCol + colStep
    while (r != toRow && c != toCol) {
        if (board[r][c] != null) return false // blocked by any piece
        r += rowStep
        c += colStep
    }

    // STEP 4: target square must be empty or contain opponent’s piece (not same color)
    val targetPiece = board[toRow][toCol]
    if (targetPiece != null) {
        val isWhite = piece in listOf("♙","♖","♘","♗","♕","♔")
        val isTargetWhite = targetPiece in listOf("♙","♖","♘","♗","♕","♔")
        if (isWhite == isTargetWhite) return false // cannot capture your own piece
    }

    // ✅ All checks passed
    return true
}





// ✅ Queen (combination of rook + bishop)
fun isValidQueenMove(
    piece: String,
    fromRow: Int, fromCol: Int,
    toRow: Int, toCol: Int,
    board: Array<Array<String?>>
): Boolean {
    // 👉 Queen is valid if EITHER rook rules OR bishop rules work
    return isValidRookMove(piece, fromRow, fromCol, toRow, toCol, board) ||
            isValidBishopMove(piece, fromRow, fromCol, toRow, toCol, board)
}





// ✅ King (♔, ♚)
// Can move only 1 square in any direction
fun isValidKingMove(
    piece: String,
    fromRow: Int, fromCol: Int,
    toRow: Int, toCol: Int,
    board: Array<Array<String?>>
): Boolean {
    val rowDiff = kotlin.math.abs(toRow - fromRow)
    val colDiff = kotlin.math.abs(toCol - fromCol)

    // ✅ Rule 1: King moves only 1 step in any direction
    if (rowDiff <= 1 && colDiff <= 1) {
        val targetPiece = board[toRow][toCol]

        if (targetPiece != null) {
            // Check colors (white pieces vs black pieces)
            val isWhite = piece in listOf("♙","♖","♘","♗","♕","♔")
            val isTargetWhite = targetPiece in listOf("♙","♖","♘","♗","♕","♔")

            // ❌ Cannot capture own piece
            if (isWhite == isTargetWhite) return false
        }
        return true // ✅ Valid king move
    }

    return false // ❌ More than 1 step = invalid
}



// ✅ Returns true if the current player is in checkmate
fun isCheckmate(isWhiteTurn: Boolean, board: Array<Array<String?>>): Boolean {

    // STEP 1: Check if the king is currently in check
    if (!isKingInCheck(isWhiteTurn, board)) return false // not in check → cannot be checkmate

    // STEP 2: Loop through all squares to find current player's pieces
    for (row in 0 until 8) {
        for (col in 0 until 8) {
            val piece = board[row][col] ?: continue // skip empty squares

            val isWhitePiece = piece in listOf("♙","♖","♘","♗","♕","♔")
            if (isWhitePiece != isWhiteTurn) continue // skip opponent's pieces

            // STEP 3: Try moving this piece to every other square on the board
            for (targetRow in 0 until 8) {
                for (targetCol in 0 until 8) {
                    if (row == targetRow && col == targetCol) continue // cannot move to same square
                    if (!isValidMove(piece, row, col, targetRow, targetCol, board)) continue // skip invalid moves

                    // STEP 4: Simulate the move on a temporary board
                    val newBoard = board.map { it.copyOf() }.toTypedArray()
                    newBoard[targetRow][targetCol] = piece   // move the piece
                    newBoard[row][col] = null                 // empty the original square

                    // STEP 5: If king is safe after this move → not checkmate
                    if (!isKingInCheck(isWhiteTurn, newBoard)) {
                        return false
                    }
                }
            }
        }
    }

    // STEP 6: No valid move can save the king → checkmate!
    return true
}


// ✅ Returns true if the king of the given color is in check
fun isKingInCheck(
    isWhiteKing: Boolean,                // true = white king, false = black king
    board: Array<Array<String?>>         // current board state
): Boolean {
    // STEP 1: Find the king’s position
    var kingRow = -1
    var kingCol = -1
    val kingSymbol = if (isWhiteKing) "♔" else "♚"

    for (r in 0 until 8) {
        for (c in 0 until 8) {
            if (board[r][c] == kingSymbol) {
                kingRow = r
                kingCol = c
                break
            }
        }
        if (kingRow != -1) break
    }

    // Safety check
    if (kingRow == -1) return false // king not found? Should not happen

    // STEP 2: Loop through all opponent pieces
    for (r in 0 until 8) {
        for (c in 0 until 8) {
            val piece = board[r][c] ?: continue

            val isWhitePiece = piece in listOf("♙","♖","♘","♗","♕","♔")
            if (isWhitePiece == isWhiteKing) continue // skip own pieces

            // STEP 3: If opponent piece can move to king → king is in check
            if (isValidMove(piece, r, c, kingRow, kingCol, board)) {
                return true
            }
        }
    }

    // No threats found
    return false
}







// 👇 One chess piece (symbol text inside box)
@Composable
fun Piece(symbol: String) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(48.dp)) {
        Text(text = symbol, fontSize = 32.sp, fontWeight = FontWeight.Bold)
    }
}




// 👇 One chess square (color + optional piece + click handling)
@Composable
fun Square(
    isWhite: Boolean,
    piece: String?,
    row: Int,
    col: Int,
    isSelected: Boolean, // 👈 new parameter
    onClick: (row: Int, col: Int) -> Unit
) {
    val bgColor = when {
        isSelected -> Color(0xFF4CAF50) // 👈 green highlight
        isWhite -> Color(0xFFF0D9B5)    // light square
        else -> Color(0xFFB58863)       // dark square
    }
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(bgColor) //  👈 Use bgcolor Here
            .clickable { onClick(row, col) }, // 👈 call onClick when tapped
        contentAlignment = Alignment.Center
    ) {
        if (piece != null) Piece(piece)
    }
} // square fun end






// 👇 Full chessboard with tap–to–move logic
@Composable
fun ChessBoard() {
    // Initial board setup
    val board = remember {
        mutableStateOf<Array<Array<String?>>>(
            arrayOf(
                arrayOf("♜","♞","♝","♛","♚","♝","♞","♜"),
                arrayOf("♟","♟","♟","♟","♟","♟","♟","♟"),
                arrayOf<String?>(null, null, null, null, null, null, null, null),
                arrayOf<String?>(null, null, null, null, null, null, null, null),
                arrayOf<String?>(null, null, null, null, null, null, null, null),
                arrayOf<String?>(null, null, null, null, null, null, null, null),
                arrayOf("♙","♙","♙","♙","♙","♙","♙","♙"),
                arrayOf("♖","♘","♗","♕","♔","♗","♘","♖"),
            )
        )
    }

    // Track selected square
    var selectedSquare by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    // Track whose turn it is → White starts
    var isWhiteTurn by remember { mutableStateOf(true) }

    // Track king status
    var whiteInCheck by remember { mutableStateOf(false) }
    var blackInCheck by remember { mutableStateOf(false) }
    var whiteCheckmate by remember { mutableStateOf(false) }
    var blackCheckmate by remember { mutableStateOf(false) }

    Column {
        // Show messages
        if (whiteInCheck) Text("White King in Check!", color = Color.Red, fontWeight = FontWeight.Bold)
        if (blackInCheck) Text("Black King in Check!", color = Color.Red, fontWeight = FontWeight.Bold)
        if (whiteCheckmate) Text("White King is Checkmate!", color = Color.Red, fontWeight = FontWeight.ExtraBold)
        if (blackCheckmate) Text("Black King is Checkmate!", color = Color.Red, fontWeight = FontWeight.ExtraBold)
// draw the board
        for (row in 0 until 8) {
            Row {
                for (col in 0 until 8) {
                    val isWhite = (row + col) % 2 == 0
                    val piece = board.value[row][col]


                    // Highlight king in red if in check
                    val isKingInCheckSquare = when (piece) {
                        "♔" -> whiteInCheck
                        "♚" -> blackInCheck
                        else -> false
                    }

                    Square(
                        isWhite = isWhite,
                        piece = board.value[row][col],
                        row = row,
                        col = col,
                        isSelected = selectedSquare?.first == row && selectedSquare?.second == col
                    ) { targetRow, targetCol ->

                        val piece = board.value[targetRow][targetCol]

                        // ✅ Step 1: Selecting a piece
                        if (selectedSquare == null && piece != null) {
                            val isWhitePiece = piece in listOf("♙","♖","♘","♗","♕","♔")
                            if ((isWhiteTurn && isWhitePiece) || (!isWhiteTurn && !isWhitePiece)) {
                                selectedSquare = targetRow to targetCol
                            }
                        }

                        // ✅ Step 2: Moving a piece
                        else if (selectedSquare != null) {
                            val (selectedRow, selectedCol) = selectedSquare!!
                            val selectedPiece = board.value[selectedRow][selectedCol] // piece we move
                            val targetPiece = board.value[targetRow][targetCol]       // piece at destination

                            if (selectedPiece != null) {
                                // ✅ Prevent capturing own piece
                                val isWhiteSelected = selectedPiece in listOf("♙","♖","♘","♗","♕","♔")
                                val isWhiteTarget = targetPiece in listOf("♙","♖","♘","♗","♕","♔")
                                if (targetPiece != null && isWhiteSelected == isWhiteTarget) {
                                    selectedSquare = null // ❌ same-color capture not allowed
                                    return@Square
                                }

                                val isValid = isValidMove(selectedPiece, selectedRow, selectedCol, targetRow, targetCol, board.value)

                                if (isValid) {
                                    //copy board
                                    val newBoard = board.value.map { it.copyOf() }.toTypedArray()
                                    //move piece
                                    newBoard[targetRow][targetCol] = selectedPiece
                                    newBoard[selectedRow][selectedCol] = null

                                    //update board
                                    board.value = newBoard
                                    selectedSquare = null
                                    isWhiteTurn = !isWhiteTurn

                                    // Update king status
                                    whiteInCheck = isKingInCheck(true, board.value)
                                    blackInCheck = isKingInCheck(false, board.value)
                                    whiteCheckmate = isCheckmate(true, board.value)
                                    blackCheckmate = isCheckmate(false, board.value)
                                } else {
                                    selectedSquare = null
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}
