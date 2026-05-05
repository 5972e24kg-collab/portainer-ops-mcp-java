package vr46.portaineropsmcppublic.converter;

import vr46.portaineropsmcppublic.portainer.ListEnvironmentsResponse;
import vr46.portaineropsmcppublic.dto.ContainerOverview;
import vr46.portaineropsmcppublic.dto.Containers;
import vr46.portaineropsmcppublic.portainer.DockerProxy_containerResponse;
import vr46.portaineropsmcppublic.portainer.DockerProxy_containerStatsResponse;
import vr46.portaineropsmcppublic.portainer.DockerProxy_containersResponse;
import vr46.portaineropsmcppublic.portainer.PortainerMcpApiCore;

import java.util.ArrayList;
import java.util.List;

public class ContainersOverviewConverter {
    public static Containers getRequest(String environmentName) throws Exception {
        Containers root = new Containers();
        root.containers = new ArrayList<>();

        PortainerMcpApiCore portainerMcpApiCore = new PortainerMcpApiCore();
        ListEnvironmentsResponse listEnvironments = portainerMcpApiCore.listEnvironments();
        int environmentId = getEnvironmentId(environmentName, listEnvironments);
        if (environmentId == -1) {
            return root;
        }
        DockerProxy_containersResponse dockerProxyContainers = portainerMcpApiCore.dockerProxy_containers(environmentId);
        for (DockerProxy_containersResponse.Container container : dockerProxyContainers.getContainers()) {
            String containerId = container.getId();
            DockerProxy_containerResponse dockerInspect = portainerMcpApiCore.dockerProxy_container(environmentId, containerId);
            DockerProxy_containerStatsResponse dockerStats = portainerMcpApiCore.dockerProxy_containerStats(environmentId, containerId);

            ContainerOverview containerOverview = new ContainerOverview();
            containerOverview.environmentId = environmentId;    // 9
            containerOverview.hostName = environmentName;   // myHost
            containerOverview.environmentName = environmentName;   // myHost
            containerOverview.containerId = container.getId();  // 12345....
            containerOverview.containerName = dockerInspect.getName();   // /myContainer
            // nullチェックと、先頭が「/」で始まっているかの判定
            if (containerOverview.containerName != null && containerOverview.containerName.startsWith("/")) {
                // インデックス1（2文字目）以降の文字列を取得し、上書きする
                containerOverview.containerName = containerOverview.containerName.substring(1);
            }

            containerOverview.image = container.getImage(); // openjdk:21
            containerOverview.state = container.getState(); // running
            containerOverview.status = container.getStatus();   // Up 16 hours
            containerOverview.networkMode = container.getHostConfig().getNetworkMode(); // host
            List<ContainerOverview.Port> ports = new ArrayList<>();
            for (DockerProxy_containersResponse.Port sourcePort : container.getPorts()) {
                ContainerOverview.Port port = new ContainerOverview.Port();
                port.ip = sourcePort.getIp();   // 0.0.0.0
                port.privatePort = sourcePort.getPrivatePort(); // 9001
                port.publicPort = sourcePort.getPublicPort();   // 9001
                port.type = sourcePort.getType();   // tcp
                ports.add(port);
            }
            containerOverview.ports = ports;
            List<ContainerOverview.Mount> mounts = new ArrayList<>();
            for (DockerProxy_containersResponse.Mount sourceMount : container.getMounts()) {
                ContainerOverview.Mount mount = new ContainerOverview.Mount();
                mount.type = sourceMount.getType();   // bind
                mount.destination = sourceMount.getDestination();   // /jars
                mount.rw = sourceMount.getRw(); // true
                mounts.add(mount);
            }
            containerOverview.mounts = mounts;
            containerOverview.compose.project = container.getLabels().get("com.docker.compose.project");
            containerOverview.compose.service = container.getLabels().get("com.docker.compose.service");

            containerOverview.createdAt = dockerInspect.getCreated(); // 2000-00-00T00:00:00.0000Z
            containerOverview.stateDetail.restartCount = dockerInspect.getRestartCount(); // 0
            containerOverview.stateDetail.error = dockerInspect.getState().getError();   // ?
            containerOverview.stateDetail.finishedAt = dockerInspect.getState().getFinishedAt(); // 2000-00-00T00:00:00.0000Z
            containerOverview.stateDetail.oomKilled = dockerInspect.getState().getOomKilled();   // false
            containerOverview.stateDetail.restarting = dockerInspect.getState().getRestarting(); // false
            containerOverview.stateDetail.running = dockerInspect.getState().getRunning();   // true
            containerOverview.stateDetail.startedAt = dockerInspect.getState().getStartedAt();   // 2000-00-00T00:00:00.0000Z
            containerOverview.stateDetail.status = dockerInspect.getState().getStatus();  // running

            containerOverview.stats.cpuPercent = getCpuPercent(dockerStats);   // 0.000
            containerOverview.stats.memoryUsageBytes = dockerStats.getMemoryStats().getUsage();    // 1234
            containerOverview.stats.memoryUsagePercent = getMemoryUsagePercent(dockerStats);   // 0.000
            containerOverview.stats.pidsCurrent = dockerStats.getPidsStats().getCurrent(); // 15

            List<String> riskFlags = new ArrayList<String>();
            if (!"running".equals(container.getState())) {
                riskFlags.add("CONTAINER_NOT_RUNNING");
            }

            if ("host".equals(container.getHostConfig().getNetworkMode())) {
                riskFlags.add("HOST_NETWORK");
            }

            if (container.getPorts() != null && !container.getPorts().isEmpty()) {
                riskFlags.add("PUBLIC_PORT_EXPOSED");
            }

            for (DockerProxy_containersResponse.Mount mount : container.getMounts()) {
                if ("/var/run/docker.sock".equals(mount.getDestination())) {
                    riskFlags.add("DOCKER_SOCK_MOUNTED");
                }

                if ("/".equals(mount.getSource()) || "/".equals(mount.getDestination())) {
                    riskFlags.add("HOST_ROOT_MOUNTED");
                }

                if (Boolean.TRUE.equals(mount.getRw())) {
                    // 必要なら、特定パスだけに絞る
                    // riskFlags.add("RW_MOUNT");
                }
            }
            containerOverview.riskFlags = riskFlags;

            root.containers.add(containerOverview);
        }

        return root;
    }

    public static int getEnvironmentId(String containerName, ListEnvironmentsResponse listEnvironments) {
        for (ListEnvironmentsResponse.Environment env : listEnvironments.getEnvironments()) {
            if (env.getName().equals(containerName)) {
                return env.getId();
            }
        }
        return -1;
    }

    public static Double getCpuPercent(DockerProxy_containerStatsResponse dockerStats) {
        Long cpuTotal = dockerStats.getCpuStats().getCpuUsage().getTotalUsage();
        Long preCpuTotal = dockerStats.getPrecpuStats().getCpuUsage().getTotalUsage();

        Long systemCpu = dockerStats.getCpuStats().getSystemCpuUsage();
        Long preSystemCpu = dockerStats.getPrecpuStats().getSystemCpuUsage();

        Integer onlineCpus = dockerStats.getCpuStats().getOnlineCpus();

        if (cpuTotal == null || preCpuTotal == null || systemCpu == null || preSystemCpu == null) {
            return null;
        }

        long cpuDelta = cpuTotal - preCpuTotal;
        long systemDelta = systemCpu - preSystemCpu;

        if (cpuDelta <= 0 || systemDelta <= 0) {
            return 0.0d;
        }

        int cpuCount = onlineCpus != null && onlineCpus > 0 ? onlineCpus : 1;

        return ((double) cpuDelta / (double) systemDelta) * cpuCount * 100.0d;
    }

    public static Double getMemoryUsagePercent(DockerProxy_containerStatsResponse dockerStats) {
        if (dockerStats.getMemoryStats() == null) {
            return null;
        }

        Long usage = dockerStats.getMemoryStats().getUsage();
        Long limit = dockerStats.getMemoryStats().getLimit();

        if (usage == null || limit == null || limit.longValue() <= 0) {
            return null;
        }

        return ((double) usage.longValue() / (double) limit.longValue()) * 100.0d;
    }

}