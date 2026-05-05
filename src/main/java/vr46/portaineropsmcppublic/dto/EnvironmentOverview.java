package vr46.portaineropsmcppublic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/*サンプル
{
  "environmentId": 9,
  "environmentName": "myHost",
  "status": "active",
  "type": "docker-agent",
  "host": {
    "os": "VMware Photon OS/Linux",
    "dockerVersion": "00.0.0",
    "kernelVersion": "0.00.000-0.ph4-esx",
    "cpu": 2,
    "memoryBytes": 00000000,
    "loggingDriver": "json-file",
    "cgroupDriver": "cgroupfs",
    "cgroupVersion": "1"
  },
  "containers": {
    "total": 10,
    "running": 10,
    "paused": 0,
    "stopped": 0
  },
  "warnings": [
    "WARNING: No kernel memory TCP limit support",
    "WARNING: bridge-nf-call-iptables is disabled",
    "WARNING: bridge-nf-call-ip6tables is disabled"
  ]
}
 */
public class EnvironmentOverview {
    @JsonProperty("environmentId")
    public int environmentId = 0;
    @JsonProperty("environmentName")
    public String environmentName = "";
    @JsonProperty("hostName")
    public String hostName = "";
    @JsonProperty("status")
    public String status = "";
    @JsonProperty("type")
    public String type = "";
    @JsonProperty("host")
    public Host host = new Host();
    @JsonProperty("containers")
    public Containers containers = new Containers();
    @JsonProperty("warnings")
    public List<String> warnings = new ArrayList<String>();

    public static class Host {
        @JsonProperty("os")
        public String os = "";
        @JsonProperty("dockerVersion")
        public String dockerVersion = "";
        @JsonProperty("kernelVersion")
        public String kernelVersion = "";
        @JsonProperty("cpu")
        public int cpu = 0;
        @JsonProperty("memoryBytes")
        public long memoryBytes = 0L;
        @JsonProperty("loggingDriver")
        public String loggingDriver = "";
        @JsonProperty("cgroupDriver")
        public String cgroupDriver = "";
        @JsonProperty("cgroupVersion")
        public String cgroupVersion = "";
    }

    public static class Containers {
        @JsonProperty("total")
        public int total = 0;
        @JsonProperty("running")
        public int running = 0;
        @JsonProperty("paused")
        public int paused = 0;
        @JsonProperty("stopped")
        public int stopped = 0;
    }
}