import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.List;

public class Main
{
    private static String symbols = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static void main(String[] args) throws Exception
    {
        FileAccess fileAccess = new FileAccess("hdfs://d0e03a147bbb:8020");
        FileSystem hdfs = fileAccess.getHdfs();
        Path file = new Path("hdfs://d0e03a147bbb:8020/test/file.txt");
        System.out.println(fileAccess.isDirectory("hdfs://d0e03a147bbb:8020/test/file.txt"));
        System.out.println(fileAccess.isDirectory("hdfs://d0e03a147bbb:8020/test/"));

        //System.out.println(fileAccess.read("hdfs://9cb0dce658f9:8020/test/file.txt"));
//        List<String> list = fileAccess.list("hdfs://d0e03a147bbb:8020/");
//        for(String str: list){
//            System.out.println(str);
//        }
//        if (hdfs.exists(file)) {
//            hdfs.delete(file, true);
//        }
//
//        OutputStream os = hdfs.create(file);
//        BufferedWriter br = new BufferedWriter(
//            new OutputStreamWriter(os, "UTF-8")
//        );
//
//        for(int i = 0; i < 100_000; i++) {
//            br.write(getRandomWord() + " ");
//        }
//
//        br.flush();
//        br.close();
        hdfs.close();
    }

    private static String getRandomWord()
    {
        StringBuilder builder = new StringBuilder();
        int length = 2 + (int) Math.round(10 * Math.random());
        int symbolsCount = symbols.length();
        for(int i = 0; i < length; i++) {
            builder.append(symbols.charAt((int) (symbolsCount * Math.random())));
        }
        return builder.toString();
    }
}
