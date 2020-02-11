//Luke Donnelly
//CS 1501 project 4

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class NetworkAnalysis {

	public static void main(String[] args) {
		//initiate graph
		Scanner fileIn = null;
		try {
			File input = new File(args[0]);
			fileIn = new Scanner(input);
		} catch(FileNotFoundException e) {
			System.out.println("File not found");
			System.exit(1);
		}

		String line;
		String[] data;
		line = fileIn.nextLine();
		Graph graph = new Graph(Integer.parseInt(line));
		while(fileIn.hasNextLine()) {
			line = fileIn.nextLine();
			data = line.split(" ");
			
			graph.addVertex(Integer.parseInt(data[0]), Integer.parseInt(data[1]), data[2], Integer.parseInt(data[3]), Integer.parseInt(data[4]));
		}
		System.out.println("Graph created");
		
		
		//user input
		Scanner in = new Scanner(System.in);
		boolean end = false;
		while(!end) {
			System.out.println("What do you want to do?");
			System.out.println("	1) Find the lowest latency path between two points");
			System.out.println("	2) Determine whether or not the graph is copper-only connected");
			System.out.println("	3) Find the lowest average latency spanning tree");
			System.out.println("	4) Determine whether or not the graph would remain connected if any two vertices in the graph were to fail");
			System.out.println("	5) Quit");
			System.out.print("Enter your option:");
			
			int option = in.nextInt();
			in.nextLine();
			
			switch(option) {
				case 1 :
					System.out.print("What is the first vertex?");
					int ID1 = Integer.parseInt(in.next());
					in.nextLine();
					System.out.print("What is the second vertex?");
					int ID2 = Integer.parseInt(in.next());
					in.nextLine();
					
					graph.dijkstra(ID1, ID2);
				break;
				case 2 :
					if(graph.isCopperConnected()) {
						System.out.println("   The graph is copper-only connected");
					} else {
						System.out.println("   The graph is not copper-only connected");
					}
				break;
				case 3 :
					//minimum spanning tree, since all spanning trees have the same number of edges
					graph.minSpanningTree();
					
				break;	
				case 4 :
					if(graph.isTriconnected()) {
						System.out.println("   the graph would remain connected if any two vertices in the graph were to fail");
					} else {
						System.out.println("   the graph would not remain connected if any two vertices in the graph were to fail");
					}
					
				break;
				case 5 :
					System.exit(0);
				break;
			
			}
		}
		
	}
	

}
