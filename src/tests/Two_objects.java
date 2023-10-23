package tests;

import irc.ISentence;
import irc.SentenceV2;
import jvn.JvnProxy;

import java.util.Objects;

public class Two_objects {
    public static void main(String args[]){
        try {
            /* texts to read and write */
            String text1 = "First message";
            String text2 = "Second message";
            /* First object */
            ISentence jo1 = (ISentence) JvnProxy.newInstance(new SentenceV2(), "Obj1");
            System.out.println("                jo1 created");
            /* Second object */
            ISentence jo2 = (ISentence) JvnProxy.newInstance(new SentenceV2(), "Obj2");
            System.out.println("                jo2 created");
            System.out.println("                jo1 read : "+jo1.read());
            System.out.println("                jo1 write 'First message'");
            jo1.write(text1);
            System.out.println("                jo1 read : "+jo1.read());
            assert Objects.equals(jo1.read(), text1) : "jo1 must read : "+text1;
            System.out.println("                jo2 read : "+jo2.read());
            assert Objects.equals(jo2.read(), text1) : "jo2 must read : "+text1;
            jo2.write(text2);
            System.out.println("                jo2 write 'Second message'");
            System.out.println("                jo2 read : "+jo2.read());
            assert Objects.equals(jo2.read(), text2) : "jo2 must read : "+text2;
            System.out.println("                jo1 read : "+jo1.read());
            assert Objects.equals(jo1.read(), text2) : "jo1 must read : "+text2;
            System.out.println("                Test well done, you can stop the execution");
        } catch (Exception e) {
            System.out.println("Locks problem : " + e.getMessage());
        }
    }
}


