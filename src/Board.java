/**
 * Kishan Patel
 * This program is an 8 puzzle solver that uses the A* algorithm
 * to solve the puzzle.
 */

import java.util.*;
import java.util.stream.Stream;

public class Board {
	private final int SIZE;
	private final int[][] board;

	private final Board parent;
	private final int depth, heuristic, holeRow, holeCol;

	private Board(int[][] board, Board beforeState, int hr, int hc) {
		SIZE = board.length;
		this.board = board;
		this.parent = beforeState;
		this.holeRow = hr;
		this.holeCol = hc;

		// increment depth if there is a parent
		depth = beforeState == null ? 0 : beforeState.depth + 1;

		// calculate heuristic with manhattan distance
		var heuristic = 0;
		for (int r = 0; r < SIZE; r++) {
			for (int c = 0; c < SIZE; c++) {
				int value = board[r][c];
				var targetRow = value / SIZE;
				var targetCol = value % SIZE;

				// add difference between current position and target position
				heuristic += Math.abs(targetRow - r) + Math.abs(targetCol - c);
			}
		}
		this.heuristic = heuristic;
	}

	/**
	 * Ansari example
	 */
	public static Board createExampleBoard() {
		return new Board(
				new int[][]{
						{8, 3, 2},
						{4, 7, 1},
						{0, 5, 6}
				},
				null,
				2, 0);
	}

	/*
	 * Returns a stream of possible moves from the current board
	 */
	public Stream<Board> streamOfFutures() {
		return Stream
				.of(
						move(0, 1),
						move(0, -1),
						move(1, 0),
						move(-1, 0)
				).filter(Objects::nonNull); // filter out nulls
	}

	/**
	 * Returns a new board with the hole moved in the given direction,
	 * or null if the move is not possible
	 */
	private Board move(int dr, int dc) {
		int newHoleRow = holeRow + dr;
		int newHoleCol = holeCol + dc;

		// returns null if move is not possible
		if (newHoleRow < 0 || newHoleRow >= SIZE || newHoleCol < 0 || newHoleCol >= SIZE)
			return null;

		// create new board & copy
		var newBoard = new int[SIZE][SIZE];
		for (int r = 0; r < SIZE; r++) {
			System.arraycopy(board[r], 0, newBoard[r], 0, SIZE);
		}

		// swap hole and new position
		newBoard[holeRow][holeCol] = newBoard[newHoleRow][newHoleCol];
		newBoard[newHoleRow][newHoleCol] = 0;

		return new Board(newBoard, this, newHoleRow, newHoleCol);
	}

	@Override
	public String toString() {
		var sb = new StringBuilder();
		for (int[] row : board) {
			for (int cell : row)
				sb.append(cell == 0 ? " " : cell)
						.append(' ')
						.append(' ');
			sb.append('\n');
		}
		return sb.toString();
	}

	/**
	 * Custom hash code based on the values of the board.
	 */
	@Override
	public int hashCode() {
		int hash = 0;
		for (var row : board)
			for (var cell : row)
				hash = hash * 10 + cell;
		return hash;
	}

	/**
	 * Returns a list of steps to solve the puzzle
	 */
	public String[] getSolutionAStar() {
		// queue that sorts by h(x) + g(x)
		var queue = new PriorityQueue<Board>(Comparator.comparingInt(b -> b.heuristic + b.depth));
		queue.add(this); // adds this board state initially

		// set of visited states
		var visited = new HashSet<Integer>();
		Board current = null;

		while (!queue.isEmpty()) {
			// pop off the best board state
			current = queue.poll();

			// if we have reached the goal state, break
			if (current.heuristic == 0) break;

			// add the current board state to the visited set
			visited.add(current.hashCode());

			// all possible future board states to the queue that have not been visited
			current
					.streamOfFutures()
					.filter(b -> !visited.contains(b.hashCode()))
					.forEach(queue::add);
		}

		// if current is null, then we have not found a solution
		if (current == null || current.heuristic != 0) {
			return null;
		}

		// backtrack to find the solution
		var path = new String[current.depth + 1];
		for (int i = current.depth; current.parent != null; i--) {
			path[i] = current.getMoveFromParent();
			current = current.parent;
		}
		return Arrays.copyOfRange(path, 1, path.length);
	}

	/**
	 * Returns the name of the move that was made to get from the parent to the
	 * child
	 */
	public String getMoveFromParent() {
		if (parent == null) return null;
		if (parent.holeRow == holeRow) {
			if (parent.holeCol == holeCol - 1) return "RIGHT";
			if (parent.holeCol == holeCol + 1) return "LEFT";
		}
		if (parent.holeCol == holeCol) {
			if (parent.holeRow == holeRow - 1) return "DOWN";
			if (parent.holeRow == holeRow + 1) return "UP";
		}
		return "Invalid Move (" + parent.holeRow + ", " + parent.holeCol + ") -> (" + holeRow + ", " + holeCol + ")";
	}

	public static Board createBoardFromUser() {
		final var SIZE = 3;
		var sc = new Scanner(System.in);
		var board = new int[SIZE][SIZE];


		int holeRow = -1, holeCol = -1;

		// read in the board
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				System.out.printf("Row %d and column %d: ", i, j);
				board[i][j] = sc.nextInt();

				// check for hole
				if (board[i][j] == 0) {
					holeRow = i;
					holeCol = j;
				}
			}
		}
		return new Board(board, null, holeRow, holeCol);
	}

	public static void main(String[] args) {

//		Board.playGame();
//		var board = Board.createExampleBoard();
		var board = Board.createBoardFromUser();
		System.out.println(board);

		String[] path = board.getSolutionAStar();

		// if path is null, then there is no solution
		if (path == null)
			System.out.println("No solution found");
		else {
			// print all the moves
			for (var state : path) {
				System.out.println(state);
			}
			System.out.println("Found solution in " + path.length + " moves");
		}
	}
}
