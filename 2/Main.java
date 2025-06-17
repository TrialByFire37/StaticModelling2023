import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

public class Main {
  public static void main(String[] args) throws IOException {
    System.out.println("Statistical modelling, lab No.2.\n");
    ArrayList<int[]> results = new ArrayList<>();

    while (true) {
      System.out.println("Choose the type of displacement:");
      System.out.println("1 - IRNUNI;\n2 - IRNBIN;\n3 - IRNGEO;\n4 - IRNPOI/IRNPSN;\n5 - Individual No.10;\n6 - Exit.\n");
      System.out.print("???: ");
      Scanner scanner = new Scanner(System.in);
      int readStr = scanner.nextInt();

      switch (readStr) {
        case 1 -> {
          System.out.println("IRNUNI has been selected.");
          results.add(Tools.IRNUNI(Constants.N, Constants.ILOW, Constants.IUP));
          Tools.statPrint(results, "IRNUNI");
          results.clear();
          System.out.println();
        }
        case 2 -> {
          System.out.println("IRNBIN has been selected.");
          results.add(Tools.IRNBIN(Constants.N, Constants.n, Constants.p));
          Tools.statPrint(results, "IRNBIN");
          results.clear();
          System.out.println();
        }
        case 3 -> {
          System.out.println("IRNGEO has been selected.");
          results.add(Tools.IRNGEO_1(Constants.N, Constants.p));
          results.add(Tools.IRNGEO_2(Constants.N, Constants.p));
          results.add(Tools.IRNGEO_3(Constants.N, Constants.p));
          Tools.statPrint(results, "IRNGEO");
          results.clear();
          System.out.println();
        }
        case 4 -> {
          System.out.println("IRNPOI/IRNPSN have been selected.");
          results.add(Tools.IRNPOI(Constants.N, Constants.mu));
          results.add(Tools.IRNPSN(Constants.N, Constants.mu));
          Tools.statPrint(results, "IRNPOI/IRNPSN");
          results.clear();
          System.out.println();
        }
        case 5 -> {
          System.out.println("Individual No.10 have been selected.");
          Individual.indiv(Constants.NIndiv, Constants.nIndiv, Constants.pIndiv);
          System.out.println();
        }
        case 6 -> System.exit(0);
        default -> System.out.println("Wrong input.");
      }
    }
  }
}
