package model;

import java.util.Vector;

class LocalElement {

    private static LocalElement localElement = null;
    private final Vector<LocalArea> gaussIntegrationAreaPoints = new Vector<>();//lokalne punkty calkowania dla powierzchni
    private final Vector<Vector<Double>> dNdXi;//po de eta
    private final Vector<Vector<Double>> dNdEta;//po de eta
    private final Vector<Vector<Double>> matrixN;//wartosci fukncji ksztaltu dla objetosci (dla kazdego z 4 puntkow 4 fukncje ksztaltu)

    private LocalElement() {

        gaussIntegrationAreaPoints.add(new LocalArea(new LocalNode(-1.0, 1.0 / Math.sqrt(3.0)),
                new LocalNode(-1.0, -1.0 / Math.sqrt(3.0))));
        gaussIntegrationAreaPoints.add(new LocalArea(new LocalNode(-1.0 / Math.sqrt(3.0), -1.0),
                new LocalNode(1.0 / Math.sqrt(3.0), -1.0)));
        gaussIntegrationAreaPoints.add(new LocalArea(new LocalNode(1.0, -1.0 / Math.sqrt(3.0)),
                new LocalNode(1.0, 1.0 / Math.sqrt(3.0))));
        gaussIntegrationAreaPoints.add(new LocalArea(new LocalNode(1.0 / Math.sqrt(3.0), 1.0),
                new LocalNode(-1.0 / Math.sqrt(3.0), 1.0)));

        dNdXi = new Vector<>();
        for (int i = 0; i < 4; i++) {
            dNdXi.add(new Vector<>());
        }
        dNdEta = new Vector<>();
        for (int i = 0; i < 4; i++) {
            dNdEta.add(new Vector<>());
        }
        matrixN = new Vector<>();
        for (int i = 0; i < 4; i++) {
            matrixN.add(new Vector<>());
        }

        //uzupełnienie macierzy funkcjami kształtu
        for (int i = 0; i < 4; i++) {
            Vector<LocalNode> gaussIntegrationPoints = new Vector<>();
            gaussIntegrationPoints.add(new LocalNode(-1.0 / Math.sqrt(3.0), -1.0 / Math.sqrt(3.0)));
            gaussIntegrationPoints.add(new LocalNode(1.0 / Math.sqrt(3.0), -1.0 / Math.sqrt(3.0)));
            gaussIntegrationPoints.add(new LocalNode(1.0 / Math.sqrt(3.0), 1.0 / Math.sqrt(3.0)));
            gaussIntegrationPoints.add(new LocalNode(-1.0 / Math.sqrt(3.0), 1.0 / Math.sqrt(3.0)));

            matrixN.get(i).add(getN1(gaussIntegrationPoints.get(i).getXi(), gaussIntegrationPoints.get(i).getEta()));
            matrixN.get(i).add(getN2(gaussIntegrationPoints.get(i).getXi(), gaussIntegrationPoints.get(i).getEta()));
            matrixN.get(i).add(getN3(gaussIntegrationPoints.get(i).getXi(), gaussIntegrationPoints.get(i).getEta()));
            matrixN.get(i).add(getN4(gaussIntegrationPoints.get(i).getXi(), gaussIntegrationPoints.get(i).getEta()));

            dNdXi.get(i).add(getdN1dXi(gaussIntegrationPoints.get(i).getEta()));
            dNdXi.get(i).add(getdN2dXi(gaussIntegrationPoints.get(i).getEta()));
            dNdXi.get(i).add(getdN3dXi(gaussIntegrationPoints.get(i).getEta()));
            dNdXi.get(i).add(getdN4dXi(gaussIntegrationPoints.get(i).getEta()));

            dNdEta.get(i).add(getdN1dEta(gaussIntegrationPoints.get(i).getXi()));
            dNdEta.get(i).add(getdN2dEta(gaussIntegrationPoints.get(i).getXi()));
            dNdEta.get(i).add(getdN3dEta(gaussIntegrationPoints.get(i).getXi()));
            dNdEta.get(i).add(getdN4dEta(gaussIntegrationPoints.get(i).getXi()));
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                gaussIntegrationAreaPoints.get(i).node.get(j).add(getN1(gaussIntegrationAreaPoints.get(i).localNode.get(j).getXi(), gaussIntegrationAreaPoints.get(i).localNode.get(j).getEta()));
                gaussIntegrationAreaPoints.get(i).node.get(j).add(getN2(gaussIntegrationAreaPoints.get(i).localNode.get(j).getXi(), gaussIntegrationAreaPoints.get(i).localNode.get(j).getEta()));
                gaussIntegrationAreaPoints.get(i).node.get(j).add(getN3(gaussIntegrationAreaPoints.get(i).localNode.get(j).getXi(), gaussIntegrationAreaPoints.get(i).localNode.get(j).getEta()));
                gaussIntegrationAreaPoints.get(i).node.get(j).add(getN4(gaussIntegrationAreaPoints.get(i).localNode.get(j).getXi(), gaussIntegrationAreaPoints.get(i).localNode.get(j).getEta()));
            }
        }
    }

    static LocalElement getInstance() {
        if (localElement == null)
            localElement = new LocalElement();

        return localElement;
    }

    private double getN1(double xi, double eta) {
        return 0.25 * (1 - xi) * (1 - eta);
    }

    private double getN2(double xi, double eta) {
        return 0.25 * (1 + xi) * (1 - eta);
    }

    private double getN3(double xi, double eta) {
        return 0.25 * (1 + xi) * (1 + eta);
    }

    private double getN4(double xi, double eta) {
        return 0.25 * (1 - xi) * (1 + eta);
    }

    private double getdN1dXi(double eta) {
        return -0.25 * (1 - eta);
    }


    private double getdN2dXi(double eta) {
        return 0.25 * (1 - eta);
    }

    private double getdN3dXi(double eta) {
        return 0.25 * (1 + eta);
    }

    private double getdN4dXi(double eta) {
        return -0.25 * (1 + eta);
    }

    private double getdN1dEta(double xi) {
        return -0.25 * (1 - xi);
    }

    private double getdN2dEta(double xi) {
        return -0.25 * (1 + xi);
    }

    private double getdN3dEta(double xi) {
        return 0.25 * (1 + xi);
    }

    private double getdN4dEta(double xi) {
        return 0.25 * (1 - xi);
    }

    Vector<LocalArea> getGaussIntegrationAreaPoints() {
        return gaussIntegrationAreaPoints;
    }

    Vector<Vector<Double>> getdNdXi() {
        return dNdXi;
    }

    Vector<Vector<Double>> getdNdEta() {
        return dNdEta;
    }

    Vector<Vector<Double>> getMatrixN() {
        return matrixN;
    }
}
