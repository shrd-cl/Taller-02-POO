package taller02;

/** Base común que permite almacenar y utilizar ambos tipos de consumo. */
public abstract class Consumo {
    private final String consumidor;
    private final String fecha;

    protected Consumo(String consumidor, String fecha) {
        if (!Validacion.nombre(consumidor) || !Validacion.fecha(fecha)) {
            throw new IllegalArgumentException("Consumidor o fecha inválidos.");
        }
        this.consumidor = consumidor.trim();
        this.fecha = fecha;
    }

    public String getConsumidor() { return consumidor; }
    public String getFecha() { return fecha; }
    public abstract String getTipo();
    public abstract double calcularKcal();
    public abstract String describir();
    public abstract String serializar();
}
