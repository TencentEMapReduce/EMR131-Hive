package org.apache.hadoop.hive.ql.udf.generic;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by Dino on 2017/2/26.
 *
 * 将传入的字符串转化成标准格式化的日期函数
 */

public class UDFStrToDate extends UDF {
    private final SimpleDateFormat standardFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final SimpleDateFormat formatter = new SimpleDateFormat();
    private final Calendar calendar = Calendar.getInstance();

    public UDFStrToDate() {
        standardFormatter.setLenient(false);
        formatter.setLenient(false);
    }

    Text result = new Text();
    Text lastPatternText = new Text();

    public Text evaluate(Text dateText, Text patternText) {
        if (dateText == null || patternText == null) {
            return null;
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
            date = formatter.parse(dateText.toString());
            result.set(standardFormatter.format(date));
            return result;
        } catch (ParseException e) {
            return null;
        }
    }

    Text t = new Text();

    public Text evaluate(Text dateText, Text patternText, IntWritable days) {
        if (dateText == null || patternText == null || days == null) {
            return null;
        }

        t = evaluate(dateText, patternText);
        try {
            calendar.setTime(standardFormatter.parse(t.toString()));
            calendar.add(Calendar.DAY_OF_MONTH, days.get());
            Date newDate = calendar.getTime();
            result.set(standardFormatter.format(newDate));
            return result;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
