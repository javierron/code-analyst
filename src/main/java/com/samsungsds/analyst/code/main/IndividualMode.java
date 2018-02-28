package com.samsungsds.analyst.code.main;

import com.samsungsds.analyst.code.api.AnalysisMode;

public class IndividualMode extends AnalysisMode {

	public void setAll() {
		setCodeSize(true);
		setDuplication(true);
		setComplexity(true);
		setSonarJava(true);
		setPmd(true);
		setFindBugs(true);
		setFindSecBugs(true);
		setWebResource(true);
		setDependency(true);
		setUnusedCode(true);
	}
}
