package taller02;

import java.io.IOException;

/** Coordina la navegación y la edición; el dominio y los archivos tienen clases propias. */
public class Aplicacion {
    private final RegistroFonda registro = new RegistroFonda();
    private final Entrada entrada = new Entrada();
    private final Metricas metricas = new Metricas(registro);

    public void ejecutar() {
        registro.cargar();
        while (!entrada.isTerminada()) {
            titulo("LA FONDA DEL PAPU | " + registro.getCantidadConsumos() + " registros");
            System.out.println("1. Ver registros\n2. Actualizar registros\n3. Calcular métricas\n4. Salir");
            int opcion = entrada.entero("Opción: ", 1, 4);
            if (entrada.isTerminada() || opcion == 4) break;
            if (opcion == 1) menuVer();
            if (opcion == 2) menuActualizar();
            if (opcion == 3) menuMetricas();
        }
        System.out.println("Hasta luego.");
    }

    private void titulo(String texto) {
        System.out.println("\n============================================================");
        System.out.println(texto);
        System.out.println("============================================================");
    }

    private void menuVer() {
        while (!entrada.isTerminada()) {
            titulo("VER REGISTROS");
            System.out.println("1. Choripanes\n2. Terremotos\n3. Volver al menú principal");
            int opcion = entrada.entero("Opción: ", 1, 3);
            if (entrada.isTerminada() || opcion == 3) return;
            listar(opcion == 1 ? "Choripan" : "Terremoto", new int[RegistroFonda.CAPACIDAD]);
        }
    }

    /** El mapa traduce el número mostrado al índice real del vector mixto. */
    private int listar(String tipo, int[] mapa) {
        titulo(tipo.equals("Choripan") ? "CHORIPANES" : "TERREMOTOS");
        int cantidad = 0;
        for (int i = 0; i < registro.getCantidadConsumos(); i++) {
            Consumo consumo = registro.getConsumo(i);
            if (consumo.getTipo().equals(tipo)) {
                mapa[cantidad] = i;
                System.out.printf("%3d | %s%n", ++cantidad, consumo.describir());
            }
        }
        System.out.println("Total: " + cantidad + " registros.");
        if (tipo.equals("Terremoto")) System.out.printf("Volumen total: %.2f L%n", metricas.litros(null, null));
        return cantidad;
    }

    private void menuActualizar() {
        while (!entrada.isTerminada()) {
            titulo("ACTUALIZAR REGISTROS");
            System.out.println("1. Agregar\n2. Editar\n3. Eliminar\n4. Volver al menú principal");
            int opcion = entrada.entero("Opción: ", 1, 4);
            if (entrada.isTerminada() || opcion == 4) return;
            try {
                actualizar(opcion);
            } catch (IllegalArgumentException | IOException | SecurityException e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("No se aplicó el cambio en memoria.");
            }
        }
    }

    private void actualizar(int opcion) throws IOException {
        if (opcion == 1 && registro.getCantidadConsumos() == RegistroFonda.CAPACIDAD) {
            System.out.println("Error: se alcanzó la capacidad máxima de registros (200).");
            return;
        }
        System.out.println("1. Choripán\n2. Terremoto\n3. Cancelar");
        int seleccion = entrada.entero("Tipo de consumo: ", 1, 3);
        if (entrada.isTerminada() || seleccion == 3) return;
        String tipo = seleccion == 1 ? "Choripan" : "Terremoto";
        int indice = -1;
        Consumo anterior = null;
        if (opcion != 1) {
            int[] mapa = new int[RegistroFonda.CAPACIDAD];
            int cantidad = listar(tipo, mapa);
            if (cantidad == 0) return;
            int numero = entrada.entero("Número del registro (0 para cancelar): ", 0, cantidad);
            if (entrada.isTerminada() || numero == 0) return;
            indice = mapa[numero - 1];
            anterior = registro.getConsumo(indice);
        }
        if (opcion == 3) {
            if (entrada.confirmar("Eliminar " + anterior.describir() + " (s/n): ") && !entrada.isTerminada()) {
                registro.eliminar(indice);
                System.out.println("Registro eliminado y guardado en Consumido.txt.");
            } else {
                System.out.println("Eliminación cancelada.");
            }
            return;
        }
        Consumo nuevo = solicitarConsumo(tipo, anterior);
        if (nuevo == null || entrada.isTerminada()) {
            System.out.println("Operación cancelada.");
            return;
        }
        if (opcion == 1) registro.agregar(nuevo);
        else registro.editar(indice, nuevo);
        System.out.println("Registro " + (opcion == 1 ? "agregado" : "actualizado") + " y guardado en Consumido.txt.");
        System.out.println(nuevo.describir());
    }

    private Consumo solicitarConsumo(String tipo, Consumo anterior) {
        if (registro.getCantidadParticipantes() == 0) {
            System.out.println("No hay participantes. Revise Participantes.txt y reinicie.");
            return null;
        }
        boolean editar = anterior != null;
        if (editar) {
            System.out.println("Registro actual: " + anterior.describir());
            System.out.println("Deje el campo vacío para mantener su valor actual.");
        }
        String persona = solicitarParticipante(editar ? anterior.getConsumidor() : null);
        if (persona == null || entrada.isTerminada()) return null;
        double largo = 0;
        double ancho = 0;
        double litros = 0;
        if (tipo.equals("Choripan")) {
            Choripan previo = editar ? (Choripan) anterior : null;
            largo = entrada.decimal("Largo en cm" + referencia(editar, editar ? previo.getLargo() + "" : "") + ": ",
                    editar ? previo.getLargo() : 0, editar);
            if (entrada.isTerminada()) return null;
            ancho = entrada.decimal("Ancho en cm" + referencia(editar, editar ? previo.getAncho() + "" : "") + ": ",
                    editar ? previo.getAncho() : 0, editar);
        } else {
            Terremoto previo = editar ? (Terremoto) anterior : null;
            litros = entrada.decimal("Litros" + referencia(editar, editar ? previo.getLitros() + "" : "") + ": ",
                    editar ? previo.getLitros() : 0, editar);
        }
        if (entrada.isTerminada()) return null;
        String fecha = entrada.fecha("Fecha YYYY-MM-DD" + referencia(editar, editar ? anterior.getFecha() : "") + ": ",
                editar ? anterior.getFecha() : null, editar);
        if (entrada.isTerminada()) return null;
        if (tipo.equals("Choripan")) return new Choripan(persona, largo, ancho, fecha);
        return new Terremoto(persona, litros, fecha);
    }

    private String referencia(boolean editar, String valor) {
        return editar ? " [" + valor + "]" : "";
    }

    private String solicitarParticipante(String anterior) {
        System.out.println("Participantes (0 para cancelar):");
        for (int i = 0; i < registro.getCantidadParticipantes(); i++) {
            System.out.println((i + 1) + ". " + registro.getParticipante(i));
        }
        while (!entrada.isTerminada()) {
            String texto = entrada.linea("Número o nombre" + referencia(anterior != null, anterior) + ": ");
            if (texto == null || texto.equals("0")) return null;
            if (texto.isEmpty() && anterior != null) return anterior;
            int indice = registro.buscarParticipante(texto);
            if (indice >= 0) return registro.getParticipante(indice);
            try {
                int numero = Integer.parseInt(texto);
                if (numero >= 1 && numero <= registro.getCantidadParticipantes()) return registro.getParticipante(numero - 1);
            } catch (NumberFormatException e) {
                // Un nombre desconocido se informa igual que un índice inválido.
            }
            System.out.println("Error: seleccione un participante existente.");
        }
        return null;
    }

    private void menuMetricas() {
        while (!entrada.isTerminada()) {
            titulo("CALCULAR MÉTRICAS");
            System.out.println("1. Choripanes comidos en total\n2. Terremotos tomados en total"
                    + "\n3. Choripanes por usuario\n4. Terremotos por usuario"
                    + "\n5. KCal por usuario\n6. Días ebrio por usuario\n7. Volver al menú principal");
            int opcion = entrada.entero("Opción: ", 1, 7);
            if (entrada.isTerminada() || opcion == 7) return;
            if (opcion == 6) System.out.println("Un día ebrio requiere al menos 2 L en la misma fecha.");
            metricas.mostrar(opcion);
        }
    }
}
