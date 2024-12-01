package org.mingzi.acm;

import java.util.Scanner;

public class MainTemplate {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext()) {

            int n = scanner.nextInt();

            int[] arr = new int[n];

            for (int i = 0; i < args.length; i++) {
                arr[i] = scanner.nextInt();
            }


            int sum = 0;
            for (int num : arr) {
                num += sum;
            }

            System.out.println("Sum:" + sum);

        }
        scanner.close();
    }
}
