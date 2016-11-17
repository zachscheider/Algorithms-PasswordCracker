import java.io.*;
import java.util.*;

public class DLBTrie{

    public DLBNode rootNode;
    public DLBPassNode rootPassNode;
    public final char TERMINATOR = '.';
    public PrintWriter writer;
    public ArrayList<String> passList = new ArrayList<>();
    public StringBuilder tempStrBuilder;

    public DLBTrie(){
        //rootNode = new DLBNode('\0');
    }

    private DLBNode addSibling(DLBNode currentNode, char c){

        if(currentNode == null){
            currentNode = new DLBNode(c);
        }

        DLBNode tempNode = getSibling(currentNode, c);

        if(tempNode != null) return tempNode;

        currentNode.sibling = new DLBNode(c);
        return currentNode.sibling;
    }

    private DLBNode addChild(DLBNode currentNode, char c){

        if(currentNode.child == null){
            currentNode.child = new DLBNode(c);
            return currentNode.child;
        }

        return addSibling(currentNode.child, c);
    }

    private DLBNode getSibling(DLBNode currentNode, char c){

        while(currentNode.sibling != null && currentNode.val != c){
            currentNode = currentNode.sibling;
            if(c < currentNode.val) return null;
        }

        if(currentNode.val == c) return currentNode;

        return null;

    }

    private DLBPassNode getSibling(DLBPassNode currentNode, char c){

        while(currentNode.sibling != null && currentNode.val != c){
            currentNode = currentNode.sibling;
            if(c < currentNode.val) return null;
        }

        if(currentNode.val == c) return currentNode;

        return null;

    }

    private DLBNode getChild(DLBNode currentNode, char c){

        return getSibling(currentNode.child, c);
    }

    private DLBPassNode getChild(DLBPassNode currentNode, char c){

        return getSibling(currentNode.child, c);
    }

    public void insertPass(String s, double time){

        rootPassNode = insertPassRecursive(s, 0, rootPassNode, time);

    }

    public void insertWordWithTERMINATOR(String s){
        s = s.toLowerCase();
        s += TERMINATOR;
        rootNode = insertWordRecursive(s, 0, rootNode);
    }

    private DLBNode insertWordRecursive(String s, int level, DLBNode currentNode){

        if(level == s.length()){
            return null;
        }
        char c = s.charAt(level);

        if(currentNode == null){
            currentNode = new DLBNode(c);
            currentNode.child = insertWordRecursive(s, level + 1, currentNode.child);
        }
        if(currentNode.val == c){
            currentNode.child = insertWordRecursive(s, level + 1, currentNode.child);
        }
        if(currentNode.val < c){
            currentNode.sibling = insertWordRecursive(s, level, currentNode.sibling);
        }
        if(currentNode.val > c){
            DLBNode newNode = new DLBNode(c);
            newNode.sibling = currentNode;
            newNode.child = insertWordRecursive(s, level + 1, newNode.child);
            return newNode;
        }
        return currentNode;
    }

    private DLBPassNode insertPassRecursive(String s, int level, DLBPassNode currentNode, double time){

        if(level == s.length()){
            return null;
        }
        char c = s.charAt(level);

        if(currentNode == null){
            currentNode = new DLBPassNode(c);
            currentNode.child = insertPassRecursive(s, level + 1, currentNode.child, time);
        }
        if(currentNode.val == c){
            if(level == s.length() - 1){// && currentNode != null){
                currentNode.time = time;
            }
            currentNode.child = insertPassRecursive(s, level + 1, currentNode.child, time);
        }
        if(currentNode.val < c){
            currentNode.sibling = insertPassRecursive(s, level, currentNode.sibling, time);
        }
        if(currentNode.val > c){
            DLBPassNode newNode = new DLBPassNode(c);
            newNode.sibling = currentNode;
            newNode.child = insertPassRecursive(s, level + 1, newNode.child, time);
            return newNode;
        }
        return currentNode;
    }

    private void trieToFile(StringBuilder s, DLBNode currentNode){

        if(currentNode.val == TERMINATOR){
            writer.println(s.toString());
            return;
        }else{
            s.append(currentNode.val);
            trieToFile(s, currentNode.child);
        }       
        s.deleteCharAt(s.length() - 1);
        if(currentNode.sibling != null){
            trieToFile(s, currentNode.sibling);
        }

    }

    public String prefix(String s){
        int level;
        s = s.toLowerCase();

        DLBNode currentNode = getSibling(rootNode, s.charAt(0));
        if(currentNode == null) return null;

        for(level = 1; level < s.length(); level++){
            currentNode = getChild(currentNode, s.charAt(level));
            if(currentNode == null) break;
        }

        return s.substring(0, level);
    }

    public String prefixPass(String s){
        int level;
        s = s.toLowerCase();

        DLBPassNode currentNode = getSibling(rootPassNode, s.charAt(0));
        if(currentNode == null) return null;

        for(level = 1; level < s.length(); level++){
            currentNode = getChild(currentNode, s.charAt(level));
            if(currentNode == null) break;
        }

        return s.substring(0, level);
    }

    private DLBPassNode getLastNode(String s){
        DLBPassNode currentNode = rootPassNode;
        currentNode = getSibling(currentNode, s.charAt(0));
        if(currentNode == null) return null;

        for(int i = 1; i < s.length(); i++){
            currentNode = getChild(currentNode, s.charAt(i));
        }
        return currentNode;
    }

    public boolean checkWord(String s){

        DLBNode currentNode = rootNode;
        s = s.toLowerCase();

        currentNode = getSibling(currentNode, s.charAt(0));
        if(currentNode == null) return false;

        for(int i = 1; i < s.length(); i++){
            currentNode = getChild(currentNode, s.charAt(i));
            if(currentNode == null) return false;
        }
        return (getChild(currentNode, TERMINATOR) != null);
    }

    public double checkPass(String s){

        DLBPassNode currentNode = rootPassNode;
        s = s.toLowerCase();

        currentNode = getSibling(currentNode, s.charAt(0));
        if(currentNode == null) return -1;

        for(int i = 1; i < s.length(); i++){
            currentNode = getChild(currentNode, s.charAt(i));
            if(currentNode == null) return -1;
        }
        return currentNode.time;
    }

    public void closestPasswords(String prefix){

        DLBPassNode currentNode = getLastNode(prefix);
        StringBuilder str = new StringBuilder(prefix);
        //while(passList.size() < 10){
            nextPassword(str, currentNode);
        //}
        System.out.println(passList.toString());

    }

    private void nextPassword(StringBuilder prefix, DLBPassNode currentNode){

        //while(passList.size() < 10){
            while(prefix.length() <= 5 && currentNode.child != null){
                currentNode = currentNode.child;
                prefix.append(currentNode.val);
            }
            
            if(!passList.contains(prefix.toString())){
                passList.add(prefix.toString());
                System.out.println(prefix.toString() + " took " + currentNode.time + " ms to find.");
            }
            else{
                if(currentNode.sibling != null){
                    currentNode = currentNode.sibling;
                    prefix.setCharAt(prefix.length() - 1, currentNode.val);
                }else{
                    
                    if(tempStrBuilder == null){
                        prefix = new StringBuilder(prefix.substring(0, prefix.length() - 1).toString());
                        currentNode = getLastNode(prefix.toString()).sibling;
                    }else{
                        tempStrBuilder = new StringBuilder(tempStrBuilder.substring(0, tempStrBuilder.length() - 1).toString());
                        currentNode = getLastNode(tempStrBuilder.toString()).sibling;
                    }
                }
                nextPassword(prefix, currentNode);
            }
        //}

    }

    // if you want the file to update you must delete it first, then print
    public void printTheTrie(String filename){
        if(!(new File(filename).isFile())){
            StringBuilder str = new StringBuilder();
            try{
                writer = new PrintWriter(filename);
                trieToFile(str, rootNode);
                writer.close();
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

class DLBPassNode{

    public char val;
    public double time;
    public DLBPassNode sibling;
    public DLBPassNode child;

    public DLBPassNode(char val){
        this.val = val;
    }

    public DLBPassNode(char val, DLBPassNode sibling, DLBPassNode child){
        this.val = val;
        this.sibling = sibling;
        this.child = child;
    }

}

class DLBNode{
    
    public char val;
    public DLBNode sibling;
    public DLBNode child;

    public DLBNode(char val){
        this.val = val;
    }

    public DLBNode(char val, DLBNode sibling, DLBNode child){
        this.val = val;
        this.sibling = sibling;
        this.child = child;
    }
}

}