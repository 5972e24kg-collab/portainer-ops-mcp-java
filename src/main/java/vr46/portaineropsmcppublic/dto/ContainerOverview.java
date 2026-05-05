package vr46.portaineropsmcppublic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ContainerOverview {
    /*サンプル
    {
      "environmentId": 9,
      "environmentName": "myHost",
      "containerId": "4a6dc293...",
      "containerName": "myContainer",
      "image": "openjdk:21",
      "state": "running",
      "status": "Up 6 hours",
      "networkMode": "host",
      "ports": [],
      "mounts": [
        {
          "type": "bind",
          "destination": "/jars",
          "rw": true
        }
      ],
      "compose": {
        "project": null,
        "service": null
      },
      "riskFlags": [],
      "Created": "2026-03-01T23:25:04.559081868Z",
      "RestartCount" : 0,
      "stats": {
        "status": "running",
        "running": true,
        "restarting": false,
        "exitCode": 0,
        "oomKilled": false,
        "startedAt": "2026-04-28T19:15:02.210703677Z",
        "finishedAt": "2026-04-28T19:15:02.147089776Z",
        "restartCount": 0,
        "cpuPercent": 0.076,
        "memoryUsageBytes": 181035008,
        "memoryUsagePercent": 4.37,
        "pidsCurrent": 39
      },
  },
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
    @JsonProperty("state")
    public String state = "";
    @JsonProperty("status")
    public String status = "";
    @JsonProperty("networkMode")
    public String networkMode = "";
    @JsonProperty("ports")
    public List<Port> ports = new ArrayList<Port>();
    @JsonProperty("mounts")
    public List<Mount> mounts = new ArrayList<Mount>();
    @JsonProperty("compose")
    public Compose compose = new Compose();
    @JsonProperty("riskFlags")
    public List<String> riskFlags = new ArrayList<String>();
    @JsonProperty("createdAt")
    public String createdAt = "";
    @JsonProperty("stateDetail")
    public StateDetail stateDetail = new StateDetail();
    @JsonProperty("stats")
    public Stats stats = new Stats();

    public static class Port {
        @JsonProperty("ip")
        public String ip = "";
        @JsonProperty("privatePort")
        public int privatePort = 0;
        @JsonProperty("publicPort")
        public int publicPort = 0;
        @JsonProperty("type")
        public String type = "";
    }

    public static class Mount {
        @JsonProperty("type")
        public String type = "";
        @JsonProperty("destination")
        public String destination = "";
        @JsonProperty("rw")
        public boolean rw = false;
    }

    public static class Compose {
        @JsonProperty("project")
        public String project = null;
        @JsonProperty("service")
        public String service = null;
    }

    public static class StateDetail {
        @JsonProperty("restartCount")
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
}