CLASSPATH="../Irc2Burst.jar:../irc"
MAIN_CLASS="irc.Irc2ForBurst"

# Execution
java -cp "$CLASSPATH" "$MAIN_CLASS" "jaja" "1" &
java -cp "$CLASSPATH" "$MAIN_CLASS" "dede" "2" &
java -cp "$CLASSPATH" "$MAIN_CLASS" "sisi" "3" &
java -cp "$CLASSPATH" "$MAIN_CLASS" "toto" "4" &

wait
