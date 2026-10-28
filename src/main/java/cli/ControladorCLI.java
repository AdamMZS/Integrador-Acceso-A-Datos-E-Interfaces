package cli;

import dao.GestorArchivos;
import model.Candidatura;
import model.EstadoCandidatura;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ControladorCLI {

    private static final DateTimeFormatter FORM_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final GestorArchivos gestor;

    public ControladorCLI() {
        this(new GestorArchivos());
    }

    public ControladorCLI(GestorArchivos gestor) {
        this.gestor = gestor;
    }

    public int ejecutar(String[] args) {
        if (args.length == 0) {
            return error("Debes indicar una operación. Consulta la ayuda con 'ayuda'.");
        }

        String comando = args[0].toLowerCase(Locale.ROOT);
        if (comando.startsWith("-")) {
            comando = comando.substring(1);
        }

        try {
            return switch (comando) {
                case "listar" -> listar(args);
                case "añadir", "anadir" -> añadir(args);
                case "ver" -> ver(args);
                case "borrar" -> borrar(args);
                case "importar" -> importar(args);
                case "ayuda", "--help", "-h" -> ayuda(args);
                default -> error("Operación desconocida '" + args[0] + "'. Consulta la ayuda con 'ayuda'.");
            };
        } catch (IOException e) {
            return error("No se pudo acceder al archivo: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        }
    }

    private int listar(String[] args) throws IOException {
        Map<String, String> opciones = opciones(args, 1);
        validarOpciones(opciones, "estado", "empresa");
        String filtroEstado = opciones.get("estado");
        String filtroEmpresa = opciones.get("empresa");

        List<Candidatura> candidaturas = gestor.leerTodas().stream()
                .filter(candidatura -> filtroEstado == null
                        || candidatura.estadoCandidatura().name().equalsIgnoreCase(filtroEstado))
                .filter(candidatura -> filtroEmpresa == null
                        || candidatura.empresa().toLowerCase(Locale.ROOT)
                        .contains(filtroEmpresa.toLowerCase(Locale.ROOT)))
                .toList();

        if (candidaturas.isEmpty()) {
            System.out.println("No hay candidaturas que coincidan con los filtros.");
        } else {
            candidaturas.forEach(this::imprimirResumen);
        }
        return 0;
    }

    private int añadir(String[] args) throws IOException {
        Map<String, String> opciones = opciones(args, 1);
        validarOpciones(opciones, "titulo", "empresa", "estado", "fecha");
        exigir(opciones, "titulo", "empresa", "estado");

        String titulo = textoRequerido(opciones.get("titulo"), "titulo");
        String empresa = textoRequerido(opciones.get("empresa"), "empresa");
        EstadoCandidatura estado = parsearEstado(opciones.get("estado"));
        LocalDate fecha = LocalDate.now();
        if (opciones.containsKey("fecha")) {
            try {
                fecha = LocalDate.parse(opciones.get("fecha"), FORM_FECHA);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("La fecha debe tener el formato dd/MM/yyyy y ser válida.");
            }
        }

        long id = gestor.siguienteId();
        gestor.añadir(new Candidatura(id, titulo, empresa, estado, fecha, "", ""));
        System.out.println("Candidatura añadida con ID " + id + ".");
        return 0;
    }

    private int ver(String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Uso: ver <id>. Ejemplo: ver 13");
        }
        long id = parsearId(args[1]);
        Candidatura candidatura = gestor.buscarPorId(id);
        if (candidatura == null) {
            throw new IllegalArgumentException("No existe ninguna candidatura con ID " + id + ".");
        }

        System.out.println("ID: " + candidatura.id());
        System.out.println("Título: " + candidatura.titulo());
        System.out.println("Empresa: " + candidatura.empresa());
        System.out.println("Estado: " + candidatura.estadoCandidatura().name().toLowerCase(Locale.ROOT));
        System.out.println("Fecha: " + candidatura.fechaPostulacion().format(FORM_FECHA));
        System.out.println("Enlace: " + (candidatura.enlace().isBlank() ? "—" : candidatura.enlace()));
        System.out.println("Notas: " + (candidatura.notas().isBlank() ? "—" : candidatura.notas()));
        return 0;
    }

    private int borrar(String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Uso: borrar <id>. Ejemplo: borrar 13");
        }
        long id = parsearId(args[1]);
        if (!gestor.borrar(id)) {
            throw new IllegalArgumentException("No existe ninguna candidatura con ID " + id + ".");
        }
        System.out.println("Candidatura " + id + " eliminada.");
        return 0;
    }

    private int importar(String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Uso: importar <archivo.csv>. Ejemplo: importar candidaturas.csv");
        }
        int importadas = gestor.importar(Path.of(args[1]));
        System.out.println(importadas + " candidatura(s) importada(s).");
        return 0;
    }

    private int ayuda(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Uso: ayuda");
        }
        System.out.println("""
                Uso:
                  listar [--estado X] [--empresa Y]
                  añadir --titulo X --empresa Y --estado Z [--fecha dd/MM/yyyy]
                  ver <id>
                  borrar <id>
                  importar <archivo.csv>

                Estados: pendiente, entrevista, aceptado, rechazado.
                Si no indicas --fecha al añadir, se utiliza la fecha de hoy.
                Los valores con espacios deben escribirse entre comillas.
                El CSV puede llevar cabecera (titulo, empresa, estado, fecha, enlace, notas).
                """);
        return 0;
    }

    private Map<String, String> opciones(String[] args, int inicio) {
        Map<String, String> opciones = new LinkedHashMap<>();
        for (int i = inicio; i < args.length; i++) {
            String argumento = args[i];
            if (!argumento.startsWith("--") || argumento.length() == 2) {
                throw new IllegalArgumentException("Argumento no válido: '" + argumento + "'. Usa opciones --nombre valor.");
            }
            String nombre = argumento.substring(2).toLowerCase(Locale.ROOT);
            if (i + 1 >= args.length || args[i + 1].startsWith("--")) {
                throw new IllegalArgumentException("Falta el valor de la opción --" + nombre + ".");
            }
            if (opciones.putIfAbsent(nombre, args[++i]) != null) {
                throw new IllegalArgumentException("La opción --" + nombre + " está repetida.");
            }
        }
        return opciones;
    }

    private void validarOpciones(Map<String, String> opciones, String... permitidas) {
        for (String opcion : opciones.keySet()) {
            boolean valida = false;
            for (String permitida : permitidas) {
                if (opcion.equals(permitida)) {
                    valida = true;
                    break;
                }
            }
            if (!valida) {
                throw new IllegalArgumentException("Opción desconocida: --" + opcion + ".");
            }
        }
    }

    private void exigir(Map<String, String> opciones, String... requeridas) {
        for (String requerida : requeridas) {
            if (!opciones.containsKey(requerida)) {
                throw new IllegalArgumentException("Falta la opción obligatoria --" + requerida + ".");
            }
        }
    }

    private String textoRequerido(String valor, String nombre) {
        if (valor.isBlank()) {
            throw new IllegalArgumentException("El valor de --" + nombre + " no puede estar vacío.");
        }
        return valor.trim();
    }

    private EstadoCandidatura parsearEstado(String texto) {
        try {
            return EstadoCandidatura.valueOf(texto.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Estado no válido '" + texto + "'. Usa: pendiente, entrevista, aceptado o rechazado.");
        }
    }

    private long parsearId(String texto) {
        try {
            long id = Long.parseLong(texto);
            if (id < 1) {
                throw new NumberFormatException();
            }
            return id;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo: '" + texto + "'.");
        }
    }

    private void imprimirResumen(Candidatura candidatura) {
        System.out.printf("%d | %s | %s | %s | %s%n",
                candidatura.id(),
                candidatura.titulo(),
                candidatura.empresa(),
                candidatura.estadoCandidatura().name().toLowerCase(Locale.ROOT),
                candidatura.fechaPostulacion().format(FORM_FECHA));
    }

    private int error(String mensaje) {
        System.err.println("Error: " + mensaje);
        return 1;
    }
}
