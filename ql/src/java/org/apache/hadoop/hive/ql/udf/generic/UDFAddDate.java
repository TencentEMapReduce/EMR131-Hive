package org.apache.hadoop.hive.ql.udf.generic;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.ql.udf.UDFType;
import org.apache.hadoop.io.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Dino on 2017/2/26.
 *
 * 实现mysql date_add方式，传入指定的单位和数值实现日期的加减
 */
@Description(name="add_date", value="_FUNC_(dateText, pattern ,number day|month|year]) - Convert time string with given pattern to time string with 'yyyy-MM-dd' pattern\n", extended="Example:\n> SELECT _FUNC_('2011/05/01','yyyy/MM/dd','1 day') FROM src LIMIT 1;\n2011-05-02\n> SELECT _FUNC_('2011/07/21 12:55:11'.'yyyy/MM/dd HH:mm:ss','1 day') FROM src LIMIT 1;\n2011-07-22 12:55:11\n")
@UDFType(deterministic=false)
public class UDFAddDate
        extends UDF
{
    private final SimpleDateFormat standardFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat formatter = new SimpleDateFormat();
    private final Calendar calendar = Calendar.getInstance();

    public UDFAddDate()
    {
        this.standardFormatter.setLenient(false);
        this.formatter.setLenient(false);
    }

    Text result = new Text();
    Text lastPatternText = new Text();

    private Text evaluate(Text dateText, Text patternText)
    {
        if ((dateText == null) || (patternText == null)) {
            return null;
        }
        try
        {
            if (!patternText.equals(this.lastPatternText))
            {
                this.formatter.applyPattern(patternText.toString());
                this.lastPatternText.set(patternText);
            }
        }
        catch (Exception e)
        {
            return null;
        }
        try
        {
            Date date = this.formatter.parse(dateText.toString());
            this.result.set(this.standardFormatter.format(date));
            return this.result;
        }
        catch (ParseException e) {}
        return null;
    }

    Text t = new Text();

    public Text evaluate(Text dateText, Text patternText, Text intervalText)
    {
        if ((dateText == null) || (patternText == null) || (intervalText == null)) {
            return null;
        }
        this.t = evaluate(dateText, patternText);
        String[] intervals = intervalText.toString().split("\\s+");
        if (intervals.length != 2) {
            return null;
        }
        try
        {
            int nums = Integer.parseInt(intervals[0]);
            this.calendar.setTime(this.standardFormatter.parse(this.t.toString()));
            if (intervals[1].equalsIgnoreCase("day")) {
                this.calendar.add(5, nums);
            } else if (intervals[1].equalsIgnoreCase("month")) {
                this.calendar.add(2, nums);
            } else if (intervals[1].equalsIgnoreCase("year")) {
                this.calendar.add(1, nums);
            } else if (intervals[1].equalsIgnoreCase("hour")) {
                this.calendar.add(11, nums);
            } else if (intervals[1].equalsIgnoreCase("minute")) {
                this.calendar.add(12, nums);
            } else if (intervals[1].equalsIgnoreCase("second")) {
                this.calendar.add(13, nums);
            }
            Date newDate = this.calendar.getTime();
            this.result.set(this.standardFormatter.format(newDate));
            return this.result;
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            return null;
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}

