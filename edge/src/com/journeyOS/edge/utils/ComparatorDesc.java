/*
 * Copyright (c) 2019 anqi.huang@outlook.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.journeyOS.edge.utils;


import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.edgeprovider.IEdgeProvider;
import com.journeyOS.core.database.edge.Edge;

import java.util.Comparator;

public class ComparatorDesc implements Comparator<Edge> {
    @Override
    public int compare(Edge a, Edge b) {
        return Integer.compare(CoreManager.getDefault().getImpl(IEdgeProvider.class).getPostion(a.item), CoreManager.getDefault().getImpl(IEdgeProvider.class).getPostion(b.item));
    }
}
