package vr46.portaineropsmcppublic.portainer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DockerProxy_containersResponse {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final List<Container> containers;

    public DockerProxy_containersResponse(String json) throws IOException {
        JsonNode root = parseJson(json);
        this.containers = Collections.unmodifiableList(extractContainers(root));
    }

    public DockerProxy_containersResponse(JsonNode root) {
        this.containers = Collections.unmodifiableList(extractContainers(root));
    }

    public List<Container> getContainers() {
        return containers;
    }
    public boolean isEmpty() {
        return containers.isEmpty();
    }
    public int size() {
        return containers.size();
    }

    public static class Container {

        private final List<String> names;
        private final HostConfig hostConfig;
        private final String id;
        private final String state;
        private final String status;
        private final Long created;
        private final List<Port> ports;
        private final List<Mount> mounts;
        private final Map<String, String> labels;
        private final JsonNode rawNode;

        public Container(JsonNode node) {
            this.names = extractTextList(node, "Names");
            this.hostConfig = new HostConfig(extractNode(node, "HostConfig"), node);
            this.id = extractText(node, "Id");
            this.state = extractText(node, "State");
            this.status = extractText(node, "Status");
            this.created = extractLong(node, "Created");
            this.ports = extractPorts(node, "Ports");
            this.mounts = extractMounts(node, "Mounts");
            this.labels = extractStringMap(node, "Labels");
            this.rawNode = copyNode(node);
        }

        public List<String> getNames() {
            return names;
        }
        public HostConfig getHostConfig() {
            return hostConfig;
        }
        public String getId() {
            return id;
        }
        public String getImage() {
            return hostConfig != null ? hostConfig.getImage() : null;
        }
        public String getImageID() {
            return hostConfig != null ? hostConfig.getImageID() : null;
        }
        public String getState() {
            return state;
        }
        public String getStatus() {
            return status;
        }
        public Long getCreated() {
            return created;
        }
        public List<Port> getPorts() {
            return ports;
        }
        public List<Mount> getMounts() {
            return mounts;
        }
        public Map<String, String> getLabels() {
            return labels;
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
        public Long getFieldAsLong(String field) {
            return extractLong(rawNode, field);
        }
        public Boolean getFieldAsBoolean(String field) {
            return extractBoolean(rawNode, field);
        }
        public List<String> getFieldAsTextList(String field) {
            return extractTextList(rawNode, field);
        }
        public boolean hasField(String field) {
            return extractNode(rawNode, field) != null;
        }
    }

    public static class HostConfig {
        private final String image;
        private final String imageID;
        private final String networkMode;
        private final JsonNode rawNode;

        public HostConfig(JsonNode hostConfigNode, JsonNode parentContainerNode) {
            this.image = extractFirstText(hostConfigNode, "Image", parentContainerNode, "Image");
            this.imageID = extractFirstText(hostConfigNode, "ImageID", parentContainerNode, "ImageID");
            this.networkMode = extractText(hostConfigNode, "NetworkMode");
            this.rawNode = copyNode(hostConfigNode);
        }

        public String getImage() {
            return image;
        }
        public String getImageID() {
            return imageID;
        }
        public String getNetworkMode() {
            return networkMode;
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
        public Long getFieldAsLong(String field) {
            return extractLong(rawNode, field);
        }
        public Boolean getFieldAsBoolean(String field) {
            return extractBoolean(rawNode, field);
        }
        public boolean hasField(String field) {
            return extractNode(rawNode, field) != null;
        }
    }
    public static class Port {

        private final String ip;
        private final Integer privatePort;
        private final Integer publicPort;
        private final String type;
        private final JsonNode rawNode;

        public Port(JsonNode node) {
            this.ip = extractText(node, "IP");
            this.privatePort = extractInteger(node, "PrivatePort");
            this.publicPort = extractInteger(node, "PublicPort");
            this.type = extractText(node, "Type");
            this.rawNode = copyNode(node);
        }

        public String getIp() {
            return ip;
        }
        public Integer getPrivatePort() {
            return privatePort;
        }
        public Integer getPublicPort() {
            return publicPort;
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
    }

    public static class Mount {

        private final String destination;
        private final String mode;
        private final String propagation;
        private final Boolean rw;
        private final String source;
        private final String type;
        private final String name;
        private final String driver;
        private final JsonNode rawNode;

        public Mount(JsonNode node) {
            this.destination = extractText(node, "Destination");
            this.mode = extractText(node, "Mode");
            this.propagation = extractText(node, "Propagation");
            this.rw = extractBoolean(node, "RW");
            this.source = extractText(node, "Source");
            this.type = extractText(node, "Type");
            this.name = extractText(node, "Name");
            this.driver = extractText(node, "Driver");
            this.rawNode = copyNode(node);
        }

        public String getDestination() {
            return destination;
        }
        public String getMode() {
            return mode;
        }
        public String getPropagation() {
            return propagation;
        }
        public Boolean getRw() {
            return rw;
        }
        public String getSource() {
            return source;
        }
        public String getType() {
            return type;
        }
        public String getName() {
            return name;
        }
        public String getDriver() {
            return driver;
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
    }

    private static JsonNode parseJson(String json) throws IOException {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }

        return mapper.readTree(json);
    }

    private static List<Container> extractContainers(JsonNode root) {
        List<Container> result = new ArrayList<Container>();

        if (root == null || !root.isArray()) {
            return result;
        }

        for (JsonNode containerNode : root) {
            if (containerNode != null && containerNode.isObject()) {
                result.add(new Container(containerNode));
            }
        }

        return result;
    }

    private static List<Port> extractPorts(JsonNode node, String field) {
        JsonNode target = extractNode(node, field);

        if (target == null || !target.isArray()) {
            return Collections.emptyList();
        }

        List<Port> result = new ArrayList<Port>();

        for (JsonNode child : target) {
            if (child != null && child.isObject()) {
                result.add(new Port(child));
            }
        }

        return Collections.unmodifiableList(result);
    }

    private static List<Mount> extractMounts(JsonNode node, String field) {
        JsonNode target = extractNode(node, field);

        if (target == null || !target.isArray()) {
            return Collections.emptyList();
        }

        List<Mount> result = new ArrayList<Mount>();

        for (JsonNode child : target) {
            if (child != null && child.isObject()) {
                result.add(new Mount(child));
            }
        }

        return Collections.unmodifiableList(result);
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

    private static String extractFirstText(JsonNode firstNode, String firstField, JsonNode secondNode, String secondField) {
        String firstValue = extractText(firstNode, firstField);

        if (firstValue != null) {
            return firstValue;
        }

        return extractText(secondNode, secondField);
    }

    private static Integer extractInteger(JsonNode node, String field) {
        JsonNode target = extractNode(node, field);
        return target != null && target.isNumber() ? target.asInt() : null;
    }

    private static Long extractLong(JsonNode node, String field) {
        JsonNode target = extractNode(node, field);
        return target != null && target.isNumber() ? target.asLong() : null;
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

    private static Map<String, String> extractStringMap(JsonNode node, String field) {
        JsonNode target = extractNode(node, field);

        if (target == null || !target.isObject()) {
            return Collections.emptyMap();
        }

        Map<String, String> result = new LinkedHashMap<String, String>();

        java.util.Iterator<Map.Entry<String, JsonNode>> fields = target.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();

            String key = entry.getKey();
            JsonNode valueNode = entry.getValue();

            if (key == null || valueNode == null || valueNode.isNull()) {
                continue;
            }

            if (valueNode.isTextual()) {
                result.put(key, valueNode.asText());
            } else {
                result.put(key, valueNode.toString());
            }
        }

        return Collections.unmodifiableMap(result);
    }
}