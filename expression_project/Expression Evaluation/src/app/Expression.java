package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	int i1=0;
    	
    	for(int i2 = 0; i2<expr.length(); i2++) {
    		char ptr2 = expr.charAt(i2);
    		int index = isDelims(ptr2);
    		if(i2 != expr.length()-1) {
    			if ((index>= 0 && index <=5)|| index == 7 || index == 9) {
    				if( i1 != i2) {
    					String a = expr.substring(i1, i2);
    					boolean check = false;
    					for (int j = 0; j <vars.size(); j++){
    						if (a.equals(vars.get(j).name)) {
    							check = true;
    						}
    					}
    					if (!check && !isNumber(a)){
    						Variable var = new Variable (a);
    						vars.add(var);
    					}
    				}
    				i1 = i2+1;
    			}else if (index==8) {							// [
    				String a = expr.substring(i1, i2);
					boolean check = false;
					for (int j = 0; j <arrays.size(); j++){
						if (a.equals(arrays.get(j).name)) {
							check = true;
						}
					}
					if (!check && !isNumber(a)){
						Array arr = new Array (a);
						arrays.add(arr);
					}
					i1 = i2 + 1;
    			}else if(index == 6) {							// (
    				i1 = i2 +1;
    			}
    		}
    		else {
    			if(index == -1) {
    				String a = expr.substring(i1);
					boolean check = false;
					for (int j = 0; j <vars.size(); j++){
						if (a.equals(vars.get(j).name)) {
							check = true;
						}
					}
					if (!check && !isNumber(a)){
						Variable var = new Variable (a);
						vars.add(var);
					}
    			}else if (index == 7) {							// )
    				String a = expr.substring(i1,i2);
					boolean check = false;
					for (int j = 0; j <vars.size(); j++){
						if (a.equals(vars.get(j).name)) {
							check = true;
						}
					}
					if (!check && !isNumber(a)){
						Variable var = new Variable (a);
						vars.add(var);
					}
    			}else if (index == 9) {							// ]
    				String a = expr.substring(i1,i2);
					boolean check = false;
					for (int j = 0; j <vars.size(); j++){
						if (a.equals(vars.get(j).name)) {
							check = true;
						}
					}
					if (!check && !isNumber(a)){
						Variable var = new Variable (a);
						vars.add(var);
					}
    			}
    		}
    	}
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	// following line just a placeholder for compilation
    	expr = expr.replace(" ","");
    	expr = expr.replace("\t","");
		Stack<Float> var = new Stack<>();
		Stack<Character> sym = new Stack<>();
    	Stack<Float> var1 = new Stack<>();
    	Stack<Character> sym1 = new Stack<>();
    	Stack<Character> sym2 = new Stack<>();
    	int i1=0;
    	
    	for ( int i = 0; i<expr.length(); i++) {
    		char ptr = expr.charAt(i);
    		int index = isDelims(ptr);
    		if( i != expr.length()-1) {
    			if(index >= 2 && index <= 5) {					//+-*/
    				if(i1 != i) {
    					String name = expr.substring(i1,i);
        				if(isNumber(name)) {
        					float value = transToFloat(name);
    						var1.push(value);
    						i1 = i+1;
        				}else {
        					for( int j = 0; j<vars.size(); j++) {
        						if(name.equals(vars.get(j).name)) {
        							float value = vars.get(j).value;
        							var1.push(value);
        							i1 = i+1;
        						}
        					}
        				}
        				if(sym2.size() != 0) {
        					float f2 = var1.pop();
        		    		float f1 = var1.pop();
        		    		char symbol = sym2.pop();
        		    		
        		    		if(isDelims(symbol)==2) {
        		    			float value = f1 * f2;
        		    			var1.push(value);
        		    		}else {
        		    			float value = f1 / f2;
        		    			var1.push(value);
        		    		}
        				}
        				if(index == 3 || index == 4) {				
        					sym1.push(ptr);
        				}else {
        					sym2.push(ptr);
        				}
    				}else {
    					if(index == 2 || index == 5) {
    						sym2.push(ptr);
    					}else {
    						sym1.push(ptr);
    					}
    					i1++;
    				}
    			}else if (index == 8) {							//array
    				int endindex = 0;
    				int num = 0;
    				for (int j = i+1; j < expr.length();j++){
    					if(expr.charAt(j) == '[') {
    						num++;
    					}
    					if(expr.charAt(j) == ']') {
    						if(num != 0) {
    							num--;
    						}else {
    						endindex = j;
    						break;
    						}
    					}
    				}
    				int arrindex =(int) evaluate (expr.substring(i+1,endindex),vars,arrays);
    				String name = expr.substring(i1,i);
    				for( int j = 0; j<arrays.size(); j++) {
    					if(name.equals(arrays.get(j).name)) {
    						float value = arrays.get(j).values[arrindex];
    						var1.push(value);
    						i = endindex;
    						i1 = i + 1;
    					}
    				}
    				if(sym2.size() != 0) {
    					float f2 = var1.pop();
    		    		float f1 = var1.pop();
    		    		char symbol = sym2.pop();
    		    		
    		    		if(isDelims(symbol)==2) {
    		    			float value = f1 * f2;
    		    			var1.push(value);
    		    		}else {
    		    			float value = f1 / f2;
    		    			var1.push(value);
    		    		}
    				}
    			}else if (index == 6) {							// ()
    				int endindex = 0;
    				int num = 0;
    				for (int j = i+1; j < expr.length();j++){
    					if(expr.charAt(j) == '(') {
    						num++;
    					}
    					if(expr.charAt(j) == ')') {
    						if(num != 0) {
    							num--;
    						}else {
    						endindex = j;
    						break;
    						}
    					}
    				}
    				float value = evaluate (expr.substring(i+1,endindex),vars,arrays);
    				var1.push(value);
    				i = endindex;
    				i1 = i + 1;
    				if(sym2.size() != 0) {
    					float f2 = var1.pop();
    		    		float f1 = var1.pop();
    		    		char symbol = sym2.pop();
    		    		
    		    		if(isDelims(symbol)==2) {
    		    			float value1 = f1 * f2;
    		    			var1.push(value1);
    		    		}else {
    		    			float value1 = f1 / f2;
    		    			var1.push(value1);
    		    		}
    				}
    			}
    			
    		}
    		else {
    			if(index == -1) {								// Last char
    				String name = expr.substring(i1);
    				if(isNumber(name)) {
    					float value = transToFloat(name);
						var1.push(value);
						i1 = i+1;
    				}else {
    					for( int j = 0; j<vars.size(); j++) {
    						if(name.equals(vars.get(j).name)) {
    							float value = vars.get(j).value;
    							var1.push(value);
    						}
    					}
    				}
    				if(sym2.size() != 0) {
    					float f2 = var1.pop();
    		    		float f1 = var1.pop();
    		    		char symbol = sym2.pop();
    		    		
    		    		if(isDelims(symbol)==2) {
    		    			float value = f1 * f2;
    		    			var1.push(value);
    		    		}else {
    		    			float value = f1 /f2;
    		    			var1.push(value);
    		    		}
    				}
    			}
    		}
    	}
    	while(var1.size() != 0 ) {
    		float value = var1.pop();
    		var.push(value);
    	}
    	while(sym1.size() != 0 ) {
    		char sym_ = sym1.pop();
    		sym.push(sym_);
    	}
    	
    	while(sym.size()!=0) {
    		float f1 = var.pop();
    		float f2 = var.pop();
    		char symbol = sym.pop();
    		if(isDelims(symbol)==3) {
    			float value = f1 + f2;
    			var.push(value);
    		}
    		else {
    			float value = f1 - f2;
    			var.push(value);
    		}
    	}
    	
    	return var.pop();
    }
    
    public static int isDelims(char a) {
    	for (int i = 0; i < delims.length(); i++) {
    		if (a == delims.charAt(i)) {
    			return i;
    		}
    	}
    	return -1;
    	
    }
    public static boolean isNumber(String a) {
    	for (int i = 0; i < a.length(); i++) {
    		int j = a.substring(i, i+1).compareTo("0");
    		if (j < 0 || j > 9) {
    			return false;
    		}
    	}
    	return true;
    }
    public static float transToFloat(String a) {
    	return (float)Integer.parseInt(a);
    }
}
