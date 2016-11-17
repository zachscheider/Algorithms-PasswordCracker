import java.util.*;
import java.io.*;
import java.lang.Math.*;

public class pw_check{

    public static DLBTrie dictionary = new DLBTrie();
    public static DLBTrie passwords = new DLBTrie();
    public static final int LEN_PASS = 5;
    public static char[] l = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
    'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    public static char[] n = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    public static char[] s = new char[]{'!', '$', '*', '@', '^', '_'};
    public static char[][] leetLetters = new char[][]{
        {'a', 'e', 'i', 'l', 'o', 's', 't'},
        {'4', '3', '1', '1', '0', '$', '7'}
    };
    public static ArrayList<String> permutations = new ArrayList<>();
    public static PrintWriter writer;
    public static long t0;

    public static void main(String[] args){

        if(args.length == 0){
            System.out.println("Valid argumends are: -find and -check");
            System.exit(0);
        }
        if(args[0].equals("-find")){
            populateDictionary();
            System.out.println("Currently finding passwords...");
            generateAllPasswords();
        }else if(args[0].equals("-check")){

            System.out.println("Loading passwords into memory...");
            loadPasswords();
            checkMethod();

        }else{
            System.out.println("Valid argumends are: -find and -check");
        }
        
    }

    private static Boolean askToCheck(){

        char response = getInputString("\nWould you like to check a password? ").charAt(0);

        if(response == 'y'){
            return true;
        }else if(response == 'n'){
            return false;
        }else{
            System.out.println("Please enter yes or no.");
            askToCheck();
        }
        return false;
    }

    private static void checkMethod(){
        if(askToCheck()){
            String passGuess = getInputString("Enter a password: ");
            double passwordTime = passwords.checkPass(passGuess);
            if(passwordTime > 0){
                System.out.println("\n" + passGuess + "is a valid password.");
                System.out.println("It took " + passwordTime + " ms to find.");
            }else{
                System.out.println("\n" + passGuess + " is not a valid password. Here are 10 valid ones:\n");
                passwords.closestPasswords(passwords.prefixPass(passGuess));
            }
            checkMethod();
        }
    }

    // gets a string entered through the keyboard
    public static String getInputString(String s1){
        Scanner sc = new Scanner(System.in);
        System.out.println(s1);
        String s2 = sc.nextLine();
        return s2;
    }

    // adds all of the words from the file entered, and their 1337 alternatives, into the dictionary trie
    public static void populateDictionary(){

        String word;
        try{
            //String fileName = getInputString("What is the file name? ");
            File file = new File("dictionary.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));

            while((word = br.readLine().toLowerCase()) != null){
                make_1337(word);
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    // takes a string and adds it and all 1337 permutations to the dictionary trie
    public static void make_1337(String s){

        boolean[] is_1337 = new boolean[s.length()];
        int how_1337 = 0;

        // puts true into array if the char at that location has a 1337 alternative
        for(int i = 0; i < s.length(); i++){
            for(int j = 0; j < leetLetters[0].length; j++){
                if(leetLetters[0][j] == s.charAt(i)){
                    is_1337[i] = true;
                    how_1337++;
                    break;
                }
            }
        }
        
        if(how_1337 > 0){
            // creates a string of 0s for the number of 1337 bits
            String numBits = "";
            for(int i = 0; i < how_1337; i++){
                numBits += "0";
            }

            // stores the position of 1337 chars in an array
            int[] pos_1337 = new int[how_1337];
            int temp = 0;
            for(int i = 0; i < is_1337.length; i++){
                if(is_1337[i]) pos_1337[temp++] = i;
            }
            
            for(int i = 0; i < (int) Math.pow(2, how_1337); i++){
                String binaryRepUnpadded = Integer.toBinaryString(i);
                String binaryRep = numBits.substring(binaryRepUnpadded.length()) + binaryRepUnpadded;
                String copyOfS = s;

                for(int j = 0; j < binaryRep.length(); j++){
                    if(binaryRep.charAt(j) == '1'){

                        char replacement = '`';
                        for(int k = 0; k < leetLetters[0].length; k++){
                            if(leetLetters[0][k] == s.charAt(pos_1337[j])){
                                replacement = leetLetters[1][k];
                                break;
                            }
                        }
                        copyOfS = copyOfS.substring(0, pos_1337[j]) + replacement + copyOfS.substring(pos_1337[j]+1);
                    }
                }
                dictionary.insertWordWithTERMINATOR(copyOfS);
            }
        } else{
            dictionary.insertWordWithTERMINATOR(s);
        }
    }

    private static void generateAllPasswords(){
        try{
            permutate("", "lllns");
            permutate("", "llnns");
            permutate("", "llnss");
            permutate("", "lnnss");
            permutations.sort(new StringComparator());

            t0 = System.nanoTime();
            writer = new PrintWriter("all_passwords.txt");

            for(int i = 0; i < permutations.size(); i++){
                StringBuilder temp = new StringBuilder();
                generatePasswords(temp, permutations.get(i));
            }
            writer.close();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }     
    }

    private static void genAllForForm(){
        for(int i = 0; i < permutations.size(); i++){
            StringBuilder temp = new StringBuilder();
            generatePasswords(temp, permutations.get(i));
        }
        permutations.clear();
    }

    // returns the correct array whenever the corresponding character is passed in
    private static char[] subAlphabet(char c){
        switch(c){
            case 'l': return l;
            case 'n': return n;
            case 's': return s;
            default: return null;
        }
    }

    // Takes a string and adds all of it's permutations to an ArrayList
    private static void permutate(String prefix, String s){
        int len = s.length();
        if(len == 0){
            if(!permutations.contains(prefix)){
                permutations.add(prefix);
            }
        }else{
            for(int i = 0; i < len; i++){
                permutate(prefix + s.charAt(i), s.substring(0, i) + s.substring(i+1, len));
            }
        }
    }

    // generates all passwords of the form passed in
    private static void generatePasswords(StringBuilder partial, String thePosistions){
        do{
            if(isValidPartial(partial.toString())){
                if(partial.length() < thePosistions.length()){
                    partial.append(subAlphabet(thePosistions.charAt(partial.length()))[0]);
                }else{
                    //passwords.insertWordWithTERMINATOR(partial.toString());
                    writer.println(appendTime(partial));
                    partial = incrementStr(partial, thePosistions);
                }
            }else{
                partial = incrementStr(partial, thePosistions);
            }
        } while(!isLastPass(partial.toString(), thePosistions));
        //passwords.insertWordWithTERMINATOR(partial.toString());
        writer.println(appendTime(partial));
    }

    private static String appendTime(StringBuilder partial){
        double time = (System.nanoTime() - t0) / 1000000.0;
        String temp = partial.toString() + "," + time;
        return temp;
    }

    // increments a string while maintaining the form passed in
    private static StringBuilder incrementStr(StringBuilder partial, String thePosistions){
        for(int i = partial.length() - 1; i >= 0; i--){
            char[] arr = subAlphabet(thePosistions.charAt(i));
            char c = partial.charAt(i);
            if(c < arr[arr.length - 1]){
                if(arr.length > s.length){
                    partial.setCharAt(i,(char) (partial.charAt(i)+1));
                }else{
                    // replace with next char in symbols array
                    int index = 0;
                    for(index = 0; index < s.length - 1; index++){
                        if(c == s[index]) break;
                    }
                    partial.setCharAt(i, s[index+1]);
                }
                // reset chars below
                for(int j = partial.length() - 1; j > i; j--){
                    partial.setCharAt(j, subAlphabet(thePosistions.charAt(j))[0]);
                }
                return partial;
            }
        }
        return partial;
    }

    // checks to see if a password is the last password of the form entered
    private static boolean isLastPass(String partial, String thePosistions){
        if(partial.length() < thePosistions.length()) return false;
        for(int i = 0; i < partial.length(); i++){
            char[] arr = subAlphabet(thePosistions.charAt(i));
            char c = partial.charAt(i);
            if(c < arr[arr.length - 1]) return false;
        }
        return true;

    }

    // checks to see if one word is contained in another
    private static boolean contains(String whole, String part){
        if(part.length() == whole.length() && part.equals(whole)) return true;
        if(whole.length() > part.length()){
            int diff = whole.length() - part.length();
            for(int i = 0; i <= diff; i++){
                if(part.equals(whole.substring(i, part.length() + i))) return true;
            }
        }
        return false;
    }

    // checks to see if a partial word follows the rules of being a valid part of password
    private static boolean isValidPartial(String partial){
        for(int i = 0; i < partial.length(); i++){
            for(int j = i+1; j <= partial.length(); j++){
                if(dictionary.checkWord(partial.substring(i, j))) return false;
            }
        }
        return true;
    }

    private static void loadPasswords(){
        String line, pass;
        double time;
        try{
            //String fileName = getInputString("What is the file name? ");
            File file = new File("all_passwords.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));

            while((line = br.readLine().toLowerCase()) != null){
                pass = line.substring(0, line.indexOf(','));
                time = Double.parseDouble(line.substring(line.indexOf(',') + 1));
                passwords.insertPass(pass, time);
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}

class StringComparator implements Comparator<String> {
    public int compare(String str1, String str2){
        if(str1 == str2) return 0;
        if(str1 == null) return -1;
        if(str2 == null) return 1;
        return str1.compareTo(str2);
    }
}