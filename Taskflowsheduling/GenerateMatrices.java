package utils;

public class GenerateMatrices {
    private static double[][] commMatrix, execMatrix;

    public GenerateMatrices() {
        commMatrix = new double[1000][1000];
        execMatrix = new double[1000][1000];
        
        for (int i=0; i<1000;i++) {
            for (int j=0; j<1000;j++) {
            	
                commMatrix[i][j] = Math.random()*10 + 1000;
                execMatrix[i][j] = Math.random()*10 + 1000;
            }
        }
    }

    public static double[][] getCommMatrix() {
        return commMatrix;
    }

    public static double[][] getExecMatrix() {
        return execMatrix;
    }
}
