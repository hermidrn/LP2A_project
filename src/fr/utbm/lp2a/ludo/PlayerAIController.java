package fr.utbm.lp2a.ludo;

public class PlayerAIController {

    Game game;

    PlayerAIController(Game game) {
        this.game = game;
    }

    // Determine the piece that should be played
    public Piece getPieceToPlay() {
        Player player = game.getTurnPlayer();

        // Rule 1: Try to capture a piece
        for (int i = 0; i < 4; i++) {
            if (!game.getTurnPlayer().getPiece(i).isBlocked() && !BoardPosition.isSafeSquare(game.getTurnPlayer().getPiece(i).getPosition())) {
                for (Piece piece : game.piecesOnSquare(game.getTurnPlayer().getPiece(i).getAbsolutePosition()+game.getLastDiceResult())) {
                    if (piece.getColor()!=game.getTurnPlayer().getPiece(i).getColor() && game.isPlayableInGameContext(game.getTurnPlayer().getPiece(i))) {
                        return game.getTurnPlayer().getPiece(i);
                    }
                }
            }
        }

        // Rule 2: Move a piece out of the starting block if it's further than halfway across the board
        for (int j = 0; j < 4; j++) {
            Piece piece = player.getPiece(j);
            // If a piece is playable
            if (game.isPlayableInGameContext(player.getPiece(j))) {
                // if the last dice result is 6
                if (game.getLastDiceResult() == 6) {
                    // if a piece is already in game
                    if (piece.isOnTrack()) {
                        // if this piece is more than halfway across the board
                        if (piece.getPosition() >= 28) {
                            // move out a piece from the starting block
                            for (int h = 0; h < 4; h++) {
                                if (player.getPiece(h).getPosition() == -1 && game.isPlayableInGameContext(player.getPiece(h))) {
                                    return player.getPiece(h);
                                }
                            }
                        }
                        else {
                            return player.getPiece(j);
                        }
                    }
                }
            }
        }

        // If no rule was applied, play the first available piece
        for (int i=0; i<4; i++) {
            if (game.isPlayableInGameContext(player.getPiece(i))) {
                return player.getPiece(i);
            }
        }
        // Java requires to return something but this case should not happen as at least 1 piece is playable when this method is called
        return null;
    }
}
