package com.samsungsds.analyst.code.api;

public class AnalysisMode {

	private boolean codeSize = true;
	private boolean duplication = true;
	private boolean complexity = true;
	private boolean sonarJava = true;
	private boolean pmd = true;
	private boolean findBugs = true;
	private boolean findSecBugs = true;
	private boolean javascript = true;
	private boolean css = false;			// disabled by default
	private boolean html = false;			// disabled by default
	private boolean dependency = true;
	private boolean unusedCode = true;

	public boolean isSonarServer() {
		return codeSize || duplication || sonarJava || javascript || css || html;
	}

	public boolean isWebResources() {
		return javascript || css || html;
	}

	public boolean isCodeSize() {
		return codeSize;
	}

	public void setCodeSize(boolean codeSize) {
		this.codeSize = codeSize;
	}

	public boolean isDuplication() {
		return duplication;
	}

	public void setDuplication(boolean duplication) {
		this.duplication = duplication;
	}

	public boolean isComplexity() {
		return complexity;
	}

	public void setComplexity(boolean complexity) {
		this.complexity = complexity;
	}

	public boolean isSonarJava() {
		return sonarJava;
	}

	public void setSonarJava(boolean sonarJava) {
		this.sonarJava = sonarJava;
	}

	public boolean isPmd() {
		return pmd;
	}

	public void setPmd(boolean pmd) {
		this.pmd = pmd;
	}

	public boolean isFindBugs() {
		return findBugs;
	}

	public void setFindBugs(boolean findBugs) {
		this.findBugs = findBugs;
	}

	public boolean isFindSecBugs() {
		return findSecBugs;
	}

	public void setFindSecBugs(boolean findSecBugs) {
		this.findSecBugs = findSecBugs;
	}

	public boolean isJavascript() {
		return javascript;
	}

	public void setJavascript(boolean javascript) {
		this.javascript = javascript;
	}

	public boolean isCss() {
		return css;
	}

	public void setCss(boolean css) {
		this.css = css;
	}

	public boolean isHtml() {
		return html;
	}

	public void setHtml(boolean html) {
		this.html = html;
	}

	public boolean isDependency() {
		return dependency;
	}

	public void setDependency(boolean dependency) {
		this.dependency = dependency;
	}

	public boolean isUnusedCode() {
		return unusedCode;
	}

	public void setUnusedCode(boolean unusedCode) {
		this.unusedCode = unusedCode;
	}

}
