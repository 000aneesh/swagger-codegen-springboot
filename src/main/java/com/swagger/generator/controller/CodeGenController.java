package com.swagger.generator.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swagger.generator.model.CodeGenObject;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.codegen.SwaggerCodegen;

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

	@ApiOperation(value = "Create projects using swagger codegen cli")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 500, message = "Server error") })
	@PostMapping
	public void generateCode(@RequestBody CodeGenObject reqObj) throws Exception {

		String server = "spring";//reqObj.getServer();
		if (!isValidRequest(reqObj)) {
			throw new Exception("Request json is not valid.");
		}
		if (SERVERS.contains(server)) {
			String jsonFile = reqObj.getJsonFilePath() + "/" + reqObj.getJsonFileName(); // "D:/aneesh/swagger-poc/student.json";
			String destPath = reqObj.getDestinationPath() + "/" + reqObj.getProjectName();
			String[] args = new String[] { "generate", "-i", jsonFile, "-l", server, "-o", destPath };
			SwaggerCodegen.main(args);
		} else {
			throw new Exception(server + " is not available");
		}

	}

	private boolean isValidRequest(CodeGenObject reqObj) {
		boolean isValid = false;
		if (reqObj != null && StringUtils.isNotBlank(reqObj.getDestinationPath())
				&& StringUtils.isNotBlank(reqObj.getJsonFileName()) && StringUtils.isNotBlank(reqObj.getJsonFilePath())
				&& StringUtils.isNotBlank(reqObj.getProjectName())) {
			isValid = true;
		}
		return isValid;
	}

}
