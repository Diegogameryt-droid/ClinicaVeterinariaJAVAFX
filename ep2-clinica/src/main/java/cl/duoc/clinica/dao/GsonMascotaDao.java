package cl.duoc.clinica.dao;

import cl.duoc.clinica.model.Mascota;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GsonMascotaDao<T extends Mascota> implements MascotaDao<T> {
    private final Path archivo;
    private final Type tipoLista;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public GsonMascotaDao(Path archivo, Class<T> clase) {
        this.archivo = archivo;
        // List<T> pierde su tipo al compilar: TypeToken lo reconstruye para Gson.
        this.tipoLista = TypeToken.getParameterized(List.class, clase).getType();
    }

    @Override
    public List<T> cargar() throws PersistenciaException {
        if (!Files.exists(archivo)) {
            return new ArrayList<>(); // primera ejecución: no es un error
        }
        try (Reader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            List<T> mascotas = gson.fromJson(lector, tipoLista);
            if (mascotas == null) {
                return new ArrayList<>(); // archivo vacío
            }
            if (mascotas.contains(null)) {
                throw new PersistenciaException("No se pudo leer " + archivo.getFileName()
                        + ": el archivo contiene un elemento vacío.", null);
            }
            return new ArrayList<>(mascotas);
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException("No se pudo leer " + archivo.getFileName()
                    + ": el archivo está dañado.", e);
        }
    }

    @Override
    public void guardar(List<T> mascotas) throws PersistenciaException {
        try (Writer escritor = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            gson.toJson(mascotas, tipoLista, escritor);
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException("No se pudo guardar " + archivo.getFileName()
                    + ". Revisa los permisos y el espacio en disco.", e);
        }
    }
}
