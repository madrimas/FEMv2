package model;

import java.util.Vector;

class LocalArea {
    final Vector<LocalNode> localNode;
    Vector<Vector<Double>> node;//tablica funkcji kształtu

    LocalArea(LocalNode firstNode, LocalNode secondNode) {
        localNode = new Vector<>();
        localNode.add(firstNode);
        localNode.add(secondNode);

        node = new Vector<>();
        node.add(new Vector<>());
        node.add(new Vector<>());//2 pubkty całkowania, 4 funkcje ksztaltu
    }
}
