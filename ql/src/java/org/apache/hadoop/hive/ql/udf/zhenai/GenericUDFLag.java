/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hive.ql.udf.zhenai;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.UDFType;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorConverters;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorConverters.Converter;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils.ObjectInspectorCopyOption;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorUtils;

import java.util.ArrayList;

@UDFType(deterministic = false, stateful = true)
public class GenericUDFLag extends GenericUDF {
    private ObjectInspector[] argumentOIs;
    private ObjectInspector resultOI, prevHashStandardOI, valueStandardOI;
    private Object prevHash;
    private ArrayList<Object> queue = new ArrayList<Object>();
    @Override
    public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException {
        if (arguments.length<2||arguments.length>4) {
            throw new UDFArgumentException("The function accepts more than two arguments.");
        }

        argumentOIs = arguments;
        prevHashStandardOI = ObjectInspectorUtils.getStandardObjectInspector(arguments[0],ObjectInspectorCopyOption.JAVA);
        valueStandardOI =  ObjectInspectorUtils.getStandardObjectInspector(arguments[1],ObjectInspectorCopyOption.JAVA);
        resultOI=arguments[1];
        return resultOI;
    }

    @Override
    public Object evaluate(DeferredObject[] arguments) throws HiveException {
        Object hash = arguments[0].get();
        Object value = arguments[1].get();
        int offset=1;
        Object defaultValue=null;

        if (arguments.length>=3)
            offset = PrimitiveObjectInspectorUtils.getInt(arguments[2].get(),(PrimitiveObjectInspector)argumentOIs[2]);
        if (arguments.length==4)
            defaultValue=arguments[3].get();

        if (prevHash==null||ObjectInspectorUtils.compare(prevHash,prevHashStandardOI,hash,argumentOIs[0])!=0) {
            queue.clear();
        }

        queue.add(ObjectInspectorUtils.copyToStandardObject(value,argumentOIs[1],ObjectInspectorCopyOption.JAVA));
        prevHash=ObjectInspectorUtils.copyToStandardObject(hash, argumentOIs[0],ObjectInspectorCopyOption.JAVA);
        if (queue.size()==offset+1) {
            Converter converter = ObjectInspectorConverters.getConverter(valueStandardOI, resultOI);
            return converter.convert(queue.remove(0));
        }
        return defaultValue;
    }

    @Override
    public String getDisplayString(String[] children) {
        return "lag(" + StringUtils.join(children, ',') + ")";
    }
}