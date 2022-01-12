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
import io.grpc.LoadBalancerProvider;


/**
 * @author lixiaoshuang
 */
public class PolarisBalancerProvider extends LoadBalancerProvider {
    
    private static final int DEFAULT_PRIORITY = 5;
    
    private String loadBalancerType;
    
    public PolarisBalancerProvider() {
        this.loadBalancerType = LoadBalancerType.WEIGHTED_RANDOM;
    }
    
    /**
     * @param loadBalancerType load balancing type
     * @see LoadBalancerType
     */
    public PolarisBalancerProvider(String loadBalancerType) {
        this.loadBalancerType = loadBalancerType;
    }
    
    @Override
    public boolean isAvailable() {
        return true;
    }
    
    @Override
    public int getPriority() {
        return DEFAULT_PRIORITY;
    }
    
    @Override
    public String getPolicyName() {
        return loadBalancerType;
    }
    
    @Override
    public LoadBalancer newLoadBalancer(LoadBalancer.Helper helper) {
        return new PolarisLoadBalancer(helper, loadBalancerType);
    }
}
