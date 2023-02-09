package Sic;

import java.io.FileNotFoundException;
import java.math.BigInteger;

public class Utils {

	// Parses it to Binary number
	static public BigInteger parseHexaDecimalString(String hexa) {
		return new BigInteger(hexa, 16);
	}

	// Parses it to Binary number
	static public BigInteger parseBinaryString(String binary) {
		return new BigInteger(binary, 2);
	}

	// Converts from hexadecimal string to binary string
	static public String hexaToBinary(String hexaString) {
		BigInteger decimal = parseHexaDecimalString(hexaString);
		return decimal.toString(2);
	}

	// omits last n bits from provided binaryString
	static public String omitLastNBits(String binaryString, int n) {
		return binaryString.substring(0, binaryString.length()-n);
	}

	static public String formatBits(String str) {
		return String.format("%4s", str).replace(' ', '0');
	}

//	public static void main(String[] args) throws FileNotFoundException {
//		String binaryString = hexaToBinary("AF");
//		System.out.println(binaryString);
//		System.out.println(omitLastNBits(binaryString, 2));
//	}
}
