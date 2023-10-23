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


public class Irc2ForBurst {
	public TextArea		text;
	public TextField	data;
	JFrame 			    frame;
	ISentence           sentence;
    static String name;
    public int          nbMessages = 10000;
    public String          number;


  /**
  * main method
  * create a JVN object nammed IRC for representing the Chat application
  **/
	public static void main(String args[]) {
	   try {
           name = "IRC2ForBurst";
           if (args.length != 0){
               name = args[0];
           }
		   ISentence jo = (ISentence) JvnProxy.newInstance( new SentenceV2(), name);
		   new Irc2ForBurst(jo, args[1]);
	   
	   } catch (Exception e) {
		   System.out.println("IRC problem : " + e.getMessage());
	   }
	}

  /**
   * IRC Constructor
   @param jo the JVN object representing the Chat
   **/
	public Irc2ForBurst(ISentence jo, String numO) {
        number = numO;
		sentence = jo;
		frame=new JFrame("Distributed object "+name);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				try {
					JvnServerImpl.jvnGetServer().jvnTerminate();
				} catch (JvnException ex) {
					System.exit(-2);
				}
			}
		});
		frame.setLayout(new GridLayout(1,1));
		text=new TextArea(10,100);
		text.setEditable(false);
		text.setForeground(Color.red);
		frame.add(text);
		data=new TextField(40);
		frame.add(data);
		Button read_button = new Button("read");
		read_button.addActionListener(new readListener2ForBurst(this));
		frame.add(read_button);
		Button write_button = new Button("write");
		write_button.addActionListener(new writeListener2ForBurst(this));
		frame.add(write_button);
        this.stressTest();
		frame.setSize(545,201);
		text.setBackground(Color.black); 
		frame.setVisible(true);
	}
    public ActionListener stressTest(){
        int count = 0;
        String s = null;
        while(count<nbMessages){
            s = this.sentence.read();
            this.data.setText(s);
            text.append(s+"\n");
            sentence.write(name+" "+number+" write : "+count);
            count++;
            s = sentence.read();
            data.setText(s);
            text.append(name+" "+number+" read : "+s+"\n");
        }
        return null;
    }
}

 /**
  * Internal class to manage user events (read) on the CHAT application
  **/
 class readListener2ForBurst implements ActionListener {
     Irc2ForBurst irc;
  
	public readListener2ForBurst (Irc2ForBurst i) {
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
 class writeListener2ForBurst implements ActionListener {
	Irc2ForBurst irc;
  
	public writeListener2ForBurst (Irc2ForBurst i) {
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



