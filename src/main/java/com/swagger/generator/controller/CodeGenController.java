package com.swagger.generator.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swagger.generator.model.RequestObject;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/generateCode")
public class CodeGenController {

	private static final Set<String> SERVERS = new HashSet<String>(
			Arrays.asList(new String[] { "android", "aspnet5", "async-scala", "cwiki", "csharp", "cpprest", "dart",
					"flash", "python-flask", "go", "groovy", "java", "jaxrs", "jaxrs-cxf", "jaxrs-resteasy",
					"jaxrs-spec", "inflector", "javascript", "javascript-closure-angular", "jmeter", "nancyfx",
					"nodejs-server", "objc", "perl", "php", "python", "qt5cpp", "ruby", "scala", "scalatra",
					"silex-PHP", "sinatra", "rails5", "slim", "spring", "dynamic-html", "html", "html2", "swagger",
					"swagger-yaml", "swift", "tizen", "typescript-angular2", "typescript-angular", "typescript-node",
					"typescript-fetch", "akka-scala", "CsharpDotNet2", "clojure", "haskell", "lumen", "go-server" }));

	/**
	 * 
	 *
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "Create projects using swagger codegen cli")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 500, message = "Server error") })
	@PostMapping
	public void generateCode(@RequestBody RequestObject reqObj) throws Exception {

		String server = reqObj.getServer();
		if (SERVERS.contains(server)) {
			String jsonFile = "\"" + reqObj.getJsonFilePath() + "/" + reqObj.getJsonFileName() + "\""; // "D:/aneesh/swagger-poc/student.json";
			String destPath = "\"" + reqObj.getDestinationPath() + "/" + reqObj.getProjectName() + "\"";
			String cmd = "java -jar D:/aneesh/swagger-poc/swagger-codegen-cli-jar/swagger-codegen-cli-2.2.1.jar generate -i "
					+ jsonFile + " -l " + server + " -o " + destPath;
			Runtime.getRuntime().exec(cmd);
			// Runtime.getRuntime().exec("java -jar swagger-codegen-cli-2.2.1.jar generate
			// -i D:/aneesh/swagger-poc/student.json -l spring -o
			// D:/aneesh/swagger-poc/swagger-codegen-cli-jar/projectfiles/spring");
		} else {
			throw new Exception(server + " is not available");
		}

	}

}
