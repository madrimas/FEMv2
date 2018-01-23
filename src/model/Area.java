package model;

import java.util.Vector;

class Area {//jedna krawędź w elemencie
    private Vector<Node> nodes;

    Area(Node firstNode, Node secondNode) {
        nodes = new Vector<>();
        this.nodes.add(firstNode);
        this.nodes.add(secondNode);
    }

    Vector<Node> getNodes() {
        return nodes;
    }
}
