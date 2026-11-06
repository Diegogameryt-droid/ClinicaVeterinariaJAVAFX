package cl.duoc.clinica.repository;

import cl.duoc.clinica.dao.MascotaDao;
import cl.duoc.clinica.dao.PersistenciaException;
import cl.duoc.clinica.model.Gato;
import cl.duoc.clinica.model.Mascota;
import cl.duoc.clinica.model.Perro;

import java.util.ArrayList;
import java.util.List;

public class MascotaRepository implements Repository<Mascota> {
    private final MascotaDao<Perro> daoPerros;
    private final MascotaDao<Gato> daoGatos;
    private final List<Mascota> mascotas = new ArrayList<>();

    public MascotaRepository(MascotaDao<Perro> daoPerros, MascotaDao<Gato> daoGatos) {
        this.daoPerros = daoPerros;
        this.daoGatos = daoGatos;
    }

    @Override
    public void cargar() throws PersistenciaException {
        List<Mascota> todas = new ArrayList<>(daoPerros.cargar());
        todas.addAll(daoGatos.cargar());
        mascotas.clear();
        mascotas.addAll(todas);
    }

    @Override
    public List<Mascota> listar() {
        return new ArrayList<>(mascotas);
    }

    @Override
    public void agregar(Mascota mascota) throws PersistenciaException {
        List<Mascota> copia = new ArrayList<>(mascotas);
        copia.add(mascota);
        guardarArchivoDe(mascota, copia);
        mascotas.add(mascota);
    }

    @Override
    public void eliminar(Mascota mascota) throws PersistenciaException {
        List<Mascota> copia = new ArrayList<>(mascotas);
        copia.remove(mascota);
        guardarArchivoDe(mascota, copia);
        mascotas.remove(mascota);
    }

    /** Reescribe solo el archivo del tipo de la mascota modificada. */
    private void guardarArchivoDe(Mascota modificada, List<Mascota> copia) throws PersistenciaException {
        if (modificada instanceof Perro) {
            daoPerros.guardar(filtrar(copia, Perro.class));
        } else if (modificada instanceof Gato) {
            daoGatos.guardar(filtrar(copia, Gato.class));
        }
    }

    private static <T extends Mascota> List<T> filtrar(List<Mascota> lista, Class<T> clase) {
        return lista.stream().filter(clase::isInstance).map(clase::cast).toList();
    }
}
