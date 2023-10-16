/***
 * Irc class : simple implementation of a chat using JAVANAISE
 * Contact: 
 *
 * Authors: 
 */

package irc;

import jvn.JvnException;
import jvn.JvnObject;
import jvn.JvnProxy;
import jvn.JvnServerImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;


public class Irc2 {
	public TextArea		text;
	public TextField	data;
	JFrame 			frame;
	SentenceV2       sentence;


  /**
  * main method
  * create a JVN object nammed IRC for representing the Chat application
  **/
	public static void main(String argv[]) {
	   try {
		   SentenceV2 jo = (SentenceV2) JvnProxy.newInstance((Serializable) new SentenceV2(), "IRC");
		   new Irc2(jo);
	   
	   } catch (Exception e) {
		   System.out.println("IRC problem : " + e.getMessage());
	   }
	}

  /**
   * IRC Constructor
   @param jo the JVN object representing the Chat
   **/
	public Irc2(SentenceV2 jo) {
		sentence = jo;
		frame=new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				try {
					System.out.println("Shutting down the localserver");
					JvnServerImpl.jvnGetServer().jvnTerminate();
				} catch (JvnException ex) {
					ex.printStackTrace();
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
	 }
	}
}


