package com.konnect.jpms.util;

/**
 * @author Dattatray
 * @since 20-07-2021
 *
 */
public class EncryptionUtils {

	/**
	 * Pass the value for encryption
	 * 
	 * @param string
	 *            for encryption
	 * @return encryption value
	 */
	public String encrypt(String str) {
		int code;
		String result = "";
		for (int i = 0; i < str.length(); i++) {
			code = Math.round((float) Math.random() * 8 + 1);
			result += code + Integer.toHexString(((int) str.charAt(i)) ^ code) + "-";
		}
		return result.substring(0, result.lastIndexOf("-"));
	}

	/**
	 * Pass the value encrypted value for decryption
	 * 
	 * @param encrypted
	 *            string
	 * @return decrypt value
	 */
	public String decrypt(String str) {
		String result = "";
//		if (str.contains("-")) {
			str = str.replace("-", "");
			for (int i = 0; i < str.length(); i += 3) {
				if (str.length() >= (i + 3)) {
					if (i <= str.length()) {
						String hex = str.substring(i + 1, i + 3);
						try {
							result += (char) (Integer.parseInt(hex, 16) ^ (Integer.parseInt(String.valueOf(str.charAt(i)))));
						} catch (NumberFormatException ex) {
							break;
						}
					}
				}
			}
//		}
		return result;
	}

}
