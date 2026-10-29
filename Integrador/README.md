# Gestor de candidaturas

Aplicación de línea de comandos (CLI) en Java para llevar el seguimiento de tus candidaturas de empleo: añadir, listar, consultar, borrar e importar desde CSV. Los datos se guardan en un fichero de texto plano, sin base de datos.

## Requisitos

- **Java 17 o superior** (el código usa `record`, `switch` con flechas y bloques de texto).
- Solo la biblioteca estándar de Java. No necesita dependencias externas para ejecutarse.
- Maven (opcional, si el proyecto se compila con `pom.xml`).

## Compilación y ejecución

### Con javac

Desde la raíz del proyecto:

```bash
javac -d out $(find src/main/java -name "*.java")
java -cp out main.Main ayuda
```

### Con Maven

```bash
mvn compile
java -cp target/classes main.Main ayuda
```

Todos los comandos siguientes se muestran como `java -cp <classpath> main.Main ...`. Sustituye `<classpath>` por `out` o `target/classes` según cómo compiles.

## Uso

```
listar [--estado X] [--empresa Y]
añadir --titulo X --empresa Y --estado Z [--fecha dd/MM/yyyy]
ver <id>
borrar <id>
importar <archivo.csv>
ayuda
```

### Comandos

| Comando | Descripción |
|---|---|
| `listar` | Muestra todas las candidaturas. Admite filtros opcionales por estado (coincidencia exacta) y por empresa (coincidencia parcial, sin distinguir mayúsculas). |
| `añadir` | Añade una candidatura. `--titulo`, `--empresa` y `--estado` son obligatorios. Si no se indica `--fecha`, se usa la fecha de hoy. |
| `ver` | Muestra todos los datos de una candidatura, incluidos enlace y notas. |
| `borrar` | Elimina una candidatura por su ID. |
| `importar` | Añade candidaturas desde un fichero CSV. |
| `ayuda` | Muestra la ayuda de uso. |



### Notas de uso

- Los valores con espacios deben ir entre comillas.
- El nombre del comando admite una barra inicial (`-listar`) y las variantes `anadir` (sin tilde), `--help` y `-h`.
- Las opciones se escriben como `--nombre valor`. Una opción no puede repetirse.
- Las fechas se escriben siempre como `dd/MM/yyyy` y deben existir en el calendario (por ejemplo, `31/02/2026` no es válida).

## Estados

Una candidatura puede estar en uno de estos cuatro estados:

- `pendiente`
- `entrevista`
- `aceptado`
- `rechazado`

Se aceptan en cualquier combinación de mayúsculas y minúsculas al escribirlos, pero se guardan siempre en minúsculas.

## Almacenamiento

Las candidaturas se guardan en el fichero `candidaturas.txt`, que se crea en el directorio desde el que ejecutas el programa. Cada línea es una candidatura con siete campos separados por `;`:

```
id;titulo;empresa;estado;fecha;enlace;notas
```

Ejemplo:

```
1;Desarrollador Java;Acme;pendiente;10/10/2026;https://acme.example/empleo;Enviada por LinkedIn
2;Backend Developer;Globex;entrevista;15/03/2026;;
```

- `id` es un número entero. Al añadir, se asigna el siguiente al mayor existente.
- `fecha` usa el formato `dd/MM/yyyy`.
- Si un texto contiene `;`, se sustituye por `,` al guardarlo, para no romper el formato.
- Las líneas en blanco se ignoran al leer.

## Importación desde CSV

El comando `importar` acepta CSV con o sin cabecera. El fichero se lee en UTF-8 y se admite BOM al inicio.

### Con cabecera

La cabecera debe contener, como mínimo, las columnas `titulo`, `empresa`, `estado` y `fecha`. Se reconocen también estos nombres alternativos:

| Campo | Nombres aceptados |
|---|---|
| titulo | `titulo`, `title` |
| empresa | `empresa`, `company` |
| estado | `estado`, `status` |
| fecha | `fecha`, `fecha_postulacion`, `date`, `application_date` |
| enlace | `enlace`, `link`, `url` (opcional) |
| notas | `notas`, `notes` (opcional) |

Ejemplo:

```csv
titulo,empresa,estado,fecha,enlace,notas
Desarrollador Java,Acme,pendiente,10/10/2026,https://acme.example/empleo,
Backend Developer,Globex,entrevista,15/03/2026,,"Prueba técnica, la semana siguiente"
```

### Sin cabecera

Las columnas deben ir en este orden: `titulo, empresa, estado, fecha` y, opcionalmente, `enlace` y `notas`. Si la fila tiene siete campos y el primero es un número, se interpreta como ID y se ignora.

```csv
Desarrollador Java,Acme,pendiente,10/10/2026
Backend Developer,Globex,entrevista,15/03/2026,,
```

### Reglas generales

- Los campos con comas van entre comillas dobles. Las comillas dentro de un campo se escriben duplicadas (`""`).
- Las filas vacías se ignoran.
- Las candidaturas importadas reciben IDs nuevos correlativos a partir del último existente.
- La importación es todo o nada: si una fila tiene errores, no se guarda ninguna y el programa indica la línea con el problema.

## Códigos de salida y errores

- Si todo va bien, el programa termina con código `0`.
- Si hay un error (argumentos incorrectos, candidatura no encontrada, fichero no accesible, datos no válidos), muestra un mensaje en la salida de error que empieza por `Error:` y termina con código `1`.

## Estructura del proyecto

```
src/main/java/
├── main/
│   └── Main.java                  Punto de entrada
├── cli/
│   └── ControladorCLI.java        Interpreta los comandos y las opciones
├── dao/
│   └── GestorArchivos.java        Lectura y escritura de candidaturas.txt, importación CSV
├── model/
│   ├── Candidatura.java           Record con los datos de una candidatura
│   └── EstadoCandidatura.java     Enumerado de estados
├── mapper/
│   └── GestionCandidaturas.java   Conversión entre línea de texto y Candidatura
└── Exception/
    └── TrackerException.java      Excepción no comprobada del proyecto
```

`ControladorCLI` solo habla con `GestorArchivos` y el modelo. `Main` no contiene lógica: crea el controlador, le pasa los argumentos y transforma el resultado en código de salida.

## Limitaciones conocidas

- La clase `GestionCandidaturas` duplica la lógica de conversión que ya existe en `GestorArchivos`, y actualmente no se usa desde la CLI.
- Los datos no se pueden editar: para cambiar una candidatura hay que borrarla y añadirla de nuevo.
- No hay control de concurrencia: si se ejecutan dos instancias a la vez, pueden sobrescribirse los cambios.
- El fichero de datos es siempre `candidaturas.txt` en el directorio de ejecución.

## Tests

Si añades JUnit 5, los tests irían en `src/test/java` con la misma estructura de paquetes que el código.

```bash
mvn test
```
