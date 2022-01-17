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

import com.tencent.polaris.api.config.consumer.LoadBalanceConfig;
import com.tencent.polaris.api.pojo.DefaultInstance;
import com.tencent.polaris.api.pojo.DefaultServiceInstances;
import com.tencent.polaris.api.pojo.Instance;
import com.tencent.polaris.api.pojo.ServiceInstancesWrap;
import com.tencent.polaris.api.pojo.ServiceKey;
import com.tencent.polaris.factory.api.RouterAPIFactory;
import com.tencent.polaris.router.api.core.RouterAPI;
import com.tencent.polaris.router.api.rpc.ProcessLoadBalanceRequest;
import com.tencent.polaris.router.api.rpc.ProcessLoadBalanceResponse;
import io.grpc.ConnectivityState;
import io.grpc.ConnectivityStateInfo;
import io.grpc.EquivalentAddressGroup;
import io.grpc.LoadBalancer;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.grpc.ConnectivityState.CONNECTING;

/**
 * @author lixiaoshuang
 */
public class PolarisLoadBalancer extends LoadBalancer {
    
    private final Logger log = LoggerFactory.getLogger(PolarisLoadBalancer.class);
    
    private final Helper helper;
    
    private Subchannel subchannel;
    
    private String loadBalancerType;
    
    private static final RouterAPI routerAPI = RouterAPIFactory.createRouterAPI();
    
    public PolarisLoadBalancer(Helper helper, String loadBalancerType) {
        this.helper = helper;
        this.loadBalancerType = loadBalancerType;
    }
    
    @Override
    public void handleResolvedAddresses(ResolvedAddresses resolvedAddresses) {
        log.info("handle address:{}", resolvedAddresses.getAddresses().toString());
    
        ServiceInstancesWrap serviceInstancesWrap = new ServiceInstancesWrap();
    
        List<DefaultInstance> instances = resolvedAddresses.getAddresses().stream().map(equivalentAddressGroup -> {
            DefaultInstance defaultInstance = new DefaultInstance();
            return defaultInstance;
        }).collect(Collectors.toList());
        
        ServiceInstancesWrap serviceInstancesWrap = new ServiceInstancesWrap(new DefaultServiceInstances(new ServiceKey(),instances));
        ProcessLoadBalanceRequest processLoadBalanceRequest = new ProcessLoadBalanceRequest();
        processLoadBalanceRequest.setDstInstances(serviceInstancesWrap);
        processLoadBalanceRequest.setLbPolicy(loadBalancerType);
        ProcessLoadBalanceResponse processLoadBalanceResponse = routerAPI.processLoadBalance(processLoadBalanceRequest);
        
        Subchannel subchannel = helper.createSubchannel(
                CreateSubchannelArgs.newBuilder().setAddresses(resolvedAddresses.getAddresses()).build());
        
//        List<EquivalentAddressGroup> servers = resolvedAddresses.getAddresses();
//        if (subchannel == null) {
//            final Subchannel subchannel = helper.createSubchannel(
//                    CreateSubchannelArgs.newBuilder().setAddresses(servers).build());
//            subchannel.start(new SubchannelStateListener() {
//                @Override
//                public void onSubchannelState(ConnectivityStateInfo stateInfo) {
//                    processSubchannelState(subchannel, stateInfo);
//                }
//            });
//            this.subchannel = subchannel;
//
//            // The channel state does not get updated when doing name resolving today, so for the moment
//            // let LB report CONNECTION and call subchannel.requestConnection() immediately.
//            helper.updateBalancingState(CONNECTING,
//                    new PickFirstLoadBalancer.Picker(PickResult.withSubchannel(subchannel)));
//            subchannel.requestConnection();
//        } else {
//            subchannel.updateAddresses(servers);
//        }
    }
    
    @Override
    public void handleNameResolutionError(Status error) {
        log.info("Name resolution error:{}", error);
        helper.updateBalancingState(ConnectivityState.TRANSIENT_FAILURE,
                new PolarisSubchannelPicker();
    }
    
    @Override
    public void shutdown() {
        if (Objects.nonNull(subchannel)) {
            log.info("shutdown channel address:{}", subchannel.getAddresses().toString());
            subchannel.shutdown();
        }
    }
    
    @Override
    public void requestConnection() {
        log.info("connection address:{}", subchannel.getAddresses().toString());
        subchannel.requestConnection();
    }
}
