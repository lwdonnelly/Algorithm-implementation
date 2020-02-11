//Luke Donnelly
//CS 1501 Project 1
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class DLBTrie {
	private Node head;
	
	public DLBTrie() {
		head = null;
	}

	//search for part of a word and return an array with 5 suggestions
	public String[] search(String str, int numToFind) {
		//traverse dlb to where str is
		char currChar;
		Node currNode = head;
		String[] words = new String[numToFind];
		for(int i = 0;i < str.length();i++) {
			currChar = str.charAt(i);
			
			if(currNode == null) {
				return words;
			} else {
				if(currChar == currNode.value) {
					currNode = currNode.next;
				} else {
					currNode = currNode.peer;
					i--;
				}
			}
		}
		
		//if it doesn't exist return, else recursiveSearch
		if(currNode == null) {
			return words;
		} else {
			index = 0;
			numLeft = numToFind;
			recursiveSearch(currNode, str, words);
		}
		
		return words;
	}
	
	//recursively search dlb from start to find numToFind suggestions
	private int index;//where to add next valid word in words array
	private int numLeft;//number of suggestions left to find
	private void recursiveSearch(Node current, String word, String[] words) {
		if(numLeft != 0) {
			if(current != null) {
				String prevWord = word;
				if(current.value == '^') {//end of a valid word
					words[index] = word;
					index++;
					numLeft--;
				} else {//append currenct character to word
					word = new String(word + Character.toString(current.value));
				}
				//search down, then search peers
				recursiveSearch(current.next, word, words);
				recursiveSearch(current.peer, prevWord, words);
			}
			return;
		}
		return;
	}

	//add a word to the DLB
	public void add(String str) {
		
		if(str == null)//do nothing
			return;
		
		Node currNode = head;
		Node prevNode = null;
		char currChar;
		boolean next = false;//true if previous node was up the trie, false if previous node was a peer
		for(int i = 0;i < str.length() + 1;i++) {//insert word and terminating character, only increment when going down a level in dlb
			if(i == str.length()) {//terminating character
				currChar = '^';
			} else {
				currChar = str.charAt(i);
			}
			
			if(currNode == null) {//character not here, add it
				currNode = new Node(currChar);
				
				if(head == null) {//set the head node
					head = currNode;
				}
				
				if(prevNode != null) {
					if(next) {//previous node was up the trie
						prevNode.next = currNode;
					} else {//previous node was a peer
						prevNode.peer = currNode;
					}
				}
				next = true;
				prevNode = currNode;
				currNode = currNode.next;
			} else {//there is a character here, check if it is the right one
				if(currNode.value == currChar) {//character already exists, don't have to look at peers
					prevNode = currNode;
					next = true;
					currNode = currNode.next;
				} else {//move to next peer, without incrementing i
					prevNode = currNode;
					currNode = currNode.peer;
					next = false;
					i--;
				}
			}
		}
	
	}
	
	//write the contents of this DLB to the user history file
	public void writeToFile() throws IOException {
		FileWriter fw = new FileWriter("user_history.txt");
	    PrintWriter printWriter = new PrintWriter(fw);
	    recursiveWrite(head, "", printWriter);
	    printWriter.close();
	}
	
	//recursively traverse the DLB trie (in a similar way to recursiveSearch), writing each word to the file out
	public void recursiveWrite(Node current, String word, PrintWriter out) {
		if(current != null) {
			String prevWord = word;
			if(current.value == '^') {//end of a word, print it to file
				out.println(word);
			} else {//letter in a word, append it to word
				word = new String(word + Character.toString(current.value));
			}
			//search down, then search peers
			recursiveWrite(current.next, word, out);
			recursiveWrite(current.peer, prevWord, out);
		}
		return;
	}
	
	//read entries from the file named fileName and add each of them to the DLB
	public void readFromFile(String fileName) throws FileNotFoundException {
		//read dictionary.txt and add each word to the dictionary DLBTrie
		File input = new File(fileName);
		if(input.exists()) {
			Scanner in = new Scanner(input);
			while(in.hasNextLine()) {
				//read in what word is
				String temp = in.nextLine();
				//add word to trie with add() method	
				this.add(temp);
			}
				
			in.close();
		}
		
	}
	
	

	private class Node {
		Node peer;
		Node next;
		char value;
	
		void setNext(Node n) {
			next = n;
		}	
		
		void setPeer(Node p) {
			peer = p;
		}
		
		Node(char val) {
			value = val;
			peer = null;
			next = null;
		}
	}
}

