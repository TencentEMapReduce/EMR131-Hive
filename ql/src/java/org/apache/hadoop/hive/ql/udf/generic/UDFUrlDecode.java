package org.apache.hadoop.hive.ql.udf.generic;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;

import java.net.URLDecoder;

/**
 * Created by Dino on 2017/2/26.
 *
 * 用于url解码，投放中经常需要使用
 */
public class UDFUrlDecode extends UDF {

    private final static Text w = new Text();
    public Text evaluate(Text s) {
        return getString(s);
    }

    public Text evaluate(NullWritable i) {
        return null;
    }

    public static Text getString(Text s) {
        if (s == null||"".equals(s.toString())) { return null; }
        else {
            try {
                String a = URLDecoder.decode(s.toString().replaceAll("%(?![0-9a-fA-F]{2})", "%25"), "UTF-8");
                w.set(a);
                return w;
            } catch (Exception e) {
                return null;
            }
        }
    }

    /*public static void main(String args[]) {
        String t = "%E5%A4%AA%E5%8E%9F-%E4%B8%89%E4%BA%9A%北京";
        System.out.println(getString(new Text(t)).toString());
    }*/
}