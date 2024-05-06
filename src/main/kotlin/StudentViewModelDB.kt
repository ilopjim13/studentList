import androidx.compose.runtime.State
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.io.File

class StudentViewModelDB(val fichero: IGestorFichero, val fileStudent: File):IViewModelStudent {


    override val newStudent: State<String>
        get() = TODO("Not yet implemented")
    override val students: SnapshotStateList<String>
        get() = TODO("Not yet implemented")
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

    override fun loadStudents(): SnapshotStateList<String>? {
        TODO("Not yet implemented")
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