package vr46.portaineropsmcppublic.converter;

import vr46.portaineropsmcppublic.portainer.ListEnvironmentsResponse;
import vr46.portaineropsmcppublic.dto.IncidentDetail;
import vr46.portaineropsmcppublic.portainer.DockerProxy_containerResponse;
import vr46.portaineropsmcppublic.portainer.DockerProxy_containerStatsResponse;
import vr46.portaineropsmcppublic.portainer.DockerProxy_containersResponse;
import vr46.portaineropsmcppublic.portainer.PortainerMcpApiCore;

import java.util.Objects;

public class IncidentDetailConverter {
    public static IncidentDetail getRequest(String environmentName, String containerName) throws Exception {
        IncidentDetail incidentDetail = new IncidentDetail();
        incidentDetail.containerName = containerName;
        incidentDetail.environmentName = environmentName;
        incidentDetail.hostName = environmentName;

        PortainerMcpApiCore portainerMcpApiCore = new PortainerMcpApiCore();
        ListEnvironmentsResponse listEnvironments = portainerMcpApiCore.listEnvironments();
        incidentDetail.environmentId = ContainersOverviewConverter.getEnvironmentId(environmentName, listEnvironments);
        if (incidentDetail.environmentId == -1) {
            return incidentDetail;
        }

        DockerProxy_containersResponse dockerProxyContainers = portainerMcpApiCore.dockerProxy_containers(incidentDetail.environmentId);
        for (DockerProxy_containersResponse.Container container : dockerProxyContainers.getContainers()) {
            String containerId = container.getId();
            DockerProxy_containerResponse dockerInspect = portainerMcpApiCore.dockerProxy_container(incidentDetail.environmentId, containerId);
            String _containerName = dockerInspect.getName();
            // nullチェックと、先頭が「/」で始まっているかの判定
            if (_containerName != null && _containerName.startsWith("/")) {
                // インデックス1（2文字目）以降の文字列を取得し、上書きする
                _containerName = _containerName.substring(1);
            }

            if (Objects.requireNonNull(_containerName).equals(containerName)) {
                DockerProxy_containerStatsResponse dockerStats = portainerMcpApiCore.dockerProxy_containerStats(incidentDetail.environmentId, containerId);

                incidentDetail.containerId = container.getId();  // 0914db2817441650af0233d7c68770eb6f041b560735aae2a9d432017dd1ba63
                incidentDetail.image = container.getImage(); // openjdk:21

                incidentDetail.states.restartCount = dockerInspect.getRestartCount(); // 0
                incidentDetail.states.error = dockerInspect.getState().getError();   // ?
                incidentDetail.states.finishedAt = dockerInspect.getState().getFinishedAt(); // 2000-00-00T00:00:00.0000Z
                incidentDetail.states.oomKilled = dockerInspect.getState().getOomKilled();   // false
                incidentDetail.states.restarting = dockerInspect.getState().getRestarting(); // false
                incidentDetail.states.running = dockerInspect.getState().getRunning();   // true
                incidentDetail.states.startedAt = dockerInspect.getState().getStartedAt();   // 2000-00-00T00:00:00.0000Z
                incidentDetail.states.status = dockerInspect.getState().getStatus();  // running

                incidentDetail.stats.cpuPercent = ContainersOverviewConverter.getCpuPercent(dockerStats);   // 0.076
                incidentDetail.stats.memoryUsageBytes = dockerStats.getMemoryStats().getUsage();    // 0000000000000
                incidentDetail.stats.memoryUsagePercent = ContainersOverviewConverter.getMemoryUsagePercent(dockerStats);   // 4.37
                incidentDetail.stats.pidsCurrent = dockerStats.getPidsStats().getCurrent(); // 15

                incidentDetail.logs.masked = true;
                incidentDetail.logs.tailLines = 200;
                incidentDetail.logs.text = portainerMcpApiCore.dockerProxy_containerLogs(incidentDetail.environmentId, containerId);
            }
        }

        return incidentDetail;
    }

}
