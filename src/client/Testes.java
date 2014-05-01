package client;

import java.io.File;

import security.SymetricKey;

public class Testes {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			SymetricKey key = SymetricKey.createKey("o cosme é fag");
			System.out.println(key.toStringHex());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
