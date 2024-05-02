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

    override fun borrarStudents(fichero: File):Boolean {
        return try {
            fichero.writeText("")
            true
        } catch (e:Exception) {
            false
        }

    }
}