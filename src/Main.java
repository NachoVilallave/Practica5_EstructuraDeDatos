import java.util.*;

/**
 * Pequeño programa de prueba de EDLinkedHashSet.
 *
 * Cada línea muestra la operación, el resultado que obtiene tu implementación
 * y, entre corchetes, el resultado esperado. No sustituye a los tests de
 * validación: sirve para probar a mano mientras implementas los ejercicios.
 *
 * Cada bloque se ejecuta por separado, así que si un método aún no está hecho
 * (o lanza una excepción) el resto de bloques se sigue ejecutando.
 */
public class Main {

    public static void main(String[] args) {
        bloque("Ejercicio 1: constructor", Main::constructor);
        bloque("Ejercicios 2 a 5: add y contains", Main::addYContains);
        bloque("Ejercicio 6: rehash", Main::rehash);
        bloque("Ejercicio 7: remove", Main::remove);
        bloque("Ejercicio 8: clear", Main::clear);
        bloque("Ejercicio 9: iterador", Main::iterador);
        bloque("Ejercicios 10 y 11: containsAll y retainAll", Main::containsAllYRetainAll);
    }

    // ------------------------------------------------------------------

    private static void constructor() {
        EDLinkedHashSet<String> s = new EDLinkedHashSet<>(8, 1.5);
        muestra("new EDLinkedHashSet(8, 1.5)", s, "[]");
        muestra("size()", s.size(), 0);
        muestra("isEmpty()", s.isEmpty(), true);

        muestraExcepcion("new EDLinkedHashSet(0, 1.5)", () -> new EDLinkedHashSet<String>(0, 1.5),
                "IllegalArgumentException");
        muestraExcepcion("new EDLinkedHashSet(8, -1.0)", () -> new EDLinkedHashSet<String>(8, -1.0),
                "IllegalArgumentException");
    }

    private static void addYContains() {
        EDLinkedHashSet<String> s = new EDLinkedHashSet<>(8, 1.5);
        muestra("add(\"cacerola\")", s.add("cacerola"), true);
        muestra("add(\"bimbo\")", s.add("bimbo"), true);
        muestra("add(\"cremallera\")", s.add("cremallera"), true);
        muestra("add(\"Ramón\")", s.add("Ramón"), true);
        muestra("add(\"sartén\")", s.add("sartén"), true);
        muestra("add(\"bimbo\") repetido", s.add("bimbo"), false);
        muestra("Conjunto (orden de inserción)", s, "[cacerola, bimbo, cremallera, Ramón, sartén]");
        muestra("size()", s.size(), 5);

        muestra("contains(\"Ramón\")", s.contains("Ramón"), true);
        muestra("contains(\"olla\")", s.contains("olla"), false);

        muestra("add(null)", s.add(null), true);
        muestra("add(null) repetido", s.add(null), false);
        muestra("contains(null)", s.contains(null), true);
        muestra("Conjunto", s, "[cacerola, bimbo, cremallera, Ramón, sartén, null]");

        // Con capacidad 11, el 1, el 12 y el 23 dispersan en la misma cubeta (la 1).
        EDLinkedHashSet<Integer> n = new EDLinkedHashSet<>(11, 1.5);
        n.add(1);
        n.add(12);
        n.add(23);
        n.add(5);
        muestra("Conjunto con 1, 12 y 23 en la misma cubeta", n, "[1, 12, 23, 5]");
        muestra("contains(12)", n.contains(12), true);
        muestra("contains(34), también en la cubeta 1", n.contains(34), false);
    }

    private static void rehash() {
        // Capacidad 2 y factor 1.0: el umbral es 2, así que el tercer add provoca un rehash
        // (tabla de 5 casillas, umbral 5), el sexto otro (11 casillas), etc.
        EDLinkedHashSet<Integer> s = new EDLinkedHashSet<>(2, 1.0);
        for (int i = 10; i >= 1; i--)
            s.add(i * 7);
        muestra("Tras añadir 70, 63, ..., 7 (varios rehash)", s, "[70, 63, 56, 49, 42, 35, 28, 21, 14, 7]");
        muestra("size()", s.size(), 10);
        muestra("contains(42)", s.contains(42), true);
        muestra("contains(43)", s.contains(43), false);
        muestra("add(42) repetido", s.add(42), false);
    }

    private static void remove() {
        EDLinkedHashSet<Integer> s = new EDLinkedHashSet<>(11, 1.5);
        for (int e : new int[]{1, 12, 23, 5, 9})
            s.add(e);
        s.add(null);
        // La cadena de la cubeta 1 es 23 -> 12 -> 1 (cada nodo nuevo va al principio).
        muestra("Conjunto inicial", s, "[1, 12, 23, 5, 9, null]");
        muestra("remove(12), en medio de su cubeta", s.remove(12), true);
        muestra("contains(12)", s.contains(12), false);
        muestra("contains(1), detrás del borrado en su cubeta", s.contains(1), true);
        muestra("remove(23), primero de su cubeta", s.remove(23), true);
        muestra("contains(23)", s.contains(23), false);
        muestra("contains(1)", s.contains(1), true);
        muestra("remove(1), primero de la lista", s.remove(1), true);
        muestra("remove(null), último de la lista", s.remove(null), true);
        muestra("remove(7), no está", s.remove(7), false);
        muestra("remove(12) otra vez", s.remove(12), false);
        muestra("Conjunto", s, "[5, 9]");
        muestra("size()", s.size(), 2);
        s.add(1);
        muestra("Tras volver a añadir el 1 (va al final)", s, "[5, 9, 1]");
        muestra("contains(1)", s.contains(1), true);
    }

    private static void clear() {
        EDLinkedHashSet<String> s = new EDLinkedHashSet<>(8, 1.5);
        s.add("a");
        s.add("b");
        s.add("c");
        s.clear();
        muestra("Tras clear()", s, "[]");
        muestra("size()", s.size(), 0);
        muestra("contains(\"a\")", s.contains("a"), false);
        s.add("d");
        muestra("Tras clear() y add(\"d\")", s, "[d]");
    }

    private static void iterador() {
        EDLinkedHashSet<String> s = new EDLinkedHashSet<>(8, 1.5);
        for (String e : new String[]{"lunes", "martes", "miércoles", "jueves", "viernes"})
            s.add(e);

        StringBuilder recorrido = new StringBuilder();
        for (String e : s)
            recorrido.append(e).append(' ');
        muestra("Recorrido con for-each", recorrido.toString().trim(), "lunes martes miércoles jueves viernes");

        Iterator<String> it = s.iterator();
        while (it.hasNext())
            if (it.next().startsWith("m"))
                it.remove();
        muestra("Tras borrar con el iterador los que empiezan por m", s, "[lunes, jueves, viernes]");

        muestra("toArray()", Arrays.toString(s.toArray()), "[lunes, jueves, viernes]");

        Iterator<String> it2 = s.iterator();
        muestraExcepcion("remove() antes de next()", it2::remove, "IllegalStateException");
        it2.next();
        it2.next();
        it2.next();
        muestraExcepcion("next() sin más elementos", it2::next, "NoSuchElementException");
    }

    private static void containsAllYRetainAll() {
        EDLinkedHashSet<Integer> s = new EDLinkedHashSet<>(11, 1.5);
        for (int e : new int[]{6, 1, 12, 9, 23, 4})
            s.add(e);
        muestra("Conjunto inicial", s, "[6, 1, 12, 9, 23, 4]");
        muestra("containsAll([1, 9, 4])", s.containsAll(List.of(1, 9, 4)), true);
        muestra("containsAll([1, 2])", s.containsAll(List.of(1, 2)), false);
        muestra("retainAll([4, 12, 6, 30])", s.retainAll(List.of(4, 12, 6, 30)), true);
        muestra("Conjunto", s, "[6, 12, 4]");
        muestra("retainAll([4, 12, 6]), no cambia nada", s.retainAll(List.of(4, 12, 6)), false);
    }

    // ------------------------------------------------------------------
    // Utilidades para mostrar los resultados
    // ------------------------------------------------------------------

    private static void bloque(String titulo, Runnable pruebas) {
        System.out.println();
        System.out.println("=== " + titulo + " ===");
        try {
            pruebas.run();
        } catch (Throwable t) {
            System.out.println("  !! Excepción inesperada: " + t);
            for (StackTraceElement st : t.getStackTrace())
                if (st.getClassName().startsWith("EDLinkedHashSet")) {
                    System.out.println("     en " + st);
                    break;
                }
        }
    }

    private static void muestra(String operacion, Object obtenido, Object esperado) {
        String o = String.valueOf(obtenido);
        String e = String.valueOf(esperado);
        String marca = o.equals(e) ? "  " : "XX";
        System.out.println(marca + " " + operacion + " -> " + o + "   [esperado: " + e + "]");
    }

    private static void muestraExcepcion(String operacion, Runnable op, String esperada) {
        String obtenido;
        try {
            op.run();
            obtenido = "no lanza excepción";
        } catch (Throwable t) {
            obtenido = "lanza " + t.getClass().getSimpleName();
        }
        String e = "lanza " + esperada;
        System.out.println((obtenido.equals(e) ? "  " : "XX") + " " + operacion + " -> " + obtenido
                + "   [esperado: " + e + "]");
    }
}
