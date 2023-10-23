/***
 * Irc class : simple implementation of a chat using JAVANAISE
 * Contact:
 * Authors: 
 */

package irc;

import jvn.JvnException;
import jvn.JvnProxy;
import jvn.JvnServerImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class Irc2 {
	public TextArea		text;
	public TextField	data;
	JFrame 			frame;
	ISentence       sentence;
	static String name;


  /**
  * main method
  * create a JVN object nammed IRC2 or given in argument for representing the Chat application
  **/
	public static void main(String[] argv) {
	   try {
		   name = "IRC2";
		   if (argv.length != 0){
			   name = argv[0];
		   }

		   ISentence jo = (ISentence) JvnProxy.newInstance( new SentenceV2(), name);
		   System.out.println("New instance of "+name+" distributed object");
		   new Irc2(jo);

	   } catch (Exception e) {
		   System.out.println("IRC problem : " + e.getMessage());
	   }
	}

  /**
   * IRC Constructor
   @param jo the JVN object representing the Chat
   **/
	public Irc2(ISentence jo) {
		sentence = jo;
		frame=new JFrame("Distributed object "+name);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				try {
					System.out.println("Shutting down the localserver");
					JvnServerImpl.jvnGetServer().jvnTerminate();
				} catch (JvnException ex) {
					System.exit(-2);
				}
			}
		});
		frame.setLayout(new GridLayout(1,1));
		text=new TextArea(10,60);
		text.setEditable(false);
		text.setForeground(Color.red);
		frame.add(text);
		data=new TextField(40);
		frame.add(data);
		Button read_button = new Button("read");
		read_button.addActionListener(new readListener2(this));
		frame.add(read_button);
		Button write_button = new Button("write");
		write_button.addActionListener(new writeListener2(this));
		frame.add(write_button);
		frame.setSize(545,201);
		text.setBackground(Color.black); 
		frame.setVisible(true);
	}
}


 /**
  * Internal class to manage user events (read) on the CHAT application
  **/
 class readListener2 implements ActionListener {
	Irc2 irc;
  
	public readListener2 (Irc2 i) {
		irc = i;
	}
   
 /**
  * Management of user events
  **/
	public void actionPerformed (ActionEvent e) {
	 try {
		String s = irc.sentence.read();
		// display the read value
		irc.data.setText(s);
		irc.text.append(s+"\n");
	   } catch (Exception je) {
		   System.out.println("IRC problem : " + je.getMessage());
		   je.printStackTrace();
	   }
	}
}

 /**
  * Internal class to manage user events (write) on the CHAT application
  **/
 class writeListener2 implements ActionListener {
	Irc2 irc;
  
	public writeListener2 (Irc2 i) {
        	irc = i;
	}
  
  /**
    * Management of user events
   **/
	public void actionPerformed (ActionEvent e) {
	   try {	
		// get the value to be written from the buffer
	    String s = irc.data.getText();
		irc.sentence.write(s);
	 } catch (Exception je) {
		   System.out.println("IRC problem  : " + je.getMessage());
		   je.printStackTrace();
	 }
	}
}



