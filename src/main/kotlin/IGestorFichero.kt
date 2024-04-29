import java.io.File

interface IGestorFichero {
    fun escribir(fichero: File, info: String):Boolean
    fun leer(fichero: File) : List<String>?
    fun borrarStudents(fichero: File)
}