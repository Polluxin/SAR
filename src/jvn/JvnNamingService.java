package jvn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JvnNamingService {

    private HashMap<Integer, List<String>> stringsFromId;
    private HashMap<String, Integer> idFromString;

    JvnNamingService() {
        stringsFromId = new HashMap<>();
        idFromString = new HashMap<>();
    }

    Integer getId(String name){
        return idFromString.get(name);
    }

    List<String> getStrings(Integer id){
        return stringsFromId.get(id);
    }

    boolean containsString(String name){
        return idFromString.containsKey(name);
    }

    void addName(String name, Integer id) throws JvnException {
        // Verify that the name is not already used
        if (idFromString.containsKey(name))
            throw new JvnException("JvnNamingService.addName: alias "+name+" already used");
        if (stringsFromId.containsKey(id)){
            stringsFromId.get(id).add(name);
        }
        else {
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(name);
            stringsFromId.put(id, arrayList);
        }
        idFromString.put(name, id);
    }

    void removeName(String name, Integer id){
        idFromString.remove(name);
        if (stringsFromId.containsKey(id)){
            stringsFromId.get(id).remove(name);
        }
    }

    void clearNames(Integer id){
        for (String s: stringsFromId.get(id)){
            idFromString.remove(s);
        }
        stringsFromId.remove(id);
    }

}
