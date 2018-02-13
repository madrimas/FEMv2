package model;

import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class GlobalData {

    private static GlobalData globalData;
    private final double height; //wysokość siatki
    private final double width; //szerokosc siatki
    private final int heightNodesNumber;//liczba węzłów w siatce w wysokości
    private final int widthNodesNumber;//liczba węzłów w siatce w szerokości
    private final int nodesNumber;//liczba węzłów w siatce
    private final int elementsNumber;//liczba elementów w siatce
    private final double tempStart;//temperatura w wezlach przy starcie procesu
    private final double tempIn;//stała temperatura w budynku
    private final double tau;//czas trwania całego procesu
    private final double tempEnvironment;//stała temperatura otoczenia
    private final double alpha;//współczynnik wymiany ciepła
    private final double alphaIn;//współczynnik wymiany ciepła wewnątrz
    private final double cS;//ciepło właściwe styropianu
    private final double cB;//ciepło właściwe cegły
    private final double lambdaStyrofoam;//współczynnik przewodzenia ciepła styropianu
    private final double lambdaBrick;//współczynnik przewodzenia ciepła cegły
    private final double rhoS;//gęstość styropianu
    private final double rhoB;//gęstość cegły

    private final LocalElement localElement;//element lokalny
    private final Vector<Vector<Double>> globalH;//macierz globalna współczynników układu równań H
    private final Vector<Double> globalP;//wektor globalny prawej części układu równań P
    private final double deltaTau;//zmiana czasu
    private Vector<Vector<Double>> localH;//macierz sztywności obecna(dla konkretnego elementu) współczynników układu równań H
    private Vector<Double> localP;//wektor obciążeń obecny(dla konkrentego elementu) prawej części układu równań P

    private GlobalData() throws IOException {

        FileReader dataFile = new FileReader("data/data.txt");
        StreamTokenizer reader = new StreamTokenizer(dataFile);
        List<Double> fileList = new ArrayList<>();

        int streamValue;
        while ((streamValue = reader.nextToken()) != StreamTokenizer.TT_EOF) {
            if (streamValue == StreamTokenizer.TT_NUMBER)
                fileList.add(reader.nval);
        }

        this.height = fileList.get(0);
        this.width = fileList.get(1);
        this.heightNodesNumber = fileList.get(2).intValue();
        this.widthNodesNumber = fileList.get(3).intValue();
        this.tempStart = fileList.get(4);
        this.tempIn = fileList.get(5);
        this.tau = fileList.get(6);
        this.deltaTau = fileList.get(7);
        this.tempEnvironment = fileList.get(8);
        this.alpha = fileList.get(9);
        this.alphaIn = fileList.get(10);
        this.cS = fileList.get(11);
        this.cB = fileList.get(12);
        this.lambdaStyrofoam = fileList.get(13);
        this.lambdaBrick = fileList.get(14);
        this.rhoS = fileList.get(15);
        this.rhoB = fileList.get(16);

        nodesNumber = heightNodesNumber * widthNodesNumber;
        elementsNumber = (heightNodesNumber - 1) * (widthNodesNumber - 1);

        localElement = LocalElement.getInstance();
        localH = new Vector<>();
        for (int i = 0; i < 4; i++) {
            localH.add(new Vector<>());
        }
        localP = new Vector<>();
        localP.setSize(4);
        globalH = new Vector<>();
        for (int i = 0; i < nodesNumber; i++) {
            globalH.add(new Vector<>());
        }
        globalP = new Vector<>();
        globalP.setSize(nodesNumber);
    }

    public static GlobalData getInstance() throws IOException {
        if (globalData == null) {
            globalData = new GlobalData();
        }
        return globalData;
    }

    public void dataCompute() throws IOException {

        for (int i = 0; i < nodesNumber; i++) {
            for (int j = 0; j < nodesNumber; j++) {
                globalH.get(i).add(j, 0.0);
            }
            globalP.set(i, 0.0);
        }

        double interpolatedTemp0;//temeperatura początkowa z węzłów zinterpolowana do konkrentego punktu calkowania
        double ijElementFromMatrixC;//element i j abstrakcyjnej macierzy C
        int id;//id elementu globalne np 0 5 6 1, następnie id powierzchni
        double detJ = 0;

        Grid grid = Grid.getInstance();
        Jacobian jacobian;
        Vector<Double> dNdX = new Vector<>();//przechowywanie to czego chcemy obliczyc
        dNdX.setSize(4);
        Vector<Double> dNdY = new Vector<>();//przechowywyanie tego co chcemy obliczyc
        dNdY.setSize(4);
        Vector<Double> x = new Vector<>();//wspolrzedne wezla z elementu (globalne wartosci)
        x.setSize(4);
        Vector<Double> y = new Vector<>();//wspolrzedne wezla z elementu (globalne wartosci)
        y.setSize(4);
        Vector<Double> temp0 = new Vector<>();//temperatura poczatkowa ktora sie zmienia przy iternacji
        temp0.setSize(4);


        for (int elementNumber = 0; elementNumber < elementsNumber; elementNumber++) {

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    localH.get(i).add(j, 0.0);
                }
                localP.set(i, 0.0);
            }

            for (int i = 0; i < 4; i++) {//wyciagamy dane elementu z elementow w siatce
                id = grid.elements.get(elementNumber).globalNodeID.get(i);
                x.set(i, grid.nodes.get(id).getX());
                y.set(i, grid.nodes.get(id).getY());
                temp0.set(i, grid.nodes.get(id).getTemp());
            }

            for (int integrationPoints = 0; integrationPoints < 4; integrationPoints++) {//lpc po powierzchni w jednym elemencie (sposób 2-punktowy)
                jacobian = new Jacobian(integrationPoints, x, y);
                //jacobian.showJacobian();//do testów
                interpolatedTemp0 = 0;

                for (int i = 0; i < 4; i++) {//nodesNumber w jednym elemencie skonczonym
                    dNdX.set(i, (1.0 / jacobian.getDetJ() * (jacobian.getInvertedMatrixJ().get(0).get(0) * localElement.getdNdXi().get(integrationPoints).get(i) + jacobian.getInvertedMatrixJ().get(0).get(1)
                            * localElement.getdNdEta().get(integrationPoints).get(i))));
                    dNdY.set(i, (1.0 / jacobian.getDetJ() * (jacobian.getInvertedMatrixJ().get(1).get(0) * localElement.getdNdXi().get(integrationPoints).get(i) + jacobian.getInvertedMatrixJ().get(1).get(1)
                            * localElement.getdNdEta().get(integrationPoints).get(i))));

                    interpolatedTemp0 += temp0.get(i) * localElement.getMatrixN().get(integrationPoints).get(i);
                }
                detJ = Math.abs(jacobian.getDetJ());
                for (int i = 0; i < 4; i++) {//bo 4 funkcje kształtu a mnozenie jest transponowane [N]*[N]^T
                    for (int j = 0; j < 4; j++) {
                        //(elementNumber % (widthNodesNumber - 1) == 0) && (elementNumber != 0)
                        //x.get(0) > 0.4
                        if (x.get(0) > 0.4) {
                            ijElementFromMatrixC = cS * rhoS * localElement.getMatrixN().get(integrationPoints).get(i) * localElement.getMatrixN().get(integrationPoints).get(j) * detJ;
                            localH.get(i).set(j, localH.get(i).get(j) + lambdaStyrofoam * (dNdX.get(i) * dNdX.get(j) + dNdY.get(i) * dNdY.get(j)) * detJ + ijElementFromMatrixC / deltaTau);
                            localP.set(i, localP.get(i) + ijElementFromMatrixC / deltaTau * interpolatedTemp0);
                        } else {
                            ijElementFromMatrixC = cB * rhoB * localElement.getMatrixN().get(integrationPoints).get(i) * localElement.getMatrixN().get(integrationPoints).get(j) * detJ;
                            localH.get(i).set(j, localH.get(i).get(j) + lambdaBrick * (dNdX.get(i) * dNdX.get(j) + dNdY.get(i) * dNdY.get(j)) * detJ + ijElementFromMatrixC / deltaTau);
                            localP.set(i, localP.get(i) + ijElementFromMatrixC / deltaTau * interpolatedTemp0);
                        }
                    }
                }
            }

            //warunki brzegowe
            for (int acn = 0; acn < grid.elements.get(elementNumber).getAreaContactNumber(); acn++) {
                id = grid.elements.get(elementNumber).getLocalAreaNumbers().get(acn);//id powierzchni lokalnej 0 1 2 3 (numerowanie od lewej)
                detJ = Math.sqrt(Math.pow(grid.elements.get(elementNumber).nodeVector.get((id + 3) % 4).getX() - grid.elements.get(elementNumber).nodeVector.get(id).getX(), 2)//wyliczenie dlugosci krawedzi
                        + Math.pow(grid.elements.get(elementNumber).nodeVector.get((id + 3) % 4).getY() - grid.elements.get(elementNumber).nodeVector.get(id).getY(), 2)) / 2.0;
                //nakladanie warunku brzegowego
                for (int i = 0; i < 2; i++) {//2 pc na powierzchni
                    for (int j = 0; j < 4; j++) {//4 bo transponowane
                        for (int k = 0; k < 4; k++) {
                            if (id == 2)
                                localH.get(j).set(i, localH.get(j).get(i) + alpha * localElement.getGaussIntegrationAreaPoints().get(id).node.get(i).get(j)
                                        * localElement.getGaussIntegrationAreaPoints().get(i).node.get(i).get(k) * detJ);//detJ z powierzchni
                            else
                                localH.get(j).set(i, localH.get(j).get(i) + alphaIn * localElement.getGaussIntegrationAreaPoints().get(id).node.get(i).get(j)
                                        * localElement.getGaussIntegrationAreaPoints().get(i).node.get(i).get(k) * detJ);//detJ z powierzchni
                        }
                        if (id == 2)
                            localP.set(j, localP.get(j) + alpha * tempEnvironment * localElement.getGaussIntegrationAreaPoints().get(id).node.get(i).get(j) * detJ);
                        else
                            localP.set(j, localP.get(j) + alphaIn * tempIn * localElement.getGaussIntegrationAreaPoints().get(id).node.get(i).get(j) * detJ);
                    }
                }
            }
            //agregacja (wpisanie do macierzy globalncyh)
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    globalH.get(grid.elements.get(elementNumber).globalNodeID.get(i)).set(grid.elements.get(elementNumber).globalNodeID.get(j),
                            globalH.get(grid.elements.get(elementNumber).globalNodeID.get(i)).get(grid.elements.get(elementNumber).globalNodeID.get(j)) + localH.get(i).get(j));
                }
                globalP.set(grid.elements.get(elementNumber).globalNodeID.get(i), globalP.get(grid.elements.get(elementNumber).globalNodeID.get(i)) + localP.get(i));
            }
        }
    }

    double getHeight() {
        return height;
    }

    double getWidth() {
        return width;
    }

    public int getHeightNodesNumber() {
        return heightNodesNumber;
    }

    public int getWidthNodesNumber() {
        return widthNodesNumber;
    }

    public int getNodesNumber() {
        return nodesNumber;
    }

    double getTempStart() {
        return tempStart;
    }

    public double getDeltaTau() {
        return deltaTau;
    }

    public double getTau() {
        return tau;
    }

    public Vector<Vector<Double>> getGlobalH() {
        return globalH;
    }

    public Vector<Double> getGlobalP() {
        return globalP;
    }

    public int getElementsNumber() {
        return elementsNumber;
    }
}
