package org.apache.hadoop.hive.ql.udf.generic;

/**
 * Created by Dino on 2017/4/19.
 */
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.serde2.io.TimestampWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Description(name="dayofweek", value="_FUNC_(dateText) - Return the weekday of date string. ", extended="Convert time string with 'yyyy-MM-dd HH:mm:ss' pattern to the weekday of week.\nExample:\n > SELECT _FUNC_ ('2014-05-27 12:05:11') FRom src LIMIT 1;\n2")
public class UDFDayOfWeek
        extends UDF
{
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private final Calendar calendar = Calendar.getInstance();
    private IntWritable result = new IntWritable();

    public UDFDayOfWeek()
    {
        this.calendar.setFirstDayOfWeek(2);
        this.calendar.setMinimalDaysInFirstWeek(4);
    }

    public IntWritable evaluate(Text dateString)
    {
        if (dateString == null) {
            return null;
        }
        try
        {
            Date date = this.formatter.parse(dateString.toString());
            this.calendar.setTime(date);
            this.result.set(this.calendar.get(7) - 1);
            return this.result;
        }
        catch (ParseException e) {}
        return null;
    }

    public IntWritable evaluate(TimestampWritable t)
    {
        if (t == null) {
            return null;
        }
        this.calendar.setTime(t.getTimestamp());
        this.result.set(this.calendar.get(7) - 1);
        return this.result;
    }
}

