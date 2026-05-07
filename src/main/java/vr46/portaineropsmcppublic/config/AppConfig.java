package vr46.portaineropsmcppublic.config;

public class AppConfig {
    public final String portainerBaseUrl;
    public final String portainerApiToken;
    public final int portainerEnvironmentId;
    public final String mcpProtocolVersion;

    private AppConfig() {
        this.portainerBaseUrl = requireEnv("PORTAINER_BASE_URL");
        this.portainerApiToken = requireEnv("PORTAINER_API_TOKEN");
        this.portainerEnvironmentId = Integer.parseInt(requireEnv("PORTAINER_ENVIRONMENT_ID"));
        this.mcpProtocolVersion = getEnvOrDefault("MCP_PROTOCOL_VERSION", "2025-06-18");
    }

    public static AppConfig load() {
        return new AppConfig();
    }

    private static String requireEnv(String name) {
        String value = System.getenv(name);
        if (value == null) {
            throw new IllegalStateException("Required environment variable is missing: " + name);
        }
        return value;
    }

    private static String getEnvOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null ? defaultValue : value;
    }
}