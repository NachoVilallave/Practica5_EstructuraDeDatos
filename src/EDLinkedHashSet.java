import java.util.*;

public class EDLinkedHashSet<T> implements Set<T> {

    private class Node {
        T data;
        Node hnext = null;   // siguiente nodo en la cadena de la cubeta de la tabla
        Node prev = null;    // anterior en la lista de orden de inserción
        Node next = null;    // siguiente en la lista de orden de inserción
    }

    static final private int DEFAULT_CAPACITY = 11;
    static final private double DEFAULT_LOAD_FACTOR = 2.0;

    Node[] table;
    Node header;
    int size = 0;
    int rehashThreshold;
    double loadFactor;

    // ------------------------------------------------------------------
    // Métodos que se dan implementados
    // ------------------------------------------------------------------

    public EDLinkedHashSet() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    /** Posición de la tabla en la que dispersa item (admite null). */
    private int hash(Object item) {
        if (item == null)
            return 0;
        return (item.hashCode() & Integer.MAX_VALUE) % table.length;
    }

    /** Compara dos elementos teniendo en cuenta que alguno puede ser null. */
    private boolean equalsNull(Object a, Object b) {
        if (a == null)
            return b == null;
        return a.equals(b);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /** Recorre directamente la lista de orden desde el nodo cabecera. */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node n = header.next;
        while (n != header) {
            sb.append(n.data);
            n = n.next;
            if (n != header)
                sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        for (T e : this)
            result[i++] = e;
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> E[] toArray(E[] a) {
        if (a.length < size)
            a = (E[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        int i = 0;
        for (T e : this)
            a[i++] = (E) e;
        if (a.length > size)
            a[size] = null;
        return a;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean changed = false;
        for (T e : c)
            if (add(e))
                changed = true;
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for (Object e : c)
            if (remove(e))
                changed = true;
        return changed;
    }

    // ------------------------------------------------------------------
    // Ejercicio 1
    // ------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public EDLinkedHashSet(int capacity, double loadFactor) {
        if (capacity <= 0 || loadFactor <= 0) {
            throw new IllegalArgumentException("La capacidad y el factor de carga deben ser mayores que cero.");
        }

        this.table = new EDLinkedHashSet.Node[capacity];
        this.loadFactor = loadFactor;
        this.rehashThreshold = (int) (capacity * loadFactor);
        this.size = 0;

        this.header = new Node();
        this.header.prev = this.header;
        this.header.next = this.header;

    }

    // ------------------------------------------------------------------
    // Ejercicio 2
    // ------------------------------------------------------------------

    private void linkLast(Node n) {
        Node last = header.prev;
        n.prev = last;
        n.next = header;
        last.next = n;
        header.prev = n;

    }

    private void unlink(Node n) {
        n.prev.next = n.next;
        n.next.prev = n.prev;
        n.prev = null;
        n.next = null;
    }

    // ------------------------------------------------------------------
    // Ejercicio 3
    // ------------------------------------------------------------------

    private Node findNode(Object item) {
        int index = hash(item);
        Node current = table[index];

        while (current != null) {
            if (equalsNull(current.data, item)) {
                return current;
            }
            current = current.hnext;
        }

        return null;
    }

    // ------------------------------------------------------------------
    // Ejercicio 4
    // ------------------------------------------------------------------

    @Override
    public boolean contains(Object item) {
        return findNode(item) != null;
    }

    // ------------------------------------------------------------------
    // Ejercicio 5
    // ------------------------------------------------------------------

    @Override
    public boolean add(T item) {
        if (contains(item)) {
            return false;
        }

        if (size >= rehashThreshold) {
            rehash();
        }

        int index = hash(item);

        Node n = new Node();
        n.data = item;

        n.hnext = table[index];
        table[index] = n;

        linkLast(n);

        size++;
        return true;
    }

    // ------------------------------------------------------------------
    // Ejercicio 6
    // ------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private void rehash() {
        // 1. Calcular la nueva capacidad: 2 veces el tamaño anterior + 1
        int newCapacity = table.length * 2 + 1;

        // 2. Crear la nueva tabla de nodos y actualizar las variables del conjunto
        Node[] newTable = new EDLinkedHashSet.Node[newCapacity];
        this.table = newTable;
        this.rehashThreshold = (int) (newCapacity * loadFactor);

        // 3. Reubicar los nodos existentes recorriendo la lista de orden de inserción
        Node current = header.next;
        while (current != header) {
            int index = hash(current.data);

            // Insertar al principio de la nueva cubeta reutilizando el nodo actual
            current.hnext = table[index];
            table[index] = current;

            // Avanzar al siguiente nodo en la lista
            current = current.next;
        }
    }

    // ------------------------------------------------------------------
    // Ejercicio 7
    // ------------------------------------------------------------------

    @Override
    public boolean remove(Object item) {
        // 1. Calcular la cubeta correspondiente
        int index = hash(item);
        Node current = table[index];
        Node prevInBucket = null;

        // 2. Buscar el elemento en la cadena de la cubeta
        while (current != null) {
            if (equalsNull(current.data, item)) {
                // Eliminar de la cadena simplemente enlazada de la cubeta (hnext)
                if (prevInBucket == null) {
                    table[index] = current.hnext;
                } else {
                    prevInBucket.hnext = current.hnext;
                }

                // Eliminar de la lista de orden de inserción usando el método del ejercicio 2
                unlink(current);

                // Actualizar tamaño y devolver true
                size--;
                return true;
            }

            prevInBucket = current;
            current = current.hnext;
        }

        // Si el elemento no existe en el conjunto
        return false;
    }

    // ------------------------------------------------------------------
    // Ejercicio 8
    // ------------------------------------------------------------------

    @Override
    public void clear() {
        // 1. Vaciar todas las casillas de la tabla conservando su tamaño actual
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }

        // 2. Reiniciar la lista circular doblemente enlazada
        header.next = header;
        header.prev = header;

        // 3. Reiniciar el contador de elementos
        size = 0;
    }

    // ------------------------------------------------------------------
    // Ejercicio 9
    // ------------------------------------------------------------------

    private class EDIterator implements Iterator<T> {
        Node current = header.next;   // nodo que devolverá next
        Node lastReturned = null;     // último nodo devuelto por next

        @Override
        public boolean hasNext() {
            return current != header;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No quedan más elementos en el conjunto.");
            }

            lastReturned = current;
            T item = current.data;
            current = current.next;

            return item;
        }

        @Override
        public void remove() {
            if (lastReturned == null)
                throw new IllegalStateException();
            EDLinkedHashSet.this.remove(lastReturned.data);
            lastReturned = null;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new EDIterator();
    }

    // ------------------------------------------------------------------
    // Ejercicio 10
    // ------------------------------------------------------------------

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object item : c) {
            if (!contains(item)) {
                return false;
            }
        }
        return true;
    }

    // ------------------------------------------------------------------
    // Ejercicio 11
    // ------------------------------------------------------------------
    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Iterator<T> it = iterator();

        while (it.hasNext()) {
            T item = it.next();
            if (!c.contains(item)) {
                it.remove();
                modified = true;
            }
        }

        return modified;
    }
}
