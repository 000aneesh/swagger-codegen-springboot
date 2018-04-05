package com.swagger.generator.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swagger.generator.model.PostmanObject;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@SuppressWarnings("unchecked")
@RestController
@RequestMapping("/generatePostmanScript")
public class PostmanController {

	@ApiOperation(value = "Modify the swagger specification to postman supporting scripts")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 500, message = "Server error") })
	@PostMapping
	public void generateScript(@RequestBody PostmanObject reqObj) throws Exception {
		JSONParser parser = new JSONParser();
		try {

			Object object = parser.parse(new FileReader(reqObj.getJsonFilePath() + "/" + reqObj.getJsonFileName()));
			JSONObject jsonObject = (JSONObject) object;
			if (jsonObject.containsKey("definitions")) {
				JSONObject definitionsObj = (JSONObject) jsonObject.get("definitions");
				JSONObject expandedDefinitionsObj = getDefinitions(definitionsObj);
				JSONObject paths = (JSONObject) jsonObject.get("paths");
				Set<String> pathArray = paths.keySet();
				for (String path : pathArray) {
					JSONObject pathObj = (JSONObject) paths.get(path);
					Set<String> requestTypes = pathObj.keySet();
					for (String requestType : requestTypes) {
						JSONObject requestTypeObj = (JSONObject) pathObj.get(requestType);
						if (requestTypeObj.containsKey("parameters")) {
							JSONArray parameters = (JSONArray) requestTypeObj.get("parameters");
							for (Object parameter : parameters) {
								JSONObject parameterObj = (JSONObject) parameter;
								String propertiesObjString = "";
								if (parameterObj.containsKey("in") && parameterObj.get("in").equals("body")
										&& parameterObj.containsKey("schema")) {
									JSONObject schemaObj = (JSONObject) parameterObj.get("schema");

									if (schemaObj.containsKey("type")) {

										// String schemaType = (String) schemaObj.get("type");
										// if (schemaType.equals("string")) {
										// propertiesObjString = "string";
										// } else if (schemaType.equals("number") || schemaType.equals("integer")) {
										// propertiesObjString = "0";
										// } else if (schemaType.equals("boolean")) {
										// propertiesObjString = "true";
										// } else if (schemaType.equals("object")) {
										// propertiesObjString = new JSONObject().toJSONString();
										// }

									} else if (schemaObj.containsKey("$ref")) {
										String refObj = (String) schemaObj.get("$ref");
										String definitionName = refObj.split("/").length == 3 ? refObj.split("/")[2]
												: "";
										propertiesObjString = expandedDefinitionsObj.containsKey(definitionName)
												? expandedDefinitionsObj.get(definitionName).toString()
												: "";

										parameterObj.put("description", propertiesObjString);
									}

								}
								// parameterObj.put("description", propertiesObjString);
							}

						}
					}
				}
			}

			try (FileWriter file = new FileWriter(reqObj.getJsonFilePath() + "/postman-" + reqObj.getJsonFileName())) {

				file.write(jsonObject.toJSONString());
				file.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to get the complex defentions as a JSONObject with key as definiton
	 * name and value as the the expanded json
	 * 
	 * @param definitionsObj
	 * @return expandedDefinitionsObj
	 */
	private JSONObject getDefinitions(JSONObject definitionsObj) {
		JSONObject expandedDefinitionsObj = new JSONObject();
		Set<String> definitions = definitionsObj.keySet();
		for (String definition : definitions) {
			JSONObject newPropertiesObj = getDefinition(definition, definitionsObj, expandedDefinitionsObj);
			expandedDefinitionsObj.put(definition, newPropertiesObj);
		}
		return expandedDefinitionsObj;

	}

	private JSONObject getDefinition(String definition, JSONObject definitionsObj, JSONObject expandedDefinitionsObj) {
		JSONObject newPropertiesObj = null;
		if (definitionsObj.containsKey(definition)) {
			newPropertiesObj = new JSONObject();
			JSONObject definitionObj = (JSONObject) definitionsObj.get(definition);
			if (definitionObj.containsKey("properties")) {
				JSONObject propertiesObj = (JSONObject) definitionObj.get("properties");
				Set<String> properties = propertiesObj.keySet();
				for (String property : properties) {
					JSONObject propertyObj = (JSONObject) propertiesObj.get(property);
					if (propertyObj.containsKey("type")) {
						String propertyType = (String) propertyObj.get("type");
						if (propertyType.equals("string")) {
							newPropertiesObj.put(property, "string");
						} else if (propertyType.equals("number") || propertyType.equals("integer")) {
							newPropertiesObj.put(property, 0);
						} else if (propertyType.equals("boolean")) {
							newPropertiesObj.put(property, true);
						} else if (propertyType.equals("object")) {
							newPropertiesObj.put(property, new JSONObject());
						} else if (propertyType.equals("array")) {
							JSONObject arrayItems = (JSONObject) propertyObj.get("items");
							JSONArray arrayObj = getArrayObj(arrayItems, property, definitionsObj,
									expandedDefinitionsObj);
							newPropertiesObj.put(property, arrayObj);

						}
					} else if (propertyObj.containsKey("$ref")) {
						String refObj = (String) propertyObj.get("$ref");
						String childDefinition = refObj.split("/").length == 3 ? refObj.split("/")[2] : "";
						if (expandedDefinitionsObj.containsKey(childDefinition)) {
							newPropertiesObj.put(childDefinition, expandedDefinitionsObj.get(childDefinition));
						} else {
							JSONObject child = getDefinition(childDefinition, definitionsObj, expandedDefinitionsObj);
							newPropertiesObj.put(childDefinition, child);
						}

					}
				}
			}
		}
		return newPropertiesObj;
	}

	private JSONArray getArrayObj(JSONObject arrayItems, String property, JSONObject definitionsObj,
			JSONObject expandedDefinitionsObj) {

		JSONArray arrayObj = new JSONArray();
		if (arrayItems.containsKey("type")) {
			String arrayType = (String) arrayItems.get("type");
			if (arrayType.equals("string")) {
				arrayObj.add("string");
			} else if (arrayType.equals("number") || arrayType.equals("integer")) {
				arrayObj.add(0);
			} else if (arrayType.equals("boolean")) {
				arrayObj.add(true);
			} else if (arrayType.equals("object")) {
				arrayObj.add(new JSONObject());
			} else if (arrayType.equals("array")) {
				JSONObject childArrayItems = (JSONObject) arrayItems.get("items");
				JSONArray childArrayObj = getArrayObj(childArrayItems, property, definitionsObj,
						expandedDefinitionsObj);
				arrayObj.add(childArrayObj);
			}
		} else if (arrayItems.containsKey("$ref")) {
			String arrayRefObj = (String) arrayItems.get("$ref");
			String childRefDefinition = arrayRefObj.split("/").length == 3 ? arrayRefObj.split("/")[2] : "";
			if (expandedDefinitionsObj.containsKey(childRefDefinition)) {
				arrayObj.add(expandedDefinitionsObj.get(childRefDefinition));
			} else {
				JSONObject child = getDefinition(childRefDefinition, definitionsObj, expandedDefinitionsObj);
				arrayObj.add(child);
			}
		}

		return arrayObj;
	}

}
