import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.snapshots.SnapshotStateList

interface IViewModelStudent {

    val newStudent: State<String>

    val students: List<String>


    val keyPressedState:State<Boolean>

    val showDialog:State<Boolean>

    val selectedIndex: State<Int>

    fun addStudents()
    fun removeStudents(index: Int)
    fun clearStundents()
    fun saveChanges()
    fun editName(index: Int, value: String)
    fun valueChange(value:String)
    fun loadStudents()
    fun setSelectedIndex(index: Int)
    fun setKeyPressedState(keyPressed: Boolean)
    fun setShowDialog(show: Boolean)
}