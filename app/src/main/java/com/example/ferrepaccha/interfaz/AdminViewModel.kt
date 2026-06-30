package com.example.ferrepaccha.interfaz

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

//Clase enum para las subpantallas que se desprenden de Administradores
enum class SubPantallaAdmin {
    ADVERTENCIA,
    LOGIN,
    DASHBOARD,
    CARGAR_PRODUCTO,
    GESTION_PEDIDOS,
    DETALLE_PEDIDO
}

class AdminViewModel : ViewModel() {
    //NAVEGACION INTERTA EN APARTADO DE ADMINISTRADORES
    var pantallaActual by mutableStateOf(SubPantallaAdmin.ADVERTENCIA)
        private set

    var nombreAdministrador by mutableStateOf("Christopher")
        private set

    //ESTADO DEL FORMULARIO
    var usuarioInput by mutableStateOf("")
    var contrasenaInput by mutableStateOf("")
    var contrasenaVisible by mutableStateOf(false)
    var mensajeError by mutableStateOf("")

    //FORMULARIO PARA CARGA DE PRODUCTOS
    var nombreProductoInput by mutableStateOf("")
    var codigoProductoInput by mutableStateOf("")
    var precioProductoInput by mutableStateOf("")
    var marcaProductoInput by mutableStateOf("")
    var medidaProductoInput by mutableStateOf("")
    var categoriaProductoInput by mutableStateOf("")
    var menuCategoriasExpandido by mutableStateOf(false)
    var descripcionProductoInput by mutableStateOf("")

    //Funcion para limpiar la pantalla al guardar los productos nuevos
    fun limpiarFormularioProducto() {
        nombreProductoInput = ""
        codigoProductoInput = ""
        precioProductoInput = ""
        marcaProductoInput = ""
        medidaProductoInput = ""
        categoriaProductoInput = ""
        descripcionProductoInput = ""
        menuCategoriasExpandido = false
    }

    //Estados para apartado de atender pedidos
    var busquedaPedidoInput by mutableStateOf("")
    var filtroFechaInput by mutableStateOf("")


    //INTENTOS DE INGRESO
    var intentosFallidos by mutableStateOf(0)
        private set
    var estaBloqueado by mutableStateOf(false)
        private set

    //FUNCIONES PARA LA NAVEGACION
    fun cambiarPantalla(nuevaPantalla: SubPantallaAdmin) {
        pantallaActual = nuevaPantalla

        //Limpiar mensaje de error al continuar a la siguiente pantalla
        if (nuevaPantalla == SubPantallaAdmin.LOGIN) {
            mensajeError = ""
        }
    }

    //VALIDACION DE ACCESO
    fun procesarLogin() {
        if (estaBloqueado) {
            mensajeError = "Acceso bloqueado por seguridad. Demasiados intentos."
            return
        }

        //usuario y contraseña demostrativas, solo para muestra
        if (usuarioInput == "admin" && contrasenaInput == "admin123") {
            intentosFallidos = 0
            mensajeError = ""

            //Si se ingresa con admin se le asigna el nombre al encabezado
            nombreAdministrador = "Christopher"
            cambiarPantalla(SubPantallaAdmin.DASHBOARD)
        } else {
            intentosFallidos++
            if (intentosFallidos >= 5) {
                estaBloqueado = true
                mensajeError = "Área bloqueda. Superó los 5 intentos permitidos."
            } else {
                mensajeError = "Credenciales incorrectas, Intento $intentosFallidos de 5."
            }
        }
    }

    fun cerrarSesion() {
        //Se reinicia las variables de sesion de forma segura
        usuarioInput = ""
        contrasenaInput = ""
        cambiarPantalla(SubPantallaAdmin.ADVERTENCIA)
    }
}