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

import com.google.common.base.MoreObjects;
import com.tencent.polaris.factory.api.RouterAPIFactory;
import com.tencent.polaris.router.api.core.RouterAPI;
import io.grpc.EquivalentAddressGroup;
import io.grpc.LoadBalancer;
import io.grpc.Status;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.grpc.ConnectivityState.TRANSIENT_FAILURE;

/**
 * @author lixiaoshuang
 */
public class PolarisLoadBalancer extends LoadBalancer {
    
    private Helper helper;
    
    private String loadBalancerType;
    
    private Subchannel subchannel;
    
    private static final RouterAPI routerAPI = RouterAPIFactory.createRouterAPI();
    
    public PolarisLoadBalancer(Helper helper, String loadBalancerType) {
        this.helper = helper;
        this.loadBalancerType = loadBalancerType;
    }
    
    @Override
    public void handleNameResolutionError(Status error) {
        helper.updateBalancingState(TRANSIENT_FAILURE, new ErrorPicker(error));
    }
    
    @Override
    public void shutdown() {
        if (subchannel != null) {
            subchannel.shutdown();
        }
    }
    
    @Override
    public void handleResolvedAddresses(ResolvedAddresses resolvedAddresses) {
        List<EquivalentAddressGroup> addresses = resolvedAddresses.getAddresses();
        for (EquivalentAddressGroup address : addresses) {
            List<SocketAddress> addresses1 = address.getAddresses();
            for (SocketAddress socketAddress : addresses1) {
                InetSocketAddress inetSocketAddress = (InetSocketAddress)socketAddress;
                int port = inetSocketAddress.getPort();
                String hostName = inetSocketAddress.getHostName();
                System.out.println("处理名字解析的服务地址：" + socketAddress.toString());
            }
        }
    }
    
    static final class ErrorPicker extends SubchannelPicker {
        
        private final Status error;
        
        ErrorPicker(Status error) {
            this.error = checkNotNull(error, "error");
        }
        
        @Override
        public PickResult pickSubchannel(PickSubchannelArgs args) {
            return PickResult.withError(error);
        }
        
        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("error", error).toString();
        }
    }
}
