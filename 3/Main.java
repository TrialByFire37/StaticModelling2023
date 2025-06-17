import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) throws IOException {
    System.out.println("Statistical modelling, lab No.3.\n");
    ArrayList<double[]> results = new ArrayList<>();

    while (true) {
      System.out.println("Choose the type of displacement:");
      System.out.println("1 - RNUNI;\n2 - RNNRM;\n3 - RNEXP;\n4 - RNCHIS;\n5 - RNSTUD;\n6 - Individual No.10;" +
              "\n7 - Exit.\n");
      System.out.print("???: ");
      Scanner scanner = new Scanner(System.in);
      int readStr = scanner.nextInt();

      switch (readStr) {
        case 1 -> {
          System.out.println("RNUNI has been selected.");
          results.add(Tools.RNUNI(Constants.N, Constants.A, Constants.B));
          Tools.statPrint(results, "RNUNI");
          results.clear();
          System.out.println();
        }
        case 2 -> {
          System.out.println("RNNRM has been selected.");
          results.add(Tools.RNNRM1(Constants.N));
          results.add(Tools.RNNRM2(Constants.N));
          Tools.statPrint(results, "RNNRM");
          results.clear();
          System.out.println();
        }
        case 3 -> {
          System.out.println("RNEXP has been selected.");
          results.add(Tools.RNEXP(Constants.N, Constants.beta));
          Tools.statPrint(results, "RNEXP");
          results.clear();
          System.out.println();
        }
        case 4 -> {
          System.out.println("RNCHIS have been selected.");
          results.add(Tools.generRNCHIS(Constants.N, Constants.n));
          Tools.statPrint(results, "RNCHIS");
          results.clear();
          System.out.println();
        }
        case 5 -> {
          System.out.println("RNSTUD have been selected.");
          results.add(Tools.RNSTUD(Constants.N, Constants.n));
          Tools.statPrint(results, "RNSTUD");
          results.clear();
          System.out.println();
        }
        case 6 -> {
          System.out.println("Individual No.10 have been selected.");
          Individual.indiv(Constants.NIndiv, Constants.tetaIndiv, Constants.xiIndiv, Constants.signLevIndiv);
          System.out.println();
        }
        case 7 -> System.exit(0);
        default -> System.out.println("Wrong input.");
      }
    }
  }
}
