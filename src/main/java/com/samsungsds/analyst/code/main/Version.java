package com.samsungsds.analyst.code.main;

public class Version {
	public static final String CODE_ANALYST = "1.0.0";
	public static final String SONAR_SCANNER = "2.8";
	public static final String SONAR_SERVER = "6.2.1";
	public static final String PMD = "5.4.6";
	public static final String FINDBUGS = "3.0.1";
	public static final String JDEPEND = "2.9.1";
	
	public static final String PMD_RULESET = "5.4 - 147 ruleset";
	public static final String FINDBUGS_RULESET = "3.0.1 - 237 ruleset";
	
	public static void printVersionInfo() {
		System.out.println("Code Analyst : " + CODE_ANALYST);
		System.out.println("  - Sonar Scanner : " + SONAR_SCANNER + " (LGPL v3.0)");
		System.out.println("  - Sonar Server : " + SONAR_SERVER + " (LGPL v3.0)");
		System.out.println("  - PMD : " + PMD + " (BSD-style)");
		System.out.println("  - FindBugs : " + FINDBUGS + " (LGPL v3.0)");
		System.out.println("  - JDepend : " + JDEPEND +"-based modification" + " (BSD-style) ");
		System.out.println();
		System.out.println("Default RuleSet");
		System.out.println("  - PMD : " + PMD_RULESET);
		System.out.println("  - FindBugs : " + FINDBUGS_RULESET);
		System.out.println();
		System.out.println("Copyright(c) 2017 By Samsung SDS (SW Technology Lab.)");
	}
}