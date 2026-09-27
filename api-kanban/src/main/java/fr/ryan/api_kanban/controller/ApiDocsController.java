package fr.ryan.api_kanban.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ApiDocsController {

	private static final String SWAGGER_HTML = """
		<!DOCTYPE html>
		<html lang="en">
		<head>
		  <meta charset="UTF-8">
		  <title>Kanban Board API</title>
		  <link rel="stylesheet" type="text/css" href="/swagger-ui/swagger-ui.css">
		  <link rel="icon" type="image/png" href="/swagger-ui/favicon-32x32.png" sizes="32x32">
		</head>
		<body>
		  <div id="swagger-ui"></div>
		  <script src="/swagger-ui/swagger-ui-bundle.js" charset="UTF-8"></script>
		  <script src="/swagger-ui/swagger-ui-standalone-preset.js" charset="UTF-8"></script>
		  <script>
		    window.onload = function () {
		      window.ui = SwaggerUIBundle({
		        url: '/openapi.yaml',
		        dom_id: '#swagger-ui',
		        presets: [SwaggerUIBundle.presets.apis, SwaggerUIStandalonePreset],
		        layout: 'StandaloneLayout',
		        deepLinking: true
		      });
		    };
		  </script>
		</body>
		</html>
		""";

	@GetMapping(value = "/api", produces = MediaType.TEXT_HTML_VALUE)
	public void swaggerUi(HttpServletResponse response) throws IOException {
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setContentType(MediaType.TEXT_HTML_VALUE);
		response.getWriter().write(SWAGGER_HTML);
	}
}
