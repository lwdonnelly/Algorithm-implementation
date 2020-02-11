import java.util.Random;
import java.math.BigInteger;

public class LargeInteger {
	
	private final byte[] ONE = {(byte) 1};

	private byte[] val;

	/**
	 * Construct the LargeInteger from a given byte array
	 * @param b the byte array that this LargeInteger should represent
	 */
	public LargeInteger(byte[] b) {
		val = b;
	}

	/**
	 * Construct the LargeInteger by generatin a random n-bit number that is
	 * probably prime (2^-100 chance of being composite).
	 * @param n the bitlength of the requested integer
	 * @param rnd instance of java.util.Random to use in prime generation
	 */
	public LargeInteger(int n, Random rnd) {
		val = BigInteger.probablePrime(n, rnd).toByteArray();
	}
	
	public LargeInteger(int n) {
		val = new byte[] {
	            (byte)(n >> 24),
	            (byte)(n >> 16),
	            (byte)(n >> 8),
	            (byte)n};
	}
	
	/**
	 * Return this LargeInteger's val
	 * @return val
	 */
	public byte[] getVal() {
		return val;
	}

	/**
	 * Return the number of bytes in val
	 * @return length of the val byte array
	 */
	public int length() {
		return val.length;
	}

	/** 
	 * Add a new byte as the most significant in this
	 * @param extension the byte to place as most significant
	 */
	public void extend(byte extension) {
		byte[] newv = new byte[val.length + 1];
		newv[0] = extension;
		for (int i = 0; i < val.length; i++) {
			newv[i + 1] = val[i];
		}
		val = newv;
	}
	
	public void leftShiftByte(int numBytes) {
		if(numBytes <= 0)
			return;
		byte[] newv = new byte[val.length + numBytes];
		for(int i = 0;i < val.length;i++) {
			newv[i] = val[i];
		}
		
		for(int i = 0;i < numBytes;i++) {
			newv[val.length + i] = (byte) 0;
		}
		val = newv;
	}

	/**
	 * If this is negative, most significant bit will be 1 meaning most 
	 * significant byte will be a negative signed number
	 * @return true if this is negative, false if positive
	 */
	public boolean isNegative() {
		return (val[0] < 0);
	}

	/**
	 * Computes the sum of this and other
	 * @param other the other LargeInteger to sum with this
	 */
	public LargeInteger add(LargeInteger other) {
		byte[] a, b;
		// If operands are of different sizes, put larger first ...
		if (val.length < other.length()) {
			a = other.getVal();
			b = val;
		}
		else {
			a = val;
			b = other.getVal();
		}

		// ... and normalize size for convenience
		if (b.length < a.length) {
			int diff = a.length - b.length;

			byte pad = (byte) 0;
			if (b[0] < 0) {
				pad = (byte) 0xFF;
			}

			byte[] newb = new byte[a.length];
			for (int i = 0; i < diff; i++) {
				newb[i] = pad;
			}

			for (int i = 0; i < b.length; i++) {
				newb[i + diff] = b[i];
			}

			b = newb;
		}

		// Actually compute the add
		int carry = 0;
		byte[] res = new byte[a.length];
		for (int i = a.length - 1; i >= 0; i--) {
			// Be sure to bitmask so that cast of negative bytes does not
			//  introduce spurious 1 bits into result of cast
			carry = ((int) a[i] & 0xFF) + ((int) b[i] & 0xFF) + carry;

			// Assign to next byte
			res[i] = (byte) (carry & 0xFF);

			// Carry remainder over to next byte (always want to shift in 0s)
			carry = carry >>> 8;
		}

		LargeInteger res_li = new LargeInteger(res);
	
		// If both operands are positive, magnitude could increase as a result
		//  of addition
		if (!this.isNegative() && !other.isNegative()) {
			// If we have either a leftover carry value or we used the last
			//  bit in the most significant byte, we need to extend the result
			if (res_li.isNegative()) {
				res_li.extend((byte) carry);
			}
		}
		// Magnitude could also increase if both operands are negative
		else if (this.isNegative() && other.isNegative()) {
			if (!res_li.isNegative()) {
				res_li.extend((byte) 0xFF);
			}
		}

		// Note that result will always be the same size as biggest input
		//  (e.g., -127 + 128 will use 2 bytes to store the result value 1)
		return res_li;
	}

	/**
	 * Negate val using two's complement representation
	 * @return negation of this
	 */
	public LargeInteger negate() {
		byte[] neg = new byte[val.length];
		int offset = 0;

		// Check to ensure we can represent negation in same length
		//  (e.g., -128 can be represented in 8 bits using two's 
		//  complement, +128 requires 9)
		if (val[0] == (byte) 0x80) { // 0x80 is 10000000
			boolean needs_ex = true;
			for (int i = 1; i < val.length; i++) {
				if (val[i] != (byte) 0) {
					needs_ex = false;
					break;
				}
			}
			// if first byte is 0x80 and all others are 0, must extend
			if (needs_ex) {
				neg = new byte[val.length + 1];
				neg[0] = (byte) 0;
				offset = 1;
			}
		}

		// flip all bits
		for (int i  = 0; i < val.length; i++) {
			neg[i + offset] = (byte) ~val[i];
		}

		LargeInteger neg_li = new LargeInteger(neg);
	
		// add 1 to complete two's complement negation
		return neg_li.add(new LargeInteger(ONE));
	}

	/**
	 * Implement subtraction as simply negation and addition
	 * @param other LargeInteger to subtract from this
	 * @return difference of this and other
	 */
	public LargeInteger subtract(LargeInteger other) {
		return this.add(other.negate());
	}
	
	/**
	 * Compute the product of this and other
	 * @param other LargeInteger to multiply by this
	 * @return product of this and other
	 */
	public LargeInteger multiply(LargeInteger other) {
		// YOUR CODE HERE (replace the return, too...)
		boolean negate = false;
		if((this.isNegative() && !other.isNegative()) || (!this.isNegative() && other.isNegative()))
			negate = true;
		byte[] otherVal = other.getVal();
		int place = 0;
		int innerPlace;
		LargeInteger partial;
		LargeInteger total = new LargeInteger(new byte[] {(byte) 0});
		for(int i = val.length - 1;i > -1;i--) {
			partial = new LargeInteger(new byte[] {(byte) 0});
			innerPlace = 0;
			for(int j = other.length()-1;j > -1;j--) {
				int temp = val[i] * otherVal[j];
				LargeInteger largeTemp = new LargeInteger(temp);
				if(largeTemp.isNegative())
					largeTemp = largeTemp.negate();
					
				
				partial = partial.addToByte(largeTemp,innerPlace);
				innerPlace++;
			}
			if(partial.isNegative())
				partial = partial.negate();
			
			partial.leftShiftByte(place);
			total = total.add(partial);
			place++;
		}
		if(negate)
			total = total.negate();
		
		return total;
	}
	
	private LargeInteger addToByte(LargeInteger n, int place) {
		byte[] res = new byte[val.length];
		int over = val.length - 1 - place;
		over *= -1;
		if(over > 0) {
			res = new byte[val.length + (over)];
		}
		
		int carry = 0;
		for (int i = res.length - 1; i > res.length - 1 - place; i--) {
			if(over > 0) {
				if(i - over < 0) {
					res[i] = 0;
				} else {
					res[i] = val[i-over];
				}
			} else {
				res[i] = val[i];
			}
		}
		
		byte[] oVal = n.getVal();
		
		int j = oVal.length - 1;
		for (int i = res.length - 1 - place; i >= 0 && j >= 0; i--) {
			
			if(over > 0) {
				carry = ((int) oVal[j] & 0xFF) + carry;
				// Assign to next byte
				res[i] = (byte) (carry & 0xFF);

				// Carry remainder over to next byte (always want to shift in 0s)
				carry = carry >>> 8;
			} else {
				// Be sure to bitmask so that cast of negative bytes does not
				//  introduce spurious 1 bits into result of cast
				carry = ((int) val[i] & 0xFF) + ((int) oVal[j] & 0xFF) + carry;

				// Assign to next byte
				res[i] = (byte) (carry & 0xFF);

				// Carry remainder over to next byte (always want to shift in 0s)
				carry = carry >>> 8;
			}
			j--;
		}
		LargeInteger res_li = new LargeInteger(res);
		
		// If both operands are positive, magnitude could increase as a result
		//  of addition
		if (!this.isNegative() && !n.isNegative()) {
			// If we have either a leftover carry value or we used the last
			//  bit in the most significant byte, we need to extend the result
			if (res_li.isNegative()) {
				res_li.extend((byte) carry);
			}
		}
		// Magnitude could also increase if both operands are negative
		else if (this.isNegative() && n.isNegative()) {
			if (!res_li.isNegative()) {
				res_li.extend((byte) 0xFF);
			}
		}

		// Note that result will always be the same size as biggest input
		//  (e.g., -127 + 128 will use 2 bytes to store the result value 1)
		return res_li;
	}
	
	/**
	 * Run the extended Euclidean algorithm on this and other
	 * @param other another LargeInteger
	 * @return an array structured as follows:
	 *   0:  the GCD of this and other
	 *   1:  a valid x value
	 *   2:  a valid y value
	 * such that this * x + other * y == GCD in index 0
	 */
	 public LargeInteger[] XGCD(LargeInteger other) {
		// YOUR CODE HERE (replace the return, too...)
		return recursiveXGCD(this, other);
	 }
	 
	 private LargeInteger[] recursiveXGCD(LargeInteger a, LargeInteger b) {
		 LargeInteger[] dxy = new LargeInteger[3];

			if (b.isZero()){
				dxy[0] =a.copy(); dxy[1] = new LargeInteger(1); dxy[2] = new LargeInteger(0);
				
				return dxy;
			}
			else{
				LargeInteger t, t2;
				dxy = recursiveXGCD(b, a.divide(b)[1]);
				t = dxy[1].copy();
				t2 = dxy[2].copy();
				dxy[1] = dxy[2].copy();
				
				dxy[2] = t.subtract(t2.multiply(a.divide(b)[0]));

				return dxy;
			}
	 }
	 
	 public LargeInteger[] divide(LargeInteger other) {
		 boolean negate = false;
			if((this.isNegative() && !other.isNegative()) || (!this.isNegative() && other.isNegative()))
				negate = true;
		 LargeInteger D = other.copy();
		 if(other.isZero()) {
			 return null;
		 }
		 LargeInteger[] ans = new LargeInteger[2];
		 ans[0] = new LargeInteger(0);
		 ans[1] = new LargeInteger(0);
		 
		 for(int i = 0;i < val.length * 8;i++) {
			 ans[1] = ans[1].multiply(new LargeInteger(2));
			 
			 ans[1].setLastBit(getBit(i));
			 
			 if(greaterThanEqualTo(D)) {
				 ans[1] = ans[1].subtract(D);
				 ans[0].setBit(i, (byte) 1);
			 }
		 }
		 
		 if(negate)
				ans[0] = ans[0].negate();
		 
		 return ans;
	 }
	 
	 public boolean greaterThanEqualTo(LargeInteger other) {
		 return (!(subtract(other).isNegative()));
	 }
	 
	 public boolean equals(LargeInteger other) {
		 return (subtract(other).isZero());
	 }
	 
	 public void setBit(int i, byte b) {
		 if(b != 0 && b != 1)
			 return;
		 if(i/8 >= val.length)
			 return;
		 
		 int byteId = i / 8;
		 byte id = (byte) (i % 8);
		 
		 byte mask = (byte) (b << (7 - id));
		 
		 val[byteId] |= mask;
	 }
	 
	 public void setLastBit(byte b) {
		 if(b != 0 && b != 1)
			 return;
		 
		 val[val.length - 1] |= b;
					 
	 }
	 
	 private byte getBit(int i) {
		 int byteId = i / 8;
		 byte id = (byte) (i % 8);
		 
		 byte mask = (byte) (1 << (7 - id));
		 
		 return (byte) ((val[byteId] & mask) >> (7 - id));
	 }
	 
	 public boolean isZero() {
		 for(byte b : val) {
			 if(b != 0)
				 return false;
		 }
		 return true;
	 }
	 
	 public LargeInteger copy() {
		 byte[] newv = new byte[val.length];
		 for(int i = 0;i < val.length;i++)
			 newv[i] = val[i];
		 
		 return new LargeInteger(newv);
	 }

	 /**
	  * Compute the result of raising this to the power of y mod n
	  * @param y exponent to raise this to
	  * @param n modulus value to use
	  * @return this^y mod n
	  */
	 public LargeInteger modularExp(LargeInteger y, LargeInteger n) {
		// YOUR CODE HERE (replace the return, too...)
		 LargeInteger total = new LargeInteger(ONE);
		 for(int i = 0;i < y.length() * 8;i++) {
			 total = total.multiply(total);
			 
			 if(y.getBit(i) == 1)
				 total = total.multiply(this);
			 
			 if(total.greaterThanEqualTo(n))
				 total = total.divide(n)[1];
		 }
		return total;
	 }
}
