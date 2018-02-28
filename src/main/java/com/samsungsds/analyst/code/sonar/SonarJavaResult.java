package com.samsungsds.analyst.code.sonar;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.samsungsds.analyst.code.util.CSVFileResult;

public class SonarJavaResult implements Serializable, CSVFileResult {

	private static final long serialVersionUID = 6343847614524493660L;

	@Expose
	private String path;
	@Expose
	private String ruleRepository;
	@Expose
	private String ruleKey;
	@Expose
	private String msg;
	@Expose
	private int severity;
	@Expose
	private int startLine;
	@Expose
	private int startOffset;
	@Expose
	private int endLine;
	@Expose
	private int endOffset;

	public SonarJavaResult() {
		// default constructor (CSV)
		// column : path, ruleRepository, ruleKey, msg, severity, startLine, startOffset, endLine, endOffset
		path = "";
		ruleRepository = "";
		ruleKey = "";
		msg = "";
		severity = 0;
		startLine = 0;
		startOffset = 0;
		endLine = 0;
		endOffset = 0;
	}

	@Override
	public int getColumnSize() {
		return 9;
	}

	@Override
	public String getDataIn(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return path;
		case 1:
			return ruleRepository;
		case 2:
			return ruleKey;
		case 3:
			return msg;
		case 4:
			return String.valueOf(severity);
		case 5:
			return String.valueOf(startLine);
		case 6:
			return String.valueOf(startOffset);
		case 7:
			return String.valueOf(endLine);
		case 8:
			return String.valueOf(endOffset);
		default:
			throw new IndexOutOfBoundsException("Index: " + columnIndex);
		}
	}

	@Override
	public void setDataIn(int columnIndex, String data) {
		switch (columnIndex) {
		case 0:
			path = data;
			break;
		case 1:
			ruleRepository = data;
			break;
		case 2:
			ruleKey = data;
			break;
		case 3:
			msg = data;
			break;
		case 4:
			severity = Integer.parseInt(data);
			break;
		case 5:
			startLine = Integer.parseInt(data);
			break;
		case 6:
			startOffset = Integer.parseInt(data);
			break;
		case 7:
			endLine = Integer.parseInt(data);
			break;
		case 8:
			endOffset = Integer.parseInt(data);
			break;
		default:
			throw new IndexOutOfBoundsException("Index: " + columnIndex);
		}
	}

	public SonarJavaResult(String path, String ruleRepository, String ruleKey, String msg, int severity, int startLine, int startOffset, int endLine, int endOffset) {
		this.path = path;
		this.ruleRepository = ruleRepository;
		this.ruleKey = ruleKey;
		this.msg = msg;
		this.severity = severity;
		this.startLine = startLine;
		this.startOffset = startOffset;
		this.endLine = endLine;
		this.endOffset = endOffset;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getRuleRepository() {
		return ruleRepository;
	}

	public void setRuleRepository(String ruleRepository) {
		this.ruleRepository = ruleRepository;
	}

	public String getRuleKey() {
		return ruleKey;
	}

	public void setRuleKey(String ruleKey) {
		this.ruleKey = ruleKey;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getSeverity() {
		return severity;
	}

	public void setSeverity(int severity) {
		this.severity = severity;
	}

	public int getStartLine() {
		return startLine;
	}

	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}

	public int getStartOffset() {
		return startOffset;
	}

	public void setStartOffset(int startOffset) {
		this.startOffset = startOffset;
	}

	public int getEndLine() {
		return endLine;
	}

	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}

	public int getEndOffset() {
		return endOffset;
	}

	public void setEndOffset(int endOffset) {
		this.endOffset = endOffset;
	}

}
