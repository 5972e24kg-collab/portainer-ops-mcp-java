package vr46.portaineropsmcppublic.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

public class IncidentDetail {
    /*サンプル
    {
      "environmentId": 9,
      "environmentName": "myHost",
      "containerId": "4a6dc293...",
      "containerName": "myContainer",
      "States": {
        "status": "running",
        "running": true,
        "restarting": false,
        "exitCode": 0,
        "oomKilled": false,
        "startedAt": "2000-00-00T00:00:00.0000Z",
        "finishedAt": "2000-00-00T00:00:00.0000Z",
        "restartCount": 0
      },
      "stats": {
        "cpuPercent": 0.076,
        "memoryUsageBytes": 000000000,
        "memoryUsagePercent": 4.37,
        "pidsCurrent": 39
      },
      "logs": {
        "tailLines": 200,
        "masked": true,
        "text": "..."
      },
      "signals": {
          "errorCount": 0,
          "warnCount": 0,
          "non2xxHttpStatusCount": 0,
          "runCount": 0,
          "finishCount": 0,
          "databaseCommitCount": 0
        }
    }
     */
    @JsonProperty("environmentId")
    public int environmentId = 0;
    @JsonProperty("environmentName")
    public String environmentName = "";
    @JsonProperty("hostName")
    public String hostName = "";
    @JsonProperty("containerId")
    public String containerId = "";
    @JsonProperty("containerName")
    public String containerName = "";
    @JsonProperty("image")
    public String image = "";
    @JsonProperty("states")
    public States states = new States();
    @JsonProperty("stats")
    public Stats stats = new Stats();
    @JsonProperty("logs")
    public Logs logs = new Logs();

    public static class States {
        @JsonProperty("RestartCount")
        public int restartCount = 0;
        @JsonProperty("error")
        public String error;
        @JsonProperty("finishedAt")
        public String finishedAt;
        @JsonProperty("oomKilled")
        public Boolean oomKilled;
        @JsonProperty("restarting")
        public Boolean restarting;
        @JsonProperty("running")
        public Boolean running;
        @JsonProperty("startedAt")
        public String startedAt;
        @JsonProperty("status")
        public String status;
    }
    public static class Stats {
        @JsonProperty("cpuPercent")
        public Double cpuPercent;
        @JsonProperty("memoryUsageBytes")
        public long memoryUsageBytes;
        @JsonProperty("memoryUsagePercent")
        public Double memoryUsagePercent;
        @JsonProperty("pidsCurrent")
        public int pidsCurrent;
    }

    public static class Logs {
        @JsonProperty("tailLines")
        public int tailLines;
        @JsonProperty("masked")
        public boolean masked;
        @JsonProperty("text")
        public String text;
    }

}