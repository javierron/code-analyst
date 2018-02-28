package com.samsungsds.analyst.code.api;

public enum ProgressEvent {
	PREPARE_COMPLETE, 
	CODE_SIZE_COMPLETE, DUPLICATION_COMPLETE, COMPLEXITY_COMPLETE,
	SONARJAVA_COMPLETE, PMD_COMPLETE, FINDBUGS_COMPLETE, FINDSECBUGS_COMPLETE, WEBRESOURCE_COMPLETE,
	DEPENDENCY_COMPLETE, UNUSED_COMPLETE,
	FINAL_COMPLETE
}
