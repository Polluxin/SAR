package tests;

import irc.ISentence;
import irc.SentenceV2;
import jvn.JvnProxy;

import java.util.Objects;

public class Simple_test {
    public static void main(String args[]){
        try {
            String text = "simple test";
            ISentence jo = (ISentence) JvnProxy.newInstance(new SentenceV2(), "Locks");
            System.out.println("                jo created");
            System.out.println("                jo read : "+jo.read());
            jo.write(text);
            System.out.println("                jo write : "+text);
            System.out.println("                jo read : "+jo.read());
            assert Objects.equals(jo.read(), text) : "jo must read : "+text;
            System.out.println("                Test well done, you can stop the execution");
        } catch (Exception e) {
            System.out.println("Locks problem : " + e.getMessage());
        }
    }
}

