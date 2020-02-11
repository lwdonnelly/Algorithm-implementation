import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RsaSign {

	public static void main(String[] args) throws NoSuchAlgorithmException, ClassNotFoundException, IOException {
		char mode = args[0].charAt(0);
		File file = new File(args[1]);
		
		if(mode == 's') {
			byte[] data = Files.readAllBytes(file.toPath());

			// create class instance to create SHA-256 hash
			MessageDigest md = MessageDigest.getInstance("SHA-256");

			// process the file
			md.update(data);
			// generate a hash of the file
			byte[] digest = md.digest();
			LargeInteger sign = new LargeInteger(digest);
			
			//get private key
			File privkey = null;
			ObjectInputStream pStream = null;
			try {
				privkey = new File("privkey.rsa");
			
			pStream = new ObjectInputStream(new FileInputStream(privkey));
			} catch(FileNotFoundException e) {
				System.out.println("privkey.rsa not found");
				System.exit(0);
			}
			LargeInteger d = new LargeInteger((byte[]) pStream.readObject());
			LargeInteger n = new LargeInteger((byte[]) pStream.readObject());
			pStream.close();
			
			sign = sign.modularExp(d, n);
			File output = new File(file.toPath() + ".sig");
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(output));
			out.writeObject(sign.getVal());
			out.close();
		} else if(mode == 'v') {
			byte[] data = Files.readAllBytes(file.toPath());

			// create class instance to create SHA-256 hash
			MessageDigest md = MessageDigest.getInstance("SHA-256");

			// process the file
			md.update(data);
			// generate a hash of the file
			byte[] digest = md.digest();
			LargeInteger vHash = new LargeInteger(digest);
			
			File in;
			ObjectInputStream is = null;
			try {
				in = new File(file.toPath() + ".sig");
				is = new ObjectInputStream(new FileInputStream(in));
			} catch(FileNotFoundException e) {
				System.out.println(file.toPath() + ".sig not found");
				System.exit(0);
			}
			
			LargeInteger oHash = new LargeInteger((byte[]) is.readObject());
			is.close();
			
			try {
				in = new File("pubkey.rsa");
				is = new ObjectInputStream(new FileInputStream(in));
			} catch(FileNotFoundException e) {
				System.out.println("pubkey.rsa not found");
				System.exit(0);
			}
			
			LargeInteger e = new LargeInteger((byte[]) is.readObject());
			LargeInteger n = new LargeInteger((byte[]) is.readObject());
			is.close();
			
			
			oHash = oHash.modularExp(e, n);
			if(oHash.equals(vHash)) {
				System.out.println("Signature is valid");
			} else {
				System.out.println("Signature is invalid");
			}
		}
	}

}
