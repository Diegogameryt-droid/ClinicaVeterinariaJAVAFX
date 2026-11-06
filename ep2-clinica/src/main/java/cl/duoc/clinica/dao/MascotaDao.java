package cl.duoc.clinica.dao;

import cl.duoc.clinica.model.Mascota;

import java.util.List;

public interface MascotaDao<T extends Mascota> {
    /** Lista vacía si el archivo no existe o está vacío. */
    List<T> cargar() throws PersistenciaException;

    /** Reemplaza el contenido del archivo por la lista recibida. */
    void guardar(List<T> mascotas) throws PersistenciaException;
}
