package vr46.portaineropsmcppublic.portainer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DockerProxy_containerStatsResponse {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String read;
    private final String preread;
    private final PidsStats pidsStats;
    private final CpuStats cpuStats;
    private final CpuStats precpuStats;
    private final MemoryStats memoryStats;
    private final String name;
    private final String id;
    private final JsonNode rawNode;

    public DockerProxy_containerStatsResponse(String json) throws IOException {
        this(parseJson(json));
    }

    public DockerProxy_containerStatsResponse(JsonNode root) {
        this.read = extractText(root, "read");
        this.preread = extractText(root, "preread");
        this.pidsStats = new PidsStats(extractNode(root, "pids_stats"));
        this.cpuStats = new CpuStats(extractNode(root, "cpu_stats"));
        this.precpuStats = new CpuStats(extractNode(root, "precpu_stats"));
        this.memoryStats = new MemoryStats(extractNode(root, "memory_stats"));
        this.name = extractText(root, "name");
        this.id = extractText(root, "id");
        this.rawNode = copyNode(root);
    }

    public String getRead() {
        return read;
    }
    public String getPreread() {
        return preread;
    }
    public PidsStats getPidsStats() {
        return pidsStats;
    }
    public CpuStats getCpuStats() {
        return cpuStats;
    }
    public CpuStats getPrecpuStats() {
        return precpuStats;
    }
    public MemoryStats getMemoryStats() {
        return memoryStats;
    }
    public String getName() {
        return name;
    }
    public String getId() {
        return id;
    }

    public Double getCpuPercent() {
        if (cpuStats == null || precpuStats == null) {
            return null;
        }

        if (cpuStats.getCpuUsage() == null || precpuStats.getCpuUsage() == null) {
            return null;
        }

        Long cpuTotal = cpuStats.getCpuUsage().getTotalUsage();
        Long preCpuTotal = precpuStats.getCpuUsage().getTotalUsage();

        Long systemCpu = cpuStats.getSystemCpuUsage();
        Long preSystemCpu = precpuStats.getSystemCpuUsage();

        Integer onlineCpus = cpuStats.getOnlineCpus();

        if (cpuTotal == null || preCpuTotal == null || systemCpu == null || preSystemCpu == null) {
            return null;
        }

        long cpuDelta = cpuTotal.longValue() - preCpuTotal.longValue();
        long systemDelta = systemCpu.longValue() - preSystemCpu.longValue();

        if (cpuDelta <= 0 || systemDelta <= 0) {
            return 0.0d;
        }

        int cpuCount = onlineCpus != null && onlineCpus.intValue() > 0
                ? onlineCpus.intValue()
                : 1;

        return ((double) cpuDelta / (double) systemDelta) * cpuCount * 100.0d;
    }

    public Double getMemoryUsagePercent() {
        if (memoryStats == null) {
            return null;
        }

        Long usage = memoryStats.getUsage();
        Long limit = memoryStats.getLimit();

        if (usage == null || limit == null || limit.longValue() <= 0) {
            return null;
        }

        return ((double) usage.longValue() / (double) limit.longValue()) * 100.0d;
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
    public List<Long> getFieldAsLongList(String field) {
        return extractLongList(rawNode, field);
    }
    public boolean hasField(String field) {
        return extractNode(rawNode, field) != null;
    }

    public static class PidsStats {

        private final Integer current;
        private final JsonNode rawNode;

        public PidsStats(JsonNode node) {
            this.current = extractInteger(node, "current");
            this.rawNode = copyNode(node);
        }

        public Integer getCurrent() {
            return current;
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
    }

    public static class CpuStats {

        private final CpuUsage cpuUsage;
        private final Long systemCpuUsage;
        private final Integer onlineCpus;
        private final ThrottlingData throttlingData;
        private final JsonNode rawNode;

        public CpuStats(JsonNode node) {
            this.cpuUsage = new CpuUsage(extractNode(node, "cpu_usage"));
            this.systemCpuUsage = extractLong(node, "system_cpu_usage");
            this.onlineCpus = extractInteger(node, "online_cpus");
            this.throttlingData = new ThrottlingData(extractNode(node, "throttling_data"));
            this.rawNode = copyNode(node);
        }

        public CpuUsage getCpuUsage() {
            return cpuUsage;
        }
        public Long getSystemCpuUsage() {
            return systemCpuUsage;
        }
        public Integer getOnlineCpus() {
            return onlineCpus;
        }
        public ThrottlingData getThrottlingData() {
            return throttlingData;
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
    }

    public static class CpuUsage {

        private final Long totalUsage;
        private final List<Long> percpuUsage;
        private final Long usageInKernelmode;
        private final Long usageInUsermode;

        private final JsonNode rawNode;

        public CpuUsage(JsonNode node) {
            this.totalUsage = extractLong(node, "total_usage");
            this.percpuUsage = extractLongList(node, "percpu_usage");
            this.usageInKernelmode = extractLong(node, "usage_in_kernelmode");
            this.usageInUsermode = extractLong(node, "usage_in_usermode");
            this.rawNode = copyNode(node);
        }

        public Long getTotalUsage() {
            return totalUsage;
        }
        public List<Long> getPercpuUsage() {
            return percpuUsage;
        }
        public Long getUsageInKernelmode() {
            return usageInKernelmode;
        }
        public Long getUsageInUsermode() {
            return usageInUsermode;
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
    }

    public static class ThrottlingData {

        private final Long periods;
        private final Long throttledPeriods;
        private final Long throttledTime;
        private final JsonNode rawNode;

        public ThrottlingData(JsonNode node) {
            this.periods = extractLong(node, "periods");
            this.throttledPeriods = extractLong(node, "throttled_periods");
            this.throttledTime = extractLong(node, "throttled_time");
            this.rawNode = copyNode(node);
        }

        public Long getPeriods() {
            return periods;
        }
        public Long getThrottledPeriods() {
            return throttledPeriods;
        }
        public Long getThrottledTime() {
            return throttledTime;
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
    }

    public static class MemoryStats {

        private final Long usage;
        private final Long maxUsage;
        private final MemoryDetail stats;
        private final Long limit;
        private final JsonNode rawNode;

        public MemoryStats(JsonNode node) {
            this.usage = extractLong(node, "usage");
            this.maxUsage = extractLong(node, "max_usage");
            this.stats = new MemoryDetail(extractNode(node, "stats"));
            this.limit = extractLong(node, "limit");
            this.rawNode = copyNode(node);
        }

        public Long getUsage() {
            return usage;
        }
        public Long getMaxUsage() {
            return maxUsage;
        }
        public MemoryDetail getStats() {
            return stats;
        }
        public Long getLimit() {
            return limit;
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
    }

    public static class MemoryDetail {

        private final Long cache;
        private final Long rss;
        private final Long rssHuge;
        private final Long mappedFile;

        private final Long pgfault;
        private final Long pgmajfault;
        private final Long pgpgin;
        private final Long pgpgout;

        private final Long totalCache;
        private final Long totalRss;
        private final Long totalRssHuge;
        private final Long totalMappedFile;

        private final Long totalPgfault;
        private final Long totalPgmajfault;
        private final Long totalPgpgin;
        private final Long totalPgpgout;

        private final JsonNode rawNode;

        public MemoryDetail(JsonNode node) {
            this.cache = extractLong(node, "cache");
            this.rss = extractLong(node, "rss");
            this.rssHuge = extractLong(node, "rss_huge");
            this.mappedFile = extractLong(node, "mapped_file");

            this.pgfault = extractLong(node, "pgfault");
            this.pgmajfault = extractLong(node, "pgmajfault");
            this.pgpgin = extractLong(node, "pgpgin");
            this.pgpgout = extractLong(node, "pgpgout");

            this.totalCache = extractLong(node, "total_cache");
            this.totalRss = extractLong(node, "total_rss");
            this.totalRssHuge = extractLong(node, "total_rss_huge");
            this.totalMappedFile = extractLong(node, "total_mapped_file");

            this.totalPgfault = extractLong(node, "total_pgfault");
            this.totalPgmajfault = extractLong(node, "total_pgmajfault");
            this.totalPgpgin = extractLong(node, "total_pgpgin");
            this.totalPgpgout = extractLong(node, "total_pgpgout");

            this.rawNode = copyNode(node);
        }

        public Long getCache() {
            return cache;
        }
        public Long getRss() {
            return rss;
        }
        public Long getRssHuge() {
            return rssHuge;
        }
        public Long getMappedFile() {
            return mappedFile;
        }
        public Long getPgfault() {
            return pgfault;
        }
        public Long getPgmajfault() {
            return pgmajfault;
        }
        public Long getPgpgin() {
            return pgpgin;
        }
        public Long getPgpgout() {
            return pgpgout;
        }
        public Long getTotalCache() {
            return totalCache;
        }
        public Long getTotalRss() {
            return totalRss;
        }
        public Long getTotalRssHuge() {
            return totalRssHuge;
        }
        public Long getTotalMappedFile() {
            return totalMappedFile;
        }
        public Long getTotalPgfault() {
            return totalPgfault;
        }
        public Long getTotalPgmajfault() {
            return totalPgmajfault;
        }
        public Long getTotalPgpgin() {
            return totalPgpgin;
        }
        public Long getTotalPgpgout() {
            return totalPgpgout;
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

    private static List<Long> extractLongList(JsonNode node, String field) {
        JsonNode target = extractNode(node, field);

        if (target == null || !target.isArray()) {
            return Collections.emptyList();
        }

        List<Long> result = new ArrayList<Long>();

        for (JsonNode child : target) {
            if (child != null && child.isNumber()) {
                result.add(child.asLong());
            }
        }

        return Collections.unmodifiableList(result);
    }
}