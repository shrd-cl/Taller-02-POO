package taller02;

/** Choripán medido en centímetros. */
public class Choripan extends Consumo {
    private final double largo;
    private final double ancho;

    public Choripan(String consumidor, double largo, double ancho, String fecha) {
        super(consumidor, fecha);
        if (!Validacion.positivo(largo) || !Validacion.positivo(ancho)
                || !Double.isFinite(largo * ancho) || largo * ancho == 0
                || (largo * ancho) / 3.0 > Double.MAX_VALUE / 201.0) {
            throw new IllegalArgumentException("Dimensiones fuera del rango permitido.");
        }
        this.largo = largo;
        this.ancho = ancho;
    }

    public double getLargo() { return largo; }
    public double getAncho() { return ancho; }
    @Override public String getTipo() { return "Choripan"; }
    @Override public double calcularKcal() { return (largo * ancho) / 3.0; }
    @Override public String describir() {
        return String.format("Choripan | %s | %.2f cm x %.2f cm | %s | %.2f kcal",
                getConsumidor(), largo, ancho, getFecha(), calcularKcal());
    }
    @Override public String serializar() {
        return "Choripan;" + getConsumidor() + ";" + largo + ";" + ancho + ";" + getFecha();
    }
}
