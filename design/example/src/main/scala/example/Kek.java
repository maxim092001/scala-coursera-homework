package example;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Grankin Maxim (maximgran@gmail.com) at 17:18 15.11.2020
 */
public class Kek {

    public static void main(String[] args) {
        int[] kek = new int[]{1, 2, 3};
        String q = Arrays.stream(kek).mapToObj(Integer::toString).collect(Collectors.joining(":"));
        System.out.println(q);
    }
}
