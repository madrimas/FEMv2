package model;

import java.util.Vector;

class Jacobian {

    private final static LocalElement LOCAL_ELEMENT = LocalElement.getInstance();
    private final Vector<Vector<Double>> matrixJ;//macierz jacobiego
    private final Vector<Vector<Double>> invertedMatrixJ;//odwrocona macierz jacobiego
    private double detJ;//jacobian
    private int integrationPoint;// ktory punkt calkowania 0 || 1 || 2 || 3

    Jacobian(int integrationPoint, Vector<Double> x, Vector<Double> y) {
        this.integrationPoint = integrationPoint;

        matrixJ = new Vector<>();//macierz Jacobiego
        matrixJ.add(new Vector<>());
        matrixJ.add(new Vector<>());

        matrixJ.get(0).add(LOCAL_ELEMENT.getdNdXi().get(integrationPoint).get(0) * x.get(0) + LOCAL_ELEMENT.getdNdXi().get(integrationPoint).get(1)
                * x.get(1) + LOCAL_ELEMENT.getdNdXi().get(integrationPoint).get(2) * x.get(2) + LOCAL_ELEMENT.getdNdXi().get(integrationPoint).get(3) * x.get(3));
        matrixJ.get(0).add(LOCAL_ELEMENT.getdNdXi().get(integrationPoint).get(0) * y.get(0) + LOCAL_ELEMENT.getdNdXi().get(integrationPoint).get(1)
                * y.get(1) + LOCAL_ELEMENT.getdNdXi().get(integrationPoint).get(2) * y.get(2) + LOCAL_ELEMENT.getdNdXi().get(integrationPoint).get(3) * y.get(3));
        matrixJ.get(1).add(LOCAL_ELEMENT.getdNdEta().get(integrationPoint).get(0) * x.get(0) + LOCAL_ELEMENT.getdNdEta().get(integrationPoint).get(1)
                * x.get(1) + LOCAL_ELEMENT.getdNdEta().get(integrationPoint).get(2) * x.get(2) + LOCAL_ELEMENT.getdNdEta().get(integrationPoint).get(3) * x.get(3));
        matrixJ.get(1).add(LOCAL_ELEMENT.getdNdEta().get(integrationPoint).get(0) * y.get(0) + LOCAL_ELEMENT.getdNdEta().get(integrationPoint).get(1)
                * y.get(1) + LOCAL_ELEMENT.getdNdEta().get(integrationPoint).get(2) * y.get(2) + LOCAL_ELEMENT.getdNdEta().get(integrationPoint).get(3) * y.get(3));

        detJ = matrixJ.get(0).get(0) * matrixJ.get(1).get(1) - matrixJ.get(0).get(1) * matrixJ.get(1).get(0);

        invertedMatrixJ = new Vector<>();//odwrócona macierz Jacobiego
        invertedMatrixJ.add(new Vector<>());
        invertedMatrixJ.add(new Vector<>());

        invertedMatrixJ.get(0).add(matrixJ.get(1).get(1));
        invertedMatrixJ.get(0).add(-matrixJ.get(0).get(1));
        invertedMatrixJ.get(1).add(-matrixJ.get(1).get(0));
        invertedMatrixJ.get(1).add(matrixJ.get(0).get(0));
    }

    public void showJacobian() {
        System.out.println("Jakobian punktu calkowania id:" + integrationPoint);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                System.out.print(+matrixJ.get(i).get(j) + "\t");
            }
            System.out.println("");
        }
        System.out.println("Det: " + detJ + "\n");
    }

    Vector<Vector<Double>> getInvertedMatrixJ() {
        return invertedMatrixJ;
    }

    double getDetJ() {
        return detJ;
    }
}
