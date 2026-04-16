package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class JSONReader {

    public static JsonNode readJson(String fileName) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(
                    new File(System.getProperty("user.dir") +
                            "/src/test/resources/testdata/" + fileName)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode getTestData(String fileName, String tcId) {
        JsonNode root = readJson(fileName);
        JsonNode testData = root.get("testData");

        for (JsonNode testCase : testData) {
            if (testCase.get("id").asText().equals(tcId)) {
                return testCase;
            }
        }
        throw new RuntimeException("Test case not found: " + tcId);
    }

}
