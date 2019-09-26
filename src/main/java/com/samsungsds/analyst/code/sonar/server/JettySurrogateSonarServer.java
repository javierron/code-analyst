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
package com.samsungsds.analyst.code.sonar.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.util.UUID;

import com.samsungsds.analyst.code.sonar.server.servlets.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.servlet.ServletHandler;

import com.samsungsds.analyst.code.main.CliParser;
import com.samsungsds.analyst.code.util.IOAndFileUtils;

/*
Request 처리 예시
- Load global settings : /api/settings/values.protobuf
- Load plugins index : /api/plugins/installed
- Load project repositories : /batch/project.protobuf?key=local%3Acom.samsungsds.analyst.code.main.App
- Load quality profiles : /api/qualityprofiles/search.protobuf?projectKey=local%3Acom.samsungsds.analyst.code.main.App
- Load active rules (변경된 quality profile만 위 search.protobuf에서 ID 반영, QualityProfilesServlet.java도 반영 필요)
	/api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt&activation=true&qprofile=AWncZv1RIhyt1CDWUP11&p=1&ps=500
	/api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt&activation=true&qprofile=AWPJooCyCQBOyaqowaiw&p=1&ps=500
	/api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt&activation=true&qprofile=AWPJpVp5CQBOyaqowaql&p=1&ps=500
	/api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt&activation=true&qprofile=AWPJoogvCQBOyaqowapc&p=1&ps=500
	/api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt&activation=true&qprofile=AWPJooTrCQBOyaqowal7&p=1&ps=500
	/api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt&activation=true&qprofile=AWPJooQ2CQBOyaqowakX&p=1&ps=500
	/api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt&activation=true&qprofile=AWxP8yw2BT8fMAYrFsSv&p=1&ps=500
	/api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt&activation=true&qprofile=AWxQGL1gjwlKak8RLaoD&p=1&ps=500
- Load metrics repository : /api/metrics/search?f=name,description,direction,qualitative,custom&ps=500&p=1
- Load server rules : /api/rules/list.protobuf

* Request (Sonar Scanner for MSBuild) 처리 예시
- Load API version : /api/server/version : Version.SONAR_SERVER 전송
- Load global settings : /api/settings/values (/statics/values.json)
- Load languages list : /api/languages/list (/statics/list.json)
- Load quality profiles : /api/qualityprofiles/search??key=local (/statics/search.json)
- Load active rules : /api/rules/search?f=repo,name,severity,lang,internalKey,templateKey,params,actives&ps=500&activation=true&qprofile=AWxP8yw2BT8fMAYrFsSv&p=1 (AWxP8yw2BT8fMAYrFsSv.json)
                      /api/rules/search?f=repo,name,severity,lang,internalKey,templateKey,params&ps=500&activation=false&qprofile=AWxP8yw2BT8fMAYrFsSv&p=1&languages=cs (AWxP8yw2BT8fMAYrFsSv_inactive.json)
- Load batch index : /batch/index
....
Accesslog sample
127.0.0.1 - - [03/9월/2019:13:49:48 +0900] "GET /api/server/version HTTP/1.1" 200 - "-" "ScannerMSBuild/4.6.2" "AWz0i+9u7mAPt+uLAABH"
127.0.0.1 - - [03/9월/2019:13:49:48 +0900] "GET /api/settings/values?component=local HTTP/1.1" 200 5988 "-" "ScannerMSBuild/4.6.2" "AWz0i+9u7mAPt+uLAABI"
127.0.0.1 - - [03/9월/2019:13:49:48 +0900] "GET /api/languages/list HTTP/1.1" 200 243 "-" "ScannerMSBuild/4.6.2" "AWz0i+9u7mAPt+uLAABJ"
127.0.0.1 - - [03/9월/2019:13:49:48 +0900] "GET /api/qualityprofiles/search?projectKey=local HTTP/1.1" 200 2981 "-" "ScannerMSBuild/4.6.2" "AWz0i+9u7mAPt+uLAABK"
127.0.0.1 - - [03/9월/2019:13:49:48 +0900] "GET /api/rules/search?f=repo,name,severity,lang,internalKey,templateKey,params,actives&ps=500&activation=true&qprofile=AWxP8yw2BT8fMAYrFsSv&p=1 HTTP/1.1" 200 - "-" "ScannerMSBuild/4.6.2" "AWz0i+9u7mAPt+uLAABL"
127.0.0.1 - - [03/9월/2019:13:49:48 +0900] "GET /api/rules/search?f=repo,name,severity,lang,internalKey,templateKey,params&ps=500&activation=false&qprofile=AWxP8yw2BT8fMAYrFsSv&p=1&languages=cs HTTP/1.1" 200 - "-" "ScannerMSBuild/4.6.2" "AWz0i+9u7mAPt+uLAABM"
127.0.0.1 - - [03/9월/2019:13:49:48 +0900] "GET /static/csharp/SonarAnalyzer-7.15.0.8572.zip HTTP/1.1" 200 - "-" "ScannerMSBuild/4.6.2" "AWz0i+9u7mAPt+uLAABN"
127.0.0.1 - - [03/9월/2019:13:50:22 +0900] "GET /batch/index HTTP/1.1" 200 - "-" "SonarQubeScanner/mode={languageType:CSHARP,codeSize:true,duplication:true,complexity:true,sonarJava:false,pmd:false,findBugs:false,findSecBugs:false,javascript:false,css:false,html:false,dependency:false,unusedCode:false,ckMetrics:false,checkStyle:false,sonarCSharp:true,sonarPython:false}" "AWz0i+9u7mAPt+uLAABO"
127.0.0.1 - - [03/9월/2019:13:50:24 +0900] "GET /api/settings/values.protobuf HTTP/1.1" 200 3834 "-" "SonarQubeScanner/mode={languageType:CSHARP,codeSize:true,duplication:true,complexity:true,sonarJava:false,pmd:false,findBugs:false,findSecBugs:false,javascript:false,css:false,html:false,dependency:false,unusedCode:false,ckMetrics:false,checkStyle:false,sonarCSharp:true,sonarPython:false}" "AWz0i+9u7mAPt+uLAABP"
127.0.0.1 - - [03/9월/2019:13:50:24 +0900] "GET /api/plugins/installed HTTP/1.1" 200 - "-" "SonarQubeScanner/mode={languageType:CSHARP,codeSize:true,duplication:true,complexity:true,sonarJava:false,pmd:false,findBugs:false,findSecBugs:false,javascript:false,css:false,html:false,dependency:false,unusedCode:false,ckMetrics:false,checkStyle:false,sonarCSharp:true,sonarPython:false}" "AWz0i+9u7mAPt+uLAABQ"
127.0.0.1 - - [03/9월/2019:13:50:25 +0900] "GET /batch/project.protobuf?key=local HTTP/1.1" 200 113 "-" "SonarQubeScanner/mode={languageType:CSHARP,codeSize:true,duplication:true,complexity:true,sonarJava:false,pmd:false,findBugs:false,findSecBugs:false,javascript:false,css:false,html:false,dependency:false,unusedCode:false,ckMetrics:false,checkStyle:false,sonarCSharp:true,sonarPython:false}" "AWz0i+9u7mAPt+uLAABR"
127.0.0.1 - - [03/9월/2019:13:50:25 +0900] "GET /api/qualityprofiles/search.protobuf?projectKey=local HTTP/1.1" 200 1096 "-" "SonarQubeScanner/mode={languageType:CSHARP,codeSize:true,duplication:true,complexity:true,sonarJava:false,pmd:false,findBugs:false,findSecBugs:false,javascript:false,css:false,html:false,dependency:false,unusedCode:false,ckMetrics:false,checkStyle:false,sonarCSharp:true,sonarPython:false}" "AWz0i+9u7mAPt+uLAABS"
127.0.0.1 - - [03/9월/2019:13:50:25 +0900] "GET /api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt&activation=true&qprofile=AWxP8yw2BT8fMAYrFsSv&p=1&ps=500 HTTP/1.1" 200 - "-" "SonarQubeScanner/mode={languageType:CSHARP,codeSize:true,duplication:true,complexity:true,sonarJava:false,pmd:false,findBugs:false,findSecBugs:false,javascript:false,css:false,html:false,dependency:false,unusedCode:false,ckMetrics:false,checkStyle:false,sonarCSharp:true,sonarPython:false}" "AWz0i+9u7mAPt+uLAABT"
127.0.0.1 - - [03/9월/2019:13:50:26 +0900] "GET /api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt&activation=true&qprofile=AWPJooCyCQBOyaqowaiw&p=1&ps=500 HTTP/1.1" 200 - "-" "SonarQubeScanner/mode={languageType:CSHARP,codeSize:true,duplication:true,complexity:true,sonarJava:false,pmd:false,findBugs:false,findSecBugs:false,javascript:false,css:false,html:false,dependency:false,unusedCode:false,ckMetrics:false,checkStyle:false,sonarCSharp:true,sonarPython:false}" "AWz0i+9u7mAPt+uLAABU"
127.0.0.1 - - [03/9월/2019:13:50:26 +0900] "GET /api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt&activation=true&qprofile=AWPJpVp5CQBOyaqowaql&p=1&ps=500 HTTP/1.1" 200 - "-" "SonarQubeScanner/mode={languageType:CSHARP,codeSize:true,duplication:true,complexity:true,sonarJava:false,pmd:false,findBugs:false,findSecBugs:false,javascript:false,css:false,html:false,dependency:false,unusedCode:false,ckMetrics:false,checkStyle:false,sonarCSharp:true,sonarPython:false}" "AWz0i+9u7mAPt+uLAABV"
127.0.0.1 - - [03/9월/2019:13:50:26 +0900] "GET /api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt&activation=true&qprofile=AWPJooTrCQBOyaqowal7&p=1&ps=500 HTTP/1.1" 200 - "-" "SonarQubeScanner/mode={languageType:CSHARP,codeSize:true,duplication:true,complexity:true,sonarJava:false,pmd:false,findBugs:false,findSecBugs:false,javascript:false,css:false,html:false,dependency:false,unusedCode:false,ckMetrics:false,checkStyle:false,sonarCSharp:true,sonarPython:false}" "AWz0i+9u7mAPt+uLAABW"
127.0.0.1 - - [03/9월/2019:13:50:26 +0900] "GET /api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt&activation=true&qprofile=AWPJooQ2CQBOyaqowakX&p=1&ps=500 HTTP/1.1" 200 4155 "-" "SonarQubeScanner/mode={languageType:CSHARP,codeSize:true,duplication:true,complexity:true,sonarJava:false,pmd:false,findBugs:false,findSecBugs:false,javascript:false,css:false,html:false,dependency:false,unusedCode:false,ckMetrics:false,checkStyle:false,sonarCSharp:true,sonarPython:false}" "AWz0i+9u7mAPt+uLAABX"
127.0.0.1 - - [03/9월/2019:13:50:26 +0900] "GET /api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt&activation=true&qprofile=AWncZv1RIhyt1CDWUP11&p=1&ps=500 HTTP/1.1" 200 - "-" "SonarQubeScanner/mode={languageType:CSHARP,codeSize:true,duplication:true,complexity:true,sonarJava:false,pmd:false,findBugs:false,findSecBugs:false,javascript:false,css:false,html:false,dependency:false,unusedCode:false,ckMetrics:false,checkStyle:false,sonarCSharp:true,sonarPython:false}" "AWz0i+9u7mAPt+uLAABY"
127.0.0.1 - - [03/9월/2019:13:50:26 +0900] "GET /api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt&activation=true&qprofile=AWxQGL1gjwlKak8RLaoD&p=1&ps=500 HTTP/1.1" 200 5613 "-" "SonarQubeScanner/mode={languageType:CSHARP,codeSize:true,duplication:true,complexity:true,sonarJava:false,pmd:false,findBugs:false,findSecBugs:false,javascript:false,css:false,html:false,dependency:false,unusedCode:false,ckMetrics:false,checkStyle:false,sonarCSharp:true,sonarPython:false}" "AWz0i+9u7mAPt+uLAABZ"
127.0.0.1 - - [03/9월/2019:13:50:26 +0900] "GET /api/rules/search.protobuf?f=repo,name,severity,lang,internalKey,templateKey,params,actives,createdAt&activation=true&qprofile=AWPJoogvCQBOyaqowapc&p=1&ps=500 HTTP/1.1" 200 - "-" "SonarQubeScanner/mode={languageType:CSHARP,codeSize:true,duplication:true,complexity:true,sonarJava:false,pmd:false,findBugs:false,findSecBugs:false,javascript:false,css:false,html:false,dependency:false,unusedCode:false,ckMetrics:false,checkStyle:false,sonarCSharp:true,sonarPython:false}" "AWz0i+9u7mAPt+uLAABa"
127.0.0.1 - - [03/9월/2019:13:50:26 +0900] "GET /api/metrics/search?f=name,description,direction,qualitative,custom&ps=500&p=1 HTTP/1.1" 200 - "-" "SonarQubeScanner/mode={languageType:CSHARP,codeSize:true,duplication:true,complexity:true,sonarJava:false,pmd:false,findBugs:false,findSecBugs:false,javascript:false,css:false,html:false,dependency:false,unusedCode:false,ckMetrics:false,checkStyle:false,sonarCSharp:true,sonarPython:false}" "AWz0i+9u7mAPt+uLAABb"
127.0.0.1 - - [03/9월/2019:13:50:27 +0900] "GET /api/rules/list.protobuf HTTP/1.1" 200 - "-" "SonarQubeScanner/mode={languageType:CSHARP,codeSize:true,duplication:true,complexity:true,sonarJava:false,pmd:false,findBugs:false,findSecBugs:false,javascript:false,css:false,html:false,dependency:false,unusedCode:false,ckMetrics:false,checkStyle:false,sonarCSharp:true,sonarPython:false}" "AWz0i+9u7mAPt+uLAABc"
127.0.0.1 - - [03/9월/2019:13:50:28 +0900] "POST /api/ce/submit?projectKey=local HTTP/1.1" 200 44 "-" "SonarQubeScanner/mode={languageType:CSHARP,codeSize:true,duplication:true,complexity:true,sonarJava:false,pmd:false,findBugs:false,findSecBugs:false,javascript:false,css:false,html:false,dependency:false,unusedCode:false,ckMetrics:false,checkStyle:false,sonarCSharp:true,sonarPython:false}" "AWz0i+9u7mAPt+uLAABd"

 */
public class JettySurrogateSonarServer implements SurrogateSonarServer {

	private static final Logger LOGGER = LogManager.getLogger(JettySurrogateSonarServer.class);

	private Server server = null;

	private UUID secretPassword = UUID.randomUUID();

	private int serverPort = 0;

	@Override
	public int startAndReturnPort(CliParser cli) {
		serverPort = IOAndFileUtils.findFreePort();

		if (serverPort == -1) {
			throw new IllegalStateException("No free port...");
		}

		server = new Server(serverPort);

		ServletHandler handler = new ServletHandler();

		handler.addServletWithMapping(SettingValuesResServlet.class, "/api/settings/values.protobuf");
		/*
		if (cli.getIndividualMode().isCodeSize() || cli.getIndividualMode().isDuplication() || cli.getIndividualMode().isSonarJava()) {
			if (cli.getIndividualMode().isWebResources()) {
				handler.addServletWithMapping(DefaultPluginInstalledResServlet.class, "/api/plugins/installed");
			} else {
				handler.addServletWithMapping(JavaPluginInstalledResServlet.class, "/api/plugins/installed");
			}
		} else if (cli.getIndividualMode().isWebResources()) {
			handler.addServletWithMapping(WebPluginInstalledResServlet.class, "/api/plugins/installed");
		}
		*/
		handler.addServletWithMapping(PluginInstalledResServlet.class, "/api/plugins/installed");

		handler.addServletWithMapping(MetricsResServlet.class, "/api/metrics/search");
		handler.addServletWithMapping(JarDownloadServlet.class, "/deploy/plugins/*");
		handler.addServletWithMapping(QualityProfilesServlet.class, "/api/qualityprofiles/search.protobuf");
		handler.addServletWithMapping(QualityProfilesServlet.class, "/api/rules/search.protobuf");
		handler.addServletWithMapping(QualityProfilesServlet.class, "/api/rules/list.protobuf");
		handler.addServletWithMapping(QualityProfilesServlet.class, "/batch/project.protobuf");
		handler.addServletWithMapping(SubmitServlet.class, "/api/ce/submit");

		// for Sonar Scanner for MSBuild
        handler.addServletWithMapping(SettingValuesJsonServlet.class, "/api/settings/values");
        handler.addServletWithMapping(ApiVersionServlet.class, "/api/server/version");
        handler.addServletWithMapping(LanguagesListJsonServlet.class, "/api/languages/list");
        handler.addServletWithMapping(QualityProfilesJsonServlet.class, "/api/qualityprofiles/search");
        handler.addServletWithMapping(QualityProfilesJsonServlet.class, "/api/rules/search");
        handler.addServletWithMapping(BatchIndexServlet.class, "/batch/index");
        handler.addServletWithMapping(ZipDownloadServlet.class, "/static/csharp/*");

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { new ShutdownHandler(secretPassword.toString(), false, true), handler });
		server.setHandler(handlers);

		try {
			server.start();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return serverPort;
	}

	@Override
	public void stop() {
		try {
			URL url = new URL("http://127.0.0.1:" + serverPort + "/shutdown?token=" + secretPassword.toString());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.getResponseCode();
			LOGGER.info("Shutting down {} : {}", url, connection.getResponseMessage());
		} catch (SocketException e) {
			LOGGER.debug("Not running");
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
