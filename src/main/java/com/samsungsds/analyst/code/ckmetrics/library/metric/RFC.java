package com.samsungsds.analyst.code.ckmetrics.library.metric;
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

Modified from CK metrics calculator(https://github.com/mauricioaniche/ck) under Apache 2.0 license
@author Mauricio Aniche
 */

import java.util.HashSet;
import java.util.List;

import com.samsungsds.analyst.code.ckmetrics.library.CKNumber;
import com.samsungsds.analyst.code.ckmetrics.library.CKReport;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;

public class RFC extends ASTVisitor implements Metric {

	private HashSet<String> methodInvocations = new HashSet<String>();

	public boolean visit(MethodInvocation node) {
		IMethodBinding binding = node.resolveMethodBinding();
		count(node.getName()  + "/" + arguments(node.arguments()), binding);
		
		return super.visit(node);
	}

	private String arguments(List<?> arguments) {
		if (arguments == null || arguments.isEmpty()) return "0";
		return "" + arguments.size();
	}

	private void count(String methodName, IMethodBinding binding) {
		if(binding!=null) {
			String method = getMethodName(binding);
			methodInvocations.add(method);
		} else {
			methodInvocations.add(methodName);
		}
	}
	
	public boolean visit(SuperMethodInvocation node) {
		IMethodBinding binding = node.resolveMethodBinding();
		count(node.getName()  + "/" + arguments(node.arguments()), binding);
		
		return super.visit(node);
	}

	private String getMethodName(IMethodBinding binding) {
		
		String argumentList = "";
		ITypeBinding[] args = binding.getParameterTypes();
		for (ITypeBinding arg : args) {
			argumentList += arg.getName();
		}
		String method = binding.getDeclaringClass().getQualifiedName() + "." + binding.getName() + "/" + binding.getTypeArguments().length + "[" + argumentList + "]";
		return method;
	}
	
	@Override
	public void execute(CompilationUnit cu, CKNumber number, CKReport report) {
		cu.accept(this);
	}

	@Override
	public void setResult(CKNumber result) {
		result.setRfc(methodInvocations.size());
	}
}
