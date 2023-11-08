package com.yeyiyi.javafx_update;


import cn.hutool.core.io.resource.ResourceUtil;
//import com.teamdev.jxbrowser.chromium.be;

import com.yeyiyi.javafx_update.config.AppConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;


import java.net.*;

import java.util.Enumeration;

@Slf4j
public class App extends Application {

    public static void main(String[] args) throws InterruptedException {
        launch(args);
    }



    @Override
    public void init() throws Exception {
        log.info("init");
        super.init();
    }

    public void start(Stage stage) throws Exception {
        log.info("start");


        // 加载并创建主场景
        Parent root = FXMLLoader.load(ResourceUtil.getResource("fxml/NewMain.fxml"));
        Scene scene = new Scene(root, AppConfig.stageWidth, AppConfig.stageHeight);

        stage.setOnCloseRequest(event -> {
            System.out.println("应用程序正在关闭");
            // 在此处执行关闭应用程序前的清理工作等

        });

        // 设置窗口信息
        stage.setTitle(AppConfig.title);
        stage.setResizable(AppConfig.stageResizable);
        stage.getIcons().add(new Image(ResourceUtil.getStream(AppConfig.icon)));
        stage.setScene(scene);
        stage.show();


    }

    @Override
    public void stop() throws Exception {
        log.info("stop");
        super.stop();
    }



}
