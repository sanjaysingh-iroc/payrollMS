package com.konnect.jpms.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.StringTokenizer;

public class ClientMacAddress {
	public final static String getMacAddress() throws IOException {
		String os = System.getProperty("os.name"); // Name of the Operating System

		try {
			if (os.startsWith("Windows")) {
				return windowsParseMacAddress(windowsRunIpConfigCommand());
			} else if (os.startsWith("Linux")) {
				return linuxParseMacAddress(linuxRunIfConfigCommand());
			} else {
				throw new IOException("unknown operating system: " + os);
			}
		} catch (ParseException ex) {
			ex.printStackTrace();
			throw new IOException(ex.getMessage());
		}
	}

	/**
	 * This method is called if the operating system is Linux
	 */
	private final static String linuxParseMacAddress(String ipConfigResponse) {
		String localHost = null;

		try {
			localHost = InetAddress.getLocalHost().getHostAddress();
		} catch (java.net.UnknownHostException ex) {
			ex.printStackTrace();
		}

		StringTokenizer tokenizer = new StringTokenizer(ipConfigResponse, "\n");
		String lastMacAddress = null;

		while (tokenizer.hasMoreTokens()) {
			String line = tokenizer.nextToken().trim();
			boolean containsLocalHost = line.indexOf(localHost) >= 0;

			// see if line contains IP address
			if (containsLocalHost && lastMacAddress != null) {
				return lastMacAddress;
			}

			// see if line contains MAC address
			int macAddressPosition = line.indexOf("HWaddr");
			if (macAddressPosition <= 0)
				continue;

			String macAddressCandidate = line.substring(macAddressPosition + 6)
					.trim();

			if (linuxIsMacAddress(macAddressCandidate)) {
				lastMacAddress = macAddressCandidate;
				continue;
			}
		}

		return lastMacAddress;
	}

	private final static boolean linuxIsMacAddress(String macAddressCandidate) {
		// TODO: use a smart regular expression
		if (macAddressCandidate.length() != 17)
			return false;
		return true;
	}

	/**
	 * This method executes the ifconfig command in linux to capture the machine
	 * information to a buffer
	 */
	private final static String linuxRunIfConfigCommand() throws IOException {
		Process p = Runtime.getRuntime().exec("ifconfig");
		InputStream stdoutStream = new BufferedInputStream(p.getInputStream());

		StringBuffer buffer = new StringBuffer();

		for (;;) {
			int c = stdoutStream.read();
			if (c == -1)
				break;
			buffer.append((char) c);
		}

		String outputText = buffer.toString();

		stdoutStream.close();

		return outputText;
	}

	/**
	 * This method is called if the operating system is Windows
	 */
	private final static String windowsParseMacAddress(String ipConfigResponse)
			throws ParseException {
		String localHost = null;
		try {
			localHost = InetAddress.getLocalHost().getHostAddress();
		} catch (java.net.UnknownHostException ex) {
			ex.printStackTrace();			
		}

		StringTokenizer tokenizer = new StringTokenizer(ipConfigResponse, "\n");
		String lastMacAddress = null;

		while (tokenizer.hasMoreTokens()) {
			String line = tokenizer.nextToken().trim();

			// see if line contains IP address
			if (line.endsWith(localHost) && lastMacAddress != null) {
				return lastMacAddress;
			}

			// see if line contains MAC address
			int macAddressPosition = line.indexOf(":");
			if (macAddressPosition <= 0)
				continue;

			String macAddressCandidate = line.substring(macAddressPosition + 1)
					.trim();

			if (windowsIsMacAddress(macAddressCandidate)) {
				lastMacAddress = macAddressCandidate;
				continue;
			}
		}

		return lastMacAddress;
	}

	private final static boolean windowsIsMacAddress(String macAddressCandidate) {
		// TODO: use a smart regular expression
		if (macAddressCandidate.length() != 17)
			return false;

		return true;
	}

	/**
	 * This method executes the "ipconfig /all" command in windows to capture
	 * the machine information to a buffer
	 */
	private final static String windowsRunIpConfigCommand() throws IOException {
		Process p = Runtime.getRuntime().exec("ipconfig /all");
		InputStream stdoutStream = new BufferedInputStream(p.getInputStream());

		StringBuffer buffer = new StringBuffer();
		for (;;) {
			int c = stdoutStream.read();
			if (c == -1)
				break;
			buffer.append((char) c);
		}
		String outputText = buffer.toString();

		stdoutStream.close();

		return outputText;
	}

}
