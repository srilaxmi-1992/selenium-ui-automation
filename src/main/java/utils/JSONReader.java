package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static List<Map<String, String>> getProductsTestData(JsonNode testCaseData) {
        List<Map<String, String>> list = new ArrayList<>();
        if (testCaseData.get("products").isArray()) {
            JsonNode products = testCaseData.get("products");
            for (JsonNode product : products) {
                Map<String, String> map = new HashMap<>();
                map.put("name", product.get("name").asText());
                map.put("price", product.get("price").asText());
                list.add(map);
            }
        }
        return list;
    }

    public static List<String> extractProductNames(JsonNode testCaseData) {
        List<Map<String, String>> productsList = JSONReader.getProductsTestData(testCaseData);
        List<String> names = new ArrayList<>();
        for (Map<String, String> map : productsList) {
            names.add(map.get("name"));
        }
        return names;
    }

    public static List<String> extractProductPrices(JsonNode testCaseData) {
        List<Map<String, String>> productsList = JSONReader.getProductsTestData(testCaseData);
        List<String> names = new ArrayList<>();
        for (Map<String, String> map : productsList) {
            names.add(map.get("price"));
        }
        return names;
    }


}
