package tests;

import irc.ISentence;
import irc.SentenceV2;
import jvn.JvnProxy;

import java.util.ArrayList;
import java.util.List;

import java.util.Objects;

public class Locks {
    static int                 nb_locks;
    static int                 times;
    static List<ISentence>     locks;

    public static void main(String args[]) {
        try {
            times = Integer.parseInt(args[1]);
            nb_locks = Integer.parseInt(args[0]);
            locks = new ArrayList<ISentence>();
            for (int i=0; i<nb_locks; i++) {
                locks.add((ISentence) JvnProxy.newInstance(new SentenceV2(), "Lock"+Integer.toString(i)));
            }
            CheckLocks();
        } catch (Exception e) {
            System.out.println("Locks problem : " + e.getMessage());
        }
    }

    private static void CheckLocks() {
        for (int i=0; i<times; i++) {
            int nb = 0;
            String var = "0";
            for (ISentence jo:locks){
                if(nb==0) {
                    jo.write(var);
                    assert(Objects.equals(jo.read(), var));
                }
                else {
                    assert(Objects.equals(jo.read(), var));
                    var = Integer.toString(nb);
                    jo.write(var);
                    assert(Objects.equals(jo.read(), var));
                }
                nb++;
            }
        }
    }
}
