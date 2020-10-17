package com.github.lmm1990.jtemplate;

import java.io.*;

public class FileUtil {

    /**
     * 读取文件所有内容
     *
     * @param inputStream 文件流
     * @return String 文件内容
     */
    public static String readAllText(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();
        String line;
        try(InputStreamReader streamReader = new InputStreamReader(inputStream);BufferedReader br = new BufferedReader(streamReader)) {
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\r\n");
            }
            int length = sb.length();
            if(length>0){
                sb.delete(length-2,length-1);
            }
        }catch (IOException e){

        }
        return sb.toString();
    }
}
