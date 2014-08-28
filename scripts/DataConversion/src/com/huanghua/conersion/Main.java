package com.huanghua.conersion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.huanghua.conersion.bean.InDataBean;
import com.huanghua.conersion.bean.OutDataBean;

public class Main {

    public static void main(String[] args) {
        if (args.length == 1) {
            String path = args[0];
            File file = new File(path);
            if (!file.exists()) {
                System.out.println("文件不存在！" + path);
                return;
            }
            List<InDataBean> allData = CommonUtil.readDataFile(path);
            List<OutDataBean> allOutdb = new ArrayList<OutDataBean>();
            for (InDataBean db : allData) {
                OutDataBean odb = CommonUtil.doDataBean(db);
                allOutdb.add(odb);
            }
            boolean isOk = CommonUtil.writeData(allOutdb);
            System.out.println(isOk ? "OK" : "Fail");
        } else {
            System.out.println("请输入文件路径!");
        }
    }

}
