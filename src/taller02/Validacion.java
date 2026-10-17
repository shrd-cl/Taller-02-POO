package taller02;

/** Validaciones sin librerías adicionales, incluidas fechas de años bisiestos. */
public final class Validacion {
    private Validacion() { }

    public static boolean nombre(String texto) {
        return texto != null && !texto.trim().isEmpty() && texto.indexOf(';') < 0
                && texto.indexOf('\n') < 0 && texto.indexOf('\r') < 0;
    }

    public static boolean positivo(double valor) {
        return Double.isFinite(valor) && valor > 0;
    }

    public static double numero(String texto) {
        String normalizado = texto.trim().replace(',', '.');
        // Double.toString usa exponentes para ciertos valores guardados en el archivo.
        // Se permite esa notación para poder volver a cargar todos los valores válidos.
        if (!normalizado.matches("[+]?(?:[0-9]+(?:\\.[0-9]*)?|\\.[0-9]+)(?:[eE][+-]?[0-9]+)?")) {
            throw new IllegalArgumentException("Ingrese un número decimal mayor que cero.");
        }
        double valor = Double.parseDouble(normalizado);
        if (!positivo(valor)) throw new IllegalArgumentException("El valor debe ser finito y mayor que cero.");
        return valor;
    }

    public static boolean fecha(String texto) {
        if (texto == null || !texto.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) return false;
        int anio = Integer.parseInt(texto.substring(0, 4));
        int mes = Integer.parseInt(texto.substring(5, 7));
        int dia = Integer.parseInt(texto.substring(8, 10));
        if (anio < 1 || mes < 1 || mes > 12) return false;
        int[] diasMes = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (anio % 400 == 0 || (anio % 4 == 0 && anio % 100 != 0)) diasMes[1] = 29;
        return dia >= 1 && dia <= diasMes[mes - 1];
    }
}
