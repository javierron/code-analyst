/*
Copyright 2018 Samsung SDS

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.samsungsds.analyst.code.util;

import java.io.IOException;

public class CSVUtil {
	private static final char DEFAULT_SEPARATOR = ',';
	private static final char DEFAULT_QUOTE_CHARACTER = '"';
	private static final char DEFAULT_ESCAPE_CHARACTER = '"';
	private static final String DEFAULT_LINE_END = "\n";

	private static boolean containsSpecialCharacters(String str) {
		return str.indexOf(DEFAULT_QUOTE_CHARACTER) != -1 
				|| str.indexOf(DEFAULT_ESCAPE_CHARACTER) != -1
				|| str.indexOf(DEFAULT_SEPARATOR) != -1 
				|| str.contains(DEFAULT_LINE_END) 
				|| str.contains("\r");
	}

	public static String getCSVStyleString(String str) {
		if (containsSpecialCharacters(str)) {
			try {
				return processLine(str);
			} catch (IOException ioe) {
				throw new IllegalStateException(ioe);
			}
		} else {
			return str;
		}
	}

	private static String processLine(String nextElement) throws IOException {
		StringBuilder appendable = new StringBuilder();
		
		appendable.append(DEFAULT_QUOTE_CHARACTER);

		for (int j = 0; j < nextElement.length(); j++) {
			char nextChar = nextElement.charAt(j);
			processCharacter(appendable, nextChar);
		}
		
		appendable.append(DEFAULT_QUOTE_CHARACTER);
		
		return appendable.toString();
	}

	private static void processCharacter(Appendable appendable, char nextChar)  throws IOException {
		if (checkCharactersToEscape(nextChar)) {
			appendable.append(DEFAULT_ESCAPE_CHARACTER);
		}
		appendable.append(nextChar);
	}
	
	private static boolean checkCharactersToEscape(char nextChar) {
		return (nextChar == DEFAULT_QUOTE_CHARACTER || nextChar == DEFAULT_ESCAPE_CHARACTER);
	}
	
	public static String getString(int number) {
		return Integer.toString(number);
	}
	
	public static String getStringsWithComma(String... strings) {
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < strings.length; i++) {
			if (i != 0) {
				builder.append(",");
			}
			builder.append(CSVUtil.getCSVStyleString(strings[i]));
		}
		
		return builder.toString();
	}
}
