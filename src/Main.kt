open class Usuario(val nombre: String, val contacto: String)

class Cliente(
    nombre: String,
    val telefono: String,
    val correo: String
) : Usuario(nombre, "Tel: $telefono | Email: $correo")

class Repartidor(
    nombre: String,
    contacto: String,
    val distanciaKm: Int,
    val disponible: Boolean = true
) : Usuario(nombre, contacto) {
    fun entregarPedido(pedido: Pedido) {
        pedido.cambiarEstado("En camino")
        println("El repartidor $nombre está entregando el pedido de ${pedido.cliente.nombre}.")
    }
}

class Producto(
    val nombre: String,
    val precio: Double,
    val ingredientes: List<String>
) {
    fun detallarProducto(): String {
        return "Producto: $nombre | Precio: $$precio | Ingredientes: ${ingredientes.joinToString(", ")}"
    }
}

class Pedido(
    val cliente: Cliente,
    val productos: List<Producto>,
    var estado: String = "En preparación",
    val notas: String? = null
) {
    fun calcularTotal(): Double {
        return calcularTotalPedido(productos)
    }

    fun cambiarEstado(nuevoEstado: String) {
        estado = nuevoEstado
    }

    fun mostrarResumen() {
        val subtotal = productos.sumOf { it.precio }
        val descuento = if (productos.size > 3) subtotal * 0.10 else 0.0
        val baseImponible = subtotal - descuento
        val impuestos = baseImponible * 0.19
        val totalFinal = calcularTotal()
        val comentario = notas?.takeIf { it.isNotBlank() } ?: "Sin notas"

        println("==========================================")
        println("RESUMEN DEL PEDIDO")
        println("Cliente: ${cliente.nombre}")
        println("Contacto: ${cliente.telefono} | ${cliente.correo}")
        println("Productos:")
        val agrupados = productos.groupBy { it.nombre }
        for ((nombreProd, lista) in agrupados) {
            val precioUnitario = lista.first().precio
            val cantidad = lista.size
            println(" - $nombreProd | Precio: $$precioUnitario | Cantidad: $cantidad")
        }
        println("Subtotal: $$subtotal")
        println("Descuento aplicado: $$descuento")
        println("Impuestos (19%): $${"%.2f".format(impuestos)}")
        println("Total final: $${"%.2f".format(totalFinal)}")
        println("Estado del pedido: $estado")
        println("Notas: $comentario")
        println("==========================================")
    }
}

fun registrarCliente(nombre: String, telefonoInput: String, correoInput: String): Cliente {
    val regexTelefono = Regex("^\\(?\\d{3}\\)?[-\\s]?\\d{3}[-\\s]?\\d{4}$|^\\d{9}$")
    val regexCorreo = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    val telefonoValido = if (regexTelefono.matches(telefonoInput)) telefonoInput else "(000) 000-0000"
    val correoValido = if (regexCorreo.matches(correoInput)) correoInput else "correo@invalido.com"

    return Cliente(nombre, telefonoValido, correoValido)
}

fun verificarIngredientes(ingredientesNecesarios: List<String>, inventario: Map<String, Int>): Boolean {
    val requeridosPorTipo = ingredientesNecesarios.groupingBy { it }.eachCount()
    val faltantes = requeridosPorTipo.filter { (ingrediente, cantidadNecesaria) ->
        (inventario[ingrediente] ?: 0) < cantidadNecesaria
    }

    return when {
        faltantes.isEmpty() -> {
            println("Inventario verificado: Todos los ingredientes están disponibles.")
            true
        }
        else -> {
            println("Faltan ingredientes: ${faltantes.keys.joinToString(", ")}")
            println("Pasos para reabastecer:")
            println("1. Contactar al proveedor de ${faltantes.keys.joinToString(", ")}.")
            println("2. Realizar orden de compra de emergencia.")
            println("3. Recepcionar y actualizar stock en el sistema.")
            false
        }
    }
}

fun calcularTotalPedido(productos: List<Producto>): Double {
    val subtotal = productos.sumOf { it.precio }
    val descuento = if (productos.size > 3) subtotal * 0.10 else 0.0
    val subtotalConDescuento = subtotal - descuento
    val impuestos = subtotalConDescuento * 0.19
    return subtotalConDescuento + impuestos
}

fun prepararLotePizzas(
    ingredientesPorPizza: List<String>,
    cantidadLote: Int,
    inventario: MutableMap<String, Int>
) {
    val totalNecesarioPorIngrediente = ingredientesPorPizza.groupingBy { it }.eachCount()
        .mapValues { it.value * cantidadLote }

    println("Cantidad necesaria para lote de $cantidadLote pizzas: $totalNecesarioPorIngrediente")

    for ((ingrediente, cantidadNecesaria) in totalNecesarioPorIngrediente) {
        val disponible = inventario[ingrediente] ?: 0
        inventario[ingrediente] = (disponible - cantidadNecesaria).coerceAtLeast(0)
    }
    println("Inventario actualizado: $inventario")
}

fun validarEntradaDatos(costoInput: String, cantidadInventarioInput: String) {
    try {
        val costo = costoInput.toDouble()
        val cantidad = cantidadInventarioInput.toInt()
        if (costo <= 0) throw IllegalArgumentException("El costo del pedido debe ser mayor a 0.")
        if (cantidad < 0) throw IllegalArgumentException("La cantidad en el inventario no puede ser negativa.")
        println("Datos validados correctamente: Costo = $$costo, Cantidad Inventario = $cantidad")
    } catch (e: NumberFormatException) {
        println("Error de formato: Ingrese valores numéricos válidos. Corriga los datos e intente nuevamente.")
    } catch (e: IllegalArgumentException) {
        println("Error de validación: ${e.message} Por favor, corrija los datos.")
    }
}

fun main() {
    val inventario = mutableMapOf(
        "Masa" to 10,
        "Salsa de tomate" to 10,
        "Queso mozzarella" to 10,
        "Pepperoni" to 8,
        "Champiñones" to 5,
        "Albahaca" to 2
    )

    println("--- Validación de Errores (try-catch) ---")
    validarEntradaDatos("-5000", "10")
    validarEntradaDatos("8990", "-3")
    validarEntradaDatos("abc", "5")
    validarEntradaDatos("8990", "10")

    println("\n--- Productos Disponibles ---")
    val pizzaPepperoni = Producto("Pizza Pepperoni", 8990.0, listOf("Masa", "Salsa de tomate", "Queso mozzarella", "Pepperoni"))
    val pizzaMargarita = Producto("Pizza Margarita", 7990.0, listOf("Masa", "Salsa de tomate", "Queso mozzarella", "Albahaca"))
    val pizzaChampi = Producto("Pizza Champiñones", 8490.0, listOf("Masa", "Salsa de tomate", "Queso mozzarella", "Champiñones"))
    println(pizzaPepperoni.detallarProducto())
    println(pizzaMargarita.detallarProducto())
    println(pizzaChampi.detallarProducto())

    val cliente1 = registrarCliente("Juan Pérez", "987654321", "juan.perez@email.com")
    val cliente2 = registrarCliente("María López", "123", "correo_invalido")
    val cliente3 = registrarCliente("Pedro Gómez", "(123) 456-7890", "pedro@duoc.cl")

    println("\n--- Verificación de Ingredientes y Preparación de Lote ---")
    val productosPedido1 = listOf(pizzaPepperoni, pizzaPepperoni, pizzaMargarita, pizzaChampi)
    val ingredientesPedido1 = productosPedido1.flatMap { it.ingredientes }
    if (verificarIngredientes(ingredientesPedido1, inventario)) {
        prepararLotePizzas(pizzaPepperoni.ingredientes, 2, inventario)
    }

    val pedidoExcesivo = listOf(pizzaMargarita, pizzaMargarita, pizzaMargarita)
    verificarIngredientes(pedidoExcesivo.flatMap { it.ingredientes }, inventario)

    val pedido1 = Pedido(cliente1, productosPedido1, "En preparación", "Sin cebolla y cortar en 8 porciones")
    val pedido2 = Pedido(cliente2, listOf(pizzaPepperoni, pizzaChampi), "Listo", null)
    val pedido3 = Pedido(cliente3, listOf(pizzaMargarita), "En preparación", "")

    println("\n--- Asignación de Repartidor ---")
    val repartidores = listOf(
        Repartidor("Carlos Soto", "911111111", distanciaKm = 5),
        Repartidor("Ana Torres", "922222222", distanciaKm = 2),
        Repartidor("Luis Rojas", "933333333", distanciaKm = 4)
    )
    val repartidorMasCercano = repartidores.sortedBy { it.distanciaKm }.find { it.disponible }
    repartidorMasCercano?.entregarPedido(pedido1)

    println("\n--- Notificación a Clientes Registrados (let) ---")
    val clienteRegistrado: Cliente? = cliente1
    clienteRegistrado?.let {
        println("Notificación enviada a ${it.nombre} (${it.correo}): Su pedido se encuentra '${pedido1.estado}'.")
    }

    val pedidosDelDia: Array<Pedido> = arrayOf(pedido1, pedido2, pedido3)

    println("\n--- Reporte de Pedidos del Día (Arreglo y Ciclo for) ---")
    for (pedido in pedidosDelDia) {
        val nombresPizzas = pedido.productos.joinToString(", ") { it.nombre }
        println("Cliente: ${pedido.cliente.nombre} | Pizzas: $nombresPizzas | Total: $${"%.2f".format(pedido.calcularTotal())} | Estado: ${pedido.estado}")
    }

    val pedidosCompletados = pedidosDelDia.filter { it.estado == "Listo" }
    val pedidosPendientes = pedidosDelDia.filter { it.estado != "Listo" }

    println("\nPedidos Completados (${pedidosCompletados.size}): ${pedidosCompletados.map { it.cliente.nombre }}")
    println("Pedidos Pendientes (${pedidosPendientes.size}): ${pedidosPendientes.map { it.cliente.nombre }}")

    println("\n--- Resumen Final de Cada Pedido ---")
    for (pedido in pedidosDelDia) {
        pedido.mostrarResumen()
    }
}
