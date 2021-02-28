package org.openjfx;
import javafx.application.Application;
import javax.swing.*;
import java.awt.*;

public class loadScreen {
    JFrame body; JLabel wallpaper = new JLabel(new ImageIcon("/Users/adilhashmi/Desktop/EECS2311/GUI Designs/Front_page1L.jpg"));
    JProgressBar bar = new JProgressBar();

    public void insertBar() {
        bar.setValue(0);
        bar.setBackground(Color.BLACK); bar.setForeground(Color.white);
        bar.setBounds(100, 250, 350, 25);
        bar.setBorderPainted(true);
        body.add(bar);
    }

    public void makeScreen() {
        body = new JFrame();
        body.setUndecorated(true);
        body.setContentPane(wallpaper);
        body.setVisible(true);
        body.setSize(700, 500);
        body.setLocationRelativeTo(null);
    }

    public void runBar() {
        int load = 0;
        while(load < 100){
            bar.setValue(load); load++;
            if(load == 100){
                body.disable();
            }

        }
    }

    loadScreen() throws Exception{
        makeScreen();
        insertBar();
        runBar();
    }

}
