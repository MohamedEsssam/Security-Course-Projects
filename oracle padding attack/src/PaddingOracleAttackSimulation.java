import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Disclaimer: 
 * This code is for illustration purposes.
 * Do not use in real-world deployments.
 */

public class PaddingOracleAttackSimulation {

    private static class Sender {
        private byte[] secretKey;
        private String secretMessage = "Top secret!";

        public Sender(byte[] secretKey) {
            this.secretKey = secretKey;
        }

        // This will return both iv and ciphertext
        public byte[] encrypt() {
            return AESDemo.encrypt(secretKey, secretMessage);
        }
    }

    private static class Receiver {
        private byte[] secretKey;

        public Receiver(byte[] secretKey) {
            this.secretKey = secretKey;
        }

        // Padding Oracle (Notice the return type)
        public boolean isDecryptionSuccessful(byte[] ciphertext) {
            return AESDemo.decrypt(secretKey, ciphertext) != null;
        }
    }

    public static class Adversary {

        // This is where you are going to develop the attack
        // Assume you cannot access the key.
        // You shall not add any methods to the Receiver class.
        // You only have access to the receiver's "isDecryptionSuccessful" only.
        public String extractSecretMessage(Receiver receiver, byte[] ciphertext) {
            byte[] iv = AESDemo.extractIV(ciphertext);
            byte[] ciphertextBlocks = AESDemo.extractCiphertextBlocks(ciphertext);

            // TODO: WRITE THE ATTACK HERE.

            //copy iv
            byte[] tempIv = Arrays.copyOfRange(iv, 0,iv.length);

            //get the break point
            int paddingStartIdx = 0;
            for (int i = 0; i < iv.length; i++) {
                tempIv[i] = (byte) (tempIv[i]^50);
                boolean isBreak = receiver.isDecryptionSuccessful(AESDemo.prepareCiphertext(tempIv, ciphertextBlocks));
                if (!isBreak){
                    paddingStartIdx = i;
                    break;
                }
            }

            //System.out.println(Arrays.toString(iv));
            for (int i = iv.length-1; i > paddingStartIdx-1; i--)
                iv[i] = (byte) (iv[i]^(iv.length-paddingStartIdx)^(iv.length-paddingStartIdx+1));

            //System.out.println(Arrays.toString(iv));

            int idx  = 0;
            int current  = iv.length-paddingStartIdx;
            int next = iv.length-paddingStartIdx+1;
            ArrayList<Character> chars = new ArrayList<Character>();
            byte[] ivCopy = Arrays.copyOfRange(iv, 0,iv.length);
            for (int i = paddingStartIdx-1; i > -1 ; i--) {
                idx++;
                for (int j = -128; j < 128; j++) {
                    int trueValue = 0;
                    iv[i] = (byte) j;
                    boolean isReturnTrue = receiver.isDecryptionSuccessful(AESDemo.prepareCiphertext(iv, ciphertextBlocks));
                    if (isReturnTrue) {
                        trueValue = j;
                        chars.add((char)(byte)(ivCopy[i]^trueValue^next));
                        break;
                    }
                }
                current++;
                next++;
                for (int k = iv.length-1; k > paddingStartIdx-idx-1; k--)
                    iv[k] = (byte) (iv[k]^(current)^(next));
                ivCopy = Arrays.copyOfRange(iv, 0,iv.length);
            }

            Collections.reverse(chars);
            StringBuilder sentence = new StringBuilder();
            for (int i = 0; i < chars.size(); i++)
                sentence.append(chars.get(i));

            return sentence.toString();
        }
    }

    public static void main(String[] args) {

        byte[] secretKey = AESDemo.keyGen();
        Sender sender = new Sender(secretKey);
        Receiver receiver = new Receiver(secretKey);

        // The adversary does not have the key
        Adversary adversary = new Adversary();

        // Now, let's get some valid encryption from the sender
        byte[] ciphertext = sender.encrypt();

        // The adversary  got the encrypted message from the network.
        // The adversary's goal is to extract the message without knowing the key.
        String message = adversary.extractSecretMessage(receiver, ciphertext);

        System.out.println("Extracted message = " + message);
    }
}