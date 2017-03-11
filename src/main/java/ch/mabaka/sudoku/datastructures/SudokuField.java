package ch.mabaka.sudoku.datastructures;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Marc Baumgartner
 *
 */
public class SudokuField {

	Character variable;

	Integer value;

	Integer assumedValue;

	List<Integer> possibleValues;

	SudokuRow sudokuRow;

	SudokuColumn sudokuColumn;

	SudokuSmallSquare sudokuSmallSquare;

	List<SudokuField> connectedFields;

	List<AdditionalRule> additionalRules;

	private SudokuField() {
		this.variable = null;
		this.value = null;
		this.possibleValues = new ArrayList<Integer>();
		this.connectedFields = new ArrayList<SudokuField>();
		this.additionalRules = new ArrayList<AdditionalRule>();
	}

	public SudokuField(Character variable) {
		this();
		this.variable = variable;
	}

	public SudokuField(Integer value) {
		this();
		if (value != 0) {
			this.value = value;
		}
	}

	public void addConnectedField(SudokuField connectedField) {
		if (!this.connectedFields.contains(connectedField)) {
			this.connectedFields.add(connectedField);
		}
	}

	public Integer getValue() {
		return value;
	}

	public Integer getAssumedValue() {
		return assumedValue;
	}

	public void setAssumedValue(Integer assumedValue) {
		this.assumedValue = assumedValue;
	}

	public List<Integer> getPossibleValues() {
		return possibleValues;
	}

	public void setPossibleValues(List<Integer> possibleValues) {
		this.possibleValues = possibleValues;
	}

	public Character getVariable() {
		return variable;
	}

	public List<SudokuField> getConnectedFields() {
		return connectedFields;
	}

	public SudokuRow getSudokuRow() {
		return sudokuRow;
	}

	public void setSudokuRow(SudokuRow sudokuRow) {
		this.sudokuRow = sudokuRow;
	}

	public SudokuColumn getSudokuColumn() {
		return sudokuColumn;
	}

	public void setSudokuColumn(SudokuColumn sudokuColumn) {
		this.sudokuColumn = sudokuColumn;
	}

	public SudokuSmallSquare getSudokuSmallSquare() {
		return sudokuSmallSquare;
	}

	public void setSudokuSmallSquare(SudokuSmallSquare sudokuSmallSquare) {
		this.sudokuSmallSquare = sudokuSmallSquare;
	}

	public void addAdditionalRules(List<AdditionalRule> rules) {
		additionalRules.addAll(rules);
	}

	public boolean isThisFieldAndAllConnectedFieldsValid(){
		boolean isValid = isValid();
		for (SudokuField connectedField : connectedFields){
			isValid = isValid && connectedField.isValid();
		}
		return isValid;
	}
	
	/**
	 * Assume next value for this and all connected fields.
	 * @return true if there was another possible value to choose.
	 */
	public boolean assumeNextValue() {
		boolean assumeOk = true;
		if (possibleValues.size() > 0) {
			if (assumedValue == null) {
				assumedValue = possibleValues.get(0);
			} else {
				int currentIndex = possibleValues.indexOf(assumedValue);
				if (currentIndex < possibleValues.size() - 1) {
					assumedValue = possibleValues.get(currentIndex + 1);
				} else {
					assumedValue = null;
					assumeOk = false;
				}
			}
		}
		for (SudokuField connectedField : connectedFields){
			connectedField.setAssumedValue(assumedValue);
		}
		return assumeOk;
	}

	public boolean reducePossibleValues() {
		boolean didAnyChanges = false;
		if (value != null) {
			return didAnyChanges;
		} else if (possibleValues.isEmpty()) {
			for (int i = 1; i <= 9; i++) {
				possibleValues.add(i);
			}
			didAnyChanges = true;
		}
		for (SudokuField connectedField : connectedFields) {
			if (connectedField.getValue() != null) {
				this.value = connectedField.getValue();
				this.possibleValues.clear();
				didAnyChanges = true;
			} else if (!connectedField.getPossibleValues().isEmpty()) {
				List<Integer> valuesToRemove = new ArrayList<Integer>();
				for (Integer possibleValue : possibleValues) {
					if (!connectedField.possibleValues.contains(possibleValue)) {
						valuesToRemove.add(possibleValue);
						didAnyChanges = true;
					}
				}
				possibleValues.removeAll(valuesToRemove);
			}
		}
		for (AdditionalRule rule : additionalRules) {
			rule.filterByRule(possibleValues);
		}
		if (!checkIfValueFound()) {
			for (SudokuField otherField : sudokuRow.getFields()) {
				if (otherField != this) {
					if (otherField.getValue() != null) {
						boolean removed = possibleValues.remove(otherField.getValue());
						didAnyChanges = removed || didAnyChanges;
					}
				}
			}
		}
		if (!checkIfValueFound()) {
			for (SudokuField otherField : sudokuColumn.getFields()) {
				if (otherField != this) {
					if (otherField.getValue() != null) {
						boolean removed = possibleValues.remove(otherField.getValue());
						didAnyChanges = removed || didAnyChanges;
					}
				}
			}
		}
		if (!checkIfValueFound()) {
			for (SudokuField otherField : sudokuSmallSquare.getFields()) {
				if (otherField != this) {
					if (otherField.getValue() != null) {
						boolean removed = possibleValues.remove(otherField.getValue());
						didAnyChanges = removed || didAnyChanges;
					}
				}
			}
		}
		return didAnyChanges;
	}

	private boolean checkIfValueFound() {
		if (value != null) {
			return true;
		} else if (possibleValues.size() == 1) {
			value = possibleValues.get(0);
			possibleValues.clear();
			return true;
		}
		return false;
	}
	
	private boolean isValid() {
		return sudokuRow.isValid() && sudokuColumn.isValid() && sudokuSmallSquare.isValid();
	}
}
