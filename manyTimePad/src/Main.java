import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        var cipherTexts = readInput(new FileInputStream("/home/mohamedessam/Desktop/manyTimePad/src/cipherTexts"));
        Map<String, Integer> spaceCharacters = new HashMap<>();
        Map<String, Integer> characters = new HashMap<>();
        String  [][]charArr = new String[cipherTexts.get(0).size()][cipherTexts.get(0).size()];
        boolean flag = false;

        for (int i = 0; i < cipherTexts.get(0).size(); i++) {
            for (int j = 0; j < cipherTexts.size(); j++) {
                var current  = cipherTexts.get(j).get(i);
                for (int k = 0; k < cipherTexts.size(); k++) {
                    var next = cipherTexts.get(k).get(i);
                    if (current.equals(next))
                        continue;
                    if ((Integer.parseInt(current, 16) ^ Integer.parseInt(next, 16))>= 65) {
                        if (!characters.containsKey(current ) || !flag ) {
                            spaceCharacters.putIfAbsent(current, i);
                            spaceCharacters.putIfAbsent(next, i);
                            //System.out.println(xor(xor(fromHexToString(current),fromHexToString(next))," ")+" "+i+" "+" "+k);
                            charArr[k][i] = (xor(xor(fromHexToString(current),fromHexToString(next))," "));
                        }
                    } else {
                        characters.put(current, i);
                        flag = true;
                        charArr[k][i] = "#";
                        break;
                    }
                }
            }
            flag = false;
        }

        var tempArr = Arrays.copyOfRange(charArr,0,6);
        for (int i = 0; i < tempArr.length; i++) {
            for (int j = 0; j < tempArr[0].length; j++)
                if (tempArr[i][j]==null)
                    tempArr[i][j] = "#";
        }
        System.out.println(Arrays.deepToString(tempArr).replace("], ", "]\n").replace("[[", "[").replace("]]", "]"));

    }

    public static String xor(String s1, String s2){
        StringBuilder xorResult = new StringBuilder();
        for(int i = 0; i < s1.length(); i++)
            xorResult.append((char)(s1.charAt(i) ^ s2.charAt(i % s2.length())));

        return xorResult.toString();
    }
    public static String fromHexToString(String arg) {
        String str = "";
        for(int i=0;i<arg.length();i+=2)
        {
            String s = arg.substring(i, (i + 2));
            int decimal = Integer.parseInt(s, 16);
            str = str + (char) decimal;
        }
        return str;
    }
    public static String fromStringToHexa(String str){
        char ch[] = str.toCharArray();
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < ch.length; i++) {
            String hexString = Integer.toHexString(ch[i]);
            sb.append(hexString);
        }
        return sb.toString();
    }
    public static List<List<String>>readInput(FileInputStream fileInputStream) throws IOException {
        List<String> cipherTexts = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));

        String strLine;
        while ((strLine = reader.readLine()) != null)   {
            cipherTexts.add(strLine);
        }
        fileInputStream.close();

        List<List<String>> byteCipherTexts = new ArrayList<>();
        List<String> tempList;
        for (int i = 0; i < cipherTexts.size(); i++) {
            tempList = new ArrayList<>();
            for (int j = 0; j < cipherTexts.get(i).length(); j += 2) {
                String s = cipherTexts.get(i).substring(j, (j + 2));
                tempList.add(s);
            }
            byteCipherTexts.add(tempList);
        }

        return byteCipherTexts;
    }
    public static String hexToBin(String s) {
        return new BigInteger(s, 16).toString(2);
    }
    public static String binXor(String str1, String str2){
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < str1.length(); i++)
            sb.append((str1.charAt(i) ^ str2.charAt(i)));

        return sb.toString();
    }
    public static String binToString(String str){
        int charCode = Integer.parseInt(str, 2);

        return new Character((char)charCode).toString();
    }
}
