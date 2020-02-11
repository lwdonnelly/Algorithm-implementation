//Luke Donnelly
//CS 1501 Project 4

import java.util.Stack;

public class Graph {
	private Vertex[] adjacencyList;
	
	public Graph(int size) {
		adjacencyList = new Vertex[size];
		for(int i = 0;i < size;i++)
			adjacencyList[i] = new Vertex(i);
	}
	
	public void addVertex(int ID1, int ID2, String cable, int bandwidth, int length) {
		Vertex temp = adjacencyList[ID1].next;
		Vertex next = new Vertex(ID2);
		adjacencyList[ID1].next = next; 
		
		next.cable = cable;
		next.bandwidth = bandwidth;
		next.length = length;
		next.next = temp;
		
		if(cable.equals("optical")) {
			next.latency = (double)length / 200000000.0;
		} else {
			next.latency = (double)length / 230000000.0;
		}
		
		temp = adjacencyList[ID2].next;
		next = new Vertex(ID1);
		adjacencyList[ID2].next = next; 
		
		next.cable = cable;
		next.bandwidth = bandwidth;
		next.length = length;
		next.next = temp;
		
		if(cable.equals("optical")) {
			next.latency = (double)length / 200000000.0;
		} else {
			next.latency = (double)length / 230000000.0;
		}
	}
	
	//dijsktra's algorithm for finding path with minimum latency between two points
	IndexMinPQ<Double> pq;
	private double[] distTo;
	private Edge[] edgeTo;
	public void dijkstra(int ID1, int ID2) {
		

        distTo = new double[adjacencyList.length];
        edgeTo = new Edge[adjacencyList.length];

        for (int v = 0; v < adjacencyList.length; v++)
            distTo[v] = Double.POSITIVE_INFINITY;
        distTo[ID1] = 0.0;

        // relax vertices in order of distance from s
        pq = new IndexMinPQ<Double>(adjacencyList.length);
        pq.insert(ID1, distTo[ID1]);
        while (!pq.isEmpty()) {
            int v = pq.delMin();
            Vertex cur = adjacencyList[v].next;
            while(cur != null) {
            	relax(cur, adjacencyList[v]);
            		
            	cur = cur.next;
            }
        }
        
        if(edgeTo[ID2] == null) {
        	System.out.println("   A path between these points does not exist");
        	return;
        }
        
        Stack<Edge> path = new Stack<Edge>();
        int minBand = Integer.MAX_VALUE;
        for (Edge e = edgeTo[ID2]; e != null; e = edgeTo[e.from.ID]) {
        	if(e.bandwidth < minBand)
        		minBand = e.bandwidth;
            path.push(e);
        }
        
        Edge currEdge = path.pop();
        
        System.out.println("Path Edges:");
        while(currEdge != null) {
        	System.out.printf("	From vertex %d to vertex %d\n", currEdge.from.ID, currEdge.to.ID);
        	if(path.isEmpty())
        		currEdge = null;
        	else
        		currEdge = path.pop();
        }
        System.out.printf("Bandwidth available:\n	%d mbps\n", minBand);
	}
	
	// relax edge and update pq if changed, for dijkstra's algorithm
    private void relax(Vertex to, Vertex from) {
        int v = from.ID, w = to.ID;
        
        if (distTo[w] > distTo[v] + to.latency) {
            distTo[w] = distTo[v] + to.latency;
            edgeTo[w] = new Edge(new Vertex(to), from, to.latency, to.bandwidth);
            if (pq.contains(w)) pq.decreaseKey(w, distTo[w]);
            else                pq.insert(w, distTo[w]);
        }
    }
    
    //determine whether graph is connected only by copper cables
    private int count;
    private boolean[] marked;
    public boolean isCopperConnected() {
    	
    	marked = new boolean[adjacencyList.length];
    	count = 0;
        dfsCopper(0);
        
        if(count != adjacencyList.length)
        	return false;
        return true;
    }
    
    
    // depth first search from v, only including copper cables
    private void dfsCopper(int v) {
        count++;
        marked[v] = true;
        
        Vertex cur = adjacencyList[v].next;
        int w;
        while(cur != null) {
        	w = cur.ID;
        	if (!marked[w] && cur.cable.equals("copper")) {
                dfsCopper(w);
            }
        		
        	cur = cur.next;
        }
    }
    
    //for each vertex, remove it and check if graph is still biconnected
    private int[] low;
    private int[] pre;
    private int cnt;
    public boolean isTriconnected() {
    	for(int i = 0;i < adjacencyList.length;i++) {
    		low = new int[adjacencyList.length];
            pre = new int[adjacencyList.length];
            for (int v = 0; v < adjacencyList.length; v++)
                low[v] = -1;
            for (int v = 0; v < adjacencyList.length; v++)
                pre[v] = -1;
            
            for (int v = 0; v < adjacencyList.length; v++){
                if (pre[v] == -1) {
                    
                	if(!isBiconnected(v,v,i)) {
                		return false;
                	}
                }
            }
    	}
    	return true;
    }
    private boolean isBiconnected(int u, int v, int deleted) {
    	if(u == deleted || v == deleted)
    		return true;
    	int children = 0;
        pre[v] = cnt++;
        low[v] = pre[v];
        Vertex cur = adjacencyList[v].next;
        while(cur != null) {
        	if(cur.ID == deleted) {
        		cur = cur.next;
        		continue;
        	}
        	
            if (pre[cur.ID] == -1) {
                children++;
                if(!isBiconnected(v, cur.ID, deleted))
                	return false;
                // update low number
                low[v] = Math.min(low[v], low[cur.ID]);

                // non-root of DFS is an articulation point if low[w] >= pre[v]
                if (low[cur.ID] >= pre[v] && u != v) 
                    return false;
            }

            // update low number - ignore reverse of edge leading to v
            else if (cur.ID != u)
                low[v] = Math.min(low[v], pre[cur.ID]);
            
            cur = cur.next;
        }

        // root of DFS is an articulation point if it has more than 1 child
        if (u == v && children > 1)
            return false;
        
        return true;
    }
    
    //to find minimum average latency spanning tree, because all spanning trees will have the same number of edges
    public void minSpanningTree() {
    	marked = new boolean[adjacencyList.length];
    	count = 0;
        dfs(0);
        
        if(count != adjacencyList.length) {
        	System.out.println("   Graph is not connected, spanning tree covering all vertices cannot be made");
        	return;
        }
    	
    	edgeTo = new Edge[adjacencyList.length];
        distTo = new double[adjacencyList.length];
        marked = new boolean[adjacencyList.length];
        pq = new IndexMinPQ<Double>(adjacencyList.length);
        for (int v = 0; v < adjacencyList.length; v++)
            distTo[v] = Double.POSITIVE_INFINITY;
        
        prim(0);
        
        //print tree
        System.out.println("Lowest average latency spanning tree:");
        for(Edge e : edgeTo) {
        	if(e != null)
        		System.out.printf("   From vertex %d to vertex %d\n", e.from.ID, e.to.ID);
        }
    }
    
    private void prim(int s) {
        distTo[s] = 0.0;
        pq.insert(s, distTo[s]);
        while (!pq.isEmpty()) {
            int v = pq.delMin();
            scan(v);
        }
    }
    
    //for determining if the graph is connected before creating spanning tree
 // depth first search from v
    private void dfs(int v) {
        count++;
        marked[v] = true;
        
        Vertex cur = adjacencyList[v].next;
        int w;
        while(cur != null) {
        	w = cur.ID;
        	if (!marked[w]) {
                dfs(w);
            }
        		
        	cur = cur.next;
        }
    }

    // scan vertex v, prim's algorithm
    private void scan(int v) {
        marked[v] = true;
        Vertex cur = adjacencyList[v].next;
        while(cur != null) {
            int w = cur.ID;
            if (marked[w]) {
            	cur = cur.next;
            	continue;         // v-w is obsolete edge
            }
            if (cur.latency < distTo[w]) {
                distTo[w] = cur.latency;
                edgeTo[w] = new Edge(cur, adjacencyList[v], cur.latency, cur.bandwidth);
                if (pq.contains(w)) pq.decreaseKey(w, distTo[w]);
                else                pq.insert(w, distTo[w]);
            }
            
            cur = cur.next;
        }
    }
	
    //edges to be stored in edgeTo arrays in dijkstra and prim's algorithms
    private class Edge {
		Vertex to;
		Vertex from;
		double latency;
		int bandwidth;
		
		Edge(Vertex t, Vertex f, double lat, int ban) {
			to = t;
			from = f;
			latency = lat;
			bandwidth = ban;
		}
		
	}
    
    //vertices stored in adjacency list
	private class Vertex {
		int ID;
		String cable;
		int bandwidth;
		int length;
		Vertex next;
		double latency;
		
		Vertex(int id) {
			ID = id;
			next = null;
		}
		
		Vertex(Vertex v) {
			ID = v.ID;
			cable = new String(v.cable);
			bandwidth = v.bandwidth;
			length = v.length;
			next = v.next;
			latency = v.latency;
		}
	}
	
	
}
