# MammaMia - Sistema de Gestión de Pizzería

Aplicación de consola desarrollada en **Kotlin** para gestionar clientes, catálogo de productos, promociones y pedidos de la pizzería **MammaMia**.

## Funcionalidades principales

- **Validación y formato de datos (`Regex` y `Ranges`):**
  - Validación de formato de correos electrónicos (`nombre@dominio.com`).
  - Formateo estandarizado de números de teléfono (`+XX (XXX) XXX-XXXX`).
  - Control de rangos para cantidades permitidas de productos (`1..100`) y periodos de fechas promocionales (`LocalDate`).
- **Anotaciones y Reflection (`@Promocionable`):**
  - Identificación dinámica de productos y combos en promoción para aplicar descuentos automáticos.
  - Inspección en tiempo de ejecución de las propiedades y métodos de las clases del sistema (`Cliente`, `Producto`, `Pedido`).
- **Sobrecarga de operadores (`+` y `==`):**
  - Fusión de dos pedidos en uno solo mediante el operador `+`, sumando productos y recalculando el total con descuentos.
  - Comparación de productos por nombre y categoría con el operador `==`.
- **Desestructuración de objetos:**
  - Extracción directa de datos clave desde instancias de `Cliente` (nombre, correo, teléfono) y `Pedido` (cliente, productos, total).
- **Control de duplicados (`equals` y `hashCode`):**
  - Evaluación de igualdad personalizada en `Cliente` y `Producto` para evitar registros repetidos en el sistema.

## Estructura del proyecto

- `src/Main.kt`: Contiene el modelo de clases, las funciones de validación y el flujo principal de demostración en consola.
