package taller02;

import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

/** Carga y conserva los registros en vectores de capacidad fija. */
public class RegistroFonda {
    public static final int CAPACIDAD = 200;
    private final String[] participantes = new String[CAPACIDAD];
    private final Consumo[] consumos = new Consumo[CAPACIDAD];
    private final File archivoParticipantes;
    private final File archivoConsumos;
    private int cantidadParticipantes;
    private int cantidadConsumos;
    private boolean cargaSegura = true;

    public RegistroFonda() { this("."); }

    public RegistroFonda(String carpeta) {
        archivoParticipantes = new File(carpeta, "Participantes.txt");
        archivoConsumos = new File(carpeta, "Consumido.txt");
    }

    public void cargar() {
        cantidadParticipantes = 0;
        cantidadConsumos = 0;
        cargaSegura = true;
        cargarParticipantes();
        cargarConsumos();
    }

    private void cargarParticipantes() {
        try (Scanner archivo = new Scanner(archivoParticipantes, "UTF-8")) {
            int linea = 0;
            while (archivo.hasNextLine()) {
                linea++;
                String nombre = limpiar(archivo.nextLine());
                if (nombre.isEmpty()) continue;
                if (!Validacion.nombre(nombre)) {
                    advertir("Participantes.txt", linea, "nombre inválido");
                } else if (buscarParticipante(nombre) >= 0) {
                    advertir("Participantes.txt", linea, "participante repetido");
                } else if (cantidadParticipantes == CAPACIDAD) {
                    advertir("Participantes.txt", linea, "capacidad máxima de 200 participantes");
                } else {
                    participantes[cantidadParticipantes++] = nombre;
                }
            }
            if (archivo.ioException() != null) throw archivo.ioException();
        } catch (IOException | SecurityException e) {
            System.out.println("Advertencia: no se pudo leer Participantes.txt. " + e.getMessage());
        }
    }

    private void cargarConsumos() {
        try {
            if (!archivoConsumos.exists()) {
                if (new File(archivoConsumos.getPath() + ".bak").exists()) {
                    cargaSegura = false;
                    System.out.println("Advertencia: falta Consumido.txt pero hay un respaldo .bak. Recupérelo antes de reiniciar.");
                    return;
                }
                System.out.println("Advertencia: no se encontró Consumido.txt. Se iniciará sin registros.");
                return;
            }
            try (Scanner archivo = new Scanner(archivoConsumos, "UTF-8")) {
                int linea = 0;
                while (archivo.hasNextLine()) {
                    linea++;
                    String texto = limpiar(archivo.nextLine());
                    if (texto.isEmpty()) continue;
                    try {
                        Consumo consumo = interpretar(texto);
                        if (cantidadConsumos == CAPACIDAD) {
                            // No sobrescribir un archivo que contiene registros válidos sin cargar.
                            cargaSegura = false;
                            advertir("Consumido.txt", linea, "capacidad máxima de 200 registros");
                        } else {
                            consumos[cantidadConsumos++] = consumo;
                        }
                    } catch (IllegalArgumentException e) {
                        advertir("Consumido.txt", linea, e.getMessage());
                    }
                }
                if (archivo.ioException() != null) throw archivo.ioException();
            }
        } catch (IOException | SecurityException e) {
            cargaSegura = false;
            System.out.println("Advertencia: no se pudo leer Consumido.txt. " + e.getMessage());
            System.out.println("Se desactivaron los cambios para proteger el archivo original.");
        }
    }

    private String limpiar(String texto) {
        return texto.replace("\uFEFF", "").trim();
    }

    private void advertir(String archivo, int linea, String motivo) {
        System.out.println("Advertencia: " + archivo + ", línea " + linea + ": " + motivo + ".");
    }

    private Consumo interpretar(String texto) {
        String[] campos = texto.split(";", -1);
        for (int i = 0; i < campos.length; i++) campos[i] = campos[i].trim();
        boolean choripan = campos[0].equals("Choripan");
        boolean terremoto = campos[0].equals("Terremoto");
        if ((!choripan && !terremoto) || campos.length != (choripan ? 5 : 4)) {
            throw new IllegalArgumentException("formato desconocido o cantidad de campos incorrecta");
        }
        int persona = buscarParticipante(campos[1]);
        if (persona < 0) throw new IllegalArgumentException("consumidor no incluido en Participantes.txt");
        if (choripan) {
            return new Choripan(participantes[persona], Validacion.numero(campos[2]),
                    Validacion.numero(campos[3]), campos[4]);
        }
        return new Terremoto(participantes[persona], Validacion.numero(campos[2]), campos[3]);
    }

    public int getCantidadParticipantes() { return cantidadParticipantes; }
    public int getCantidadConsumos() { return cantidadConsumos; }

    public String getParticipante(int indice) {
        if (indice < 0 || indice >= cantidadParticipantes) throw new IllegalArgumentException("Participante inexistente.");
        return participantes[indice];
    }

    public Consumo getConsumo(int indice) {
        validarIndice(indice);
        return consumos[indice];
    }

    public int buscarParticipante(String nombre) {
        if (nombre == null) return -1;
        for (int i = 0; i < cantidadParticipantes; i++) {
            if (participantes[i].equalsIgnoreCase(nombre.trim())) return i;
        }
        return -1;
    }

    public void agregar(Consumo nuevo) throws IOException {
        validarNuevo(nuevo);
        if (cantidadConsumos == CAPACIDAD) throw new IllegalArgumentException("Se alcanzó la capacidad máxima (200).");
        // Guardar primero; si falla, la colección en memoria permanece sin cambios.
        guardar(-1, nuevo, false);
        consumos[cantidadConsumos++] = nuevo;
    }

    public void editar(int indice, Consumo nuevo) throws IOException {
        validarIndice(indice);
        validarNuevo(nuevo);
        if (!consumos[indice].getTipo().equals(nuevo.getTipo())) {
            throw new IllegalArgumentException("No se puede cambiar el tipo al editar.");
        }
        guardar(indice, nuevo, false);
        consumos[indice] = nuevo;
    }

    public void eliminar(int indice) throws IOException {
        validarIndice(indice);
        guardar(indice, null, true);
        for (int i = indice; i < cantidadConsumos - 1; i++) consumos[i] = consumos[i + 1];
        consumos[--cantidadConsumos] = null;
    }

    private void validarIndice(int indice) {
        if (indice < 0 || indice >= cantidadConsumos) throw new IllegalArgumentException("No existe ese registro.");
    }

    private void validarNuevo(Consumo nuevo) {
        if (nuevo == null || buscarParticipante(nuevo.getConsumidor()) < 0) {
            throw new IllegalArgumentException("Debe seleccionar un participante existente.");
        }
        if (!cargaSegura) throw new IllegalArgumentException("Cambios desactivados: revise el archivo de entrada y reinicie.");
        double kcal = nuevo.calcularKcal();
        double litros = nuevo instanceof Terremoto ? ((Terremoto) nuevo).getLitros() : 0;
        for (int i = 0; i < cantidadConsumos; i++) {
            kcal += consumos[i].calcularKcal();
            if (consumos[i] instanceof Terremoto) litros += ((Terremoto) consumos[i]).getLitros();
        }
        if (!Double.isFinite(kcal) || !Double.isFinite(litros)) {
            throw new IllegalArgumentException("Valores demasiado grandes para calcular métricas.");
        }
    }

    private void guardar(int indice, Consumo nuevo, boolean eliminar) throws IOException {
        if (!cargaSegura) throw new IOException("Carga incompleta: no se sobrescribirá Consumido.txt.");
        File temporal = new File(archivoConsumos.getPath() + ".tmp");
        File respaldo = new File(archivoConsumos.getPath() + ".bak");
        try {
            // El archivo original no se abre para escritura mientras se genera la nueva versión.
            try (BufferedWriter salida = new BufferedWriter(new FileWriter(temporal, false))) {
                for (int i = 0; i < cantidadConsumos; i++) {
                    if (i == indice && eliminar) continue;
                    Consumo actual = i == indice ? nuevo : consumos[i];
                    salida.write(actual.serializar());
                    salida.newLine();
                }
                if (indice < 0 && nuevo != null) {
                    salida.write(nuevo.serializar());
                    salida.newLine();
                }
            }
            boolean existia = archivoConsumos.exists();
            if (existia) {
                if (respaldo.exists() && !respaldo.delete()) throw new IOException("No se pudo actualizar el respaldo.");
                if (!archivoConsumos.renameTo(respaldo)) throw new IOException("No se pudo respaldar el archivo original.");
            }
            if (!temporal.renameTo(archivoConsumos)) {
                if (existia && !respaldo.renameTo(archivoConsumos)) {
                    cargaSegura = false;
                    throw new IOException("No se pudo restaurar el original; sus datos están en Consumido.txt.bak. Reinicie tras recuperarlo.");
                }
                throw new IOException("No se pudo reemplazar Consumido.txt. El original se conservó.");
            }
        } catch (SecurityException e) {
            throw new IOException("Permisos insuficientes para guardar los cambios.", e);
        }
    }
}
