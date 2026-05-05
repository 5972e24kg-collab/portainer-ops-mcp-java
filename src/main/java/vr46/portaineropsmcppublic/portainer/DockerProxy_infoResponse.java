package vr46.portaineropsmcppublic.portainer;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DockerProxy_infoResponse {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final Integer containers;
    private final Integer containersRunning;
    private final Integer containersPaused;
    private final Integer containersStopped;
    private final Integer images;
    private final String serverVersion;
    private final Boolean memoryLimit;
    private final String systemTime;
    private final String loggingDriver;
    private final String operatingSystem;
    private final String kernelVersion;
    private final Integer ncpu;
    private final Long memTotal;
    private final String cgroupDriver;
    private final String cgroupVersion;
    private final List<String> warnings;
    private final JsonNode rawNode;


    public DockerProxy_infoResponse(String json) throws IOException {
        this(parseJson(json));
    }


    public DockerProxy_infoResponse(JsonNode root) {
        this.containers = extractInteger(root, "Containers");
        this.containersRunning = extractInteger(root, "ContainersRunning");
        this.containersPaused = extractInteger(root, "ContainersPaused");
        this.containersStopped = extractInteger(root, "ContainersStopped");
        this.images = extractInteger(root, "Images");

        this.serverVersion = extractText(root, "ServerVersion");
        this.memoryLimit = extractBoolean(root, "MemoryLimit");
        this.systemTime = extractText(root, "SystemTime");
        this.loggingDriver = extractText(root, "LoggingDriver");
        this.operatingSystem = extractText(root, "OperatingSystem");
        this.kernelVersion = extractText(root, "KernelVersion");

        this.ncpu = extractInteger(root, "NCPU");
        this.memTotal = extractLong(root, "MemTotal");

        this.cgroupDriver = extractText(root, "CgroupDriver");
        this.cgroupVersion = extractText(root, "CgroupVersion");

        this.warnings = extractTextList(root, "Warnings");
        this.rawNode = copyNode(root);
    }

    public Integer getContainers() {
        return containers;
    }
    public Integer getContainersRunning() {
        return containersRunning;
    }
    public Integer getContainersPaused() {
        return containersPaused;
    }
    public Integer getContainersStopped() {
        return containersStopped;
    }
    public Integer getImages() {
        return images;
    }
    public String getServerVersion() {
        return serverVersion;
    }
    public Boolean getMemoryLimit() {
        return memoryLimit;
    }
    public String getSystemTime() {
        return systemTime;
    }
    public String getLoggingDriver() {
        return loggingDriver;
    }
    public String getOperatingSystem() {
        return operatingSystem;
    }
    public String getKernelVersion() {
        return kernelVersion;
    }
    public Integer getNcpu() {
        return ncpu;
    }
    public Long getMemTotal() {
        return memTotal;
    }
    public String getCgroupDriver() {
        return cgroupDriver;
    }
    public String getCgroupVersion() {
        return cgroupVersion;
    }
    public List<String> getWarnings() {
        return warnings;
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
    public List<Integer> getFieldAsIntegerList(String field) {
        return extractIntegerList(rawNode, field);
    }
    public boolean hasField(String field) {
        return extractNode(rawNode, field) != null;
    }

    private static JsonNode parseJson(String json) throws IOException {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }

        return mapper.readTree(json);
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