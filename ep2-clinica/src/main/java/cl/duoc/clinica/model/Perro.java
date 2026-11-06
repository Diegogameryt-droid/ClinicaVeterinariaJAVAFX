package cl.duoc.clinica.model;

public class Perro extends Mascota implements Adoptable {
    private String raza;
    private boolean vacunado;
    private boolean disponibleAdopcion;

    public Perro(String nombre, int edad, double peso, String raza, boolean vacunado) {
        super(nombre, edad, peso);
        setRaza(raza);
        setVacunado(vacunado);
        this.disponibleAdopcion = false;
    }

    @Override
    public double calcularCostoConsulta() {
        double costoBase = 15000;
        if (!vacunado) {
            costoBase *= 1.30; // +30% si no está vacunado
        }
        return costoBase;
    }

    @Override
    public String obtenerDetalle() {
        return "Tipo: Perro | Nombre: " + getNombre()
                + " | Edad: " + getEdad() + " años"
                + " | Peso: " + getPeso() + " kg"
                + " | Raza: " + raza
                + " | Vacunado: " + (vacunado ? "Sí" : "No")
                + " | Adopción: " + (disponibleAdopcion ? "Disponible para adopción" : "No disponible")
                + " | Costo consulta: $" + String.format("%.0f", calcularCostoConsulta());
    }

    @Override
    public boolean estaDisponible() {
        return disponibleAdopcion;
    }

    @Override
    public void marcarDisponible() {
        this.disponibleAdopcion = true;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        if (raza == null || raza.trim().isEmpty()) {
            throw new IllegalArgumentException("La raza no puede estar vacía.");
        }
        this.raza = raza.trim();
    }

    public boolean isVacunado() {
        return vacunado;
    }

    public void setVacunado(boolean vacunado) {
        this.vacunado = vacunado;
    }
}
