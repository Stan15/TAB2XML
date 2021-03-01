package org.openjfx;
import javax.swing.*;
import java.awt.*;

public class LoadScreen {
    JFrame body; JLabel wallpaper = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("org.openjfx/image_assets/loading_page_background.jpg")));
    JProgressBar bar = new JProgressBar();

    public void insertBar() {
        bar.setValue(0);
        bar.setBackground(Color.BLACK); bar.setForeground(Color.white);
        int width = 350;
        int height = 20;
        int offset = 20;
        bar.setBounds((body.getBounds().width/2)-(width/2), body.getBounds().height-offset-height, width, height);
        bar.setBorderPainted(true);
        body.add(bar);
    }

    public static void run() {
        LoadScreen ls = new LoadScreen();
    }

    public void makeScreen() {
        body = new JFrame();
        body.setUndecorated(true);
        body.setContentPane(wallpaper);
        body.setVisible(true);
        body.setSize(700, 525);
        body.setLocationRelativeTo(null);
    }

    public void runBar() {
        int load = 0;
        try {
            while(load < 100){
                Thread.sleep(10);
                bar.setValue(load); load++;
                if(load == 100){
                    body.dispose();
                }

            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    LoadScreen() {
        makeScreen();
        insertBar();
        runBar();
    }

}
