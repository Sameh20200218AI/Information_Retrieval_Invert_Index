package invertedIndex;

import invertedIndex.Index5;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {

    public static void main(String[] args) throws IOException {
        Index5 index = new Index5();  // Create an instance of the Index5 class

        // Set the path to the collection directory
        String files = "C:\\Users\\osama ibrahim\\Downloads\\tmp11\\tmp11\\rl\\collection\\";

        File file = new File(files);  // Create a File object for the collection directory
        // Get the list of files in the collection directory
        String[] fileList = file.list();

        fileList = index.sort(fileList);  // Sort the file list
        Index5.N = fileList.length;  // Set the total number of documents in the index

        // Update file paths to include the directory path
        for (int i = 0; i < fileList.length; i++) {
            System.out.println(fileList[i]);
            fileList[i] = files + fileList[i];
        }

        // Build the inverted index using the file list
        index.buildIndex(fileList);
        // Store the inverted index to a file
        index.store("index");
        // Print the inverted index dictionary
        index.printDictionary();

        // Test a search phrase
        String test3 = "data should plain greatest comif"; // Test search phrase
        System.out.println("Boolean Model result = \n" + index.find_24_01(test3));

        String phrase = "";

        // Continuous loop to accept search phrases from the user
        do {
            System.out.println("Print search phrase: ");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            phrase = in.readLine();  // Read search phrase from user input

            if (!phrase.isEmpty()) {
                //print results
                String result =  index.find_24_01(phrase);
                System.out.println("search result =");
                if(!result.isEmpty()){
                    System.out.println(result);
                }
                else{
                    System.out.println("NOT FOUND!!");
                }
            }
            else {
                System.out.println("Your test is Empty!!");
            }

        } while (!phrase.isEmpty());  // Continue loop until an empty search phrase is entered (user presses Enter)
    }
}
