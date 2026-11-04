import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/**
 * Ejecuta los tests de la Práctica 5 en orden: primero los que no hacen rehash
 * y después los que sí. Cada test indica qué comprueba y, si falla, qué debería
 * obtenerse y qué se obtiene.
 */
public class TestRunner {

    public static void main(String[] args) {
        int fallos = 0, total = 0;

        Class<?>[] clases = {EDLinkedHashSetSinRehashTest.class, EDLinkedHashSetConRehashTest.class};
        String[] titulos = {"TESTS SIN REHASH", "TESTS CON REHASH"};

        for (int i = 0; i < clases.length; i++) {
            System.out.println();
            System.out.println("==================== " + titulos[i] + " ====================");
            Result r = JUnitCore.runClasses(clases[i]);
            int ok = r.getRunCount() - r.getFailureCount();
            System.out.println("-- " + ok + " de " + r.getRunCount() + " tests superados");
            fallos += r.getFailureCount();
            total += r.getRunCount();
        }

        System.out.println();
        System.out.println("====================================================");
        if (fallos == 0)
            System.out.println("Todos los tests superados (" + total + "). Práctica APTA.");
        else
            System.out.println(fallos + " de " + total + " tests fallan. Revisa los mensajes de arriba.");
    }
}
