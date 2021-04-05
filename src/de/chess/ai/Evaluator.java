package de.chess.ai;

import de.chess.game.Board;
import de.chess.game.PieceCode;

public class Evaluator {
	
	private static final int PAWN_VALUE_MG = 126;
	private static final int KNIGHT_VALUE_MG = 781;
	private static final int BISHOP_VALUE_MG = 825;
	private static final int ROOK_VALUE_MG = 1276;
	private static final int QUEEN_VALUE_MG = 2538;
	
	private static final int PAWN_VALUE_EG = 208;
	private static final int KNIGHT_VALUE_EG = 854;
	private static final int BISHOP_VALUE_EG = 915;
	private static final int ROOK_VALUE_EG = 1380;
	private static final int QUEEN_VALUE_EG = 2682;
	
	public static final int GENERIC_PAWN_VALUE = (PAWN_VALUE_MG + PAWN_VALUE_EG) / 2;
	
	private static final int TEMPO_BONUS = 28;
	
	private static final int[] PAWN_TABLE_MG = new int[] {
			   0,   0,   0,   0,   0,   0,   0,   0,
			   2,   4,  11,  18,  16,  21,   9,  -3,
			  -9, -15,  11,  15,  31,  23,   6, -20,
			  -3, -20,   8,  19,  39,  17,   2,  -5,
			  11,  -4, -11,   2,  11,   0, -12,   5,
			   3, -11,  -6,  22,  -8,  -5, -14, -11,
			  -7,   6,  -2, -11,   4, -14,  10,  -9,
			   0,   0,   0,   0,   0,   0,   0,   0
	};
	
	private static final int[] PAWN_TABLE_EG = new int[] {
			   0,   0,   0,   0,   0,   0,   0,   0,
			  -8,  -6,   9,   5,  16,   6,  -6, -18,
			  -9,  -7, -10,   5,   2,   3,  -8,  -5,
			   7,   1,  -8,  -2, -14, -13, -11,  -6,
			  12,   6,   2,  -6,  -5,  -4,  14,   9,
			  27,  18,  19,  29,  30,   9,   8,  14,
			  -1, -14,  13,  22,  24,  17,   7,   7,
			   0,   0,   0,   0,   0,   0,   0,   0
	};
	
	private static final int[] KNIGHT_TABLE_MG = new int[] {
			-175, -92, -74, -73,
			 -77, -41, -27, -15,
			 -61, -17,   6,  12,
			 -35,   8,  40,  49,
			 -34,  13,  44,  51,
			  -9,  22,  58,  53,
			 -67, -27,   4,  37,
			-201, -83, -56, -26
	};
	
	private static final int[] KNIGHT_TABLE_EG = new int[] {
			 -96, -65, -49, -21,
			 -67, -54, -18,   8,
			 -40, -27,  -8,  29,
			 -35,  -2,  13,  28,
			 -45, -16,   9,  39,
			 -51, -44, -16,  17,
			 -69, -50, -51,  12,
			-100, -88, -56, -17
	};
	
	private static final int[] BISHOP_TABLE_MG = new int[] {
			 -37,  -4,  -6, -16,
			 -11,   6,  13,   3,
			  -5,  15,  -4,  12,
			  -4,   8,  18,  27,
			  -8,  20,  15,  22,
			 -11,   4,   1,   8,
			 -12, -10,   4,   0,
			 -34,   1, -10, -16
	};
	
	private static final int[] BISHOP_TABLE_EG = new int[] {
			 -40, -21, -26,  -8,
			 -26,  -9, -12,   1,
			 -11,  -1,  -1,   7,
			 -14,  -4,   0,  12,
			 -12,  -1, -10,  11,
			 -21,   4,   3,   4,
			 -22, -14,  -1,   1,
			 -32, -29, -26, -17
	};
	
	private static final int[] ROOK_TABLE_MG = new int[] {
			 -31, -20, -14,  -5,
			 -21, -13,  -8,   6,
			 -25, -11,  -1,   3,
			 -13,  -5,  -4,  -6,
			 -27, -15,  -4,   3,
			 -22,  -2,   6,  12,
			  -2,  12,  16,  18,
			 -17, -19,  -1,   9
	};
	
	private static final int[] ROOK_TABLE_EG = new int[] {
			  -9, -13, -10,  -9,
			 -12,  -9,  -1,  -2,
			   6,  -8,  -2,  -6,
			  -6,   1,  -9,   7,
			  -5,   8,   7,  -6,
			   6,   1,  -7,  10,
			   4,   5,  20,  -5,
			  18,   0,  19,  13
	};
	
	private static final int[] QUEEN_TABLE_MG = new int[] {
			   3,  -5,  -5,   4,
			  -3,   5,   8,  12,
			  -3,   6,  13,   7,
			   4,   5,   9,   8,
			   0,  14,  12,   5,
			  -4,  10,   6,   8,
			  -5,   6,  10,   8,
			  -2,  -2,   1,  -2
	};
	
	private static final int[] QUEEN_TABLE_EG = new int[] {
			 -69, -57, -47, -26,
			 -54, -31, -22,  -4,
			 -39, -18,  -9,   3,
			 -23,  -3,  13,  24,
			 -29,  -6,   9,  21,
			 -38, -18, -11,   1,
			 -50, -27, -24,  -8,
			 -74, -52, -43, -34
	};
	
	private static final int[] KING_TABLE_MG = new int[] {
			 271, 327, 271, 198,
			 278, 303, 234, 179,
			 195, 258, 169, 120,
			 164, 190, 138,  98,
			 154, 179, 105,  70,
			 123, 145,  81,  31,
			  88, 120,  65,  33,
			  59,  89,  45,  -1
	};
	
	private static final int[] KING_TABLE_EG = new int[] {
			   1,  45,  85,  76,
			  53, 100, 133, 135,
			  88, 130, 169, 175,
			 103, 156, 172, 172,
			  96, 166, 199, 199,
			  92, 172, 184, 191,
			  47, 121, 116, 131,
			  11,  59,  73,  78
	};
	
	private static final int[][] TABLES_MG = new int[][] {
			null,
			null,
			PAWN_TABLE_MG,
			KNIGHT_TABLE_MG,
			BISHOP_TABLE_MG,
			ROOK_TABLE_MG,
			QUEEN_TABLE_MG,
			KING_TABLE_MG,
			KING_TABLE_MG
	};
	
	private static final int[][] TABLES_EG = new int[][] {
			null,
			null,
			PAWN_TABLE_EG,
			KNIGHT_TABLE_EG,
			BISHOP_TABLE_EG,
			ROOK_TABLE_EG,
			QUEEN_TABLE_EG,
			KING_TABLE_EG,
			KING_TABLE_EG
	};
	
	private static final int[] PIECE_VALUES_MG = new int[] {
			0,
			0,
			PAWN_VALUE_MG,
			KNIGHT_VALUE_MG,
			BISHOP_VALUE_MG,
			ROOK_VALUE_MG,
			QUEEN_VALUE_MG
	};
	
	private static final int[] PIECE_VALUES_EG = new int[] {
			0,
			0,
			PAWN_VALUE_EG,
			KNIGHT_VALUE_EG,
			BISHOP_VALUE_EG,
			ROOK_VALUE_EG,
			QUEEN_VALUE_EG
	};
	
	private static final int[] MIRROR_TABLE = new int[] {
			56,  57,  58,  59,  60,	 61,  62,  63,
			48,	 49,  50,  51,  52,	 53,  54,  55,
			40,	 41,  42,  43,  44,	 45,  46,  47,
			32,	 33,  34,  35,  36,	 37,  38,  39,
			24,	 25,  26,  27,  28,	 29,  30,  31,
			16,  17,  18,  19,  20,	 21,  22,  23,
			 8,   9,  10,  11,  12,  13,  14,  15,
			 0,   1,   2,   3,   4,   5,   6,	7
	};
	
//	private static final int[] ATTACKER_AMOUNT_WEIGHTS = new int[] {
//			0,
//			50,
//			75,
//			88,
//			94,
//			97,
//			99
//	};
	
	public static int eval(Board b, int side) {
		int score = eval(b);
		
		if(side == PieceCode.WHITE) return score;
		return -score;
	}
	
	private static int eval(Board b) {
		int endgameWeight = b.getEndgameWeight();
		int openingWeight = 256 - endgameWeight;
		
		int scoreMiddle = 0;
		int scoreEnd = 0;
		
		if(openingWeight != 0) scoreMiddle = evalMiddleGame(b);
		if(endgameWeight != 0) scoreEnd = evalEndGame(b);
		
		int score = ((scoreMiddle * openingWeight) + (scoreEnd * endgameWeight)) / 256;
		
		score += b.getSide() == PieceCode.WHITE ? TEMPO_BONUS : -TEMPO_BONUS;
		
		return score;
	}
	
	private static int evalMiddleGame(Board b) {
		int score = evalMaterial(b, PieceCode.WHITE, PIECE_VALUES_MG) - evalMaterial(b, PieceCode.BLACK, PIECE_VALUES_MG);
		
		score += evalPiecePositions(b, PieceCode.WHITE, TABLES_MG) - evalPiecePositions(b, PieceCode.BLACK, TABLES_MG);
		
		score += evalTotalImbalance(b);
		
		score += evalMobility(b, PieceCode.WHITE) - evalMobility(b, PieceCode.BLACK);
		
		score += evalSpace(b, PieceCode.WHITE) - evalSpace(b, PieceCode.BLACK);
		
		return score;
	}
	
	private static int evalEndGame(Board b) {
		int score = evalMaterial(b, PieceCode.WHITE, PIECE_VALUES_EG) - evalMaterial(b, PieceCode.BLACK, PIECE_VALUES_EG);
		
		score += evalPiecePositions(b, PieceCode.WHITE, TABLES_EG) - evalPiecePositions(b, PieceCode.BLACK, TABLES_EG);
		
		score += evalTotalImbalance(b);
		
		score += evalMobility(b, PieceCode.WHITE) - evalMobility(b, PieceCode.BLACK);
		
		return score;
	}
	
	private static int evalMaterial(Board b, int side, int[] table) {
		int score = evalMaterial(b, side, table, PieceCode.PAWN);
		
		score += evalMaterial(b, side, table, PieceCode.KNIGHT);
		score += evalMaterial(b, side, table, PieceCode.BISHOP);
		score += evalMaterial(b, side, table, PieceCode.ROOK);
		score += evalMaterial(b, side, table, PieceCode.QUEEN);
		
		return score;
	}
	
	private static int evalMaterial(Board b, int side, int[] table, int type) {
		return b.getPieceAmount(PieceCode.getSpriteCode(side, type)) * table[type];
	}
	
	private static int evalPiecePositions(Board b, int side, int[][] tables) {
		int score = evalPiecePositions(b, side, tables, PieceCode.PAWN);
		
		score += evalPiecePositions(b, side, tables, PieceCode.KNIGHT);
		score += evalPiecePositions(b, side, tables, PieceCode.BISHOP);
		score += evalPiecePositions(b, side, tables, PieceCode.ROOK);
		score += evalPiecePositions(b, side, tables, PieceCode.QUEEN);
		score += evalPiecePositions(b, side, tables, PieceCode.KING);
		
		return score;
	}
	
	private static int evalPiecePositions(Board b, int side, int[][] tables, int type) {
		int score = 0;
		
		int[] table = tables[type];
		
		boolean mirrorTable = table.length == 32;
		
		int w = 8;
		
		if(mirrorTable) w = 4;
		
		boolean flip = side == PieceCode.WHITE;
		
		int code = PieceCode.getSpriteCode(side, type);
		
		int l = b.getPieceAmount(code);
		
		for(int i=0; i<l; i++) {
			int square = b.getPieceIndex(code, i);
			
			if(flip) square = MIRROR_TABLE[square];
			
			int x = square % 8;
			int y = square / 8;
			
			if(mirrorTable && x >= w) x = 2 * w - 1 - x;
			
			score += table[y * w + x];
		}
		
		return score;
	}
	
	private static int evalTotalImbalance(Board b) {
		int score = evalImbalance(b, PieceCode.WHITE) - evalImbalance(b, PieceCode.BLACK);
		
		score += evalBishopPair(b, PieceCode.WHITE) - evalBishopPair(b, PieceCode.BLACK);
		
		return score / 16;
	}
	
	private static int evalImbalance(Board b, int side) {
		return 0;
	}
	
	private static int evalBishopPair(Board b, int side) {
		int count = b.getPieceAmount(PieceCode.getSpriteCode(side, PieceCode.BISHOP));
		
		return count >= 2 ? 1438 : 0;
	}
	
	private static int evalMobility(Board b, int side) {
		return 0;
	}
	
	private static int evalSpace(Board b, int side) {
		return 0;
	}
	
//	public static int eval(Board b, int beta) {
//		int endgameWeight = b.getEndgameWeight();
//		int normalWeight = 256 - endgameWeight;
//		
//		int score = 0;
//		
//		for(int i=0; i<12; i++) {
//			int color = PieceCode.getColorFromSpriteCode(i);
//			int type = PieceCode.getTypeFromSpriteCode(i);
//			
//			for(int j=0; j<b.getPieceAmount(i); j++) {
//				
//				int index = b.getPieceIndex(i, j);
//				
//				if(type == PieceCode.KING) {
//					
//					if(color == PieceCode.WHITE) {
//						index = MIRROR_TABLE[index];
//						
//						score += (TABLES[type][index] * normalWeight + TABLES[type + 1][index] * endgameWeight) / 256;
//					} else {
//						score -= (TABLES[type][index] * normalWeight + TABLES[type + 1][index] * endgameWeight) / 256;
//					}
//					
//				} else {
//					
//					if(color == PieceCode.WHITE) {
//						index = MIRROR_TABLE[index];
//						
//						score += TABLES[type][index];
//					} else {
//						score -= TABLES[type][index];
//					}
//					
//				}
//			}
//		}
//		
//		// Lazy Evaluation
//		
//		int materialScore = score;
//		
//		if(b.getSide() == PieceCode.BLACK) materialScore = -materialScore;
//		
//		int margin = 1716;
//		
//		if(materialScore >= beta + margin) return materialScore;
//		
//		long occupiedSquares = b.getBitBoard(PieceCode.WHITE).orReturn(b.getBitBoard(PieceCode.BLACK));
//		
////		score += evalMobility(b, PieceCode.WHITE);
////		score -= evalMobility(b, PieceCode.BLACK);
//		
//		score += evalKingSafety(b, PieceCode.WHITE, normalWeight, occupiedSquares);
//		score -= evalKingSafety(b, PieceCode.BLACK, normalWeight, occupiedSquares);
//		
//		score += evalEarlyQueenDevelopment(b, PieceCode.WHITE, normalWeight);
//		score -= evalEarlyQueenDevelopment(b, PieceCode.BLACK, normalWeight);
//		
//		score += evalPassingPawns(b, PieceCode.WHITE);
//		score -= evalPassingPawns(b, PieceCode.BLACK);
//		
//		score += evalCenterPawns(b, normalWeight);
//		
//		if(b.getSide() == PieceCode.WHITE) return score;
//		return -score;
//	}
//	
////	private static int evalMobility(Board b, int side) {
////		MoveList list = new MoveList();
////		
////		MoveGenerator.generateAllMoves(b, side, list);
////		
////		return list.getCount() * 2;
////	}
//	
//	private static int evalKingSafety(Board b, int side, int normalWeight, long occupiedSquares) {
//		int safety = 0;
//		
//		int index = b.getPieceIndex(PieceCode.getSpriteCode(side, PieceCode.KING), 0);
//		
//		int x = index % 8;
//		int y = index / 8;
//		
//		int dir = -8;
//		if(side == PieceCode.BLACK) dir = 8;
//		
//		if(normalWeight != 0) {
//			if(x < 3 || x > 4) {
//				int shieldY;
//				
//				boolean needsShield;
//				
//				if(side == PieceCode.WHITE) {
//					shieldY = 6;
//					
//					needsShield = y > 5;
//				} else {
//					shieldY = 1;
//					
//					needsShield = y < 2;
//				}
//				
//				if(needsShield) {
//					int shieldX = x;
//					
//					if(shieldX < 3) shieldX = 1;
//					else if(shieldX > 4) shieldX = 6;
//					
//					for(int i=-1; i<2; i++) {
//						int squareX = shieldX + i;
//						int square = shieldY * 8 + squareX;
//						
//						boolean closed = checkFriendlyPawn(b, square, side) || checkFriendlyPawn(b, square + dir, side);
//						
//						if(!closed) {
//							int penalty = 156;
//							
//							int disToKing = x - squareX;
//							if(disToKing < 0) disToKing = -disToKing;
//							
//							if(disToKing < 2) penalty = 195;
//							
//							safety -= (penalty * normalWeight) / 256;
//						}
//					}
//				}
//			}
//			
//			long bishopMoves = MoveGenerator.getSliderMoves(index, occupiedSquares, LookupTable.RELEVANT_BISHOP_MOVES, LookupTable.BISHOP_MAGIC_VALUES, LookupTable.BISHOP_MAGIC_INDEX_BITS, LookupTable.BISHOP_MOVES);
//			long rookMoves = MoveGenerator.getSliderMoves(index, occupiedSquares, LookupTable.RELEVANT_ROOK_MOVES, LookupTable.ROOK_MAGIC_VALUES, LookupTable.ROOK_MAGIC_INDEX_BITS, LookupTable.ROOK_MOVES);
//			
//			long queenMoves = bishopMoves | rookMoves;
//			
//			int sliderAttackSquareAmount = BitOperations.countBits(queenMoves);
//			
//			safety -= (sliderAttackSquareAmount * 2 * normalWeight) / 256;
//		}
//		
//		int opponentSide = (side + 1) % 2;
//		
//		int opponentPawnCode = PieceCode.getSpriteCode(opponentSide, PieceCode.PAWN);
//		int opponentPawnAmount = b.getPieceAmount(opponentPawnCode);
//		
//		for(int i=0; i<opponentPawnAmount; i++) {
//			int square = b.getPieceIndex(opponentPawnCode, i);
//			
//			int disX = square % 8 - x;
//			int disY = square / 8 - y;
//			
//			if(disX < 0) disX = -disX;
//			if(disY < 0) disY = -disY;
//			
//			int dis = disX + disY;
//			
//			if(dis < 4) {
//				safety -= (4 - dis) * 78;
//			}
//		}
//		
//		long kingZone = LookupTable.KING_MOVES[index] | BoardConstants.BIT_SET[index];
//		
//		int extraSquare = index + dir * 2;
//		
//		if(extraSquare >= 0 && extraSquare < 64) kingZone |= BoardConstants.BIT_SET[extraSquare];
//		
//		int attackingPiecesAmount = 0;
//		int valueOfAttacks = 0;
//		
//		int opponentKnightCode = PieceCode.getSpriteCode(opponentSide, PieceCode.KNIGHT);
//		int opponentBishopCode = PieceCode.getSpriteCode(opponentSide, PieceCode.BISHOP);
//		int opponentRookCode = PieceCode.getSpriteCode(opponentSide, PieceCode.ROOK);
//		int opponentQueenCode = PieceCode.getSpriteCode(opponentSide, PieceCode.QUEEN);
//		
//		for(int i=0; i<b.getPieceAmount(opponentKnightCode); i++) {
//			int square = b.getPieceIndex(opponentKnightCode, i);
//			
//			long attackingSquares = LookupTable.KNIGHT_MOVES[square] & kingZone;
//			
//			int l = BitOperations.countBits(attackingSquares);
//			
//			if(l != 0) {
//				attackingPiecesAmount++;
//				valueOfAttacks += l * 32;
//			}
//		}
//		
//		for(int i=0; i<b.getPieceAmount(opponentBishopCode); i++) {
//			int square = b.getPieceIndex(opponentBishopCode, i);
//			
//			long attackingSquares = MoveGenerator.getSliderMoves(square, occupiedSquares, LookupTable.RELEVANT_BISHOP_MOVES, LookupTable.BISHOP_MAGIC_VALUES, LookupTable.BISHOP_MAGIC_INDEX_BITS, LookupTable.BISHOP_MOVES) & kingZone;
//			
//			int l = BitOperations.countBits(attackingSquares);
//			
//			if(l != 0) {
//				attackingPiecesAmount++;
//				valueOfAttacks += l * 32;
//			}
//		}
//		
//		for(int i=0; i<b.getPieceAmount(opponentRookCode); i++) {
//			int square = b.getPieceIndex(opponentRookCode, i);
//			
//			long attackingSquares = MoveGenerator.getSliderMoves(square, occupiedSquares, LookupTable.RELEVANT_ROOK_MOVES, LookupTable.ROOK_MAGIC_VALUES, LookupTable.ROOK_MAGIC_INDEX_BITS, LookupTable.ROOK_MOVES) & kingZone;
//			
//			int l = BitOperations.countBits(attackingSquares);
//			
//			if(l != 0) {
//				attackingPiecesAmount++;
//				valueOfAttacks += l * 64;
//			}
//		}
//		
//		for(int i=0; i<b.getPieceAmount(opponentQueenCode); i++) {
//			int square = b.getPieceIndex(opponentQueenCode, i);
//			
//			long attackingSquares = MoveGenerator.getSliderMoves(square, occupiedSquares, LookupTable.RELEVANT_ROOK_MOVES, LookupTable.ROOK_MAGIC_VALUES, LookupTable.ROOK_MAGIC_INDEX_BITS, LookupTable.ROOK_MOVES);
//			
//			attackingSquares |= MoveGenerator.getSliderMoves(square, occupiedSquares, LookupTable.RELEVANT_BISHOP_MOVES, LookupTable.BISHOP_MAGIC_VALUES, LookupTable.BISHOP_MAGIC_INDEX_BITS, LookupTable.BISHOP_MOVES);
//			
//			attackingSquares &= kingZone;
//			
//			int l = BitOperations.countBits(attackingSquares);
//			
//			if(l != 0) {
//				attackingPiecesAmount++;
//				valueOfAttacks += l * 128;
//			}
//		}
//		
//		if(attackingPiecesAmount != 0) {
//			safety -= valueOfAttacks * ATTACKER_AMOUNT_WEIGHTS[attackingPiecesAmount - 1] / 100;
//		}
//		
//		if(safety == 0) return 0;
//		
//		int opponentMaterial = 0;
//		
//		opponentMaterial += b.getPieceAmount(opponentPawnCode);
//		opponentMaterial += b.getPieceAmount(opponentKnightCode) * 3;
//		opponentMaterial += b.getPieceAmount(opponentBishopCode) * 3;
//		opponentMaterial += b.getPieceAmount(opponentRookCode) * 5;
//		opponentMaterial += b.getPieceAmount(opponentQueenCode) * 9;
//		
//		int totalMaterial = 8 * 1 + 4 * 3 + 2 * 5 + 1 * 9;
//		
//		return safety * opponentMaterial / totalMaterial;
//	}
//	
//	private static boolean checkFriendlyPawn(Board b, int index, int side) {
//		int code = b.getPiece(index);
//		
//		if(code == -1) return false;
//		
//		if(PieceCode.getTypeFromSpriteCode(code) != PieceCode.PAWN) return false;
//		
//		return PieceCode.getColorFromSpriteCode(code) == side;
//	}
//	
//	private static int evalEarlyQueenDevelopment(Board b, int side, int normalWeight) {
//		int weight = (normalWeight - 128) * 2;
//		
//		if(weight <= 0) return 0;
//		
//		int code = PieceCode.getSpriteCode(side, PieceCode.QUEEN);
//		
//		if(b.getPieceAmount(code) == 1) {
//			int y = b.getPieceIndex(code, 0) / 8;
//			
//			int firstY = 0;
//			
//			if(side == PieceCode.WHITE) firstY = 7;
//			
//			if(y == firstY) return 0;
//			else {
//				int count = 0;
//				
//				for(int x=0; x<8; x++) {
//					if(b.getPiece(firstY * 8 + x) != -1) count++;
//				}
//				
//				int dis = y - firstY;
//				if(dis < 0) dis = -dis;
//				
//				if(dis > 4) dis = 4;
//				
//				return -(count * 39 * dis / 4 * weight) / 256;
//			}
//		}
//		return 0;
//	}
//	
//	private static int evalPassingPawns(Board b, int side) {
//		int score = 0;
//		
//		int opponentSide = (side + 1) % 2;
//		
//		int code = PieceCode.getSpriteCode(side, PieceCode.PAWN);
//		int opponentCode = PieceCode.getSpriteCode(opponentSide, PieceCode.PAWN);
//		
//		int targetY = 7;
//		
//		if(side == PieceCode.WHITE) targetY = 0;
//		
//		int[] opponentDefence = new int[8];
//		boolean hasOpponentDefence = false;
//		
//		int l = b.getPieceAmount(code);
//		
//		for(int i=0; i<l; i++) {
//			int square = b.getPieceIndex(code, i);
//			
//			int y = square / 8;
//			
//			int disToTarget = y - targetY;
//			if(disToTarget < 0) disToTarget = -disToTarget;
//			
//			if(!hasOpponentDefence) {
//				for(int j=0; j<opponentDefence.length; j++) {
//					opponentDefence[j] = -1;
//				}
//				
//				int l2 = b.getPieceAmount(opponentCode);
//				
//				for(int j=0; j<l2; j++) {
//					int defendingSquare = b.getPieceIndex(opponentCode, j);
//					
//					int defendingX = defendingSquare % 8;
//					int defendingY = defendingSquare / 8;
//					
//					int otherY = opponentDefence[defendingX];
//					
//					if(otherY == -1 || (side == PieceCode.WHITE ? defendingY < otherY : defendingY > otherY)) {
//						opponentDefence[defendingX] = defendingY;
//					}
//				}
//			}
//			
//			int x = square % 8;
//			
//			boolean passing = true;
//			
//			for(int j=-1; j<=1; j++) {
//				int checkX = x + j;
//				
//				if(checkX >= 0 && checkX < 8) {
//					int defenceY = opponentDefence[checkX];
//					
//					boolean free = defenceY == -1 || (side == PieceCode.WHITE ? y <= defenceY : y >= defenceY);
//					
//					if(!free) {
//						passing = false;
//						break;
//					}
//				}
//			}
//			
//			if(passing) {
//				int m = 7 - disToTarget;
//				
//				score += 62 + 16 * m;
//			}
//		}
//		return score;
//	}
//	
//	private static int evalCenterPawns(Board b, int normalWeight) {
//		int score = 0;
//		
//		int bonus = 31 * normalWeight / 256;
//		
//		for(int x=3; x<5; x++) {
//			for(int y=3; y<5; y++) {
//				
//				int code = b.getPiece(y * 8 + x);
//				
//				if(code != -1 && PieceCode.getTypeFromSpriteCode(code) == PieceCode.PAWN) {
//					
//					int side = PieceCode.getColorFromSpriteCode(code);
//					
//					if(side == PieceCode.WHITE) score += bonus;
//					else score -= bonus;
//				}
//			}
//		}
//		return score;
//	}
	
}
