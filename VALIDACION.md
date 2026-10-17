# Validación realizada

Fecha: 16 de octubre de 2026.

## Compilación

Se compiló el código con el JDK instalado en
`C:\Program Files\Java\jdk-21.0.12.1\bin`, usando `javac -encoding UTF-8 -Xlint:all`.
Resultado: compilación correcta, sin advertencias.

## Pruebas de dominio y archivos

Un programa de pruebas externo al proyecto ejecutó **47 comprobaciones correctas**:

- Carga de ocho registros; cantidades totales y por usuario.
- Litros totales, kcal por persona y total sin redondeos intermedios.
- Días ebrio: suma de consumos en una fecha, umbral exacto y fechas distintas.
- Agregar, editar y eliminar; recargar desde disco y verificar los cambios.
- Desplazamiento de índices tras eliminar e índices inválidos.
- Fechas imposibles, años bisiestos y formato estricto.
- Números vacíos, negativos, cero, texto, infinito, NaN y coma decimal.
- Nombres repetidos, marca BOM, líneas mal formadas y consumidores desconocidos.
- Creación de Consumido.txt cuando no existe.
- Límite de 200 consumos y bloqueo de escritura ante una carga de 201 registros.
- Fallo de escritura simulado: se conservan la memoria y el archivo original.
- Recarga de decimales pequeños y grandes que Java serializa usando exponentes.
- Umbral de dos litros acumulando veinte consumos de 0,1 L y consumos de 0,7,
  0,7 y 0,6 L; suma compensada para reducir errores de precisión.
- Protección del respaldo si falta el archivo original.

Las pruebas externas pueden usar utilidades adicionales; esas utilidades no forman
parte del código Java entregable. El programa del taller solo importa las cinco
clases permitidas por el enunciado.

## Pruebas de consola

Se iniciaron diez procesos independientes de `taller02.Main`, cada uno sobre una
carpeta de prueba, verificando su salida, terminación y ausencia de excepciones:

1. Letras, opción fuera de rango, entrada vacía y salida.
2. Listados de ambos tipos y retorno al menú principal.
3. Las seis métricas y retorno.
4. Cancelación de alta, litros negativos, fecha inválida y alta válida.
5. Índice inválido al editar y edición conservando campos vacíos.
6. Confirmación inválida, cancelación y eliminación confirmada.
7. Cancelación por índice cero al editar y eliminar.
8. Fin de entrada en el menú principal.
9. Fin de entrada durante el alta, sin guardar datos incompletos.
10. Archivos inexistentes y tentativa de alta sin participantes.

Resultado: **10 de 10 casos correctos**. La comparación acepta el separador decimal
que corresponda a la configuración regional del sistema. Los archivos incluidos
en el proyecto conservan los ocho registros iniciales.

## Alcance y pendientes

La compilación y las pruebas automatizadas fueron ejecutadas con Java desde
herramientas de archivos y procesos. Además, se importó el proyecto desde la
interfaz de Eclipse, se ejecutó `Main` y se comprobó que su consola carga ocho
registros y muestra los valores esperados de kcal por usuario y el total 58,83.
Los listados de ambos tipos también se observaron funcionando en Eclipse.

La revisión encontró y corrigió una incompatibilidad entre la notación científica
generada al guardar números y el lector de datos. También se mejoró la precisión
al sumar litros y se bloquearon cambios si falla la lectura de participantes.
Ambos integrantes están en las primeras dos líneas de Main.java; Eclipse puede
plegar el comentario y mostrar únicamente su primera línea.

No se evaluó el README general de talleres, que no fue proporcionado.
El repositorio del equipo es https://github.com/shrd-cl/Taller-02-POO.
