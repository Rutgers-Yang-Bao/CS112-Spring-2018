package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 * Version 2
 * 2018/3/23
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file, through scanner passed
	 * in to the constructor and stored in the sc field of this object. 
	 * 
	 * The root of the tree that is built is referenced by the root field of this object.
	 */
	public void build() {
		String txt = sc.nextLine();
		boolean check1 = true;
		TagNode term = null;
		Stack<TagNode> tags = new Stack<TagNode>();
		
		root = new TagNode(txt.substring(1,txt.length()-1), null, null);
		tags.push(root);
		TagNode ptr = root;
		while (sc.hasNextLine()) {
			txt = sc.nextLine();
			if(!isEnd(txt)) {
				boolean check2 = false;
				if(isTag(txt)){
					txt = txt.substring(1, txt.length()-1);
					check2 = true;
					term = new TagNode(txt,null,null);
					tags.push(term);
				}else {
					term = new TagNode(txt,null,null);
				}
				if(check1) {
					if (ptr.firstChild != null) {
						ptr.sibling = term;
					}else {
						ptr.firstChild = term;
					}
				}else {
					ptr.sibling = term;
				}
				ptr = term;
				check1 = check2;
			}
			else {
				ptr = tags.pop();
				check1 = true;
			}
			
		}

		
		/** COMPLETE THIS METHOD **/
	}
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		replaceTag(root, oldTag, newTag);
	}
	
	public void replaceTag(TagNode root, String oldTag, String newTag) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if(ptr.firstChild != null) {
				if(ptr.tag.equals(oldTag)) {
					ptr.tag = newTag;
				}
			}
			if (ptr.firstChild != null) {
				replaceTag(ptr.firstChild, oldTag, newTag);
			}
		}
	}
	
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		Stack<TagNode> a = new Stack<TagNode>();
		Stack<TagNode> b = new Stack<TagNode>();
		TagNode target = null, term = new TagNode("b",null,null);
		boldRow(root, a);
		while(!a.isEmpty()) {
			b.push(a.pop());
		}
		while (row > 0) {
			target = b.pop();
			row --;
		}
		for(TagNode ptr = target.firstChild; ptr != null; ptr = ptr.sibling) {
			term.firstChild = ptr.firstChild;
			ptr.firstChild = term;
		}
		/** COMPLETE THIS METHOD **/
	}
	
	public void boldRow(TagNode root, Stack<TagNode> a) {
		for(TagNode ptr = root; ptr != null; ptr = ptr.sibling) {
			if (ptr.tag.equals("tr")) {
				a.push(ptr);
			}
			if(ptr.firstChild != null) {
				boldRow(ptr.firstChild, a);
			}
		}
	}
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		String a = "pemb", b = "ulol";
		Stack<TagNode> stack = new Stack<TagNode>();
		if(a.contains(tag)) {
			removeTag1(root,tag);
		}
		if(b.contains(tag)){
			removeTag2(root, tag, stack);
			while(!stack.isEmpty()) {
				TagNode ptr = stack.pop();
				ptr.tag = "p";
			}
		}
		
	}
	
	public void removeTag1(TagNode root, String tag) {
		TagNode ptr = null;
		for(TagNode ptr1 = root; ptr1 != null; ptr1 = ptr1.sibling) {
			if(ptr1.tag.equals("body")) {
				if(ptr1.firstChild.tag.equals(tag)){
					TagNode ptr2 = ptr1.firstChild.sibling;
					TagNode ptr3 = ptr1.firstChild.firstChild;
					while (ptr3.sibling != null) {
						ptr3 = ptr3.sibling;
					}
					ptr1.firstChild = ptr1.firstChild.firstChild;
					ptr3.sibling = ptr2;
				}
			}
			if(ptr1.sibling != null) {
				if(ptr1.sibling.tag.equals(tag)) {
					TagNode ptr2 = ptr1.sibling.sibling;
					TagNode ptr3 = ptr1.sibling.firstChild;
					while (ptr3.sibling != null) {
						ptr3 = ptr3.sibling;
					}
					ptr1.sibling = ptr1.sibling.firstChild;
					ptr3.sibling = ptr2;
				}else if(ptr1.firstChild != null) {
					if(ptr1.firstChild.tag.equals(tag)) {
						TagNode ptr2 = ptr1.firstChild.sibling;
						TagNode ptr3 = ptr1.firstChild.firstChild;
						while (ptr3.sibling != null) {
							ptr3 = ptr3.sibling;
						}
						ptr1.firstChild = ptr1.firstChild.firstChild;
						ptr3.sibling = ptr2;
					}
				}
				ptr = ptr1;
			}else {
				if(ptr1.tag.equals(tag)) {
					ptr.sibling = ptr1.firstChild;
				}
				ptr = ptr1;
			}
			if (ptr1.firstChild != null) {
				removeTag1(ptr1.firstChild, tag);
			}
		}
	}
	
	public void removeTag2(TagNode root, String tag, Stack<TagNode> stack) {
		TagNode ptr = null;
		for(TagNode ptr1 = root; ptr1 != null; ptr1 = ptr1.sibling) {
			if(ptr1.tag.equals("body")) {
				if(ptr1.firstChild.tag.equals(tag)){
					TagNode pttr = ptr1.firstChild.firstChild;
					while(pttr!=null) {
						if(pttr.tag.equals("li")) {
							stack.push(pttr);
						}
						pttr = pttr.sibling;
					}
					TagNode ptr2 = ptr1.firstChild.sibling;
					TagNode ptr3 = ptr1.firstChild.firstChild;
					while (ptr3.sibling != null) {
						ptr3 = ptr3.sibling;
					}
					ptr1.firstChild = ptr1.firstChild.firstChild;
					ptr3.sibling = ptr2;
				}
			}
			if(ptr1.sibling != null) {
				if(ptr1.sibling.tag.equals(tag)) {
					TagNode pttr = ptr1.sibling.firstChild;
					while(pttr!=null) {
						if(pttr.tag.equals("li")) {
							stack.push(pttr);
						}
						pttr = pttr.sibling;
					}
					TagNode ptr2 = ptr1.sibling.sibling;
					TagNode ptr3 = ptr1.sibling.firstChild;
					while (ptr3.sibling != null) {
						ptr3 = ptr3.sibling;
					}
					ptr1.sibling = ptr1.sibling.firstChild;
					ptr3.sibling = ptr2;
				}else if(ptr1.firstChild != null) {
					if(ptr1.firstChild.tag.equals(tag)) {
						TagNode pttr = ptr1.firstChild.firstChild;
						while(pttr!=null) {
							if(pttr.tag.equals("li")) {
								stack.push(pttr);
							}
							pttr = pttr.sibling;
						}
						TagNode ptr2 = ptr1.firstChild.sibling;
						TagNode ptr3 = ptr1.firstChild.firstChild;
						while (ptr3.sibling != null) {
							ptr3 = ptr3.sibling;
						}
						ptr1.firstChild = ptr1.firstChild.firstChild;
						ptr3.sibling = ptr2;
					}
				}
				ptr = ptr1;
			}else {
				if(ptr1.tag.equals(tag)) {
					TagNode pttr = ptr1.firstChild;
					while(pttr!=null) {
						if(pttr.tag.equals("li")) {
							stack.push(pttr);
						}
						pttr = pttr.sibling;
					}
					ptr.sibling = ptr1.firstChild;
				}
				ptr = ptr1;
			}
			if (ptr1.firstChild != null) {
				removeTag2(ptr1.firstChild, tag, stack);
			}
		}
	}
	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		word = word.toLowerCase();
		Stack<TagNode> a = new Stack<TagNode>();
		String pun = "!?.;:";
		
		addTag(root, a);
		while (!a.isEmpty()) {
			TagNode ptr = a.pop();
			String txt = ptr.tag.toLowerCase();
			if(txt.equals(word)) {
				TagNode term = new TagNode(ptr.tag,null,null);
				ptr.tag = word;
				ptr.firstChild = term;
			}else {
				if(txt.length() > word.length()) {
					if(txt.contains(word)) {
						if(txt.startsWith(word)) {
							if(pun.contains(txt.charAt(word.length())+"")) {
								TagNode term1 = new TagNode(ptr.tag.substring(0, word.length()+1),null,null);
								TagNode term2 = new TagNode(ptr.tag.substring(word.length()+1),null,null);
								ptr.tag = tag;
								ptr.firstChild = term1;
								term2.sibling = ptr.sibling;
								ptr.sibling = term2;
								a.push(term2);
							}
							else {
								TagNode term1 = new TagNode(ptr.tag.substring(0, word.length()),null,null);
								TagNode term2 = new TagNode(ptr.tag.substring(word.length()),null,null);
								ptr.tag = tag;
								ptr.firstChild = term1;
								term2.sibling = ptr.sibling;
								ptr.sibling = term2;
								a.push(term2);
							}
						}
						else {
							int index = 0;
							for(int i = 0;i<txt.length()-word.length()+1;i++) {
								if(txt.substring(i, i+word.length()).equals(word)){
									index = i;
									break;
								}
							}
							if((index + word.length()) == txt.length() || 
									((index + word.length()+1) == txt.length() && pun.contains(txt.charAt(txt.length()-1)+""))) {
								TagNode term1 = new TagNode(ptr.tag.substring(index),null,null);
								TagNode term2 = new TagNode(tag, term1, ptr.sibling);
								ptr.tag = ptr.tag.substring(0, index);
								ptr.sibling = term2;
							}
							else {
								if(pun.contains(txt.charAt(index + word.length() +1) +"")) {
									TagNode term1 = new TagNode(ptr.tag.substring(index, index + word.length()+1),null,null);
									TagNode term2 = new TagNode(ptr.tag.substring(index + word.length()+1),null,ptr.sibling);
									TagNode term3 = new TagNode(tag,term1,term2);
									ptr.tag = ptr.tag.substring(0, index);
									ptr.sibling = term3;
									a.push(term2);
								}else {
									TagNode term1 = new TagNode(ptr.tag.substring(index, index + word.length()),null,null);
									TagNode term2 = new TagNode(ptr.tag.substring(index + word.length()),null,ptr.sibling);
									TagNode term3 = new TagNode(tag,term1,term2);
									ptr.tag = ptr.tag.substring(0, index);
									ptr.sibling = term3;
									a.push(term2);
								}
							}
						}
					}
				}
			}
		}
		
		/** COMPLETE THIS METHOD **/
	}
	
	public void addTag(TagNode root, Stack<TagNode> a) {
		for(TagNode ptr = root; ptr != null; ptr = ptr.sibling) {
			if (ptr.firstChild == null) {
				a.push(ptr);
				System.out.println(a.size() + "    " + a.peek());
			}
			if(ptr.firstChild != null) {
				addTag(ptr.firstChild, a);
			}
		}
	}
	
	
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
	/**
	 * Prints the DOM tree. 
	 *
	 */
	public void print() {
		print(root, 1);
	}
	
	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|---- ");
			} else {
				System.out.print("      ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
	
	public boolean isTag(String txt) {
		if(txt.charAt(0) == '<' && txt.charAt(txt.length()-1) == '>'){
			return true;
		}else return false;
	}
	
	public boolean isEnd(String txt) {
		if(isTag(txt) && txt.charAt(1) == '/') {
			return true;
		}else return false;
	}
	
}
