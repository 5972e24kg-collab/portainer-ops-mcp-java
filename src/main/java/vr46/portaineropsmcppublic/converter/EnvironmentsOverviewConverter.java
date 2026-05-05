package vr46.portaineropsmcppublic.converter;

import vr46.portaineropsmcppublic.portainer.ListEnvironmentsResponse;
import vr46.portaineropsmcppublic.dto.EnvironmentOverview;
import vr46.portaineropsmcppublic.dto.Environments;
import vr46.portaineropsmcppublic.portainer.DockerProxy_infoResponse;
import vr46.portaineropsmcppublic.portainer.PortainerMcpApiCore;

import java.util.ArrayList;

public class EnvironmentsOverviewConverter {
    public static Environments getRequest() throws Exception {
        Environments root = new Environments();
        root.environments = new ArrayList<>();

        PortainerMcpApiCore portainerMcpApiCore = new PortainerMcpApiCore();
        ListEnvironmentsResponse listEnvironments = portainerMcpApiCore.listEnvironments();
        for (ListEnvironmentsResponse.Environment env : listEnvironments.getEnvironments()) {
            EnvironmentOverview host = new EnvironmentOverview();
            host.environmentId = env.getId();   // 9
            host.environmentName = env.getName();   // myHost
            host.hostName = env.getName();   // myHost
            host.status = env.getStatus();  // active
            host.type = env.getType();  // docker-agent

            DockerProxy_infoResponse dockerProxyInfo = portainerMcpApiCore.dockerProxy_info(host.environmentId);
            host.host.os = dockerProxyInfo.getOperatingSystem();    // VMware Photon OS/Linux
            host.host.dockerVersion = dockerProxyInfo.getServerVersion();   // 00.0.0   - ServerVersion
            host.host.kernelVersion = dockerProxyInfo.getKernelVersion();   // 0.00.000-0.ph4-esx
            host.host.cpu = dockerProxyInfo.getNcpu();  // 2
            host.host.memoryBytes = dockerProxyInfo.getMemTotal();  // 00000000
            host.host.loggingDriver = dockerProxyInfo.getLoggingDriver();   // json-file
            host.host.cgroupDriver = dockerProxyInfo.getCgroupDriver(); // cgroupfs
            host.host.cgroupVersion = dockerProxyInfo.getCgroupVersion();   // 1
            host.containers.paused = dockerProxyInfo.getContainersPaused(); // 0
            host.containers.running = dockerProxyInfo.getContainersRunning();   // 10
            host.containers.stopped = dockerProxyInfo.getContainersStopped();   // 0
            host.containers.total = dockerProxyInfo.getContainers();    // 10
            host.warnings = dockerProxyInfo.getWarnings();  // List<String>

            root.environments.add(host);
        }
        return root;
    }

}