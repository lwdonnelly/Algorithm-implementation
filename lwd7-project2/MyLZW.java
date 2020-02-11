/*************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *  WARNING: STARTING WITH ORACLE JAVA 6, UPDATE 7 the SUBSTRING
 *  METHOD TAKES TIME AND SPACE LINEAR IN THE SIZE OF THE EXTRACTED
 *  SUBSTRING (INSTEAD OF CONSTANT SPACE AND TIME AS IN EARLIER
 *  IMPLEMENTATIONS).
 *
 *  See <a href = "http://java-performance.info/changes-to-string-java-1-7-0_06/">this article</a>
 *  for more details.
 *
 *************************************************************************/
//Luke Donnelly
//CS 1501 Project 2
public class MyLZW {
    private static final int R = 256;        // number of input chars
    private static int L = 512;       // number of codewords = 2^W
    private static int W = 9;         // codeword width
	private static final int maxWidth = 16; //max codeword width
	private static char mode; //mode of encoding: r, n, m
	private static int bitsCompressed = 0; //total number of compressed bits
	private static int sourceBits = 0; //total number of uncompressed bits
	private static float originalRatio = 0; //compression ratio when codebook fills up
	private static float currRatio; //current compression ratio

    public static void compress() { 
        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF

		BinaryStdOut.write(mode);
        while (input.length() > 0) {
            String s = st.longestPrefixOf(input);  // Find max prefix match s.
            BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
			bitsCompressed += W;
			
            int t = s.length();
			sourceBits += t * 8;
            if (t < input.length() && code < L) {    // Add s to symbol table.
                st.put(input.substring(0, t + 1), code++);
			} else if(code >= 1 << maxWidth) { //max codeword size reached
				if(mode == 'r') {//reset mode
					W = 9;
					L = 512;
					
					st = new TST<Integer>();
					for (int i = 0; i < R; i++)
						st.put("" + (char) i, i);
					code = R+1;  // R is codeword for EOF
					
					st.put(input.substring(0, t + 1), code++);
				} else if(mode == 'm') {//monitor mode
					currRatio = (float) sourceBits / (float) bitsCompressed;
					if(originalRatio == 0) {
						originalRatio = currRatio;
					} else {
						
						float monitorRatio = originalRatio / currRatio; 
						
						if(monitorRatio > 1.1) {//reset
							originalRatio = 0;
							W = 9;
							L = 512;
					
							st = new TST<Integer>();
							for (int i = 0; i < R; i++)
								st.put("" + (char) i, i);
							code = R+1;  // R is codeword for EOF
						
							st.put(input.substring(0, t + 1), code++);
						}
					}
				}
			} else if (code >= L && t < input.length()) { //increse codeword width, then add to symbol table
				
				W++;
				L <<= 1;//2^W
				st.put(input.substring(0, t + 1), code++);
			}
			
            input = input.substring(t);            // Scan past s in input.
        }
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    } 


    public static void expand() {
        String[] st = new String[L];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

		char mode = BinaryStdIn.readChar();
        int codeword = BinaryStdIn.readInt(W);
		bitsCompressed += W;
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];

        while (true) {
			sourceBits += val.length() * 8;
			currRatio = (float) sourceBits / (float) bitsCompressed;
			
			if(i == L && W != maxWidth) {
				W++;
				L <<= 1;
				String[] temp = st;
				st = new String[L];
				System.arraycopy(temp, 0, st, 0, temp.length );
			} else if(i >= 1 << maxWidth) {//max codeword size reached
				if(mode == 'r') {//reset mode
					
					W = 9;
					L = 512;
					
					st = new String[L];

					// initialize symbol table with all 1-character strings
					for (i = 0; i < R; i++)
						st[i] = "" + (char) i;
					st[i++] = "";
				} else if(mode == 'm') {//monitor mode
					if(originalRatio == 0) {
						originalRatio = currRatio;
					} else {
						//check if reset or do nothing
						float monitorRatio = originalRatio / currRatio;
						
						if(monitorRatio > 1.1) {//reset
							W = 9;
							L = 512;
							originalRatio = 0;
					
							st = new String[L];

							// initialize symbol table with all 1-character strings
							for (i = 0; i < R; i++)
								st[i] = "" + (char) i;
							st[i++] = "";
						} 
					}
				}
			}
			
			
            BinaryStdOut.write(val);
            codeword = BinaryStdIn.readInt(W);
			bitsCompressed += W;
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L) {
				st[i++] = val + s.charAt(0);
			} 
            val = s;
        }
        BinaryStdOut.close();
    }



    public static void main(String[] args) {
		
		
        if (args[0].equals("-")) { 
		
			//set the mode
			if(args.length > 1) {
				if(args[1].equals("n")) mode = args[1].charAt(0);
				else if(args[1].equals("r")) mode = args[1].charAt(0);
				else if(args[1].equals("m")) mode = args[1].charAt(0);
				else throw new IllegalArgumentException("Illegal command line argument");
			} else {
				throw new IllegalArgumentException("Illegal command line argument");
			}
		
			compress();
		}
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
		
    }

}
