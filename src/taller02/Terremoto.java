package taller02;

/** Cada objeto representa un registro de bebida, independientemente de sus litros. */
public class Terremoto extends Consumo {
    private final double litros;

    public Terremoto(String consumidor, double litros, String fecha) {
        super(consumidor, fecha);
        if (!Validacion.positivo(litros) || !Double.isFinite(litros * 10)
                || litros > Double.MAX_VALUE / 201.0
                || (litros * 10) / 3.0 > Double.MAX_VALUE / 201.0) {
            throw new IllegalArgumentException("Litros fuera del rango permitido.");
        }
        this.litros = litros;
    }

    public double getLitros() { return litros; }
    @Override public String getTipo() { return "Terremoto"; }
    @Override public double calcularKcal() { return (litros * 10) / 3.0; }
    @Override public String describir() {
        return String.format("Terremoto | %s | %.2f L | %s | %.2f kcal",
                getConsumidor(), litros, getFecha(), calcularKcal());
    }
    @Override public String serializar() {
        return "Terremoto;" + getConsumidor() + ";" + litros + ";" + getFecha();
    }
}
