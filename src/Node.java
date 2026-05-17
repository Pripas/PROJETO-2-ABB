/*Classe Node<T>: nó da Árvore de Busca Binária (ABB).*/
public class Node<T extends Comparable<T>> {

    private T value;
    private Node<T> filhoEsquerdo;
    private Node<T> filhoDireito;

    public Node(T valor) {
        this.value = valor;
        this.filhoEsquerdo = null;
        this.filhoDireito  = null;
    }

    public String toString() {
        return value.toString();
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Node<T> getFilhoEsquerdo() {
        return filhoEsquerdo;
    }

    public void setFilhoEsquerdo(Node<T> filhoEsquerdo) {
        this.filhoEsquerdo = filhoEsquerdo;
    }

    public Node<T> getFilhoDireito() {
        return filhoDireito;
    }

    public void setFilhoDireito(Node<T> filhoDireito) {
        this.filhoDireito = filhoDireito;
    }
}
