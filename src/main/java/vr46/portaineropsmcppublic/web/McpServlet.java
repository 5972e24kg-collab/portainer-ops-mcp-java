package vr46.portaineropsmcppublic.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import vr46.portaineropsmcppublic.converter.ContainersOverviewConverter;
import vr46.portaineropsmcppublic.converter.EnvironmentsOverviewConverter;
import vr46.portaineropsmcppublic.dto.Containers;
import vr46.portaineropsmcppublic.dto.Environments;
import vr46.portaineropsmcppublic.dto.IncidentDetail;
import vr46.portaineropsmcppublic.converter.IncidentDetailConverter;
import vr46.portaineropsmcppublic.mcp.*;

import javax.servlet.annotation.WebServlet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet(name = "McpServlet", urlPatterns = {"/mcp"})
public class McpServlet extends BaseMcpServlet {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String SERVER_NAME = "portainer-ops-mcp-java";
    private static final String SERVER_TITLE = "Portainer Ops MCP Java Server";
    private static final String SERVER_VERSION = "0.2.0";
    private static final String TOOL_ENVIRONMENTS = "environments";
    private static final String TOOL_CONTAINERS = "containers";
    private static final String TOOL_INCIDENT_DETAIL = "incidentDetail";

    public McpServlet() {
        super(SERVER_VERSION);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // initialize
    // -----------------------------------------------------------------------------------------------------------------
    @Override
    protected InitializeResponse resInitialize(InitializeRequest initializeRequest) {
        InitializeResponse initializeResponse = super.resInitialize(initializeRequest);

        // LLM向けの全体説明。
        /*
         * ここでは「何ができるか」だけでなく、
         * 「どの順番でツールを使うべきか」も明示。
        */
        initializeResponse.result.instructions =
                "Read-only MCP server for observing Portainer-managed Docker environments. "
                        + "Use 'environments' first to discover available Docker hosts and their high-level health. "
                        + "Use 'containers' with an environmentName to inspect container state, resource usage, ports, mounts, compose labels, and risk flags for that host. "
                        + "Use 'incidentDetail' only when investigating a specific container, because it may include detailed inspect data, stats, and recent logs. "
                        + "This server is for observation and troubleshooting only; it does not create, update, restart, stop, or delete containers.";

        initializeResponse.result.capabilities.tools.listChanged = false;
        initializeResponse.result.serverInfo.name = SERVER_NAME;
        initializeResponse.result.serverInfo.title = SERVER_TITLE;
        initializeResponse.result.serverInfo.version = SERVER_VERSION;

        return initializeResponse;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // tools/list
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    protected ToolsListResponse resToolsList(ToolsListRequest toolsListRequest) {
        ToolsListResponse toolsListResponse = super.resToolsList(toolsListRequest);

        addEnvironmentsTool(toolsListResponse);
        addContainersTool(toolsListResponse);
        addIncidentDetailTool(toolsListResponse);

        return toolsListResponse;
    }

    private void addEnvironmentsTool(ToolsListResponse toolsListResponse) {
        ToolsListResponse.Tool environments = new ToolsListResponse.Tool();
        environments.name = TOOL_ENVIRONMENTS;
        environments.description =
                "List all registered Portainer Docker environments as host-level overview JSON. "
                        + "Use this tool first when the user asks about Docker hosts, available environments, host health, Docker versions, OS, CPU, memory, container counts, or host warnings. "
                        + "The response is compact and suitable for choosing the target environmentName for follow-up calls. "
                        + "This tool has no arguments and performs read-only observation.";

        environments.inputSchema.type = "object";
        environments.inputSchema.additionalProperties = false;

        toolsListResponse.result.addTool(environments);
    }

    private void addContainersTool(ToolsListResponse toolsListResponse) {
        ToolsListResponse.Tool containers = new ToolsListResponse.Tool();
        containers.name = TOOL_CONTAINERS;
        containers.description =
                "List containers for one Docker environment as normalized monitoring JSON. "
                        + "Use this tool after 'environments' when the user asks what containers are running on a host, which containers expose ports, which containers use host networking, which containers mount host paths, or which containers consume more CPU, memory, or PIDs. "
                        + "The response includes containerName, image, state, status, networkMode, ports, mounts, compose project/service labels, riskFlags, stateDetail, and stats. "
                        + "Use the returned containerName exactly as the containerName argument for 'incidentDetail' when deeper troubleshooting is needed.";

        containers.inputSchema.type = "object";
        containers.inputSchema.additionalProperties = false;

        containers.inputSchema.addProperty(
                "environmentName",
                ToolsListResponse.Property.string(
                        "Target environment name returned by the environments tool. Example: myHost or local."
                )
        );

        containers.inputSchema.addRequired("environmentName");

        toolsListResponse.result.addTool(containers);
    }

    private void addIncidentDetailTool(ToolsListResponse toolsListResponse) {
        ToolsListResponse.Tool incidentDetail = new ToolsListResponse.Tool();
        incidentDetail.name = TOOL_INCIDENT_DETAIL;
        incidentDetail.description =
                "Get detailed troubleshooting data for one container in one Docker environment. "
                        + "Use this tool only when the user is investigating a specific container, asking why a container failed, restarted, is unhealthy, consumes unusual resources, or needs log analysis. "
                        + "The response may include inspect-derived stateDetail such as restartCount, exitCode, oomKilled, startedAt, finishedAt, current stats such as cpuPercent, memoryUsageBytes, memoryUsagePercent, pidsCurrent, and recent masked log text. "
                        + "Prefer 'containers' first to identify the exact containerName before calling this tool. "
                        + "This tool is read-only and does not execute commands inside the container.";

        incidentDetail.inputSchema.type = "object";
        incidentDetail.inputSchema.additionalProperties = false;

        incidentDetail.inputSchema.addProperty(
                "environmentName",
                ToolsListResponse.Property.string(
                        "Target environment name returned by the environments tool. Example: myHost or local."
                )
        );

        incidentDetail.inputSchema.addProperty(
                "containerName",
                ToolsListResponse.Property.string(
                        "Target container name returned by the containers tool. Do not add a leading slash. Example: myContainer."
                )
        );

        incidentDetail.inputSchema.addRequired("environmentName");
        incidentDetail.inputSchema.addRequired("containerName");

        toolsListResponse.result.addTool(incidentDetail);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // tools/call
    // -----------------------------------------------------------------------------------------------------------------
    @Override
    protected ToolsCallResponse resToolsCall(ToolsCallRequest toolsCallRequest) {
        String toolName = getToolName(toolsCallRequest);

        if (toolName == null) {
            return errorResponse(
                    toolsCallRequest,
                    "Invalid tools/call request.",
                    "Tool name is missing."
            );
        }

        switch (toolName) {
            case TOOL_ENVIRONMENTS:
                return environmentsToolResponse(toolsCallRequest);

            case TOOL_CONTAINERS:
                return containersToolResponse(toolsCallRequest);

            case TOOL_INCIDENT_DETAIL:
                return incidentDetailToolResponse(toolsCallRequest);

            default:
                /*
                 * 未知ツールは BaseMcpServlet 側に任せるため null を返す。
                 */
                return null;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // tool implementations
    // -----------------------------------------------------------------------------------------------------------------
    private ToolsCallResponse environmentsToolResponse(ToolsCallRequest toolsCallRequest) {
        try {
            Environments environments = EnvironmentsOverviewConverter.getRequest();
            return successResponse(toolsCallRequest, environments);

        } catch (Exception e) {
            logException("environmentsToolResponse", e);
            return errorResponse(
                    toolsCallRequest,
                    "Failed to get Docker environments.",
                    safeMessage(e)
            );
        }
    }

    private ToolsCallResponse containersToolResponse(ToolsCallRequest toolsCallRequest) {
        try {
            String environmentName = getRequiredArgument(toolsCallRequest, "environmentName");
            this.logger.info2("environmentName = " + environmentName);

            Containers containers = ContainersOverviewConverter.getRequest(environmentName);
            return successResponse(toolsCallRequest, containers);

        } catch (Exception e) {
            logException("containersToolResponse", e);
            return errorResponse(
                    toolsCallRequest,
                    "Failed to get containers for the specified environment.",
                    safeMessage(e)
            );
        }
    }

    private ToolsCallResponse incidentDetailToolResponse(ToolsCallRequest toolsCallRequest) {
        try {
            String environmentName = getRequiredArgument(toolsCallRequest, "environmentName");
            String containerName = getRequiredArgument(toolsCallRequest, "containerName");

            this.logger.info2("environmentName = " + environmentName);
            this.logger.info2("containerName = " + containerName);

            IncidentDetail incidentDetail = IncidentDetailConverter.getRequest(environmentName, containerName);
            return successResponse(toolsCallRequest, incidentDetail);

        } catch (Exception e) {
            logException("incidentDetailToolResponse", e);
            return errorResponse(
                    toolsCallRequest,
                    "Failed to get incident detail for the specified container.",
                    safeMessage(e)
            );
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // response helpers
    // -----------------------------------------------------------------------------------------------------------------
    private ToolsCallResponse successResponse(ToolsCallRequest toolsCallRequest, Object payload) throws Exception {
        ToolsCallResponse toolsCallResponse = baseToolCallResponse(toolsCallRequest);

        ToolsCallResponse.Content content = new ToolsCallResponse.Content();
        content.type = "text";
        content.text = mapper.writeValueAsString(payload);

        toolsCallResponse.result.addContent(content);
        return toolsCallResponse;
    }

    private ToolsCallResponse errorResponse(ToolsCallRequest toolsCallRequest, String message, String detail) {
        ToolsCallResponse toolsCallResponse = baseToolCallResponse(toolsCallRequest);
        toolsCallResponse.result.isError = true;

        ToolsCallResponse.Content content = new ToolsCallResponse.Content();
        content.type = "text";
        content.text = toErrorJson(message, detail);

        toolsCallResponse.result.addContent(content);
        return toolsCallResponse;
    }

    private ToolsCallResponse baseToolCallResponse(ToolsCallRequest toolsCallRequest) {
        ToolsCallResponse toolsCallResponse = new ToolsCallResponse();
        toolsCallResponse.jsonrpc = JSONRPC_VERSION;

        if (toolsCallRequest != null) {
            toolsCallResponse.id = toolsCallRequest.getId();
        }

        return toolsCallResponse;
    }

    private String toErrorJson(String message, String detail) {
        try {
            Map<String, Object> error = new LinkedHashMap<String, Object>();
            error.put("error", true);
            error.put("message", message);
            error.put("detail", detail);
            return mapper.writeValueAsString(error);
        } catch (Exception e) {
            return "{\"error\":true,\"message\":\"" + safeForJson(message) + "\"}";
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // request helpers
    // -----------------------------------------------------------------------------------------------------------------
    private String getToolName(ToolsCallRequest toolsCallRequest) {
        if (toolsCallRequest == null) {
            return null;
        }

        if (toolsCallRequest.getParams() == null) {
            return null;
        }

        return trimToNull(toolsCallRequest.getParams().getName());
    }

    private String getRequiredArgument(ToolsCallRequest toolsCallRequest, String argumentName) {
        String value = null;

        try {
            if (toolsCallRequest != null
                    && toolsCallRequest.getParams() != null
                    && toolsCallRequest.getParams().getArguments() != null) {
                value = toolsCallRequest.getParams().getArguments().getValue(argumentName);
            }
        } catch (Exception e) {
            value = null;
        }

        value = trimToNull(value);

        if (value == null) {
            throw new IllegalArgumentException("Missing required argument: " + argumentName);
        }

        return value;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();

        if (trimmed.isEmpty()) {
            return null;
        }

        return trimmed;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // logging helpers
    // -----------------------------------------------------------------------------------------------------------------
    private void logException(String context, Exception e) {
        this.logger.error_YELLOW(context);

        if (e == null) {
            this.logger.error_RED("Exception is null.");
            return;
        }

        this.logger.error_RED(e.toString());

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        printWriter.flush();

        this.logger.error_RED(stringWriter.toString());
    }

    private String safeMessage(Exception e) {
        if (e == null) {
            return "";
        }

        if (e.getMessage() == null || e.getMessage().trim().isEmpty()) {
            return e.toString();
        }

        return e.getMessage();
    }

    private String safeForJson(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}