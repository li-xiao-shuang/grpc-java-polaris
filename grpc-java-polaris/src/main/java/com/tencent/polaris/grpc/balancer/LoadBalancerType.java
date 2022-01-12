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

/**
 * load balancing type.
 *
 * @author lixiaoshuang
 */
public class LoadBalancerType {
    
    /**
     * Weighted random load balancing.
     */
    public static final String WEIGHTED_RANDOM = LoadBalanceConfig.LOAD_BALANCE_WEIGHTED_RANDOM;
    
    /**
     * Ring hash load balancing.
     */
    public static final String RING_HASH = LoadBalanceConfig.LOAD_BALANCE_RING_HASH;
}
