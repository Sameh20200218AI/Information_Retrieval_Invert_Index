/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Math.log10;
import static java.lang.Math.sqrt;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.io.PrintWriter;

/**
 *
 * @author ehab
 */
public class Index5 {

   // public static final int N = 0;
    //--------------------------------------------
    public static int N = 0;
    public Map<Integer, SourceRecord> sources;  // store the doc_id and the file name.

    public HashMap<String, DictEntry> index; // THe inverted index
    //--------------------------------------------

    public Index5() {
        sources = new HashMap<Integer, SourceRecord>();
        index = new HashMap<String, DictEntry>();
    }

    public void setN(int n) {
        N = n;
    }


    /**
     * The function `printPostingList` prints the document IDs in a linked list format enclosed in
     * square brackets.
     * 
     * @param p Posting class with a structure like this:
     */
    public void printPostingList(Posting p) {
        // Iterator<Integer> it2 = hset.iterator();
        System.out.print("[");
        boolean isFirst = true;  // Flag to track if it's the first element in the list
        while (p != null) {
            // Print comma only if it's not the first element
            if (!isFirst) {
                System.out.print(",");
            } else {
                isFirst = false;  // Update flag after printing the first element
            }
            System.out.print(p.docId);
            p = p.next;
        }
        System.out.println("]");
    }


   /**
    * The `printDictionary` function iterates through a dictionary, printing key-value pairs along with
    * associated document frequencies and posting lists.
    */
    public void printDictionary() {
        Iterator it = index.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            DictEntry dd = (DictEntry) pair.getValue();
            System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "]       =--> ");
            printPostingList(dd.pList);
        }
        System.out.println("------------------------------------------------------");
        System.out.println("*** Number of terms = " + index.size());
    }
 
    /**
     * This Java method builds an inverted index from text files on disk.
     * 
     * @param files The `files` parameter in the `buildIndex` method is an array of strings that
     * contains the file names of the text files on disk from which the inverted index will be built.
     * The method iterates over each file in the array, reads the content of the file line by line, and
     * processes
     */
    public void buildIndex(String[] files) {  // This method builds the inverted index from text files on disk
        int fid = 0;
        for (String fileName : files) {
            try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                if (!sources.containsKey(fileName)) {
                    sources.put(fid, new SourceRecord(fid, fileName, fileName, "notext"));
                }
                String ln;
                int flen = 0;
                while ((ln = file.readLine()) != null) {
                    /// -2- **** complete here ****
                    flen += indexOneLine(ln, fid);
                }
                sources.get(fid).length = flen;

            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }
            fid++;
        }
        // printDictionary();
    }


    /**
     * The indexOneLine function processes a given line of text, extracting words, checking for stop
     * words, stemming words, updating a dictionary index with document frequencies and posting lists,
     * and printing specific information for the word "lattice".
     * 
     * @param ln The `ln` parameter in the `indexOneLine` method seems to represent a line of text that
     * is being processed. This line of text is split into words, processed, and added to an index data
     * structure. The method seems to be part of a text indexing system, where words are stemmed
     * @param fid The `fid` parameter in the `indexOneLine` method stands for the document ID. It is
     * used to uniquely identify the document being processed in the indexing operation.
     * @return The method `indexOneLine` returns the total number of words processed in the input line
     * `ln`.
     */
    public int indexOneLine(String ln, int fid) {
        int flen = 0;

        String[] words = ln.split("\\W+");
      //   String[] words = ln.replaceAll("(?:[^a-zA-Z0-9 -]|(?<=\\w)-(?!\\S))", " ").toLowerCase().split("\\s+");
        flen += words.length;
        for (String word : words) {
            word = word.toLowerCase();
            if (stopWord(word)) {
                continue;
            }
            word = stemWord(word);
            // check to see if the word is not in the dictionary
            // if not add it
            if (!index.containsKey(word)) {
                index.put(word, new DictEntry());
            }
            // add document id to the posting list
            if (!index.get(word).postingListContains(fid)) {
                index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term 
                if (index.get(word).pList == null) {
                    index.get(word).pList = new Posting(fid);
                    index.get(word).last = index.get(word).pList;
                } else {
                    index.get(word).last.next = new Posting(fid);
                    index.get(word).last = index.get(word).last.next;
                }
            } else {
                index.get(word).last.dtf += 1;
            }
            //set the term_fteq in the collection
            index.get(word).term_freq += 1;
            if (word.equalsIgnoreCase("lattice")) {

                System.out.println("  <<" + index.get(word).getPosting(1) + ">> " + ln);
            }

        }
        return flen;
    }

    /**
     * The function `stopWord` checks if a given word is a stop word or has a length less than 2 in
     * Java.
     * 
     * @param word The `stopWord` method checks if a given word is a stop word or if it has a length
     * less than 2. If the word is one of the specified stop words or has a length less than 2, the
     * method returns `true`, indicating that it should be considered a stop word
     * @return The method `stopWord` returns `true` if the input word is one of the specified stop
     * words ("the", "to", "be", "for", "from", "in", "a", "into", "by", "or", "and", "that") or if the
     * length of the word is less than 2 characters. Otherwise, it returns `false`.
     */
    boolean stopWord(String word) {
        if (word.equals("the") || word.equals("to") || word.equals("be") || word.equals("for") || word.equals("from") || word.equals("in")
                || word.equals("a") || word.equals("into") || word.equals("by") || word.equals("or") || word.equals("and") || word.equals("that")) {
            return true;
        }
        if (word.length() < 2) {
            return true;
        }
        return false;

    }

    /**
     * The function `stemWord` currently returns the input word without stemming it.
     * 
     * @param word It looks like you have a method called `stemWord` that is currently returning the
     * original word without any stemming applied. If you provide me with the word that you want to
     * stem, I can help you implement the stemming functionality in the `stemWord` method. What word
     * would you like to stem
     * @return The method is currently returning the original word without any stemming applied.
     */
    String stemWord(String word) { //skip for now
        return word;
//        Stemmer s = new Stemmer();
//        s.addString(word);
//        s.stem();
//        return s.toString();
    }

    /**
     * The intersect function takes two Posting linked lists as input and returns a new linked list
     * containing the intersection of the documents present in both input lists.
     * 
     * @param pL1 Posting pL1 represents the first posting list, which contains document IDs in sorted
     * order.
     * @param pL2 Posting pL2 is a linked list of postings containing document IDs. The intersect method
     * takes two input parameters, pL1 and pL2, which are both Posting objects representing linked lists of
     * postings. The method compares the document IDs in the two lists and returns a new Posting object
     * that contains the
     * @return The intersect method returns a Posting object that contains the intersection of two Posting
     * objects, pL1 and pL2, based on their docIDs.
     */
    public Posting intersect(Posting pL1, Posting pL2) {
        // INTERSECT ( p1 , p2 )
        // 1 answer ← {}
        Posting answer = null;
        Posting last = null;
        // 2 while p1  != NIL and p2  != NIL
        while (pL1 != null && pL2 != null) {
            // 3 if docID ( p 1 ) = docID ( p2 )
            if (pL1.docId == pL2.docId) {
                // 4 then ADD ( answer, docID ( p1 ))
                if (answer == null) {
                    answer = new Posting(pL1.docId);
                    last = answer;
                } else {
                    last.next = new Posting(pL1.docId);
                    last = last.next;
                }
                // 5 p1 ← next ( p1 )
                pL1 = pL1.next;
                // 6 p2 ← next ( p2 )
                pL2 = pL2.next;
            } else if (pL1.docId < pL2.docId) { // 7 else if docID ( p1 ) < docID ( p2 )
                // 8 then p1 ← next ( p1 )
                pL1 = pL1.next;
            } else {
                // 9 else p2 ← next ( p2 )
                pL2 = pL2.next;
            }
        }
        // 10 return answer
        return answer;
    }

    /**
     * The function `find_24_01` takes a phrase as input, splits it into words, checks if the words
     * exist in an index, intersects the posting lists of the words, and generates a result based on
     * the intersected posting lists.
     * 
     * @param phrase It looks like the code you provided is a method that takes a phrase as input,
     * splits it into words, and then searches for documents that contain all the words in the phrase.
     * The method uses an index to find the relevant documents based on the words in the phrase.
     * @return The `find_24_01` method returns a String that contains information about documents that
     * contain all the words provided in the input phrase. The information includes the document ID,
     * title, and length of each document that matches the search criteria. If no documents match the
     * search criteria, an empty string is returned.
     */
    public String find_24_01(String phrase) {
        String result = "";
        String[] words = phrase.split("\\W+");
        int len = words.length;

        // Initialize posting as null
        Posting posting = null;

        // Check if the first word exists in the index
        if (index.containsKey(words[0].toLowerCase())) {
            // Get the posting list of the first word
            posting = index.get(words[0].toLowerCase()).pList;
        }

        // If posting is still null after checking the first word, return an empty result
        if (posting == null) {
            return result;
        }

        // Iterate through the rest of the words
        for (int i = 1; i < len; i++) {
            String word = words[i].toLowerCase();
            // Check if the word exists in the index and the posting list is not null
            if (index.containsKey(word) && index.get(word).pList != null) {
                // Intersect the current posting list with the posting list of the current word
                posting = intersect(posting, index.get(word).pList);
            } else {
                // If the word doesn't exist in the index or the posting list is null, set posting to null and break the loop
                posting = null;
                break;
            }
        }

        // If posting is not null after intersecting all words, generate the result
        if (posting != null) {
            while (posting != null) {
                result += "\t" + posting.docId + " - " + sources.get(posting.docId).title + " - " + sources.get(posting.docId).length + "\n";
                posting = posting.next;
            }
        }
        return result;
    }


    /**
     * The function sorts an array of strings using the bubble sort algorithm.
     * 
     * @param words The `sort` method you provided is a basic implementation of the bubble sort
     * algorithm for sorting an array of strings in ascending order.
     * @return The `sort` method is returning the sorted array of strings after applying the bubble
     * sort algorithm to arrange the strings in ascending order.
     */
    String[] sort(String[] words) {  //bubble sort
        boolean sorted = false;
        String sTmp;
        // The above code is implementing a bubble sort algorithm to sort an array of words in
        // ascending order. It iterates through the array and compares adjacent elements, swapping them
        // if they are in the wrong order. The process continues until the array is fully sorted. The
        // `sorted` flag is used to determine if any swaps were made in a pass through the array, and
        // if no swaps were made, the array is considered sorted and the loop exits.
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < words.length - 1; i++) {
                int compare = words[i].compareTo(words[i + 1]);
                if (compare > 0) {
                    sTmp = words[i];
                    words[i] = words[i + 1];
                    words[i + 1] = sTmp;
                    sorted = false;
                }
            }
        }
        return words;
    }


    /**
     * The `store` function in Java writes data from a map and an index to a specified file location,
     * handling exceptions along the way.
     * 
     * @param storageName The `store` method you provided is responsible for storing data into a file
     * located at a specific path. The `storageName` parameter is used to specify the name of the file
     * where the data will be stored.
     */
    public void store(String storageName) {
        try {
            String pathToStorage = "C:\\Users\\osama ibrahim\\Downloads\\tmp11\\tmp11\\rl\\"+storageName;
            Writer wr = new FileWriter(pathToStorage);
            for (Map.Entry<Integer, SourceRecord> entry : sources.entrySet()) {
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue().URL + ", Value = " + entry.getValue().title + ", Value = " + entry.getValue().text);
                wr.write(entry.getKey().toString() + ",");
                wr.write(entry.getValue().URL.toString() + ",");
                wr.write(entry.getValue().title.replace(',', '~') + ",");
                wr.write(entry.getValue().length + ","); //String formattedDouble = String.format("%.2f", fee );
                wr.write(String.format("%4.4f", entry.getValue().norm) + ",");
                wr.write(entry.getValue().text.toString().replace(',', '~') + "\n");
            }
            wr.write("section2" + "\n");

            Iterator it = index.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                DictEntry dd = (DictEntry) pair.getValue();
                //  System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "] <" + dd.term_freq + "> =--> ");
                wr.write(pair.getKey().toString() + "," + dd.doc_freq + "," + dd.term_freq + ";");
                Posting p = dd.pList;
                while (p != null) {
                    //    System.out.print( p.docId + "," + p.dtf + ":");
                    wr.write(p.docId + "," + p.dtf + ":");
                    p = p.next;
                }
                wr.write("\n");
            }
            wr.write("end" + "\n");
            wr.close();
            System.out.println("=============EBD STORE=============");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The function checks if a file with a specific name exists in a specified directory.
     * 
     * @param storageName The `storageName` parameter in the `storageFileExists` method is a String
     * that represents the name of the file you want to check for existence in the specified directory.
     * @return The method `storageFileExists` returns a boolean value indicating whether a file with
     * the specified `storageName` exists in the directory
     * "C:/Users/PC/Downloads/Compressed/tmp11/tmp11/rl/".
     */
    public boolean storageFileExists(String storageName){
        java.io.File f = new java.io.File("C:/Users/PC/Downloads/Compressed/tmp11/tmp11/rl/"+storageName);
        if (f.exists() && !f.isDirectory())
            return true;
        return false;
            
    }
    
   /**
    * The `createStore` function creates a new file in a specified directory with the given storage
    * name and writes "end" to the file.
    * 
    * @param storageName The `createStore` method you provided creates a new file in the specified
    * directory with the given `storageName`. The file will contain the text "end" followed by a new
    * line character.
    */
    public void createStore(String storageName) {
        try {
            String pathToStorage = "C:/Users/PC/Downloads/Compressed/tmp11/"+storageName;
            Writer wr = new FileWriter(pathToStorage);
            wr.write("end" + "\n");
            wr.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     //load index from hard disk into memory
    /**
    * The `load` function reads data from a file, processes it, and populates a HashMap with the
    * extracted information.
    * 
    * @param storageName The `load` method you provided reads data from a file located at a specific
    * path and populates two HashMaps (`sources` and `index`) with the extracted information. The
    * method reads the file line by line, processes the data, and stores it in the HashMaps.
    * @return The `load` method returns a `HashMap` containing `String` keys and `DictEntry` values.
    */
    public HashMap<String, DictEntry> load(String storageName) {
        try {
            String pathToStorage = "C:/Users/PC/Downloads/Compressed/tmp11/tmp11/rl/"+storageName;
            sources = new HashMap<Integer, SourceRecord>();
            index = new HashMap<String, DictEntry>();
            BufferedReader file = new BufferedReader(new FileReader(pathToStorage));
            String ln = "";
            int flen = 0;
            while ((ln = file.readLine()) != null) {
                if (ln.equalsIgnoreCase("section2")) {
                    break;
                }
                String[] ss = ln.split(",");
                int fid = Integer.parseInt(ss[0]);
                try {
                    System.out.println("**>>" + fid + " " + ss[1] + " " + ss[2].replace('~', ',') + " " + ss[3] + " [" + ss[4] + "]   " + ss[5].replace('~', ','));

                    SourceRecord sr = new SourceRecord(fid, ss[1], ss[2].replace('~', ','), Integer.parseInt(ss[3]), Double.parseDouble(ss[4]), ss[5].replace('~', ','));
                    //   System.out.println("**>>"+fid+" "+ ss[1]+" "+ ss[2]+" "+ ss[3]+" ["+ Double.parseDouble(ss[4])+ "]  \n"+ ss[5]);
                    sources.put(fid, sr);
                } catch (Exception e) {

                    System.out.println(fid + "  ERROR  " + e.getMessage());
                    e.printStackTrace();
                }
            }
            while ((ln = file.readLine()) != null) {
                //     System.out.println(ln);
                if (ln.equalsIgnoreCase("end")) {
                    break;
                }
                String[] ss1 = ln.split(";");
                String[] ss1a = ss1[0].split(",");
                String[] ss1b = ss1[1].split(":");
                index.put(ss1a[0], new DictEntry(Integer.parseInt(ss1a[1]), Integer.parseInt(ss1a[2])));
                String[] ss1bx;   //posting
                for (int i = 0; i < ss1b.length; i++) {
                    ss1bx = ss1b[i].split(",");
                    if (index.get(ss1a[0]).pList == null) {
                        index.get(ss1a[0]).pList = new Posting(Integer.parseInt(ss1bx[0]), Integer.parseInt(ss1bx[1]));
                        index.get(ss1a[0]).last = index.get(ss1a[0]).pList;
                    } else {
                        index.get(ss1a[0]).last.next = new Posting(Integer.parseInt(ss1bx[0]), Integer.parseInt(ss1bx[1]));
                        index.get(ss1a[0]).last = index.get(ss1a[0]).last.next;
                    }
                }
            }
            System.out.println("============= END LOAD =============");
            //    printDictionary();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return index;
    }
}

//=====================================================================
