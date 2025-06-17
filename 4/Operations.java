import dnl.utils.text.table.TextTable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Operations {
  private static boolean func(double[] x, double T) {
    return (((x[0] > T) && (x[1] > T)) || ((x[2] > T) && (x[3] > T)))
            && (x[4] > T)
            && (x[5] > T)
            && ((x[6] > T) || (x[7] > T) || (x[8] > T))
            && ((x[9] > T) || (x[10] > T))
            && ((x[11] > T) || (x[12] > T));
  }

  private static int minIndex(ArrayList<Double> tArr) {
    int min = 0;
    for (int i = 1; i < tArr.size(); i++) {
      if (tArr.get(i) < tArr.get(min)) {
        min = i;
      }
    }
    return min;
  }

  private static double VBR(double T, int[] LArr) {
    double d = 0;
    double N = Constants.getN();
    int M = Constants.getM();
    int[] nArr = Constants.getnArr();
    double[] lambdaArr = Constants.getLambdaArr();

    for (int k = 0; k < N; k++) {
      ArrayList<Double> xArr = new ArrayList<>();
      for (int i = 0; i < M; i++) {
        ArrayList<Double> tArr = new ArrayList<>();
        for (int j = 0; j < nArr[i]; j++) {
          tArr.add(-Math.log(ThreadLocalRandom.current().nextDouble()) / lambdaArr[i]);
        }
        for (int j = 0; j < LArr[i]; j++) {
          int l = minIndex(tArr);
          tArr.set(l, tArr.get(l) - Math.log(ThreadLocalRandom.current().nextDouble()) / lambdaArr[i]);
        }
        for (int j = 0; j < nArr[i]; j++) {
          xArr.add(tArr.get(j));
        }
      }
      double[] arr = xArr.stream().mapToDouble(a -> a).toArray();
      if (!func(arr, T)) d++;
    }

    return 1 - d / N;
  }

  public static void statBatch() {
    System.out.println("Lambdas: " + Arrays.toString(Constants.getLambdaArr()));
    int M = Constants.getM();
    int T = Constants.getT();
    double P0 = Constants.getP0();

    String[] colNamesMain;
    DecimalFormat frm = new DecimalFormat("#0.000000");
    colNamesMain = new String[]{"Combination", "P", "Number of parts"};
    List<List<String>> colDataMain = Stream.generate(ArrayList<String>::new)
            .limit(3)
            .collect(Collectors.toList());

    int[] LArr = new int[M];
    for (int i = 1; i <= 6; i++) {
      LArr[0] = i;
      for (int j = 1; j <= 6; j++) {
        LArr[1] = j;
        for (int k = 1; k <= 6; k++) {
          LArr[2] = k;
          for (int l = 1; l <= 6; l++) {
            LArr[3] = l;
            for (int z = 1; z <= 6; z++) {
              LArr[4] = z;
              double p = VBR(T, LArr);
              if (p > P0) {
                colDataMain.get(0).add(Arrays.toString(LArr));
                colDataMain.get(1).add(frm.format(p));
                colDataMain.get(2).add(Integer.toString(i + j + k + l + z));
              }
            }
          }
        }
      }
    }
    Object[][] colDataFinal = new Object[colDataMain.get(0).size()][colNamesMain.length];
    for (int i = 0; i < colDataMain.get(0).size(); i++) {
      colDataFinal[i][0] = colDataMain.get(0).get(i);
      colDataFinal[i][1] = colDataMain.get(1).get(i);
      colDataFinal[i][2] = colDataMain.get(2).get(i);
    }
    TextTable ttMain = new TextTable(colNamesMain, colDataFinal);
    ttMain.setAddRowNumbering(true);
    ttMain.setSort(2);
    ttMain.printTable();
  }
}
