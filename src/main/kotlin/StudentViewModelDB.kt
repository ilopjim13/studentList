import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList

class StudentViewModelDB(private val studentRepo: IRepo):IViewModelStudent {


    private val _newStudent = mutableStateOf("")
    override val newStudent: State<String> = _newStudent

    private val _students = mutableStateListOf("")
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
        studentRepo.updateStudents(students)
        setShowDialog(true)
    }

    override fun editName(index: Int, value: String) {
        _students[index] = value
    }

    override fun valueChange(value: String) {
        _newStudent.value = value
    }

    override fun loadStudents(){
        val result = studentRepo.getAllStudents()
        result.onSuccess{ student ->
            _students.clear()
            student.forEach {
                _students.add(it)
            }
        }.onFailure {
            throw DatabaseTimeoutException("${it.message}")
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