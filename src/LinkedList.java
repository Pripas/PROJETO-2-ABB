/* Classe LinkedList<T>: lista ligada genérica usada como fila auxiliar no percurso em nível (BFS).
 */
public class LinkedList<T> {

    private NodeLL<T> head;
    private int size;

    public LinkedList() {
        head = null;
        size = 0;
    }

    public boolean isEmpty() {
        return getHead() == null;
    }

    // verifica se há memória disponível para novo nó
    public boolean isFull() {
        NodeLL<T> aux = new NodeLL<T>();
        return aux == null;
    }

    public int getSize() {
        return size;
    }

    public NodeLL<T> getHead() {
        return head;
    }

    // adiciona elemento no final da lista (enfileirar)
    public boolean addLast(T id) {
        NodeLL<T> aux;
        NodeLL<T> pAnda;
        if (!isFull()) {
            aux = new NodeLL<T>(id, null);
            if (isEmpty()) {
                head = aux;
            } else {
                pAnda = head;
                while (pAnda.getProx() != null)
                    pAnda = pAnda.getProx();
                pAnda.setProx(aux);
            }
            size++;
            return true;
        } else return false;
    }

    // remove e retorna o primeiro elemento da lista (desenfileirar)
    public T pollFirst() {
        if (isEmpty()) return null;
        else {
            NodeLL<T> pAux = head;
            head = head.getProx();
            size--;
            return pAux.getDado();
        }
    }

    @Override
    public String toString() {
        String resultado = "[ ";
        NodeLL<T> pAnda = head;
        while (pAnda != null) {
            resultado += pAnda.getDado() + " ";
            pAnda = pAnda.getProx();
        }
        resultado += "] Qtde.: " + size;
        return resultado;
    }
}
