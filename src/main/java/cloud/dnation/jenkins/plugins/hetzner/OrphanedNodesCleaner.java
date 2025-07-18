/*
 *     Copyright 2021 https://dnation.cloud
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cloud.dnation.jenkins.plugins.hetzner;

import cloud.dnation.hetznerclient.ServerDetail;
import hudson.Extension;
import hudson.model.PeriodicWork;
import jenkins.model.Jenkins;
import lombok.extern.slf4j.Slf4j;
import org.jenkinsci.Symbol;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Extension
@Symbol("OrphanedNodesCleaner")
@Slf4j
public class OrphanedNodesCleaner extends PeriodicWork {
    @Override
    public long getRecurrencePeriod() {
        return HOUR;
    }

    private static Set<HetznerCloud> getHetznerClouds() {
        return Jenkins.get().clouds.stream()
                .filter(HetznerCloud.class::isInstance)
                .map(HetznerCloud.class::cast)
                .collect(Collectors.toSet());
    }

    @Override
    protected void doRun() {
        doCleanup();
    }

    static void doCleanup() {
        getHetznerClouds().forEach(OrphanedNodesCleaner::cleanCloud);
    }

    private static void cleanCloud(HetznerCloud cloud) {
        try {
            final List<ServerDetail> allInstances = cloud.getResourceManager()
                    .fetchAllServers(cloud.name);
            final List<String> jenkinsNodes = Helper.getHetznerAgents()
                    .stream()
                    .map(HetznerServerAgent::getNodeName)
                    .toList();
            allInstances.stream().filter(server -> !jenkinsNodes.contains(server.getName()))
                    .forEach(serverDetail -> terminateServer(serverDetail, cloud));
        } catch (IOException e) {
            log.warn("Error while fetching all servers", e);
        }
    }

    private static void terminateServer(ServerDetail serverDetail, HetznerCloud cloud) {
        log.info("Terminating orphaned server {}", serverDetail.getName());
        cloud.getResourceManager().destroyServer(serverDetail);
    }
}
