package irc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class IrcManipulator {

    JFrame frame;
    TextField objName;

    public static void main(String[] argv) {
        new IrcManipulator();
    }

    IrcManipulator(){
        frame = new JFrame();
        frame.setLayout(new GridLayout());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Button create = new Button("Create an IRC");
        create.addActionListener(new createListener());

        objName = new TextField();


        frame.add(objName);
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
                runtime.exec("java -jar .\\SOURCES\\Irc2.jar "+objName.getText());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}
