/**
 * Copyright 2016 Nikita Koksharov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.redisson.mapreduce;

import java.util.Map.Entry;

import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.mapreduce.RCollector;
import org.redisson.api.mapreduce.RMapper;
import org.redisson.api.mapreduce.RReducer;
import org.redisson.misc.Injector;

/**
 * 
 * @author Nikita Koksharov
 *
 * @param <KIn> input key type
 * @param <VIn> input key type
 * @param <KOut> output key type
 * @param <VOut> output key type
 */
public class MapperTask<KIn, VIn, KOut, VOut> extends BaseMapperTask<KOut, VOut> {

    private static final long serialVersionUID = 2441161019495880394L;
    
    RMapper<KIn, VIn, KOut, VOut> mapper;
    
    public MapperTask() {
    }

    public MapperTask(RMapper<KIn, VIn, KOut, VOut> mapper, RReducer<KOut, VOut> reducer, String mapName, String semaphoreName, String resultMapName,
            Class<?> mapCodecClass, Class<?> mapClass) {
        super(reducer, mapName, semaphoreName, resultMapName, mapCodecClass, mapClass);
        this.mapper = mapper;
    }

    @Override
    protected void map(RCollector<KOut, VOut> collector) {
        Injector.inject(mapper, redisson);

        RMap<KIn, VIn> map = null;
        if (RMapCache.class.isAssignableFrom(objectClass)) {
            map = redisson.getMapCache(objectName, codec);
        } else {
            map = redisson.getMap(objectName, codec);
        }

        for (Entry<KIn, VIn> entry : map.entrySet()) {
            mapper.map(entry.getKey(), entry.getValue(), collector);
        }
    }

}
