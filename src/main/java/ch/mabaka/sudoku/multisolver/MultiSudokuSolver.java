package ch.mabaka.sudoku.multisolver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.mabaka.sudoku.datastructures.AdditionalRule;
import ch.mabaka.sudoku.datastructures.Sudoku;
import ch.mabaka.sudoku.datastructures.SudokuField;
import ch.mabaka.sudoku.datastructures.SudokuRow;

/**
 * 
 * @author Marc Baumgartner
 *
 */
public class MultiSudokuSolver {

	List<Sudoku> sudokus;

	Map<Character, List<AdditionalRule>> additionalRules;

	public MultiSudokuSolver() {
		sudokus = new ArrayList<Sudoku>();
		additionalRules = new HashMap<Character, List<AdditionalRule>>();
	}

	public static void main(String args[]) {
		if (args.length >= 1) {
			File sudokuFile = new File(args[0]);
			MultiSudokuSolver solver = new MultiSudokuSolver();
			try {
				solver.readSudokusFromFile(sudokuFile);
				if (args.length >= 2) {
					File rulesFile = new File(args[1]);
					solver.readAdditionalRules(rulesFile);
				}
				solver.solveSudokus();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Syntax MultiSudokuSolver <PathToInputFile>");
		}
	}

	public void readSudokusFromFile(File inputFile) throws IOException {
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(inputFile);
			br = new BufferedReader(fr);
			List<List<Character>> characterRows = new ArrayList<List<Character>>();
			String line = br.readLine();
			int lineCounter = 0;
			while (line != null) {
				if ((lineCounter + 1) % 10 == 0) {
					// Separator line
					addNewSudoku(characterRows);
					characterRows.clear();
				} else {
					String[] input = line.split(" ");
					List<Character> characters = convertStringArrayToCharacterList(input);
					characterRows.add(characters);
				}
				line = br.readLine();
				lineCounter++;
			}
			addNewSudoku(characterRows);
			connectFieldsWithSameVariableName();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// ignore
				}
			}
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	public void readAdditionalRules(File rulesFile) throws IOException {
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(rulesFile);
			br = new BufferedReader(fr);
			String line = br.readLine();
			while (line != null) {
				AdditionalRule additionalRule = new AdditionalRule(line);
				List<AdditionalRule> list = additionalRules.get(additionalRule.getVariable());
				if (list == null) {
					list = new ArrayList<AdditionalRule>(1);
					additionalRules.put(additionalRule.getVariable(), list);
				}
				list.add(additionalRule);
				line = br.readLine();
			}
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// ignore
				}
			}
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	public void addSudoku(Sudoku sudoku) {
		sudokus.add(sudoku);
	}

	public void solveSudokus() {
		boolean allSudokusSolved = false;
		boolean didUpdateFiledsInIteration = true;

		for (Sudoku sudoku : sudokus) {
			for (SudokuRow row : sudoku.getRows()) {
				for (SudokuField field : row.getFields()) {
					if (field.getVariable() != null) {
						List<AdditionalRule> rules = additionalRules.get(field.getVariable());
						if (rules != null) {
							field.addAdditionalRules(rules);
						}
					}
				}
			}
		}
		
		// reduce possibilities
		while (!allSudokusSolved && didUpdateFiledsInIteration) {
			didUpdateFiledsInIteration = false;
			for (Sudoku sudoku : sudokus) {
				for (SudokuRow row : sudoku.getRows()) {
					for (SudokuField field : row.getFields()) {
						if (field.reducePossibleValues()) {
							didUpdateFiledsInIteration = true;
						}
					}
				}
			}
			for (Sudoku sudoku : sudokus) {
				allSudokusSolved = allSudokusSolved && sudoku.isSolved();
				if (!allSudokusSolved) {
					break;
				}
			}
		}
		
		if (!allSudokusSolved){
			// try with backtracking
		}
		
		if (!allSudokusSolved) {
			System.out.println("No unique solution found!");
		}
		for (Sudoku sudoku : sudokus) {
			System.out.println(sudoku.toString());
		}
		Map<String, Object> variables = new HashMap<String, Object>();
		for (Sudoku sudoku : sudokus) {
			for (SudokuRow row : sudoku.getRows()) {
				for (SudokuField field : row.getFields()) {
					if (field.getVariable() != null) {
						if (field.getValue() != null){
							variables.put(field.getVariable().toString(), field.getValue());
						} else {
							variables.put(field.getVariable().toString(), field.getPossibleValues().toString());
						}
					}
				}
			}
		}
		List<String> keys = new ArrayList<String>(variables.keySet());
		Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);
		for (String key : keys) {
			System.out.println("Variable: " + key + " Value: " + variables.get(key));
		}

	}

	private void addNewSudoku(List<List<Character>> characterRows) {
		if (characterRows.size() == 9) {
			Sudoku sudoku = new Sudoku();
			sudoku.init(characterRows);
			sudokus.add(sudoku);
		}
	}

	private void connectFieldsWithSameVariableName() {
		Map<Character, List<SudokuField>> varibaleFieldMap = new HashMap<Character, List<SudokuField>>();
		for (Sudoku sudoku : sudokus) {
			for (SudokuRow row : sudoku.getRows()) {
				for (SudokuField field : row.getFields()) {
					if (field.getVariable() != null) {
						List<SudokuField> fieldList = varibaleFieldMap.get(field.getVariable());
						if (fieldList == null) {
							fieldList = new ArrayList<SudokuField>();
							varibaleFieldMap.put(field.getVariable(), fieldList);
						}
						fieldList.add(field);
					}
				}
			}
		}
		for (Character variable : varibaleFieldMap.keySet()) {
			List<SudokuField> fieldList = varibaleFieldMap.get(variable);
			for (SudokuField field : fieldList) {
				for (SudokuField otherField : fieldList) {
					if (field != otherField) {
						field.addConnectedField(otherField);
					}
				}
			}
		}
	}

	public List<Character> convertStringArrayToCharacterList(String[] input) {
		List<Character> characters = new ArrayList<Character>();
		for (String inputString : input) {
			if (inputString.length() > 1) {
				throw new IllegalArgumentException("Wrong data format: " + inputString);
			}
			Character c = inputString.charAt(0);
			characters.add(c);
		}
		return characters;
	}
}
