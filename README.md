# Taller 02 — La fonda del Papu

Aplicación Java de consola para administrar consumos de choripanes y terremotos.

**Integrantes:** Renato Martínez (ITI) y Alonso Arriagada (ITI). Los datos completos
solicitados están en las primeras dos líneas de `src/taller02/Main.java`.

## Requisitos y ejecución en Eclipse

- JDK 21 y Eclipse IDE for Java Developers.
- No requiere dependencias externas, Maven ni Gradle.

1. En Eclipse, seleccione **File → Import → General → Existing Projects into Workspace**.
2. En **Select root directory**, seleccione la carpeta `Taller02Fonda`, donde se
   encuentran `.project`, `.classpath` y este README. Finalice con **Finish**.
3. Abra `src/taller02/Main.java` y elija **Run As → Java Application**.
4. Interactúe en la pestaña **Console**. Los menús aceptan las opciones numéricas mostradas.

Si el proyecto ya aparece en **Package Explorer**, puede ejecutarlo directamente
desde `Main.java`; no necesita volver a importarlo.

Los archivos `Participantes.txt` y `Consumido.txt` deben estar en la raíz del proyecto,
no dentro de `src`. En **Run Configurations → Arguments → Working directory** use
la raíz de `Taller02Fonda` (el valor predeterminado de ejecución del proyecto).
El proyecto usa UTF-8. Si hay problemas con tildes, compruebe la codificación de
la consola en **Run Configurations → Common → Encoding → UTF-8**.

## Clonación y ejecución desde una terminal

El repositorio local está inicializado con Git. Falta vincularlo al repositorio
de GitHub del equipo. Una vez publicado, reemplace `URL_DEL_REPOSITORIO_DEL_EQUIPO`
por su URL real:

```text
git clone URL_DEL_REPOSITORIO_DEL_EQUIPO Taller02Fonda
cd Taller02Fonda
javac -encoding UTF-8 -d bin src/taller02/*.java
java -cp bin taller02.Main
```

En Eclipse también puede importar la carpeta resultante de la clonación mediante
el procedimiento anterior. Mantenga commits descriptivos al realizar mejoras.
El historial local actual fue creado por Codex; no sustituye el desarrollo y
los commits propios del equipo ni acredita por sí solo trabajo en GitHub.

## Funcionalidades

- Ver ambos tipos de consumo con consumidor, dimensiones o litros, fecha y kcal.
- Agregar, editar o eliminar registros; guardar cada cambio en `Consumido.txt`.
- Editar dejando campos vacíos para conservar el valor previo.
- Cancelar la selección de un registro o participante con `0`.
- Confirmar eliminaciones con `s` o cancelarlas con `n`.
- Calcular cantidades totales y por usuario, litros, kcal y días ebrio.
- Volver desde cada submenú y salir también al terminar la entrada de consola.

## Datos y reglas

`Participantes.txt`: un nombre por línea. No admite nombres vacíos, repetidos
(ignorando mayúsculas) ni punto y coma en el nombre.

`Consumido.txt`:

```text
Choripan;Consumidor;Largo;Ancho;YYYY-MM-DD
Terremoto;Consumidor;Litros;YYYY-MM-DD
```

El consumidor debe existir en `Participantes.txt`. Se aceptan números decimales
positivos con punto o coma y notación científica; se guardan con punto. Las fechas
se validan, incluidos años bisiestos, con años de 0001 a 9999. Se rechazan valores numéricos que
desborden los cálculos. Cada archivo admite hasta 200 registros válidos.

- Choripán: kcal = `(largo * ancho) / 3.0`.
- Terremoto: kcal = `(litros * 10) / 3.0`.
- Cada terremoto registrado cuenta como una unidad, cualquiera sea su volumen.
- Un día ebrio requiere al menos 2 litros acumulados por la misma persona y fecha.

Las fórmulas son las del taller. Se redondea únicamente al mostrar dos decimales.
Con los ocho registros incluidos, el total es **58,83 kcal**; el ejemplo del
enunciado muestra 58,84, valor que coincide con sumar los totales por usuario
previamente redondeados. El programa conserva la precisión hasta la presentación.

## Diseño POO

| Clase | Responsabilidad |
|---|---|
| `Main` | Iniciar la aplicación |
| `Aplicacion` | Menús, selección y coordinación de las operaciones |
| `Entrada` | Validar entrada de consola y detectar fin de entrada |
| `Consumo` | Abstracción de consumidor, fecha y comportamiento común |
| `Choripan` / `Terremoto` | Datos específicos, fórmulas, despliegue y serialización |
| `RegistroFonda` | Vectores, lectura de archivos y persistencia |
| `Metricas` | Cantidades, kcal, litros y agrupación por fecha |
| `Validacion` | Nombres, números y fechas |

Los atributos que contienen los datos son privados. Los consumos se almacenan en `Consumo[200]` y se
tratan polimórficamente para calcular kcal, mostrar y guardar. No hay colecciones
dinámicas. Las únicas importaciones del programa son `Scanner`, `File`,
`FileWriter`, `BufferedWriter` e `IOException`; las demás clases utilizadas
pertenecen a `java.lang`, disponible automáticamente.

## Errores y persistencia

Las entradas inválidas vuelven a solicitarse sin finalizar el programa. Las líneas
mal formadas se informan y descartan. Los archivos ausentes generan advertencias;
sin participantes no es posible agregar consumos. Un `Consumido.txt` inexistente
se crea al guardar el primer registro válido.

Para guardar se genera `Consumido.txt.tmp`, se conserva la versión anterior en
`Consumido.txt.bak` y se reemplaza el original. La memoria cambia después de
guardar correctamente. Si el guardado falla, se informa el error y se conserva
la memoria; si falla también la restauración, el mensaje indica el respaldo que
debe recuperar. Este reemplazo con `File.renameTo` no es una transacción atómica
y no está pensado para varias instancias simultáneas ni ediciones externas
mientras se ejecuta la aplicación.

Si la lectura falla o existen más de 200 consumos válidos, se bloquea la escritura
para evitar sobrescribir registros que no pudieron cargarse. Revise el archivo y
reinicie. Si falta el original pero existe `.bak`, recupérelo antes de reiniciar.
Las líneas defectuosas descartadas no se conservan al guardar cambios válidos.

## Comprobación manual

1. Ejecute con los datos incluidos: deben cargarse ocho registros.
2. Verifique cuatro choripanes y cuatro terremotos, con 4,75 litros en total.
3. En kcal: Koshao 25,50; Lemus 17,67; MrNacho 15,67; total 58,83.
4. En días ebrio: Koshao 1 (2026-09-18, 2,25 L); Lemus 0; MrNacho 1
   (2026-09-19, 2,00 L).
5. Agregue un consumo, edítelo y reinicie para comprobar su persistencia.
6. Elimínelo, confirme con `s` y reinicie para comprobar la eliminación.
7. Pruebe letras, entrada vacía, índices fuera de rango, litros negativos,
   fecha 2026-02-30 y las opciones de cancelar y volver.
8. En una copia de prueba, pruebe archivos ausentes, líneas inválidas y 200
   consumos. El registro número 201 debe rechazarse sin modificar el archivo.

La validación automática realizada se documenta en `VALIDACION.md`.

## Pendientes para la entrega del equipo

- Revisar el README general de talleres referido por el enunciado: no fue adjuntado
  y sus reglas adicionales no están verificadas.
- Crear o vincular el repositorio de GitHub del equipo, publicarlo y sustituir la
  URL de clonación de ejemplo por su URL real.
- Revisar y comprender el código; el enunciado solicita originalidad del equipo.

## Revisión frente al enunciado adjunto

| Requisito | Estado y evidencia |
|---|---|
| POO, encapsulamiento, abstracción y polimorfismo | Implementados en las clases del dominio y sus operaciones comunes |
| Solo vectores y capacidad de 200 | Vectores de capacidad fija; rechazo de un consumo adicional probado |
| Importaciones permitidas | Solo Scanner, File, FileWriter, BufferedWriter e IOException |
| Lectura, menús, listados y modificaciones | Implementados y probados; ambos tipos tienen sus campos específicos |
| Persistencia y métricas | Probadas, incluida recarga después de agregar, editar y eliminar |
| Control de errores | Probado con entradas inválidas, índices incorrectos, archivos faltantes y líneas defectuosas |
| Datos de integrantes en primeras cinco líneas | Ambos integrantes aparecen en las primeras dos líneas de Main.java |
| Compilación y ejecución | Compilación sin advertencias y ejecución comprobada en Eclipse |
| Documentación de ejecución y testeo | Instrucciones y comprobación manual en este README; resultados en VALIDACION.md |
| GitHub e historial de desarrollo | Hay commits locales; falta GitHub y no se acredita el proceso de trabajo del equipo |
| Reglas del README general y originalidad | Requieren revisión adicional del equipo; no se consideran verificadas |

La revisión cubre el enunciado adjunto y los casos de prueba documentados; no
equivale a garantizar una calificación ni ausencia de fallos en cualquier entorno.
