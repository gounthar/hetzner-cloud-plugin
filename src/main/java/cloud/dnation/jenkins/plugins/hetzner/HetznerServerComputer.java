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

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.slaves.AbstractCloudComputer;
import org.jenkinsci.plugins.cloudstats.ProvisioningActivity;
import org.jenkinsci.plugins.cloudstats.TrackedItem;


public class HetznerServerComputer extends AbstractCloudComputer<HetznerServerAgent> implements TrackedItem {
    private final ProvisioningActivity.Id provisioningId;

    public HetznerServerComputer(HetznerServerAgent agent) {
        super(agent);
        this.provisioningId = agent.getId();
    }

    @NonNull
    @Override
    public ProvisioningActivity.Id getId() {
        return provisioningId;
    }
}
