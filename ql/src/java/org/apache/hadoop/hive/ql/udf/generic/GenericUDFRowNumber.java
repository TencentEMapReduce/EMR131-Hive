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

package org.apache.hadoop.hive.ql.udf.generic;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.UDFType;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils.ObjectInspectorCopyOption;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.io.LongWritable;

/**
 * GenericUDFRowNumber
 * 2017年2月26日15:17:54 create by Dino
 * 用于满足老集群中的row_number函数
 */

@UDFType(deterministic = false, stateful = true)
public class GenericUDFRowNumber extends GenericUDF {
  private final LongWritable result = new LongWritable(1);
  private ObjectInspector argumentIOs, prevHashKeyIO;
  private Object prevHashKey;
  @Override
  public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException {
    if (arguments.length!=1) {
        throw new UDFArgumentException("The function ROW_NUMBER accepts 1 argument.");
      }
    result.set(1);
    argumentIOs = arguments[0];
    prevHashKeyIO = ObjectInspectorUtils.getStandardObjectInspector(arguments[0], ObjectInspectorCopyOption.JAVA);
    return PrimitiveObjectInspectorFactory.writableLongObjectInspector;
  }

  @Override
  public Object evaluate(DeferredObject[] arguments) throws HiveException {
	  Object hashKey = arguments[0].get();
	  if (prevHashKey==null|| ObjectInspectorUtils.compare(prevHashKey, prevHashKeyIO, hashKey, argumentIOs)!=0) {
	  	  result.set(0);
	  }
	  result.set(result.get()+1);
	  prevHashKey= ObjectInspectorUtils.copyToStandardObject(arguments[0].get(), argumentIOs, ObjectInspectorCopyOption.JAVA);
	  return result;
  }

  @Override
  public String getDisplayString(String[] children) {
	  return "row_number(" + StringUtils.join(children, ',') + ")";
  }
}
