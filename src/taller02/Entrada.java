package taller02;

import java.util.Scanner;

/** Lee líneas completas para evitar residuos entre números y textos. */
public class Entrada {
    private final Scanner teclado = new Scanner(System.in);
    private boolean terminada;

    public boolean isTerminada() { return terminada; }

    public String linea(String mensaje) {
        System.out.print(mensaje);
        if (!teclado.hasNextLine()) {
            terminada = true;
            return null;
        }
        return teclado.nextLine().trim();
    }

    public int entero(String mensaje, int minimo, int maximo) {
        while (!terminada) {
            String texto = linea(mensaje);
            if (texto == null) return minimo;
            try {
                int valor = Integer.parseInt(texto);
                if (valor >= minimo && valor <= maximo) return valor;
                System.out.println("Error: opción fuera de rango (" + minimo + " a " + maximo + ").");
            } catch (NumberFormatException e) {
                System.out.println("Error: ingrese un número entero entre " + minimo + " y " + maximo + ".");
            }
        }
        return minimo;
    }

    public double decimal(String mensaje, double anterior, boolean editar) {
        while (!terminada) {
            String texto = linea(mensaje);
            if (texto == null || (editar && texto.isEmpty())) return anterior;
            try {
                return Validacion.numero(texto);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        return anterior;
    }

    public String fecha(String mensaje, String anterior, boolean editar) {
        while (!terminada) {
            String texto = linea(mensaje);
            if (texto == null || (editar && texto.isEmpty())) return anterior;
            if (Validacion.fecha(texto)) return texto;
            System.out.println("Error: fecha inexistente o formato incorrecto. Use YYYY-MM-DD.");
        }
        return anterior;
    }

    public boolean confirmar(String mensaje) {
        while (!terminada) {
            String texto = linea(mensaje);
            if (texto == null) return false;
            if (texto.equalsIgnoreCase("s")) return true;
            if (texto.equalsIgnoreCase("n")) return false;
            System.out.println("Error: responda s o n.");
        }
        return false;
    }
}
