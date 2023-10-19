package cacheTests;

import irc.ISentence;
import irc.SentenceV2;
import jvn.JvnException;
import jvn.JvnObject;
import jvn.JvnProxy;
import jvn.JvnServerImpl;

import java.util.ArrayList;
import java.util.List;

public class CacheClear {

    private final int NB_INIT_SENTENCES = 15;

    public static void main(String[] argv) {
        new CacheClear();
    }

    CacheClear(){
        createSentences(NB_INIT_SENTENCES);
        int res = getSentences(NB_INIT_SENTENCES);
        System.out.println(res + " Sentences received");
    }

    public List<ISentence> createSentences(int number){
        ISentence aSentence;
        List<ISentence> sentencesList = new ArrayList<>();
        for (int i = 0; i<number; i++){
            aSentence = (ISentence) JvnProxy.newInstance(new SentenceV2("Sentence "+i), "Sentence "+i);
            sentencesList.add(aSentence);
        }
        return sentencesList;
    }

    public int getSentences(int number){
        JvnObject jo;
        int counter = 0;
        for (int i = 0; i<number; i++){
            try {
                jo = (JvnObject) JvnServerImpl.jvnGetServer().jvnLookupObject("Sentence " + i);
            } catch (JvnException e){
                System.out.println("getSentences problem : " + e.getMessage());
                return -1;
            }
            if (jo != null ){
                counter++;
                try {
                    System.out.println("Sentence data received: "+((SentenceV2) jo.jvnGetSharedObject()).read());
                } catch (JvnException e) {
                    System.out.println("getSentences problem : " + e.getMessage());
                }
            }
        }
        return counter;

    }

}
