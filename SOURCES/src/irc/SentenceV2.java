/***
 * Sentence class : used for keeping the text exchanged between users
 * during a chat application
 * Contact:
 * Authors: 
 */

package irc;

import jvn.JvnAnnotation;
import jvn.JvnAnnotationType;

import java.io.Serial;

public class SentenceV2 implements ISentence {
	/**
	 *
	 */
	@Serial
	private static final long serialVersionUID = 1L;
	String 	data;

	public SentenceV2() {
		data = "";
	}

	@JvnAnnotation(name = JvnAnnotationType.WRITE)
	public void write(String text) {
		data = text;
	}

	@JvnAnnotation(name = JvnAnnotationType.READ)
	public String read() {
		return data;	
	}
	
}