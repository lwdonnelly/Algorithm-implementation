//Luke Donnelly
//CS 1501 Project 1
import java.io.*;
import java.util.Scanner;
public class ac_test {

	public static void main(String[] args) throws IOException {
		DLBTrie dictionary = new DLBTrie();
		
		dictionary.readFromFile("dictionary.txt");
		
		//user input
		DLBTrie userHistory = new DLBTrie();
		userHistory.readFromFile("user_history.txt");
		boolean end = false;//end session if true
		boolean endWord = false;//end of current word if true
		String input;
		StringBuilder word;
		Scanner user = new Scanner(System.in);
		double totalTime = 0;//total time  predictions ran
		int numTimes = 0;//number of times predictions were found
		while(!end) {
			//receive first character
			endWord = false;
			word = new StringBuilder();
			System.out.print("Enter the first character: ");
			input = user.next();
			char letter = input.charAt(0);//first letter of input
			
			if(letter == '!' || letter == '$' || (letter >= '0' && letter <= '9')) {//not valid first characters
				endWord = true;
			} else {
				word.append(letter);
			}
			
			String[] predictions;
			System.out.println();
			while(!endWord) {
				//predictions
				predictions = new String[5];
				String[] histPredict = new String[5];
				String[] dicPredict = new String[5];
				
				long startTime = System.nanoTime();
				histPredict = userHistory.search(word.toString(), 5);
				dicPredict = dictionary.search(word.toString(), 5);
				
				
				//combine histPredict and dicPredict into predictions array
				int dicIndex = 0;//next index of dicPredict to check after leaving to add a word
				for(int i = 0;i < predictions.length;i++) {
					if(histPredict[i] == null) {//use dictionary, if it's not a duplicate
						for(int j = dicIndex;j < dicPredict.length;j++) {//continue searching dicPredict from where left off (dicIndex)
							if(dicPredict[j] == null) {//end of dicPredict, leave for loop and don't search dicPredict anymore
								dicIndex = dicPredict.length;
								break;
							}
							boolean valid = true;
							for(int k = 0;k < histPredict.length;k++) {
								if(histPredict[k] == null)//end of histPredic, leave for loop
									break;
								
								if(dicPredict[j].equals(histPredict[k]))//dicPredict word already in histPredict, do not add to predictions
									valid = false;
									
							}
							if(valid) {//add dicPredict word, leave for loop
								predictions[i] = dicPredict[j];
								dicIndex++;
								break;
							}
							//word not valid, increment dicIndex
							dicIndex++;
						}
						
					} else {//use the user history
						predictions[i] = histPredict[i];
					}
					
					if(predictions[i] == null)//no suggestions found from dictionary or user history; done
						break;
				}
				long endTime = System.nanoTime();
				double elapsedTime = (double) (endTime - startTime) / 1000000000.0;
				totalTime += elapsedTime;
				numTimes++;
				
				System.out.printf("(%f s)\n", elapsedTime);
				//print predictions
				System.out.println("Predictions:");
				for(int i = 0;i < predictions.length;i++) {
					if(predictions[i] != null) {
						System.out.print("(" + (i + 1) + ") " + predictions[i] + "     ");
					} else {
						if(i == 0)
							System.out.print("No predictions were found");
						break;
					} 
				}
				System.out.println();
				System.out.println();
				
				//next character
				System.out.print("Enter the next character: ");
				
				input = user.next();
				letter = input.charAt(0);
				
				//check if end of word or if they chose a suggestion, save if a completed word
				if(letter == '!' || letter == '$') {
					endWord = true;
				} else if(letter >= '0' && letter <= '9') {//check if suggestion
					try {
						if(predictions[Character.getNumericValue(letter) - 1] != null) {//valid suggestion
							word = new StringBuilder(predictions[Character.getNumericValue(letter) - 1]);//set word to the chosen prediction
							endWord = true;
						} else {//user entered a number that is an index of the array, but there was no suggestion corresponding to the number
							throw new ArrayIndexOutOfBoundsException();//invalid suggestion, jump to catch 
						}
					} catch(ArrayIndexOutOfBoundsException e) {//invalid prediction, end the word
						System.out.println("Prediction not found");
						endWord = true;
					}
					
				} else {//not end of word yet
					word.append(letter);
					
					System.out.println();
				}
				
				
				
				
			}
			if(word.length() > 0) {
				System.out.printf("  WORD COMPLETED: %s\n", word);
				userHistory.add(word.toString());
			}
			
			//check if end of session
			if(letter == '!') {//end of session
				end = true;
				userHistory.writeToFile();//save to file
				if(numTimes > 0) {//calculate average run time of predictions
					double avgTime = totalTime / numTimes;
					System.out.printf("Average time: %f s\n", avgTime);
				}
				System.out.println("Bye");
			}
			
		}
		
	}

}