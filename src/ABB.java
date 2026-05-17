/* Classe ABB<T>: implementa a Árvore de Busca Binária genérica.
 * A chave de comparação é definida pelo compareTo() do tipo T (campo "id" no projeto).
 * Percursos: emOrdem, preOrdem, posOrdem, emNivel (BFS com fila auxiliar LinkedList).
 */
public class ABB<T extends Comparable<T>> {

    private Node<T> raiz;

    public ABB() {
        raiz = null;
    }

    public boolean isEmpty() {
        return (raiz == null);
    }

    public void setRaiz(Node<T> araiz) {
        raiz = araiz;
    }

    public Node<T> getRaiz() {
        return raiz;
    }

    // insere valor na ABB (versão recursiva)
    public T inserir(T valor) {
        try {
            Node<T> novo = new Node<>(valor);
            raiz = inserir(novo, raiz);
            return valor;
        } catch (Exception e) {
            return null;
        }
    }

    private Node<T> inserir(Node<T> novo, Node<T> atual) {
        if (atual == null) {
            return novo;
        }
        if (compara(novo.getValue(), atual.getValue()) < 0) {
            atual.setFilhoEsquerdo(inserir(novo, atual.getFilhoEsquerdo()));
        } else {
            atual.setFilhoDireito(inserir(novo, atual.getFilhoDireito()));
        }
        return atual;
    }

    // busca iterativa com contagem de comparações; exibe resultado na tela
    public void buscar(T e) {
        int comparacoes = 0;
        Node<T> atual = raiz;
        while (atual != null) {
            comparacoes++;
            int cmp = compara(e, atual.getValue());
            if (cmp == 0) {
                System.out.println("\n--- Resultado da Busca ---");
                System.out.println("Comparações realizadas: " + comparacoes);
                System.out.println(atual.getValue().toString());
                return;
            } else if (cmp < 0) {
                atual = atual.getFilhoEsquerdo();
            } else {
                atual = atual.getFilhoDireito();
            }
        }
        System.out.println("\n--- Resultado da Busca ---");
        System.out.println("Comparações realizadas: " + comparacoes);
        System.out.println("Programa não encontrado na árvore.");
    }

    // remove o elemento e da ABB
    public boolean eliminar(T e) {
        return eliminar(raiz, null, e);
    }

    // remoção recursiva: trata nó folha, um filho e dois filhos (predecessor)
    private boolean eliminar(Node<T> node, Node<T> paiRaiz, T e) {
        Node<T> aux;
        if (node == null) {
            return false;
        } else {
            if (compara(e, node.getValue()) == 0) {
                aux = node;
                if (node.getFilhoEsquerdo() == null && node.getFilhoDireito() == null) {
                    // caso 1: nó folha
                    if (paiRaiz == null) {
                        setRaiz(null);
                    } else {
                        if (paiRaiz.getFilhoEsquerdo() != null &&
                                compara(paiRaiz.getFilhoEsquerdo().getValue(), e) == 0) {
                            paiRaiz.setFilhoEsquerdo(null);
                        } else if (paiRaiz.getFilhoDireito() != null &&
                                compara(paiRaiz.getFilhoDireito().getValue(), e) == 0) {
                            paiRaiz.setFilhoDireito(null);
                        }
                    }
                } else if (node.getFilhoDireito() == null) {
                    // caso 2a: só tem filho esquerdo
                    if (paiRaiz != null) {
                        if (paiRaiz.getFilhoEsquerdo() != null &&
                                compara(paiRaiz.getFilhoEsquerdo().getValue(), e) == 0) {
                            paiRaiz.setFilhoEsquerdo(node.getFilhoEsquerdo());
                        } else {
                            paiRaiz.setFilhoDireito(node.getFilhoEsquerdo());
                        }
                    } else {
                        node.setValue(node.getFilhoEsquerdo().getValue());
                        node.setFilhoEsquerdo(node.getFilhoEsquerdo().getFilhoEsquerdo());
                        node.setFilhoDireito(node.getFilhoEsquerdo().getFilhoDireito());
                    }
                } else if (node.getFilhoEsquerdo() == null) {
                    // caso 2b: só tem filho direito
                    if (paiRaiz != null) {
                        if (paiRaiz.getFilhoEsquerdo() != null &&
                                compara(paiRaiz.getFilhoEsquerdo().getValue(), e) == 0) {
                            paiRaiz.setFilhoEsquerdo(node.getFilhoDireito());
                        } else {
                            paiRaiz.setFilhoDireito(node.getFilhoDireito());
                        }
                    } else {
                        node.setValue(node.getFilhoDireito().getValue());
                        node.setFilhoEsquerdo(node.getFilhoDireito().getFilhoEsquerdo());
                        node.setFilhoDireito(node.getFilhoDireito().getFilhoDireito());
                    }
                } else {
                    // caso 3: dois filhos — substitui pelo predecessor (maior da subárvore esquerda)
                    aux = getMax(node.getFilhoEsquerdo(), node);
                    node.setValue(aux.getValue());
                }
                aux = null;
                return true;
            } else {
                if (compara(e, node.getValue()) < 0) {
                    return eliminar(node.getFilhoEsquerdo(), node, e);
                } else {
                    return eliminar(node.getFilhoDireito(), node, e);
                }
            }
        }
    }

    // percurso em ordem (Esq → Raiz → Dir); retorna String com os valores
    public String emOrdem() {
        return emOrdem(raiz);
    }

    public String emOrdem(Node<T> no) {
        if (no == null) return "";
        String resultado = "";
        resultado += emOrdem(no.getFilhoEsquerdo());
        resultado += no.getValue() + " ";
        resultado += emOrdem(no.getFilhoDireito());
        return resultado;
    }

    // percurso pré-ordem (Raiz → Esq → Dir)
    public void preOrdem() {
        preOrdem(raiz);
    }

    public void preOrdem(Node<T> no) {
        if (no != null) {
            System.out.println(no.getValue());
            preOrdem(no.getFilhoEsquerdo());
            preOrdem(no.getFilhoDireito());
        }
    }

    // percurso pós-ordem (Esq → Dir → Raiz)
    public void posOrdem() {
        posOrdem(raiz);
    }

    public void posOrdem(Node<T> no) {
        if (no != null) {
            posOrdem(no.getFilhoEsquerdo());
            posOrdem(no.getFilhoDireito());
            System.out.println(no.getValue());
        }
    }

    // percurso em nível / BFS usando LinkedList como fila auxiliar
    public void emNivel() {
        Node<T> noAux;
        LinkedList<Node<T>> fila = new LinkedList<Node<T>>();
        fila.addLast(raiz);
        while (!fila.isEmpty()) {
            noAux = fila.pollFirst();
            if (noAux.getFilhoEsquerdo() != null) {
                fila.addLast(noAux.getFilhoEsquerdo());
            }
            if (noAux.getFilhoDireito() != null) {
                fila.addLast(noAux.getFilhoDireito());
            }
            System.out.println(noAux.getValue());
        }
    }

    // compara dois objetos genéricos usando compareTo
    private int compara(T ob1, T ob2) {
        return ob1.compareTo(ob2);
    }

    // retorna o nó com menor valor a partir de um nó
    public Node<T> getMenor(Node<T> node) {
        if (isEmpty()) return null;
        if (node.getFilhoEsquerdo() == null) return node;
        else return getMenor(node.getFilhoEsquerdo());
    }

    // retorna o nó com maior valor a partir de um nó
    public Node<T> getMaior(Node<T> node) {
        if (isEmpty()) return null;
        if (node.getFilhoDireito() == null) return node;
        else return getMaior(node.getFilhoDireito());
    }

    // retorna e remove o maior nó a partir de raiz (usado na remoção com dois filhos)
    public Node<T> getMax(Node<T> raiz, Node<T> paiRaiz) {
        if (isEmpty()) return null;
        Node<T> aux;
        if (raiz.getFilhoDireito() == null) {
            aux = raiz;
            if (paiRaiz != null) {
                if (paiRaiz.getFilhoEsquerdo() == raiz) {
                    paiRaiz.setFilhoEsquerdo(raiz.getFilhoEsquerdo());
                } else {
                    paiRaiz.setFilhoDireito(raiz.getFilhoEsquerdo());
                }
            }
            return aux;
        } else {
            return getMax(raiz.getFilhoDireito(), raiz);
        }
    }

    // retorna a altura da ABB (-1 para árvore vazia, 0 para um único nó)
    public int altura() {
        return alturaRecursiva(raiz);
    }

    private int alturaRecursiva(Node<T> no) {
        if (no == null) return -1;
        int altEsq = alturaRecursiva(no.getFilhoEsquerdo());
        int altDir = alturaRecursiva(no.getFilhoDireito());
        if (altEsq > altDir)
            return 1 + altEsq;
        else
            return 1 + altDir;
    }

    // remove todos os nós da ABB
    public void liberarArvore() {
        raiz = null;
        System.out.println("Árvore ABB liberada com sucesso.");
    }
}
