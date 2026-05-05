package vr46.portaineropsmcppublic.portainer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class DockerProxy_containerResponse {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String name;
    private final String created;
    private final int restartCount;
    private final State state;
    private final JsonNode rawNode;
    public DockerProxy_containerResponse(String json) throws IOException {
        this(parseJson(json));
    }

    public DockerProxy_containerResponse(JsonNode root) {
        this.name = extractText(root, "Name");
        this.created = extractText(root, "Created");
        this.restartCount = extractInteger(root, "RestartCount");
        this.state = new State(extractNode(root, "State"));
        this.rawNode = copyNode(root);
    }

    public String getName() {
        return name;
    }
    public String getCreated() {
        return created;
    }
    public int getRestartCount() {
        return restartCount;
    }

    public State getState() {
        return state;
    }

    public JsonNode getRawNode() {
        return rawNode;
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

    public static class State {

        private final Boolean dead;
        private final String error;
        private final Integer exitCode;
        private final String finishedAt;
        private final Boolean oomKilled;
        private final Boolean paused;
        private final Integer pid;
        private final Boolean restarting;
        private final Boolean running;
        private final String startedAt;
        private final String status;
        private final JsonNode rawNode;

        public State(JsonNode node) {
            this.dead = extractBoolean(node, "Dead");
            this.error = extractText(node, "Error");
            this.exitCode = extractInteger(node, "ExitCode");
            this.finishedAt = extractText(node, "FinishedAt");
            this.oomKilled = extractBoolean(node, "OOMKilled");
            this.paused = extractBoolean(node, "Paused");
            this.pid = extractInteger(node, "Pid");
            this.restarting = extractBoolean(node, "Restarting");
            this.running = extractBoolean(node, "Running");
            this.startedAt = extractText(node, "StartedAt");
            this.status = extractText(node, "Status");
            this.rawNode = copyNode(node);
        }

        public Boolean getDead() {
            return dead;
        }
        public String getError() {
            return error;
        }
        public Integer getExitCode() {
            return exitCode;
        }
        public String getFinishedAt() {
            return finishedAt;
        }
        public Boolean getOomKilled() {
            return oomKilled;
        }
        public Boolean getPaused() {
            return paused;
        }
        public Integer getPid() {
            return pid;
        }
        public Boolean getRestarting() {
            return restarting;
        }
        public Boolean getRunning() {
            return running;
        }
        public String getStartedAt() {
            return startedAt;
        }
        public String getStatus() {
            return status;
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
        return target != null && target.isNumber() ? target.asInt() : 0;
    }

    private static Long extractLong(JsonNode node, String field) {
        JsonNode target = extractNode(node, field);
        return target != null && target.isNumber() ? target.asLong() : null;
    }

    private static Boolean extractBoolean(JsonNode node, String field) {
        JsonNode target = extractNode(node, field);
        return target != null && target.isBoolean() ? target.asBoolean() : null;
    }
}
