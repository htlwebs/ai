package com.m3.tso.det

class AuthenticationController {

	public static String generateKey(int length) {
		String alphabet = new String("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
		int n = alphabet.length();

		String result = new String();
		Random r = new Random();
		for (int i = 0; i < length; i++) {
			result = result + alphabet.charAt(r.nextInt(n));
		}
		return result;
	}
}
