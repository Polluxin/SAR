package irc;

import jvn.JvnAnnotation;
import jvn.JvnAnnotationType;

import java.io.Serializable;

public interface ISentence extends Serializable {

    @JvnAnnotation(name = JvnAnnotationType.READ)
    String read();

    @JvnAnnotation(name = JvnAnnotationType.WRITE)
    void write(String text);

}
