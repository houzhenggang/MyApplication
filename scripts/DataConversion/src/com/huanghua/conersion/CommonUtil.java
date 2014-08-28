package com.huanghua.conersion;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.huanghua.conersion.bean.InDataBean;
import com.huanghua.conersion.bean.OutDataBean;

public class CommonUtil {

    public static final String filterStr = "REGFLAG_DELAY";
    public static final String ARRAY_ONE = "30";
    public static final String ARRAY_TWO = "92";
    public static final String ARRAY_HEAD = "data_array";
    public static final float HEX_COUNT = 4.0f;

    public static boolean writeData(List<OutDataBean> allOutdb) {
        File file = new File("out_data.txt");
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            for (OutDataBean odb : allOutdb) {
                List<String> heads = odb.getHead();
                for (String head : heads) {
                    bw.write(head + "\n");
                }
                String foot = odb.getFoot();
                bw.write(foot + "\n\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public static OutDataBean doDataBean(InDataBean idb) {
        OutDataBean odb = new OutDataBean();
        String data = idb.getHead().replace("0x", "");
        int tempIndex = 1;
        for (String s : idb.getOther()) {
            if (tempIndex % HEX_COUNT == 0) {
                data += ",";
            }
            data += ":" + s.replace("0x", "");
            tempIndex++;
        }
        String[] data_array = data.split(",");
        data_array = descString(data_array);
        int number = idb.getNumber() + 1;
        int lenght = (int) Math.ceil(number / HEX_COUNT) + 1;
        List<String> head = new ArrayList<String>();
        for (int i = 0; i < lenght; i++) {
            String line = "data_array[" + i + "] = ";
            if (i == 0) {
                String numberHex = toNumberHex(number);
                line += toEightHex(new String[] { numberHex, ARRAY_ONE,
                        ARRAY_TWO });
            } else {
                line += toEightHex(data_array[i - 1]);
            }
            line += ";";
            head.add(line);
        }
        String foot = "dsi_set_cmdq(data_array" + "," + lenght + ",1);";
        odb.setHead(head);
        odb.setFoot(foot);
        return odb;
    }

    public static List<InDataBean> readDataFile(String path) {
        List<InDataBean> result = new ArrayList<InDataBean>();
        File file = new File(path);
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.equals("") && !line.contains(filterStr)) {
                    result.add(parseLine(line));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static InDataBean parseLine(String line) {
        line = line.replace("{", "").replace("}", "");
        String[] datas = line.split(",");
        InDataBean db = new InDataBean();
        List<String> other = new ArrayList<String>();
        for (int i = 0; i < datas.length; i++) {
            if (i == 0) {
                db.setHead(datas[i]);
            } else if (i == 1) {
                db.setNumber(Integer.parseInt(datas[i]));
            } else {
                other.add(datas[i]);
            }
        }
        db.setOther(other);
        return db;
    }

    public static String[] descString(String[] src) {
        for (int a = 0; a < src.length; a++) {
            String tempData = src[a];
            String[] temp_array = tempData.split(":");
            String new_tempData = "";
            for (int i = 0; i < temp_array.length; i++) {
                int y = temp_array.length - i - 1;
                new_tempData += temp_array[y];
            }
            src[a] = new_tempData;
        }
        return src;
    }

    public static String toEightHex(String[] hex) {
        String result = "";
        for (String s : hex) {
            result += s;
        }
        int lenght = hex.length * 2;
        switch (lenght) {
        case 2:
            result = "0x000000" + result;
            break;
        case 4:
            result = "0x0000" + result;
            break;
        case 6:
            result = "0x00" + result;
            break;
        default:
            result = "0x" + result;
            break;
        }
        return result;
    }

    public static String toEightHex(String hex) {
        String result = hex;
        int lenght = hex.length();
        switch (lenght) {
        case 2:
            result = "0x000000" + result;
            break;
        case 4:
            result = "0x0000" + result;
            break;
        case 6:
            result = "0x00" + result;
            break;
        default:
            result = "0x" + result;
            break;
        }
        return result;
    }

    public static String toNumberHex(int number) {
        String numberHex = Integer.toHexString(number).toUpperCase();
        if (numberHex.length() == 1) {
            numberHex = "0" + numberHex;
        }
        return numberHex;
    }
}
