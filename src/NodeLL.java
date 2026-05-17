/* Classe NodeLL<T>: nó da lista ligada (LinkedList).
 */
public class NodeLL<T> {

    private T dado;
    private NodeLL<T> prox;

    public NodeLL() {
        this(null, null);
    }

    public NodeLL(T dado, NodeLL<T> prox) {
        this.dado = dado;
        this.prox = prox;
    }

    public NodeLL<T> getProx() { return prox; }

    public T getDado() { return dado; }

    public void setProx(NodeLL<T> prox) { this.prox = prox; }

    public void setDado(T dado) { this.dado = dado; }
}
