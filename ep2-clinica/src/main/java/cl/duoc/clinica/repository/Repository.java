package cl.duoc.clinica.repository;

import cl.duoc.clinica.dao.PersistenciaException;

import java.util.List;

public interface Repository<T> {
    void cargar() throws PersistenciaException;

    /** Copia de los elementos en memoria: modificarla no cambia el repositorio. */
    List<T> listar();

    void agregar(T elemento) throws PersistenciaException;

    void eliminar(T elemento) throws PersistenciaException;
}
