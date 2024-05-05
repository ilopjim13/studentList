import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.io.File

class ViewModelStudent(private val fichero: IGestorFichero, private val fileStudiants: File):IViewModelStudent {

    private lateinit var _newStudent: MutableState<String>
    override lateinit var newStudent: State<String>

    private val _students: SnapshotStateList<String> = loadStudents()
    override val students: SnapshotStateList<String> = _students

    private lateinit var _keyPressedState: MutableState<Boolean>
    override lateinit var keyPressedState: State<Boolean>

    private lateinit var _showDialog: MutableState<Boolean>
    override lateinit var showDialog: State<Boolean>

    private lateinit var _selectedIndex: MutableState<Int>
    override lateinit var selectedIndex: State<Int>


    @Composable
    fun IniciarEstados() {
        _newStudent = remember { mutableStateOf("") }
        newStudent = _newStudent
        _keyPressedState = remember { mutableStateOf(false) }
        keyPressedState = _keyPressedState
        _showDialog = remember { mutableStateOf(false) }
        showDialog = _showDialog
        _selectedIndex = remember { mutableStateOf(-1) }
        selectedIndex = _selectedIndex
    }

    override fun addStudents() {
        if (newStudent.value.isNotBlank()) students.add(newStudent.value)
        _newStudent.value = ""
    }

    override fun removeStudents(index: Int) {
        students.removeAt(index)
    }

    override fun clearStundents() {
        students.clear()
    }

    override fun saveChanges() {
        fichero.borrarStudents(fileStudiants)
        students.forEach { fichero.escribir(fileStudiants, "$it\n") }
        setShowDialog(true)
    }

    override fun valueChange(value:String) {
        _newStudent.value = value
    }

    override fun loadStudents(): SnapshotStateList<String> {
        val datos = fichero.leer(fileStudiants)?.toMutableStateList()
        if (datos != null) return datos
        return mutableStateListOf("")
    }

    override fun setSelectedIndex(index: Int) {
        _selectedIndex.value = index
    }

    override fun setKeyPressedState(keyPressed: Boolean) {
        _keyPressedState.value = keyPressed
    }

    override fun setShowDialog(show: Boolean) {
        _showDialog.value = show
    }
}