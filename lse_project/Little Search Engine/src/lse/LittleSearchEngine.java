package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		File file = new File(docFile);
		if(!file.exists()) {
			throw new FileNotFoundException();
		}
		
		HashMap<String,Occurrence> answer = new HashMap<String,Occurrence> ();
		Scanner sc = new Scanner(file);
		while (sc.hasNext()) {
			String word = sc.next();
			String key = getKeyword(word);
			
			if(key != null) {
				if(answer.containsKey(key)) {
					int i = answer.get(key).frequency;
					i++;
					answer.put(key, new Occurrence(docFile,i));
				}else {
					answer.put(key, new Occurrence(docFile, 1));
				}
			}
		}
		return answer;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		 for (Map.Entry<String, Occurrence> entry : kws.entrySet()) {
	            String key = entry.getKey();
	            Occurrence value = entry.getValue();

	            ArrayList<Occurrence> list = new ArrayList<>();
	            if (keywordsIndex.containsKey(key)) {
	                list.addAll(keywordsIndex.get(key));
	            }

	            // new value added to the end of the list
	            list.add(value);
	            // insert last occurrence
	            insertLastOccurrence(list);
	            keywordsIndex.put(key, list);
	        }
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		if(word == null) {
			return null;
		}
		
		word = word.toLowerCase();
		String punc = ".,?;:!";
		int index = 0;
		
		for (index = word.length()-1;index >= 0;index--) {
			if(!punc.contains("" + word.charAt(index))) {
				break;
			}
		}
		word = word.substring(0,index+1);
		for(int i =0; i < word.length();i++) {
			if(punc.contains("" + word.charAt(i))) {
				word = null;
			}
		}
		if(noiseWords.contains(word)) {
			return null;
		}
		return word;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		if(occs.size() == 1) {
			return null;
		}
		ArrayList<Integer> answer = new ArrayList<>();
		ArrayList<Integer> numbers = new ArrayList<>();
		
		if(occs.size() == 2) {
			Occurrence temp = occs.get(0);
			if(occs.get(0).frequency < occs.get(1).frequency) {
				occs.add(temp);
				occs.remove(0);
			}
			answer.add(0);
			return answer;
		}
		
		for(Occurrence temp : occs) {
			numbers.add(temp.frequency);
		}
		
		int insertNumber = numbers.get(numbers.size()-1);
		int size = numbers.size()-1;
		int insertIndex = 0;
		
		int left = 0;
		int right = size -1;
		
		while(left >= 0 && right >= 0 && right >= left) {
			int index = (left + right) / 2;
			answer.add(index);
			int target = numbers.get(index);
			if(insertNumber > target) {
				right = index - 1;
			}else {
				left = index + 1;
			}
		}
		
		if ( left > right) {
			if(right >= 0) {
				insertIndex = left;
			}
		}
		if(left == occs.size() -1) {
			return answer;
		}
		
		Occurrence insertTerm = occs.get(occs.size()-1);
		List<Occurrence> front = occs.subList(0, insertIndex);
		List<Occurrence> rear = occs.subList(insertIndex, occs.size()-1);
		occs.clear();
		occs.addAll(front);
		occs.add(insertTerm);
		occs.addAll(rear);
		
		return answer;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, returns null.
	 */
	
	public ArrayList<String> top5search(String kw1, String kw2) {
		ArrayList<String> answer = new ArrayList<>();
        kw1 = kw1.toLowerCase();
        kw2 = kw2.toLowerCase();
        if (!keywordsIndex.containsKey(kw1) && !keywordsIndex.containsKey(kw2)) {
            return null;
        }

        List<Occurrence> list1 = keywordsIndex.get(kw1);
        List<Occurrence> list2 = keywordsIndex.get(kw2);
        if (list1 != null) {
            list1 = list1.subList(0, list1.size() >= 5 ? 5 : list1.size());
        }
        if (list2 != null) {
            list2 = list2.subList(0, list2.size() >= 5 ? 5 : list2.size());
        }
        while ((list1 != null && list1.isEmpty()) || (list2 != null && list2.isEmpty())) {
            boolean check;
            Occurrence o1 = !(list1 != null && list1.isEmpty()) ? null : list1.get(0);
            Occurrence o2 = !(list2 != null && list2.isEmpty()) ? null : list2.get(0);
            Occurrence temp;

            if (o1 != null && o2 == null) {
                temp = o1;
                check = true;
            } else if (o1 == null && o2 != null) {
                temp = o2;
                check = false;
            } else {
                if (o1.frequency >= o2.frequency) {
                    temp = o1;
                    check = true;
                } else {
                    temp = o2;
                    check = false;
                }
            }

            if (!answer.contains(temp.document)) {
                if (answer.size() == 5){
                    return answer;
                }
                answer.add(temp.document);
            }
            if (check) {
                list1.remove(0);
            } else {
                list2.remove(0);
            }
        }

        return answer;
    }
}
