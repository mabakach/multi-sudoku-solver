package ch.mabaka.sudoku.datastructures;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Marc Baumgartner
 *
 */
public abstract class SudokuFieldGroup {
	
	private List<SudokuField> fields;
	
	public SudokuFieldGroup(){
		fields = new ArrayList<SudokuField>();
	}
	
	public void addSudokuField(SudokuField field){
		this.fields.add(field);
	}
	
	public boolean isValid(){
		boolean isValid = true;
		List<Integer> values = new ArrayList<Integer>();
		for (SudokuField field : fields){
			Integer fieldValue = field.getValue() != null ? field.getValue() : field.getAssumedValue();
			if (fieldValue != null) {
				if (values.contains(field.getValue())) {
					isValid = false;
					break;
				} else {
					values.add(fieldValue);
				}
			}
		}
		return isValid;
	}
	
	public boolean isComplete(){
		for (SudokuField field : fields) {
			if (field.value == null) {
				return false;
			}
		}
		return true;
	}

	public List<SudokuField> getFields() {
		return fields;
	}
}
