import java.time.LocalDate

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Promocionable(val descuento: Double = 0.10)

val rangoFechasPromocion: ClosedRange<LocalDate> =
    LocalDate.of(2026, 11, 1)..LocalDate.of(2026, 11, 30)

class Cliente(
    val nombre: String,
    val correo: String,
    val telefono: String
) {
    operator fun component1(): String = nombre
    operator fun component2(): String = correo
    operator fun component3(): String = telefono

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cliente) return false
        return nombre.equals(other.nombre, ignoreCase = true) &&
                correo.equals(other.correo, ignoreCase = true)
    }

    override fun hashCode(): Int {
        return 31 * nombre.lowercase().hashCode() + correo.lowercase().hashCode()
    }
}

open class Producto(
    val nombre: String,
    val categoria: String,
    val precio: Double
) {
    override operator fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Producto) return false
        return nombre.equals(other.nombre, ignoreCase = true) &&
                categoria.equals(other.categoria, ignoreCase = true)
    }

    override fun hashCode(): Int {
        return 31 * nombre.lowercase().hashCode() + categoria.lowercase().hashCode()
    }
}

@Promocionable(descuento = 0.15)
class PizzaPromocional(nombre: String, precio: Double) : Producto(nombre, "pizza", precio)

@Promocionable(descuento = 0.20)
class ComboPromocional(nombre: String, precio: Double) : Producto(nombre, "combo", precio)

class Pedido(
    val cliente: Cliente,
    val productos: List<Producto>,
    val fechaPedido: LocalDate = LocalDate.now()
) {
    fun calcularTotal(): Double {
        val subtotal = productos.sumOf { it.precio }
        val descuentoPromo = productos.sumOf { prod ->
            val anotacion = prod::class.java.getAnnotation(Promocionable::class.java)
            if (anotacion != null) prod.precio * anotacion.descuento else 0.0
        }
        val descuentoFecha = if (fechaPedido in rangoFechasPromocion) subtotal * 0.10 else 0.0
        return (subtotal - descuentoPromo - descuentoFecha).coerceAtLeast(0.0)
    }

    operator fun plus(otro: Pedido): Pedido {
        return Pedido(
            cliente = this.cliente,
            productos = this.productos + otro.productos,
            fechaPedido = this.fechaPedido
        )
    }

    operator fun component1(): Cliente = cliente
    operator fun component2(): List<Producto> = productos
    operator fun component3(): Double = calcularTotal()
}

fun validarCorreo(entradas: Iterator<String>): String {
    val regexCorreo = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    while (entradas.hasNext()) {
        val correo = entradas.next().trim()
        if (regexCorreo.matches(correo)) {
            return correo
        }
        println("Error: El correo '$correo' es inválido (formato esperado: nombre@dominio.com). Reingrese.")
    }
    return "invalido@dominio.com"
}

fun formatearTelefono(telefono: String): String {
    val digitos = telefono.replace(Regex("\\D"), "")
    val regexDoceDigitos = Regex("^(\\d{2})(\\d{3})(\\d{3})(\\d{4})$")
    val match = regexDoceDigitos.matchEntire(digitos)
    return if (match != null) {
        val (pais, area, p1, p2) = match.destructured
        "+$pais ($area) $p1-$p2"
    } else {
        println("Error: El teléfono '$telefono' debe tener 12 dígitos para el formato +XX (XXX) XXX-XXXX.")
        "+00 (000) 000-0000"
    }
}

fun validarFechaPromocion(fecha: LocalDate): Boolean {
    return if (fecha in rangoFechasPromocion) {
        println("Fecha $fecha dentro del periodo de promociones (${rangoFechasPromocion.start} a ${rangoFechasPromocion.endInclusive}). Aplica descuento.")
        true
    } else {
        println("Error: Fecha $fecha fuera del periodo de promociones (${rangoFechasPromocion.start} a ${rangoFechasPromocion.endInclusive}).")
        false
    }
}

fun validarCantidadProductos(entradas: Iterator<Int>): Int {
    val rangoPermitido = 1..100
    while (entradas.hasNext()) {
        val cantidad = entradas.next()
        if (cantidad in rangoPermitido) {
            return cantidad
        }
        println("Error: La cantidad '$cantidad' está fuera del rango permitido (1-100). Ingrese un valor válido.")
    }
    return 1
}

fun esPromocionable(producto: Producto): Boolean {
    return producto::class.java.isAnnotationPresent(Promocionable::class.java)
}

fun analizarClaseConReflection(clazz: Class<*>) {
    val propiedades = clazz.declaredFields.map { it.name }
    val metodos = clazz.declaredMethods.map { it.name }.filter { !it.contains("$") }.distinct()
    println("Clase ${clazz.simpleName} -> Propiedades: $propiedades | Métodos: $metodos")
}

fun registrarCliente(lista: MutableList<Cliente>, cliente: Cliente) {
    if (lista.contains(cliente)) {
        println("Cliente duplicado omitido: ${cliente.nombre} (${cliente.correo})")
    } else {
        lista.add(cliente)
        println("Cliente registrado: ${cliente.nombre}")
    }
}

fun registrarProducto(lista: MutableList<Producto>, producto: Producto) {
    if (lista.contains(producto)) {
        println("Producto duplicado omitido: ${producto.nombre} [${producto.categoria}]")
    } else {
        lista.add(producto)
        println("Producto registrado: ${producto.nombre} [${producto.categoria}]")
    }
}

fun main() {
    println("=== PASO 1: REGEX Y RANGES ===")
    val correoValido = validarCorreo(listOf("correo_malo", "juan@mammamia.cl").iterator())
    formatearTelefono("12345")
    val telefonoValido = formatearTelefono("569876543210")
    val cantidadValida = validarCantidadProductos(listOf(0, 150, 2).iterator())
    validarFechaPromocion(LocalDate.of(2026, 10, 15))
    val fechaPedido = LocalDate.of(2026, 11, 9)
    validarFechaPromocion(fechaPedido)

    println("\n=== PASO 2: ANOTACIONES Y REFLECTION ===")
    analizarClaseConReflection(Cliente::class.java)
    analizarClaseConReflection(Producto::class.java)
    analizarClaseConReflection(Pedido::class.java)

    val pizzaPepperoni = PizzaPromocional("Pizza Pepperoni", 10000.0)
    val comboFamiliar = ComboPromocional("Combo Familiar", 20000.0)
    val bebidaCola = Producto("Bebida Cola", "bebida", 2500.0)

    println("\n=== PASO 5: IGUALDAD DE OBJETOS (EQUALS Y HASHCODE) ===")
    val clientes = mutableListOf<Cliente>()
    val cliente1 = Cliente("Juan Pérez", correoValido, telefonoValido)
    val clienteDuplicado = Cliente("JUAN PÉREZ", "JUAN@mammamia.cl", "+56 (000) 000-0000")
    registrarCliente(clientes, cliente1)
    registrarCliente(clientes, clienteDuplicado)

    val productosCatalogo = mutableListOf<Producto>()
    val pizzaDuplicada = Producto("pizza pepperoni", "PIZZA", 12000.0)
    registrarProducto(productosCatalogo, pizzaPepperoni)
    registrarProducto(productosCatalogo, pizzaDuplicada)
    registrarProducto(productosCatalogo, comboFamiliar)
    registrarProducto(productosCatalogo, bebidaCola)

    println("\n=== PASO 3 Y 4: OPERATOR OVERLOADING Y DESESTRUCTURACIÓN ===")
    val pedido1 = Pedido(cliente1, List(cantidadValida) { pizzaPepperoni }, fechaPedido)
    val pedido2 = Pedido(cliente1, listOf(comboFamiliar, bebidaCola), fechaPedido)
    val pedidoCombinado = pedido1 + pedido2

    val (nombreCliente, correoCliente, telefonoCliente) = cliente1
    val (clientePedido, productosPedido, totalPedido) = pedidoCombinado

    println("\n=== PASO 6: RESUMEN EN CONSOLA ===")
    println("1. Cliente desestructurado:")
    println("   Nombre: $nombreCliente | Correo: $correoCliente | Teléfono: $telefonoCliente")

    println("2. Productos identificados como @Promocionable:")
    for (prod in productosPedido.filter { esPromocionable(it) }.distinct()) {
        val anotacion = prod::class.java.getAnnotation(Promocionable::class.java)
        println("   - ${prod.nombre} (${prod.categoria}) -> Descuento promoción: ${(anotacion.descuento * 100).toInt()}%")
    }

    println("3. Validación de igualdad de objetos:")
    println("   - cliente1 == clienteDuplicado: ${cliente1 == clienteDuplicado} (hashCode: ${cliente1.hashCode()} == ${clienteDuplicado.hashCode()})")
    println("   - pizzaPepperoni == pizzaDuplicada: ${pizzaPepperoni == pizzaDuplicada} (hashCode: ${pizzaPepperoni.hashCode()} == ${pizzaDuplicada.hashCode()})")

    println("4. Pedido combinado (+) y desestructurado:")
    println("   - Cliente: ${clientePedido.nombre}")
    println("   - Productos (${productosPedido.size}): ${productosPedido.joinToString(", ") { it.nombre }}")
    println("   - Total recalculado con descuentos: $${"%.2f".format(totalPedido)}")
}
