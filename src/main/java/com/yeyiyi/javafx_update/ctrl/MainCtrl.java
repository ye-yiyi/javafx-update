package com.yeyiyi.javafx_update.ctrl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.jfoenix.controls.*;

import com.yeyiyi.javafx_update.utils.*;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;

import javafx.scene.control.Label;

import javafx.scene.layout.Pane;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;


import java.net.URL;
import java.net.URLConnection;

import java.util.*;



/**
 * 主界面控制器
 *
 * @author yeyiyi
 * @date 2022/10/18 18:13
 */
@Slf4j
public class MainCtrl implements Initializable {



    // 主容器
    public Pane rootPane;


    public Label fileNum,speed;
    public JFXProgressBar progress,progress2;
    public void initialize(URL location, ResourceBundle resources) {
        log.info("initialize: {}", location.getPath());

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                boolean updateFlag = false;
                JSONArray libList = new JSONArray();
                JSONObject pure = new JSONObject();
                try {
                    String url = "填写自己的后端接口URL";
                    String ret = HttpUtil.sendGet(url,10000);
                    if(StringUtils.isNotBlank(ret)){
                        JSONObject data = new JSONObject(ret);
                        libList = data.getJSONArray("libList");

                        pure = data.getJSONObject("exe");
                        if(libList != null && pure != null){
                            updateFlag = true;
                        }

                    }

                }catch (Exception e){
                    e.printStackTrace();

                }

                JSONArray downloadLibList = new JSONArray();
                if(updateFlag){
                    //检测包是否存在

                    for(int i=0;i<libList.size();i++){
                        JSONObject lib = libList.getJSONObject(i);
                        if(lib != null){
                            String filePath = lib.getStr("filePath");//本地安装位置
//                            String fileDownloadUrl = lib.getStr("fileDownloadUrl");//云端下载链接
                            File file = new File(filePath);
                            if(!file.exists()){//不存在则添加到下载进度
                                downloadLibList.add(lib);
                            }

                        }
                    }

                    if(downloadLibList.size() > 0){
                        int size = downloadLibList.size();

                        Platform.runLater(() -> fileNum.setText("0/"+size));




                        for(int i=0;i<downloadLibList.size();i++){
                            JSONObject lib = downloadLibList.getJSONObject(i);
                            String filePath = lib.getStr("filePath");//本地安装位置
                            String fileDownloadUrl = lib.getStr("fileDownloadUrl");//云端下载链接

                            FileOutputStream fos = null;
                            InputStream ins = null;
                            try {
                                URL realUrl = new URL(fileDownloadUrl);
                                // 打开和URL之间的连接
                                URLConnection connection = realUrl.openConnection();

                                // 建立实际的连接
                                connection.connect();

                                ins = connection.getInputStream();
                                int length = connection.getContentLength();

                                File libPath = new File(filePath);
                                File parentDir = libPath.getParentFile();
                                if (!parentDir.exists()) {
                                    parentDir.mkdirs();
                                }
                                fos = new FileOutputStream(libPath);

                                int count = 0;
                                byte[] buf = new byte[1024];
                                while (true) {
                                    int numread = ins.read(buf);//返回读入的字节个数并将读到的字节内容放入buf中
                                    count += numread;
                                    float progressNum = ((float) count / length) ;//当前进度，用来更新progressBar的进度

                                    String nowSize = bytesToKBString(count);
                                    String sumSize = bytesToKBString(length);
                                    Platform.runLater(() -> speed.setText(nowSize +"/"+sumSize));
                                    
                                    //可在此处增加页面下载进度显示
                                    int finalCount = count;
                                    Platform.runLater(() -> speed.setText(finalCount +"/"+length));

                                    // 通知主线程更新下载进度
                                    Platform.runLater(() -> progress.setProgress(progressNum));


                                    if (numread <= 0) {
                                        // 下载完成通知安装
                                        int finalI = i;
                                        Platform.runLater(() -> fileNum.setText((finalI +1)+"/"+size));
                                        break;
                                    }
                                    //已经全部读入，不需要再读入字节为-1的内容
                                    fos.write(buf, 0, numread);//从fos中写入读出的字节个数到buf中
                                }


                            } catch (Exception e) {
                                System.out.println("下载异常！" );
                                Platform.runLater(() ->
                                        new DialogBuilder(fileNum)
                                                .setTitle("提示")
                                                .setMessage("下载异常")
                                                .setPositiveBtn("发生什么事了")
                                                .create()
                                );
                                e.printStackTrace();
                            }
                            // 使用finally块来关闭输入流
                            finally {
                                try {
                                    if (fos != null) {
                                        fos.close();
                                    }
                                    if (ins != null) {
                                        ins.close();
                                    }
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                }
                            }

                        }
                    }

                    if(pure != null){
                        String filePath = pure.getStr("filePath");//本地安装位置
                        String fileDownloadUrl = pure.getStr("fileDownloadUrl");//云端下载链接

                        FileOutputStream fos = null;
                        InputStream ins = null;
                        try {
                            URL realUrl = new URL(fileDownloadUrl);
                            // 打开和URL之间的连接
                            URLConnection connection = realUrl.openConnection();
                            // 建立实际的连接
                            connection.connect();

                            ins = connection.getInputStream();
                            int length = connection.getContentLength();

                            File libPath = new File(filePath);
                            fos = new FileOutputStream(libPath);

                            int count = 0;
                            byte[] buf = new byte[1024];
                            while (true) {
                                int numread = ins.read(buf);//返回读入的字节个数并将读到的字节内容放入buf中
                                count += numread;
                                float progressNum = ((float) count / length) ;//当前进度，用来更新progressBar的进度

                                String nowSize = bytesToKBString(count);
                                String sumSize = bytesToKBString(length);
                                Platform.runLater(() -> speed.setText(nowSize +"/"+sumSize));
                                
                                // 通知主线程更新下载进度
                                Platform.runLater(() -> progress2.setProgress(progressNum));


                                if (numread <= 0) {
                                    // 下载完成通知安装
                                    Platform.runLater(() ->
                                            new DialogBuilder(fileNum)
                                                    .setTitle("提示")
                                                    .setMessage("更新完成！")
                                                    .setPositiveBtn("泰裤辣，现在就运行",new DialogBuilder.OnClickListener(){

                                                        @Override
                                                        public void onClick() {
                                                            //打开目标软件关闭当前软件
                                                            ProcessBuilder processBuilder = new ProcessBuilder(filePath);
                                                            try {
                                                                processBuilder.start();
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }finally {
                                                                // 关闭当前Java进程
                                                                System.exit(0);
                                                            }



                                                        }
                                                    })
                                                    .create()
                                    );
                                    break;
                                }
                                //已经全部读入，不需要再读入字节为-1的内容
                                fos.write(buf, 0, numread);//从fos中写入读出的字节个数到buf中
                            }


                        } catch (Exception e) {
                            System.out.println("下载异常！" );
                            Platform.runLater(() ->
                                    new DialogBuilder(fileNum)
                                            .setTitle("提示")
                                            .setMessage("下载异常")
                                            .setPositiveBtn("发生什么事了")
                                            .create()
                            );
                            e.printStackTrace();
                        }
                        // 使用finally块来关闭输入流
                        finally {
                            try {
                                if (fos != null) {
                                    fos.close();
                                }
                                if (ins != null) {
                                    ins.close();
                                }
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                        }
                    }



                }else{
                    Platform.runLater(() ->
                            new DialogBuilder(fileNum)
                            .setTitle("提示")
                            .setMessage("获取版本失败")
                            .setPositiveBtn("发生什么事了")
                            .create()
                    );

                }

                return null;
            }
        };
        new Thread(task).start();





    }

    public String bytesToKBString(long bytes){
        DecimalFormat df = new DecimalFormat("#.00");
        if (bytes < 1024 * 1024) {
            double kilobytes = (double) bytes / 1024;
            return df.format(kilobytes) +" KB";
        } else {
            double megabytes = (double) bytes / (1024 * 1024);
            return df.format(megabytes) +" MB";
        }

    }
    
    public void elseSet(){
        new DialogBuilder(fileNum)
                .setTitle("提示")
                .setMessage("还没开始搞！！！")
                .setPositiveBtn("那没事了")
                .create();
    }




    public void sleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void log(String logText){
        log.info(logText);
//        String logs = logInfo.getText();
//        logs += "\r\n"+log;
//        logInfo.setText(logs);
    }



}
