package advancedIrc;

import cacheTests.CacheClear;
import irc.ISentence;
import irc.Irc2;
import irc.SentenceV2;
import jvn.JvnProxy;
import jvn.JvnServerImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class AdvancedIrc {

    JFrame frame;

    public static void main(String[] argv) {
        new AdvancedIrc();
    }

    AdvancedIrc(){
        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Button create = new Button("Create an IRC");
        create.addActionListener(new createListener());

        frame.add(create);
        frame.setSize(545,201);
        frame.setVisible(true);
    }

    class createListener implements ActionListener {
        createListener(){

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("java -jar .\\SOURCES\\SAR.jar");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}
