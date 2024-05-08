import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.io.File

class ViewModelStudent(private val fichero: IGestorFichero, private val fileStudiants: File):IViewModelStudent {

    private val _newStudent: MutableState<String> = mutableStateOf("")
    override val newStudent: State<String> = _newStudent


    private val _students: SnapshotStateList<String> = mutableStateListOf("")
    override val students: List<String> = _students

    private val _keyPressedState: MutableState<Boolean> = mutableStateOf(false)
    override val keyPressedState: State<Boolean> = _keyPressedState

    private val _showDialog: MutableState<Boolean> = mutableStateOf(false)
    override val showDialog: State<Boolean> = _showDialog

    private val _selectedIndex: MutableState<Int> = mutableStateOf(-1)
    override val selectedIndex: State<Int> = _selectedIndex

    init {
        loadStudents()
    }

    override fun addStudents() {
        if (newStudent.value.isNotBlank()) _students.add(newStudent.value)
        _newStudent.value = ""
    }

    override fun removeStudents(index: Int) {
        _students.removeAt(index)
    }

    override fun clearStundents() {
        _students.clear()
    }

    override fun saveChanges() {
        fichero.borrarStudents(fileStudiants)
        students.forEach { fichero.escribir(fileStudiants, "$it\n") }
        setShowDialog(true)
    }

    override fun editName(index: Int, value: String) {
        _students[index] = value
    }

    override fun valueChange(value:String) {
        _newStudent.value = value
    }

    override fun loadStudents() {
        _students.clear()
        fichero.leer(fileStudiants)?.toMutableStateList()?.forEach {
            _students.add(it)
        }

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