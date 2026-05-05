package vr46.portaineropsmcppublic.portainer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListEnvironmentsResponse {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final List<Environment> environments;

    public ListEnvironmentsResponse(String json) throws IOException {
        JsonNode root = parseJson(json);
        this.environments = Collections.unmodifiableList(extractEnvironments(root));
    }

    public ListEnvironmentsResponse(JsonNode root) {
        this.environments = Collections.unmodifiableList(extractEnvironments(root));
    }

    public List<Environment> getEnvironments() {
        return environments;
    }
    public boolean isEmpty() {
        return environments.isEmpty();
    }
    public int size() {
        return environments.size();
    }

    public static class Environment {

        private final Integer id;
        private final String name;
        private final String status;
        private final String type;
        private final JsonNode rawNode;

        public Environment(JsonNode node) {
            this.id = extractInteger(node, "id");
            this.name = extractText(node, "name");
            this.status = extractText(node, "status");
            this.type = extractText(node, "type");
            this.rawNode = copyNode(node);
        }

        public Integer getId() {
            return id;
        }
        public String getName() {
            return name;
        }
        public String getStatus() {
            return status;
        }
        public String getType() {
            return type;
        }
        public JsonNode getRawNode() {
            return rawNode;
        }
        public JsonNode getFieldNode(String field) {
            return extractNode(rawNode, field);
        }
        public String getFieldAsText(String field) {
            return extractText(rawNode, field);
        }
        public Integer getFieldAsInteger(String field) {
            return extractInteger(rawNode, field);
        }
        public Boolean getFieldAsBoolean(String field) {
            return extractBoolean(rawNode, field);
        }
        public List<String> getFieldAsTextList(String field) {
            return extractTextList(rawNode, field);
        }
        public List<Integer> getFieldAsIntegerList(String field) {
            return extractIntegerList(rawNode, field);
        }
        public boolean hasField(String field) {
            return extractNode(rawNode, field) != null;
        }
    }

    private static JsonNode parseJson(String json) throws IOException {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        return mapper.readTree(json);
    }

    private static List<Environment> extractEnvironments(JsonNode root) {
        List<Environment> result = new ArrayList<Environment>();

        if (root == null || !root.isArray()) {
            return result;
        }

        for (JsonNode environmentNode : root) {
            if (environmentNode != null && environmentNode.isObject()) {
                result.add(new Environment(environmentNode));
            }
        }

        return result;
    }

    private static JsonNode copyNode(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }
        return node.deepCopy();
    }

    private static JsonNode extractNode(JsonNode node, String field) {
        if (node == null) return null;
        if (field == null || field.trim().isEmpty()) return null;

        JsonNode target = node.path(field);

        if (target.isMissingNode() || target.isNull()) {
            return null;
        }

        return target;
    }

    private static String extractText(JsonNode node, String field) {
        JsonNode target = extractNode(node, field);
        return target != null && target.isTextual() ? target.asText() : null;
    }

    private static Integer extractInteger(JsonNode node, String field) {
        JsonNode target = extractNode(node, field);
        return target != null && target.isNumber() ? target.asInt() : null;
    }

    private static Boolean extractBoolean(JsonNode node, String field) {
        JsonNode target = extractNode(node, field);
        return target != null && target.isBoolean() ? target.asBoolean() : null;
    }

    private static List<String> extractTextList(JsonNode node, String field) {
        JsonNode target = extractNode(node, field);

        if (target == null || !target.isArray()) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<String>();

        for (JsonNode child : target) {
            if (child != null && child.isTextual()) {
                result.add(child.asText());
            }
        }

        return Collections.unmodifiableList(result);
    }

    private static List<Integer> extractIntegerList(JsonNode node, String field) {
        JsonNode target = extractNode(node, field);

        if (target == null || !target.isArray()) {
            return Collections.emptyList();
        }

        List<Integer> result = new ArrayList<Integer>();

        for (JsonNode child : target) {
            if (child != null && child.isNumber()) {
                result.add(child.asInt());
            }
        }

        return Collections.unmodifiableList(result);
    }
}
