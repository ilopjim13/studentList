import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList

class StudentViewModelDB(val studentRepo: StudentRepo):IViewModelStudent {


    private val _newStudent = mutableStateOf("")
    override val newStudent: State<String> = _newStudent

    private val _students = mutableStateListOf("")
    override val students: List<String> = _students

    override val keyPressedState: State<Boolean>
        get() = TODO("Not yet implemented")
    override val showDialog: State<Boolean>
        get() = TODO("Not yet implemented")
    override val selectedIndex: State<Int>
        get() = TODO("Not yet implemented")

    override fun addStudents() {
        TODO("Not yet implemented")
    }

    override fun removeStudents(index: Int) {
        TODO("Not yet implemented")
    }

    override fun clearStundents() {
        TODO("Not yet implemented")
    }

    override fun saveChanges() {
        TODO("Not yet implemented")
    }

    override fun valueChange(value: String) {
        TODO("Not yet implemented")
    }

    override fun loadStudents(){
        val result = studentRepo.getAllStudents()
        result.onSuccess{

        }.onFailure {

        }
    }

    override fun setSelectedIndex(index: Int) {
        TODO("Not yet implemented")
    }

    override fun setKeyPressedState(keyPressed: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setShowDialog(show: Boolean) {
        TODO("Not yet implemented")
    }
}