/*
 * Tencent is pleased to support the open source community by making Polaris available.
 *
 * Copyright (C) 2019 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.tencent.polaris.grpc.balancer;

import io.grpc.LoadBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lixiaoshuang
 */
public class PolarisSubchannelPicker extends LoadBalancer.SubchannelPicker {
    
    private final Logger log = LoggerFactory.getLogger(PolarisSubchannelPicker.class);
    
    private final AtomicInteger index = new AtomicInteger();
    
    private LoadBalancer.Subchannel subchannel;
    
    public PolarisSubchannelPicker() {
    }
    
    
    public PolarisSubchannelPicker(LoadBalancer.Subchannel subchannel) {
        this.subchannel = subchannel;
    }
    
    @Override
    public LoadBalancer.PickResult pickSubchannel(LoadBalancer.PickSubchannelArgs args) {
        log.info("return subchannel:{}", subchannel);
        if (Objects.nonNull(subchannel)) {
            return LoadBalancer.PickResult.withSubchannel(subchannel);
        }
        return LoadBalancer.PickResult.withNoResult();
    }
    
}
