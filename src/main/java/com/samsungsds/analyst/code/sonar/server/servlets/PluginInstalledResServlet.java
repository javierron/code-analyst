package com.samsungsds.analyst.code.sonar.server.servlets;

import com.google.gson.Gson;
import com.samsungsds.analyst.code.main.IndividualMode;
import com.samsungsds.analyst.code.util.IOAndFileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@SuppressWarnings("serial")
public class PluginInstalledResServlet extends HttpServlet {
    private static final Logger LOGGER = LogManager.getLogger(PluginInstalledResServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Declare response encoding and types
        response.setContentType("application/json; charset=utf-8");

        String userAgent = request.getHeader("User-Agent");
        LOGGER.info("User-Agent : {}", userAgent);

        String mode = null;

        String infoString = userAgent.split("\\s*/\\s*")[1];
        String[] info = infoString.split("\\s*§\\s*");

        for (String item : info) {
            String[] keyValue = item.split("\\s*=\\s*");
            String key = keyValue[0];

            if (key.equalsIgnoreCase("mode")) {
                mode = keyValue[1];
            }
        }

        if (mode == null) {
            throw new IllegalStateException("Mode Information not passed");
        }

        Gson gson = new Gson();
        IndividualMode individualMode = gson.fromJson(mode, IndividualMode.class);

        // Declare response status code
        response.setStatus(HttpServletResponse.SC_OK);

        // Write back response
        try (OutputStream outStream = response.getOutputStream()) {
            boolean isWritten = false;

            IOAndFileUtils.writeString(outStream, "{\n" + "\t\"plugins\": [\n");

            if (individualMode.isCodeSize() || individualMode.isDuplication() || individualMode.isSonarJava()) {
                IOAndFileUtils.write(outStream, "/statics/plugin_java.json");
                isWritten = true;
            }
            if (individualMode.isJavascript()) {
                if (isWritten) {
                    IOAndFileUtils.writeString(outStream, ",\n");
                }
                IOAndFileUtils.write(outStream, "/statics/plugin_javascript.json");
            }
            if (individualMode.isHtml()) {
                if (isWritten) {
                    IOAndFileUtils.writeString(outStream, ",\n");
                }
                IOAndFileUtils.write(outStream, "/statics/plugin_web.json");
            }
            if (individualMode.isCss()) {
                if (isWritten) {
                    IOAndFileUtils.writeString(outStream, ",\n");
                }
                IOAndFileUtils.write(outStream, "/statics/plugin_css.json");
            }

            IOAndFileUtils.writeString(outStream, "\t]\n" + "}");
        }
    }
}
