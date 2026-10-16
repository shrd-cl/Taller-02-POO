package taller02;

/** Calcula agregaciones recorriendo los vectores, sin colecciones dinámicas. */
public class Metricas {
    private final RegistroFonda registro;

    public Metricas(RegistroFonda registro) { this.registro = registro; }

    public int cantidad(String tipo, String persona) {
        int total = 0;
        for (int i = 0; i < registro.getCantidadConsumos(); i++) {
            Consumo consumo = registro.getConsumo(i);
            if (consumo.getTipo().equals(tipo) && corresponde(consumo, persona)) total++;
        }
        return total;
    }

    private boolean corresponde(Consumo consumo, String persona) {
        return persona == null || consumo.getConsumidor().equalsIgnoreCase(persona);
    }

    public double litros(String persona, String fecha) {
        double total = 0;
        for (int i = 0; i < registro.getCantidadConsumos(); i++) {
            Consumo consumo = registro.getConsumo(i);
            if (consumo instanceof Terremoto && corresponde(consumo, persona)
                    && (fecha == null || consumo.getFecha().equals(fecha))) {
                total += ((Terremoto) consumo).getLitros();
            }
        }
        return total;
    }

    public double kcal(String persona, String tipo) {
        double total = 0;
        for (int i = 0; i < registro.getCantidadConsumos(); i++) {
            Consumo consumo = registro.getConsumo(i);
            if (corresponde(consumo, persona) && (tipo == null || consumo.getTipo().equals(tipo))) {
                total += consumo.calcularKcal();
            }
        }
        return total;
    }

    public int diasEbrio(String persona) {
        return fechasEbrias(persona, null);
    }

    private int fechasEbrias(String persona, String[] fechas) {
        String[] vistas = new String[RegistroFonda.CAPACIDAD];
        int cantidadVistas = 0;
        int dias = 0;
        for (int i = 0; i < registro.getCantidadConsumos(); i++) {
            Consumo consumo = registro.getConsumo(i);
            if (!(consumo instanceof Terremoto) || !corresponde(consumo, persona)) continue;
            String fecha = consumo.getFecha();
            boolean repetida = false;
            for (int j = 0; j < cantidadVistas; j++) {
                if (vistas[j].equals(fecha)) { repetida = true; break; }
            }
            if (repetida) continue;
            vistas[cantidadVistas++] = fecha;
            if (litros(persona, fecha) >= 2.0) {
                if (fechas != null) fechas[dias] = fecha;
                dias++;
            }
        }
        return dias;
    }

    public void mostrar(int opcion) {
        if (opcion == 1) {
            System.out.println("Total de choripanes: " + cantidad("Choripan", null));
            return;
        }
        if (opcion == 2) {
            System.out.printf("Total de terremotos: %d | %.2f L%n", cantidad("Terremoto", null), litros(null, null));
            return;
        }
        if (registro.getCantidadParticipantes() == 0) {
            System.out.println("No hay participantes disponibles.");
            return;
        }
        for (int i = 0; i < registro.getCantidadParticipantes(); i++) {
            String persona = registro.getParticipante(i);
            if (opcion == 3) {
                System.out.printf("%-20s | %d choripanes%n", persona, cantidad("Choripan", persona));
            } else if (opcion == 4) {
                System.out.printf("%-20s | %d terremotos | %.2f L%n", persona,
                        cantidad("Terremoto", persona), litros(persona, null));
            } else if (opcion == 5) {
                System.out.printf("%-20s | Choripán: %.2f | Terremoto: %.2f | Total: %.2f kcal%n",
                        persona, kcal(persona, "Choripan"), kcal(persona, "Terremoto"), kcal(persona, null));
            } else if (opcion == 6) {
                String[] fechas = new String[RegistroFonda.CAPACIDAD];
                int dias = fechasEbrias(persona, fechas);
                System.out.printf("%-20s | %d días ebrio", persona, dias);
                if (dias == 0) System.out.print(" | -");
                for (int j = 0; j < dias; j++) {
                    System.out.printf(" | %s (%.2f L)", fechas[j], litros(persona, fechas[j]));
                }
                System.out.println();
            }
        }
        if (opcion == 5) System.out.printf("Consumo total de la fonda: %.2f kcal%n", kcal(null, null));
    }
}
