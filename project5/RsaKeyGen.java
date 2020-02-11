
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Random;

public class RsaKeyGen {

	public static void main(String[] args) throws IOException {
		LargeInteger p = new LargeInteger(512, new Random());
		LargeInteger q = new LargeInteger(512, new Random());
		LargeInteger n = p.multiply(q);
		LargeInteger one = new LargeInteger(1);
		LargeInteger phiN = (p.subtract(one)).multiply(q.subtract(one));
		
		Random r = new Random();
		
		LargeInteger e = new LargeInteger(3);
		
		
	
		
		LargeInteger d = one.divide(e.divide(phiN)[1])[0];
		
		File pubkey = new File("pubkey.rsa");
		File privkey = new File("privkey.rsa");
		ObjectOutputStream pubStream = new ObjectOutputStream(new FileOutputStream(pubkey));
		ObjectOutputStream privStream = new ObjectOutputStream(new FileOutputStream(privkey));
		
		pubStream.writeObject(e.getVal());
		pubStream.writeObject(n.getVal());
		pubStream.close();
		
		
		privStream.writeObject(d.getVal());
		privStream.writeObject(n.getVal());
		privStream.close();
	}

}
