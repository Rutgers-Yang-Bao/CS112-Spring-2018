package poly;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class implements evaluate, add and multiply for polynomials.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {
	
	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param sc Scanner from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 * @return The polynomial linked list (front node) constructed from coefficients and
	 *         degrees read from scanner
	 */
	public static Node read(Scanner sc) 
	throws IOException {
		Node poly = null;
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			poly = new Node(scLine.nextFloat(), scLine.nextInt(), poly);
			scLine.close();
		}
		return poly;
	}
	
	/**
	 * Returns the sum of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list
	 * @return A new polynomial which is the sum of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node add(Node poly1, Node poly2) {
		/** COMPLETE THIS METHOD **/
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE THIS METHOD COMPILE
		// CHANGE IT AS NEEDED FOR YOUR IMPLEMENTATION
		
		int a = Math.max(getDegree(poly1), getDegree(poly2));			// get highest degree needed
		Node answer = new Node(0,0,null);					
		Node pointer = answer;
		
		for( int i = 1; i <= a; i++) {
			pointer.next = new Node(0,i,null);
			pointer = pointer.next;
		}
		for(Node ptr = poly1; ptr !=null; ptr = ptr.next) {				// add poly1 up to the answer
			Node ptr2 = answer;
			while (ptr.term.degree != ptr2.term.degree) {
				ptr2 = ptr2.next;
			}
			ptr2.term.coeff += ptr.term.coeff;
		}
		for(Node ptr = poly2; ptr !=null; ptr = ptr.next) {				// add poly2 up to the answer
			Node ptr2 = answer;
			while (ptr.term.degree != ptr2.term.degree) {
				ptr2 = ptr2.next;
			}
			ptr2.term.coeff += ptr.term.coeff;
		}
		delete(answer);
		return answer;
		
	}
	
	/**
	 * Returns the product of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list)
	 * @return A new polynomial which is the product of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node multiply(Node poly1, Node poly2) {
		int a = getDegree(poly1) + getDegree(poly2);
		Node answer = new Node(0,0,null);					
		Node ptr = answer;
		
		for( int i = 1; i <= a; i++) {
			ptr.next = new Node(0,i,null);
			ptr = ptr.next;
		}
		
		for (Node ptr1 = poly1; ptr1 != null; ptr1 = ptr1.next) {
			for(Node ptr2 = poly2; ptr2 != null; ptr2 = ptr2.next) {
				Node n = new Node(ptr1.term.coeff * ptr2.term.coeff, ptr1.term.degree + ptr2.term.degree, null);
				ptr = answer;
				while (ptr.term.degree != n.term.degree) {
					ptr = ptr.next;
				}
				
				ptr.term.coeff += n.term.coeff;
				System.out.println("------------");
				System.out.println(n.term.coeff + "  " + n.term.degree);
			}
		}
		delete(answer);
		return answer;
	}
	
		
	/**
	 * Evaluates a polynomial at a given value.
	 * 
	 * @param poly Polynomial (front of linked list) to be evaluated
	 * @param x Value at which evaluation is to be done
	 * @return Value of polynomial p at x
	 */
	public static float evaluate(Node poly, float x) {
		float answer = 0;
		
		for (Node ptr = poly; ptr != null; ptr = ptr.next) {
			answer += ptr.term.coeff * (Math.pow(x, ptr.term.degree));
		}
		
		return answer;
	}
	
	/**
	 * Returns string representation of a polynomial
	 * 
	 * @param poly Polynomial (front of linked list)
	 * @return String representation, in descending order of degrees
	 */
	public static String toString(Node poly) {
		if (poly == null) {
			return "0";
		} 
		
		String retval = poly.term.toString();
		for (Node current = poly.next ; current != null ;
		current = current.next) {
			retval = current.term.toString() + " + " + retval;
		}
		return retval;
	}	
	
	public static int getDegree(Node n) {
		int i = 0;
		Node ptr = n;
		while(ptr.next != null) {
			ptr = ptr.next;
		}
		i = ptr.term.degree;
		return i;
	}
	
	public static void delete(Node n) {
		for (Node ptr = n; ptr.next != null; ptr = ptr.next) { 	// check other degrees
			Node pttr = ptr.next;
			if (pttr.term.coeff == 0) {
				ptr.next = pttr.next;
			}
		}
		if(n.term.coeff == 0) {									//check the lowest degree
			n = n.next;
		}
	}
}
