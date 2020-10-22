package org.apache.hadoop.hive.ql.udf.generic;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

/**
 * Created by Dino on 2016/12/16.
 *
 * 临时udf 用于汉字转拼音
 * 场景：邮件内容中识别相同发音敏感词
 * 更多请查阅当前模块文档说明
 */
public class UDFHanyuToPinyin extends UDF {

    private Text result= new Text();
/*    public Text evaluate(NullWritable i) {
        return null;
    }*/

    public Text evaluate(String input,String sep){
        if(null==input){
            return null;
        }
        result.clear();
        result.set(hanyuToPinyin(input,sep));
        return result;
    }

    public Text evaluate(String input){
        if(null==input){
            return null;
        }
        result.clear();
        result.set(hanyuToPinyin(input," "));
        return result;
    }
    public Text evaluate(Text input){
        if(null==input){
            return null;
        }
        result.clear();
        result.set(hanyuToPinyin(input.toString()," "));
        return result;
    }
    private static String hanyuToPinyin(String in,String sep){
        StringBuilder pinyin =new StringBuilder();
        char[] inChar = in.toCharArray();
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        int len = inChar.length-1;
        boolean lock = false;
        for (int i=0;i<=len;i++){
            if(inChar[i]>128){ // 只区分中文
                if(lock&& i!= len){
                    pinyin.append(sep);
                    lock=false;
                }
                try{
                    pinyin.append(PinyinHelper.toHanyuPinyinStringArray(inChar[i],format)[0]);
                }catch (Exception e){
                    pinyin.append("**");
                }
                if(i != len)pinyin.append(sep);
            }else {
                pinyin.append(inChar[i]);
                if(!lock) lock=true;
            }
        }

        return pinyin.toString().replaceAll("\\s+"," ");
    }
/*    public static void main(String[] a){
        System.out.println(hanyuToPinyin("啊呢 是我的   眼泪在QQ12345678雨中   纷飞,似玻璃破碎ahahah啦啦,不在意的样子是我最后的表演"," "));
    }*/
}
