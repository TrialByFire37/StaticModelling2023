public class Constants {
  private static final int m = 5;
  private static final double[] lambdaArr = {40e-6, 10e-6, 80e-6, 30e-6, 20e-6};
  private static final double P0 = 0.995;
  private static final int T = 8760;
  private static final double epsilon = 0.0001;
  private static final double t_alpha = 2.5758293035489004;
  private static final double N = (int) (Math.pow(t_alpha, 2) * P0 * (1 - P0) / Math.pow(epsilon, 2));
  private static final int[] nArr = new int[]{2, 2, 3, 4, 2};

  public static int getM() {
    return m;
  }

  public static double[] getLambdaArr() {
    return lambdaArr;
  }

  public static double getP0() {
    return P0;
  }

  public static int getT() {
    return T;
  }

  public static double getN() {
    return N;
  }

  public static int[] getnArr() {
    return nArr;
  }
}
