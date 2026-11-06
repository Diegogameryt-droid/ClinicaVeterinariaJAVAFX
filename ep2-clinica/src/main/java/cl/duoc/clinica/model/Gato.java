package cl.duoc.clinica.model;

public class Gato extends Mascota {
    private boolean viveExterior;

    public Gato(String nombre, int edad, double peso, boolean viveExterior) {
        super(nombre, edad, peso);
        setViveExterior(viveExterior);
    }

    @Override
    public double calcularCostoConsulta() {
        double costoBase = 12000;
        if (viveExterior) {
            costoBase *= 1.15; // +15% si vive en exterior
        }
        return costoBase;
    }

    @Override
    public String obtenerDetalle() {
        return "Tipo: Gato | Nombre: " + getNombre()
                + " | Edad: " + getEdad() + " años"
                + " | Peso: " + getPeso() + " kg"
                + " | Vive en: " + (viveExterior ? "Exterior" : "Interior")
                + " | Costo consulta: $" + String.format("%.0f", calcularCostoConsulta());
    }

    public boolean isViveExterior() {
        return viveExterior;
    }

    public void setViveExterior(boolean viveExterior) {
        this.viveExterior = viveExterior;
    }
}
