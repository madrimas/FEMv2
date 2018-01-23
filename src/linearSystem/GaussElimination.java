package linearSystem;

import java.util.Vector;

public class GaussElimination {

    public Vector<Double> gaussElimination(int size, Vector<Vector<Double>> matrix, Vector<Double> vector) {
        Vector<Double> resultVector = new Vector<>();
        Vector<Vector<Double>> arrayAB = new Vector<>();

        for (int i = 0; i < size; i++) {
            arrayAB.add(new Vector<>());
            for (int j = 0; j < size; j++) {
                arrayAB.get(i).add(j, matrix.get(i).get(j));
            }
        }

        for (int i = 0; i < size; i++) {
            arrayAB.get(i).add(size, vector.get(i));
        }

        double temp;
        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                if (Math.abs(arrayAB.get(i).get(i)) < Math.pow(10, -12)) {
                    System.err.println("Błąd! Dzielnik równy 0");
                    break;
                }

                temp = -arrayAB.get(j).get(i) / arrayAB.get(i).get(i);
                for (int k = 0; k < size + 1; k++) {
                    arrayAB.get(j).set(k, arrayAB.get(j).get(k) + temp * arrayAB.get(i).get(k));
                }
            }
        }

        for (int i = 0; i < size; i++) {
            resultVector.add(0.0);
        }

        for (int i = size - 1; i >= 0; i--) {
            temp = arrayAB.get(i).get(size);
            for (int j = size - 1; j >= 0; j--) {
                temp -= arrayAB.get(i).get(j) * resultVector.get(j);
            }
            if (Math.abs(arrayAB.get(i).get(i)) < Math.pow(10, -12)) {
                System.err.println("Błąd! Dzielnik równy 0");
                break;
            }
            resultVector.set(i, temp / arrayAB.get(i).get(i));
        }

        return resultVector;
    }
}
