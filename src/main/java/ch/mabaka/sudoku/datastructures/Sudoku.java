package ch.mabaka.sudoku.datastructures;

import java.util.ArrayList;
import java.util.List;

public class Sudoku {

	List<SudokuRow> rows;

	List<SudokuColumn> columns;

	List<SudokuSmallSquare> smallSquares;

	boolean isSolved = false;

	public Sudoku() {
		rows = new ArrayList<SudokuRow>();
		columns = new ArrayList<SudokuColumn>();
		smallSquares = new ArrayList<SudokuSmallSquare>();
	}

	public void init(List<List<Character>> input) {
		int rowCounter = 0;
		for (List<Character> row : input) {
			SudokuRow sudokuRow = new SudokuRow();
			rows.add(sudokuRow);
			int columnCounter = 0;
			for (Character c : row) {
				SudokuColumn sudokuColumn = null;
				SudokuSmallSquare sudokuSmallSquare = getCreateSudokuSmallSquare(rowCounter, columnCounter);
				if (columns.size() < columnCounter + 1) {
					sudokuColumn = new SudokuColumn();
					columns.add(sudokuColumn);
				} else {
					sudokuColumn = columns.get(columnCounter);
				}
				SudokuField field = null;
				if (Character.getType(c) == Character.DECIMAL_DIGIT_NUMBER) {
					field = new SudokuField(Character.getNumericValue(c));
				} else {
					field = new SudokuField(c);
				}
				sudokuRow.addSudokuField(field);
				sudokuColumn.addSudokuField(field);
				sudokuSmallSquare.addSudokuField(field);
				field.setSudokuRow(sudokuRow);
				field.setSudokuColumn(sudokuColumn);
				field.setSudokuSmallSquare(sudokuSmallSquare);
				columnCounter++;
			}
			rowCounter++;
		}
	}

	private SudokuSmallSquare getCreateSudokuSmallSquare(int row, int column) {
		int index = -1;
		if (row >= 0 && row <= 2) {
			if (column >= 0 && column <= 2) {
				index = 0;
			} else if (column >= 3 && column <= 5) {
				index = 1;
			} else if (column >= 6 && column <= 8) {
				index = 2;
			}
		} else if (row >= 3 && row <= 5) {
			if (column >= 0 && column <= 2) {
				index = 3;
			} else if (column >= 3 && column <= 5) {
				index = 4;
			} else if (column >= 6 && column <= 8) {
				index = 5;
			}
		} else if (row >= 6 && row <= 8) {
			if (column >= 0 && column <= 2) {
				index = 6;
			} else if (column >= 3 && column <= 5) {
				index = 7;
			} else if (column >= 6 && column <= 8) {
				index = 8;
			}
		}
		SudokuSmallSquare sudokuSmallSquare = null;
		if (smallSquares.size() < index + 1) {
			sudokuSmallSquare = new SudokuSmallSquare();
			smallSquares.add(sudokuSmallSquare);
		} else {
			sudokuSmallSquare = smallSquares.get(index);
		}
		return sudokuSmallSquare;
	}

	public List<SudokuRow> getRows() {
		return rows;
	}

	public List<SudokuColumn> getColumns() {
		return columns;
	}

	public List<SudokuSmallSquare> getSmallSquares() {
		return smallSquares;
	}

	public boolean isSolved() {
		if (!isSolved) {
			boolean allFieldsSolved = true;
			for (SudokuRow row : rows) {
				for (SudokuField field : row.getFields()) {
					if (field.getValue() == null) {
						allFieldsSolved = false;
						break;
					}
				}
				if (!allFieldsSolved) {
					break;
				}
			}
			isSolved = allFieldsSolved;
		}
		return isSolved;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (SudokuRow row : rows) {
			for (SudokuField field : row.getFields()) {
				if (field.getValue() != null) {
					sb.append(field.getValue());
				} else if (field.getVariable() != null) {
					sb.append(field.getVariable());
				} else {
					sb.append("?");
				}
				sb.append(" ");
			}
			sb.append("\n");
		}
		return sb.toString();

	}
}
