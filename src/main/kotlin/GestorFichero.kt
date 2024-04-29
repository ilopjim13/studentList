import java.io.File

class GestorFichero :IGestorFichero {
    override fun escribir(fichero: File, info: String): Boolean {
        return try {
            fichero.appendText(info)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun leer(fichero: File): List<String> {
        return fichero.readLines()
    }

    override fun borrarStudents(fichero: File) {
        fichero.writeText("")
    }
}