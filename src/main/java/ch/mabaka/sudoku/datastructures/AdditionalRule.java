package ch.mabaka.sudoku.datastructures;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Marc Baumgartner
 *	
 * Class for simple additional rules. 
 * 
 * The rules must be formatted as follows:
 * <Variable> <Comparison> <Value>
 * 
 * Variables must be one character only.
 * 
 * The following comparisons can be used: < = >
 * 
 * Examples:
 * X = 3
 * A < 8
 * B > 4
 * 
 * Value can be a number between 0 and 9.
 */
public class AdditionalRule {

	private Character variable;
	
	private String comparisonSign;
	
	private Integer value;
	
	public AdditionalRule(String ruleText){
		parseRule(ruleText);
	}
	
	private void parseRule(String ruleText){
		String ruleTextCopy = ruleText;
		ruleTextCopy = ruleTextCopy.replaceAll(" ", "");
		if (ruleTextCopy.length() == 3){
			variable = ruleTextCopy.charAt(0);
			comparisonSign = ruleTextCopy.substring(1, 2);
			value = Integer.valueOf(ruleTextCopy.substring(2, 3));
		} else {
			throw new IllegalArgumentException("Wrong syntax in rule: " + ruleText);
		}
	}

	public Character getVariable() {
		return variable;
	}
	
	public void filterByRule(List<Integer> possibleValues){
		List<Integer> filteredValues = new ArrayList<Integer>();
		for (Integer possibleValue : possibleValues){
			if ("<".equals(comparisonSign)){
				if (possibleValue >= value){
					filteredValues.add(possibleValue);
				}
			} else if ("=".equals(comparisonSign)){
				if (possibleValue != value){
					filteredValues.add(possibleValue);
				}
			} if (">".equals(comparisonSign)){
				if (possibleValue <= value){
					filteredValues.add(possibleValue);
				}
			}
		}
		possibleValues.removeAll(filteredValues);
	}
}
