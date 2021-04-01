package de.chess.ai;

import de.chess.game.Board;
import de.chess.game.Move;
import de.chess.game.MoveGenerator;
import de.chess.game.MoveList;
import de.chess.game.Winner;
import de.chess.ui.WidgetUI;
import de.chess.util.MathUtil;

public class AlphaBetaAI {
	
	private static final int INFINITY = 1000000;
	
	private static final int MATE_SCORE = 100000;
	
	private static final int ALLOCATED_TIME = 3000;
	
	private static final int MAX_CHECKING_MOVES_DEPTH = -11;
	
	private static Move responseMove;
	
	private static long visitedNormalNodes;
	private static long visitedQuiesceNodes;
	private static int transpositionUses;
	
	public static Move findNextMove(Board b) {
		System.out.println("------");
		
		long before = System.currentTimeMillis();
		
		visitedNormalNodes = 0;
		visitedQuiesceNodes = 0;
		transpositionUses = 0;
		
		int score = 0;
		
		int depth = 1;
		
		while(System.currentTimeMillis() - before < ALLOCATED_TIME) {
			score = startSearch(b, depth);
			
			System.out.println("depth "+depth+" search complete");
			
			depth++;
		}
		
		float time = (System.currentTimeMillis() - before) / 1000f;
		
		long visitedNodes = visitedNormalNodes + visitedQuiesceNodes;
		
		System.out.println("---");
		System.out.println("time: "+MathUtil.DECIMAL_FORMAT.format(time)+"s");
		System.out.println("prediction: "+MathUtil.DECIMAL_FORMAT.format(score));
		System.out.println("visited_nodes: "+MathUtil.DECIMAL_FORMAT.format(visitedNodes));
		System.out.println("nodes_per_second: "+MathUtil.DECIMAL_FORMAT.format(visitedNodes / time));
		System.out.println("visited_normal_nodes: "+MathUtil.DECIMAL_FORMAT.format(visitedNormalNodes));
		System.out.println("visited_quiesce_nodes: "+MathUtil.DECIMAL_FORMAT.format(visitedQuiesceNodes));
		System.out.println("transposition_uses: "+MathUtil.DECIMAL_FORMAT.format(transpositionUses));
		
		WidgetUI.setPrediction(score);
		WidgetUI.addToEvalHistory(score);
		
		return responseMove;
	}
	
	private static int startSearch(Board b, int depth) {
		int score = runAlphaBeta(b, -INFINITY, INFINITY, depth);
		
		return score;
	}
	
	private static int runAlphaBeta(Board b, int alpha, int beta, int depth) {
		visitedNormalNodes++;
		
		MoveList list = new MoveList();
		
		MoveGenerator.generateAllMoves(b, list);
		
		MoveEvaluator.eval(list, b);
		
		TranspositionEntry entry = TranspositionTable.getEntry(b.getPositionKey());
		
		if(entry != null && entry.getMove() != null) {
			list.applyMoveScore(entry.getMove(), MoveEvaluator.HASH_MOVE_SCORE);
		}
		
		applyKillerMoves(list, b.getHistoryPly());
		
		Move bestMove = null;
		
		while(list.hasMovesLeft()) {
			Move m = list.next();
			
			b.makeMove(m);
			
			if(!b.isOpponentInCheck()) {
				int score = -alphaBeta(b, 1, -beta, -alpha, depth - 1);
				
				if(score > alpha) {
					bestMove = m;
					alpha = score;
				}
			}
			
			b.undoMove(m);
		}
		
		TranspositionTable.putEntry(b.getPositionKey(), depth, bestMove, TranspositionEntry.TYPE_EXACT, alpha, b.getHistoryPly());
		
		responseMove = bestMove;
		
		return alpha;
	}
	
	private static int alphaBeta(Board b, int plyFromRoot, int alpha, int beta, int depth) {
		if(depth == 0) {
			return quiesce(b, plyFromRoot, alpha, beta, depth);
		}
		
		visitedNormalNodes++;
		
		if(b.getFiftyMoveCounter() == 100 || b.hasThreefoldRepetition()) return 0;
		
		TranspositionEntry entry = TranspositionTable.getEntry(b.getPositionKey());
		
		if(entry != null && entry.getDepth() >= depth) {
			transpositionUses++;
			
			if(entry.getType() == TranspositionEntry.TYPE_EXACT) return entry.getScore();
			else if(entry.getType() == TranspositionEntry.TYPE_LOWER_BOUND) alpha = Math.max(alpha, entry.getScore());
			else beta = Math.min(beta, entry.getScore());
			
			if(alpha >= beta) return entry.getScore();
		}
		
		int type = TranspositionEntry.TYPE_UPPER_BOUND;
		
		MoveList list = new MoveList();
		
		MoveGenerator.generateAllMoves(b, list);
		
		MoveEvaluator.eval(list, b);
		
		if(entry != null && entry.getMove() != null) {
			list.applyMoveScore(entry.getMove(), MoveEvaluator.HASH_MOVE_SCORE);
		}
		
		applyKillerMoves(list, b.getHistoryPly());
		
		boolean hasLegalMove = false;
		
		Move bestMove = null;
		int bestScore = Integer.MIN_VALUE;
		
		while(list.hasMovesLeft()) {
			Move m = list.next();
			
			b.makeMove(m);
			
			if(!b.isOpponentInCheck()) {
				hasLegalMove = true;
				
				int score = -alphaBeta(b, plyFromRoot + 1, -beta, -alpha, depth - 1);
				
				if(score > bestScore) {
					bestMove = m;
					bestScore = score;
				}
				
				if(score > alpha) {
					alpha = score;
					
					type = TranspositionEntry.TYPE_EXACT;
				}
			}
			
			b.undoMove(m);
			
			if(alpha >= beta) {
				KillerTable.storeMove(m, b.getHistoryPly());
				
				TranspositionTable.putEntry(b.getPositionKey(), depth, bestMove, TranspositionEntry.TYPE_LOWER_BOUND, beta, b.getHistoryPly());
				
				return alpha;
			}
		}
		
		if(!hasLegalMove) {
			int winner = b.findWinner(false);
			
			int score;
			
			if(winner == Winner.DRAW) {
				score = 0;
			} else {
				int i = MATE_SCORE - plyFromRoot;
				
				score = b.getSide() == winner ? i : -i;
			}
			
			TranspositionTable.putEntry(b.getPositionKey(), depth, null, TranspositionEntry.TYPE_EXACT, score, b.getHistoryPly());
			
			return score;
		}
		
		TranspositionTable.putEntry(b.getPositionKey(), depth, bestMove, type, alpha, b.getHistoryPly());
		
		return alpha;
	}
	
	private static int quiesce(Board b, int plyFromRoot, int alpha, int beta, int depth) {
		visitedQuiesceNodes++;
		
		if(b.getFiftyMoveCounter() == 100 || b.hasThreefoldRepetition()) return 0;
		
		TranspositionEntry entry = TranspositionTable.getEntry(b.getPositionKey());
		
		if(entry != null && entry.getDepth() >= depth) {
			transpositionUses++;
			
			if(entry.getType() == TranspositionEntry.TYPE_EXACT) return entry.getScore();
			else if(entry.getType() == TranspositionEntry.TYPE_LOWER_BOUND) alpha = Math.max(alpha, entry.getScore());
			else beta = Math.min(beta, entry.getScore());
			
			if(alpha >= beta) return entry.getScore();
		}
		
		boolean inCheck = b.isSideInCheck();
		
		if(!inCheck) {
			int evalScore = Evaluator.eval(b);
			
			if(evalScore >= beta) return beta;
			
			if(evalScore > alpha) alpha = evalScore;
		}
		
		int type = TranspositionEntry.TYPE_UPPER_BOUND;
		
		MoveList list = new MoveList();
		
		MoveGenerator.generateAllMoves(b, list);
		
		MoveEvaluator.eval(list, b);
		
		if(entry != null && entry.getMove() != null) {
			list.applyMoveScore(entry.getMove(), MoveEvaluator.HASH_MOVE_SCORE);
		}
		
		applyKillerMoves(list, b.getHistoryPly());
		
		MoveList checkingMoves = new MoveList();
		
		boolean allowCheckingMoves = depth > MAX_CHECKING_MOVES_DEPTH;
		
		boolean hasLegalMove = false;
		boolean hasAlphaRisen = false;
		
		Move bestMove = null;
		int bestScore = Integer.MIN_VALUE;
		
		while(list.hasMovesLeft()) {
			Move m = list.next();
			
			b.makeMove(m);
			
			int score = 0;
			boolean hasDoneMove = false;
			
			if(!b.isOpponentInCheck()) {
				hasLegalMove = true;
				
				if(inCheck || m.getCaptured() != 0) {
					hasDoneMove = true;
					
					score = -quiesce(b, plyFromRoot + 1, -beta, -alpha, depth - 1);
					
					if(score > bestScore) {
						bestMove = m;
						bestScore = score;
					}
					
					if(score > alpha) {
						alpha = score;
						
						hasAlphaRisen = true;
						
						type = TranspositionEntry.TYPE_EXACT;
					}
				} else if(allowCheckingMoves && !inCheck && b.isSideInCheck()) {
					checkingMoves.addMove(m);
				}
			}
			
			b.undoMove(m);
			
			if(hasDoneMove && score >= beta) {
				KillerTable.storeMove(m, b.getHistoryPly());
				
				TranspositionTable.putEntry(b.getPositionKey(), depth, bestMove, TranspositionEntry.TYPE_LOWER_BOUND, beta, b.getHistoryPly());
				
				return beta;
			}
		}
		
		if(!hasLegalMove) {
			int winner = b.findWinner(false);
			
			int score;
			
			if(winner == Winner.DRAW) {
				score = 0;
			} else {
				int i = MATE_SCORE - plyFromRoot;
				
				score = b.getSide() == winner ? i : -i;
			}
			
			TranspositionTable.putEntry(b.getPositionKey(), depth, null, TranspositionEntry.TYPE_EXACT, score, b.getHistoryPly());
			
			return score;
		}
		
		if(allowCheckingMoves && !inCheck && !hasAlphaRisen) {
			checkingMoves.reset();
			
			while(checkingMoves.hasMovesLeft()) {
				Move m = checkingMoves.next();
				
				b.makeMove(m);
				
				int score = -quiesce(b, plyFromRoot + 1, -beta, -alpha, depth - 1);
				
				if(bestMove == null || score > bestScore) {
					bestMove = m;
					bestScore = score;
				}
				
				if(score > alpha) {
					alpha = score;
					
					type = TranspositionEntry.TYPE_EXACT;
				}
				
				b.undoMove(m);
				
				if(score >= beta) {
					KillerTable.storeMove(m, b.getHistoryPly());
					
					TranspositionTable.putEntry(b.getPositionKey(), depth, bestMove, TranspositionEntry.TYPE_LOWER_BOUND, beta, b.getHistoryPly());
					
					return beta;
				}
			}
		}
		
		TranspositionTable.putEntry(b.getPositionKey(), depth, bestMove, type, alpha, b.getHistoryPly());
		
		return alpha;
	}
	
	private static void applyKillerMoves(MoveList list, int ply) {
		for(int i=0; i<KillerTable.SIZE; i++) {
			Move killer = KillerTable.getMove(ply, i);
			
			if(killer != null) list.applyMoveScore(killer, MoveEvaluator.KILLER_MOVE_SCORE);
		}
	}
	
}
