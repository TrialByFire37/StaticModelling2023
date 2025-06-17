import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import dnl.utils.text.table.TextTable;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import java.util.ArrayList;
import java.util.stream.IntStream;

import static org.jfree.chart.plot.PlotOrientation.VERTICAL;

public class Tools {
  public static double M(int[] uArr) {
    return Arrays.stream(uArr)
            .average()
            .orElse(0);
  }

  public static double D(int[] uArr, double M) {
    return Arrays.stream(uArr)
            .mapToDouble(num -> Math.pow((num - M), 2))
            .average()
            .orElse(0);
  }

  private static double[] func(int[] uArr, char flag) {
    int intervals = (int) (1 + Math.log(uArr.length) / Math.log(2));
    int min = Arrays.stream(uArr).min().getAsInt();
    int max = Arrays.stream(uArr).max().getAsInt();

    double delta = (double) (max - min) / intervals;
    delta = delta < 1 ? 1. : delta > 1 ? Math.round(delta * 10) / 10.0 : delta;

    double first = (max - min) / 2. - intervals/2. * delta;
    double[] func = new double[intervals];

    if (flag == 'f') {
      for (int i = 1; i <= intervals; i++) {
        double x = first + delta * i;
        double xPrev = first + delta * (i - 1);
        func[i - 1] = Arrays.stream(uArr).filter(k -> (k < x && k >= xPrev)).count() / ((double) uArr.length * delta);
      }
    }
    else if (flag == 'F') {
      for (int i = 1; i <= intervals; i++) {
        double x = first + delta * i;
        func[i - 1] = Arrays.stream(uArr).filter(k -> (k < x)).count() / (double) uArr.length;
      }
    } else {
      throw new IllegalArgumentException("Flag must be either \"F\" or \"f\" only.");
    }

    return func;
  }

  public static void statPrint(ArrayList<int[]> list, String flag) throws IOException {
    // tables and main values
    String[] colNamesMain;
    DecimalFormat frm = new DecimalFormat("#0.000000");
    colNamesMain = switch (flag) {
      case "IRNUNI" -> new String[]{"Estimation", "IRNUNI", "Theoretical value", "Error"};
      case "IRNBIN" -> new String[]{"Estimation", "IRNBIN", "Theoretical value", "Error"};
      case "IRNGEO" -> new String[]{"Moment", "IRNGEO_1", "IRNGEO_2", "IRNGEO_3", "Theoretical value",
              "Error 1", "Error 2", "Error 3"};
      case "IRNPOI/IRNPSN" -> new String[]{"Moment", "IRNPOI", "IRNPSN", "Theoretical value", "Error 1", "Error 2"};
      default -> new String[]{};
    };
    Object[][] colDataMain = new Object[2][colNamesMain.length];
    colDataMain[0][0] = "M";
    colDataMain[1][0] = "D";

    int k = 0;
    ArrayList<Double> MList = new ArrayList<>();
    ArrayList<Double> DList = new ArrayList<>();
    ArrayList<double[]> fList = new ArrayList<>();
    ArrayList<double[]> FList = new ArrayList<>();
    for (int[] uArr: list) {
      MList.add(M(uArr));
      DList.add(D(uArr, M(uArr)));
      fList.add(func(uArr, 'f'));
      FList.add(func(uArr, 'F'));
      k++;
      colDataMain[0][k] = frm.format(MList.get(k-1));
      colDataMain[1][k] = frm.format(DList.get(k-1));
    }

    switch (flag) {
      case "IRNUNI" -> {
        colDataMain[0][k + 1] = 50.5;
        colDataMain[1][k + 1] = 833.25;
        colDataMain[0][k + 2] = frm.format(MList.get(k-1) - 50.5);
        colDataMain[1][k + 2] = frm.format(DList.get(k-1) - 833.25);
      }
      case "IRNBIN" -> {
        colDataMain[0][k + 1] = 5.0;
        colDataMain[1][k + 1] = 2.5;
        colDataMain[0][k + 2] = frm.format(MList.get(k-1) - 5.0);
        colDataMain[1][k + 2] = frm.format(DList.get(k-1) - 2.5);
      }
      case "IRNGEO" -> {
        colDataMain[0][k + 1] = 2.0;
        colDataMain[1][k + 1] = 2.0;
        colDataMain[0][k + 2] = frm.format(MList.get(k-3) - 2.0);
        colDataMain[1][k + 2] = frm.format(DList.get(k-3) - 2.0);
        colDataMain[0][k + 3] = frm.format(MList.get(k-2) - 2.0);
        colDataMain[1][k + 3] = frm.format(DList.get(k-2) - 2.0);
        colDataMain[0][k + 4] = frm.format(MList.get(k-1) - 2.0);
        colDataMain[1][k + 4] = frm.format(DList.get(k-1) - 2.0);
      }
      case "IRNPOI/IRNPSN" -> {
        colDataMain[0][k + 1] = 10.0;
        colDataMain[1][k + 1] = 10.0;
        colDataMain[0][k + 2] = frm.format(MList.get(k-2) - 10.0);
        colDataMain[1][k + 2] = frm.format(DList.get(k-2) - 10.0);
        colDataMain[0][k + 3] = frm.format(MList.get(k-1) - 10.0);
        colDataMain[1][k + 3] = frm.format(DList.get(k-1) - 10.0);
      }
    }

    TextTable ttMain = new TextTable(colNamesMain, colDataMain);
    ttMain.printTable();

    //graphs
    int counter = 1;
    for (double[] f: fList) {
      XYSeriesCollection fDataset = new XYSeriesCollection();
      XYSeries fSeries = new XYSeries("f(x) Graph");
      IntStream.range(0, f.length).forEach(j -> fSeries.add(j, f[j]));
      fDataset.addSeries(fSeries);
      JFreeChart fHistogram = ChartFactory.createHistogram(
              "f(x) Chart (N = " + Constants.N + ")",
              null,
              "f(x)",
              fDataset,
              VERTICAL,
              false,
              false,
              false
      );
      File fHistogramChart = flag.equals("IRNGEO") ?
              new File("./src/main/graphs/fHistogramChart" + Constants.N + flag + "_" + counter + ".png") :
              flag.equals("IRNPOI/IRNPSN") ?
              new File("./src/main/graphs/fHistogramChart" + Constants.N +
                      flag.split("/")[counter - 1] + ".png") :
              new File("./src/main/graphs/fHistogramChart" + Constants.N + flag + ".png");
      ChartUtils.saveChartAsPNG(fHistogramChart, fHistogram, 640, 480);
      counter++;
    }

    counter = 1;
    for (double[] F: FList) {
      XYSeriesCollection FDataset = new XYSeriesCollection();
      XYSeries FSeries = new XYSeries("F(x) Graph");
      IntStream.range(0, F.length).forEach(j -> FSeries.add(j, F[j]));
      FDataset.addSeries(FSeries);
      JFreeChart FChart = ChartFactory.createXYLineChart(
              "F(x) Chart (n = " + Constants.N + ")",
              null,
              "F(x)",
              FDataset
      );
      File FXYChart = flag.equals("IRNGEO") ?
              new File("./src/main/graphs/FXYLineChart" + Constants.N + flag + "_" + counter + ".png") :
              flag.equals("IRNPOI/IRNPSN") ?
                      new File("./src/main/graphs/FXYLineChart" + Constants.N +
                              flag.split("/")[counter - 1] + ".png") :
                      new File("./src/main/graphs/FXYLineChart" + Constants.N + flag + ".png");
      ChartUtils.saveChartAsPNG(FXYChart, FChart, 640, 480);
      counter++;
    }
  }

  public static int[] IRNUNI(int N, int ILOW, int IUP) {
    int[] uniArr = new int[N];
    for (int i = 0; i < N; i++) {
      uniArr[i] = (int) Math.floor(
              (IUP - ILOW + 1) * ThreadLocalRandom.current().nextDouble(0, 1) + ILOW);
    }

    return uniArr;
  }

  public static int[] IRNBIN(int N, int n, double p) {
    int[] binArr = new int[N];
    for (int i = 0; i < N; i++) {
      if (n < 100) {
        int r = 0;
        double pr = Math.pow(1 - p, n);
        double M = ThreadLocalRandom.current().nextDouble();

        M -= pr;
        while (M >= 0) {
          pr *= ((double) (n - r) / (r + 1)) * (p / (1 - p));
          M -= pr;
          r++;
        }
        binArr[i] = r;

      } else {
        binArr[i] = (int) Math.round(((n * p) + (ThreadLocalRandom.current().nextGaussian()) *
                (Math.sqrt((n * p) * (1.0 - p)))) + 0.5);
      }
    }
    return binArr;
  }

  public static int[] IRNGEO_1(int N, double p) {
    int[] geo1Arr = new int[N];
    for (int i = 0; i < N; i++) {
      int r = 0;
      double pr = p;
      double M = ThreadLocalRandom.current().nextDouble();

      while (M >= 0) {
        M -= pr;
        pr *= (1 - p);
        r++;
      }
      geo1Arr[i] = r;
    }
    return geo1Arr;
  }

  public static int[] IRNGEO_2(int N, double p) {
    int[] geo2Arr = new int[N];
    for (int i = 0; i < N; i++) {
      int k = 1;
      double M = ThreadLocalRandom.current().nextDouble();

      while (M > p) {
        M = ThreadLocalRandom.current().nextDouble();
        k++;
      }

      geo2Arr[i] = k;
    }
    return geo2Arr;
  }

  public static int[] IRNGEO_3(int N, double p) {
    int[] geo3Arr = new int[N];
    for (int i = 0; i < N; i++) {
      double u = ThreadLocalRandom.current().nextDouble();
      geo3Arr[i] = (int) Math.floor(Math.log(u) / Math.log(1 - p)) + 1;
    }
    return geo3Arr;
  }

  public static int[] IRNPOI(int N, double mu) {
    int[] poiArr = new int[N];
    for (int i = 0; i < N; i++) {
      if (mu < 88) {
        int r = 0;
        double M = ThreadLocalRandom.current().nextDouble();
        double Pr = Math.exp(-mu);
        M -= Pr;

        while (M >= 0) {
          r++;
          Pr *= mu / r;
          M -= Pr;
        }
        poiArr[i] = r;
      } else {
        poiArr[i] = (int) Math.round(
                (mu + ThreadLocalRandom.current().nextGaussian() * (Math.sqrt(mu)) + 0.5));
      }
    }
    return poiArr;
  }

  public static int[] IRNPSN(int N, double mu) {
    int[] psnArr = new int[N];
    for (int i = 0; i < N; i++) {
      if (mu < 88) {
        int r = 0;
        double M = ThreadLocalRandom.current().nextDouble();
        double Pr = Math.exp(-mu);

        while (M >= Pr) {
          r++;
          M *= ThreadLocalRandom.current().nextDouble();
        }
        psnArr[i] = r;
      } else {
        psnArr[i] = (int) Math.round(
                (mu + ThreadLocalRandom.current().nextGaussian() * (Math.sqrt(mu)) + 0.5));
      }
    }
    return psnArr;
  }
}
