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
package com.samsungsds.analyst.code.api;

public interface CodeAnalyst {
	void addProgressObserver(ProgressObserver observer);
	void deleteProgressObserver(ProgressObserver observer);
	
	String analyze(String where, ArgumentInfo argument, TargetFileInfo targetFile);
	
	ResultInfo analyzeWithSeparatedResult(String where, ArgumentInfo argument, TargetFileInfo targetFile);

	String analyzeWebResource(String where, WebArgumentInfo webArgument, WebTargetFileInfo webTargetFile, boolean includeCssAndHtml);
}
