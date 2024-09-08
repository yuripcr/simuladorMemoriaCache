package utils;
import java.util.Arrays;

public class BinaryConverter {
    public static int[] intToBinary(int value, int size) {
        if (value > Math.pow(2, size) - 1) {
            return null;
        }
        int[] bin = new int[size];
        int i = 0;
        while (value > 0 && i < size) {
            int num = value % 2;
            value = value / 2;
            bin[i] = num;
            i++;
        }
        for (int j = 0; j <= size / 2; j++) {
            int temp = bin[j];
            bin[j] = bin[size - j - 1];
            bin[size - j - 1] = temp;
        }
        return bin;
    }

    public static String intToBinaryString(int value, int size) {
        if (value > Math.pow(2, size) - 1) {
            return null;
        }
        char[] bin = new char[size];
        Arrays.fill(bin, '0');
        int i = 0;
        while (value > 0 && i < size) {
            int num = value % 2;
            value = value / 2;
            bin[i] = (String.valueOf(num)).charAt(0);
            i++;
        }
        for (int j = 0; j <= size / 2; j++) {
            char temp = bin[j];
            bin[j] = bin[size - j - 1];
            bin[size - j - 1] = temp;
        }
        return new String(bin);
    }
}
