package games.scenarios;

import java.util.ArrayList;
import java.util.List;

import cecj.app.go.GoGame;

import ec.util.MersenneTwisterFast;
import games.BoardGame;
import games.GameMove;
import games.Player;

public class ALPGameScenario extends GameScenario {

	private Player player;
	private double[] prob;
	private int color;

	public ALPGameScenario(MersenneTwisterFast random, Player player, int color, double[] prob) {
		super(random);
		this.player = player;
		this.color = color;
		this.prob = prob;
	}

	@Override
	public int play(BoardGame game) {
		while (!game.endOfGame()) {
			List<? extends GameMove> moves = game.findMoves();
			if (!moves.isEmpty()) {
				GameMove bestMove = null;
				if (random.nextBoolean(prob[game.getCurrentPlayer()])) {
					bestMove = moves.get(random.nextInt(moves.size()));
				} else if (game.getCurrentPlayer() == color) {
					bestMove = chooseBestMove(game, player, moves);
				} else {
					bestMove = chooseBestALPMove((GoGame) game, moves);
				}
				game.makeMove(bestMove);
			} else {
				game.pass();
			}
		}
		return game.getOutcome();
	}

	private GameMove chooseBestALPMove(GoGame game, List<? extends GameMove> moves) {
		int bestEval = Integer.MIN_VALUE;
		List<GameMove> bestMoves = new ArrayList<GameMove>();

		for (GameMove move : moves) {
			int eval = game.getLibertyDifference(move);
			if (eval == bestEval) {
				bestMoves.add(move);
			} else if (eval > bestEval) {
				bestEval = eval;
				bestMoves.clear();
				bestMoves.add(move);
			}
		}

		return bestMoves.get(random.nextInt(bestMoves.size()));
	}
}
