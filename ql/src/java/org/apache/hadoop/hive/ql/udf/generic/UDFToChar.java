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

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.serde2.ByteStream;
import org.apache.hadoop.hive.serde2.io.ByteWritable;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.hive.serde2.io.ShortWritable;
import org.apache.hadoop.hive.serde2.lazy.LazyInteger;
import org.apache.hadoop.hive.serde2.lazy.LazyLong;
import org.apache.hadoop.io.*;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * UDFToChar
 *
 * This function is an alternative to Oracle to_char function.
 */


public class UDFToChar extends UDF {
	private final SimpleDateFormat standardFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final SimpleDateFormat formatter = new SimpleDateFormat();
	private final DecimalFormat decimalFormat = new DecimalFormat();
	private ByteStream.Output out = new ByteStream.Output();

	private Text result = new Text();
	private Text lastPatternText = new Text();

	public UDFToChar() {
    standardFormatter.setLenient(false);
    formatter.setLenient(false);
	}

	public Text evaluate(NullWritable i) {
		return null;
	}
	
	public Text evaluate(ByteWritable i) {
		if (i == null) {
			return null;
		} else {
			out.reset();
			LazyInteger.writeUTF8NoException(out, i.get());
			result.set(out.getData(), 0, out.getLength());
			return result;
		}
	}
	public Text evaluate(ByteWritable i, Text format) {
		if (i == null|| format==null) {
			return null;
		} else {
			String pattern  = format.toString().replace("9", "#");
			decimalFormat.applyPattern(pattern);
			result.set(decimalFormat.format(i.get()));
			return result;
		}
	}

	public Text evaluate(ShortWritable i) {
		if (i == null) {
			return null;
		} else {
			out.reset();
			LazyInteger.writeUTF8NoException(out, i.get());
			result.set(out.getData(), 0, out.getLength());
			return result;
		}
	}
	
	public Text evaluate(ShortWritable i, Text format) {
		if (i == null|| format==null) {
			return null;
		} else {
			String pattern  = format.toString().replace("9", "#");
			decimalFormat.applyPattern(pattern);
			result.set(decimalFormat.format(i.get()));
			return result;
		}
	}

	public Text evaluate(IntWritable i) {
		if (i == null) {
			return null;
		} else {
			out.reset();
			LazyInteger.writeUTF8NoException(out, i.get());
			result.set(out.getData(), 0, out.getLength());
			return result;
		}
	}
	
	public Text evaluate(IntWritable i, Text format) {
		if (i == null|| format==null) {
			return null;
		} else {
			String pattern  = format.toString().replace("9", "#");
			decimalFormat.applyPattern(pattern);
			result.set(decimalFormat.format(i.get()));
			return result;
		}
	}

	public Text evaluate(LongWritable i) {
		if (i == null) {
			return null;
		} else {
			out.reset();
			LazyLong.writeUTF8NoException(out, i.get());
			result.set(out.getData(), 0, out.getLength());
			return result;
		}
	}
	
	public Text evaluate(LongWritable i, Text format) {
		if (i == null|| format==null) {
			return null;
		} else {
			String pattern  = format.toString().replace("9", "#");
			decimalFormat.applyPattern(pattern);
			result.set(decimalFormat.format(i.get()));
			return result;
		}
	}

	public Text evaluate(FloatWritable i) {
		if (i == null) {
			return null;
		} else {
			result.set(i.toString());
			return result;
		}
	}
	
	
	public Text evaluate(FloatWritable i, Text format) {
		if (i == null|| format==null) {
			return null;
		} else {
			String pattern  = format.toString().replace("9", "#");
			decimalFormat.applyPattern(pattern);
			result.set(decimalFormat.format(i.get()));
			return result;
		}
	}

	public Text evaluate(DoubleWritable i) {
		if (i == null) {
			return null;
		} else {
			result.set(i.toString());
			return result;
		}
	}
	
	public Text evaluate(DoubleWritable i, Text format) {
		if (i == null|| format==null) {
			return null;
		} else {
			String pattern  = format.toString().replace("9", "#");
			decimalFormat.applyPattern(pattern);
			result.set(decimalFormat.format(i.get()));
			return result;
		}
	}
	
	public Text evaluate(Text dateText, Text patternText) {
		if (dateText == null || patternText == null) {
			return null;
		}
		if (dateText.toString().trim().length()==10){
			standardFormatter.applyPattern("yyyy-MM-dd");
		}
		
		try {
			if (!patternText.equals(lastPatternText)) {
				formatter.applyPattern(patternText.toString());
				lastPatternText.set(patternText);
			}
		} catch (Exception e) {
			return null;
		}

		Date date;
		try {
			date = standardFormatter.parse(dateText.toString());
			result.set(formatter.format(date));
			return result;
		} catch (ParseException e) {
			return null;
		}
	}
	
	public Text evaluate(Text text){
		return text;
	}
	
}