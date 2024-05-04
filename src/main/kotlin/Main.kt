import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import kotlinx.coroutines.delay
import java.awt.Toolkit
import java.io.File

@Composable
fun MainScreen() {
    val fichero = GestorFichero()
    val ficheroStudiantes = File("students.txt")
    val students = remember { fichero.leer(ficheroStudiantes).toMutableStateList() }
    val focusRequester = remember { FocusRequester() }
    val stateVertical = rememberScrollState(0)
    var newStudent by remember { mutableStateOf("") }
    val keyPressedState = remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val state = rememberLazyListState()

    Surface(
        color = Color.LightGray,
        modifier = Modifier.fillMaxSize()
    ) {
        StudentList(students = students,
            state,
            showDialog = showDialog,
            keyPressedState,
            interactionSource,
            stateVertical,
            newStudent,
            focusRequester,
            onClearAll = {
                focusRequester.requestFocus()
                students.clear()
            },
            onValueChange = {
                newStudent = it
            },
            onSaveChange = {
                fichero.borrarStudents(ficheroStudiantes)
                students.forEach { fichero.escribir(ficheroStudiantes, "$it\n") }
                showDialog = true
            })
        {
            focusRequester.requestFocus()
            if (newStudent.isNotBlank()) students.add(newStudent)
            newStudent = ""
        }


        if (showDialog) {
            Toast("Changes Saved") {
                showDialog = false
                focusRequester.requestFocus()
            }
        }

        LaunchedEffect(showDialog) {
            if (showDialog) {
                delay(2000)
                showDialog = false
            }
        }
    }
}


@Composable
@Preview
fun StudentList(
    students: MutableList<String>,
    state: LazyListState,
    showDialog: Boolean,
    keyPressedState: MutableState<Boolean>,
    interactionSource: MutableInteractionSource,
    stateVertical: ScrollState,
    newStudent: String,
    focusRequester: FocusRequester,
    onClearAll: () -> Unit,
    onValueChange: (String) -> Unit,
    onSaveChange: () -> Unit,
    onButtonClick: () -> Unit
) {
    val (selectedIndex, setSelectedIndex) = remember { mutableStateOf(-1) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxHeight(0.8f).fillMaxSize()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(15.dp, alignment = Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    NuevosEstuiandtes(newStudent, onValueChange, focusRequester, onButtonClick)

                    Boton("Add new student", onButtonClick, keyPressedState, interactionSource)

                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Students ${students.size}")
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth(0.5f).fillMaxHeight(0.7f)
                    ) {

                        ListaEstudiantes(students, state, focusRequester, selectedIndex) { index -> setSelectedIndex(index)}

                        VerticalScrollbar(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .fillMaxHeight(),
                            adapter = rememberScrollbarAdapter(scrollState = state)
                        )
                    }

                    Boton("Clear All", onClearAll, keyPressedState, interactionSource)

                }
            }
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxHeight(1f)) {
                Boton("Save Changes", onSaveChange, keyPressedState, interactionSource)
            }

        }
    }
}

@Composable
fun ListaEstudiantes(students: MutableList<String>, state: LazyListState, focusRequester: FocusRequester, selectedIndex: Int, onStudenSelected: (Int) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .border(2.dp, Color.Black)
            .background(Color.White)
            .focusable()
            .onFocusChanged { focusState ->
                if (focusState.isFocused && selectedIndex >= 0) onStudenSelected(selectedIndex)
            }
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyUp) {
                    when (event.key) {
                        Key.DirectionUp -> {
                            if (selectedIndex > 0) {
                                onStudenSelected(selectedIndex - 1)
                                true
                            } else false
                        }
                        Key.DirectionDown -> {
                            if (selectedIndex < students.size - 1) {
                                onStudenSelected(selectedIndex + 1)
                                true
                            } else false
                        }
                        else -> false
                    }
                } else {
                    false
                }
            },
        state
    ) {

        items(students.size) { index ->
            MessageRow(index, students, selectedIndex, onStudenSelected)
        }


    }
}

@Composable
fun NuevosEstuiandtes(
    newStudent: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    onButtonClick: () -> Unit
) {
    OutlinedTextField(
        value = newStudent,
        onValueChange = onValueChange,
        label = { Text("New student") },
        singleLine = true,
        placeholder = { Text("Enter the new student...") },
        modifier = Modifier.focusRequester(focusRequester)
            .onKeyEvent { event ->
                if (event.key == Key.Enter) {
                    onButtonClick.invoke()
                    true
                } else false
            }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Boton(
    texto: String,
    onClick: () -> Unit,
    keyPressedState: MutableState<Boolean>,
    interactionSource: MutableInteractionSource
) {

    Button(
        onClick = onClick,
        modifier = Modifier.onPointerEvent(PointerEventType.Press) { onClick() }
            .onPreviewKeyEvent {
                if (
                    it.key == Key.Enter ||
                    it.key == Key.Spacebar
                ) {
                    when (it.type) {
                        KeyEventType.KeyDown -> {
                            keyPressedState.value = true
                        }

                        KeyEventType.KeyUp -> {
                            keyPressedState.value = false
                            onClick.invoke()
                        }
                    }
                }
                false
            }
            .focusable(interactionSource = interactionSource)
    ) {
        Text(text = texto)
    }
}


@Composable
fun Toast(message: String, onDismiss: () -> Unit) {
    Dialog(
        icon = painterResource("info_icon.png"),
        title = "Atención",
        resizable = false,
        onCloseRequest = onDismiss
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Text(message)
        }
    }
}

@Composable
fun MessageRow(index: Int, students: MutableList<String>, selectedIndex: Int, onStudenSelected: (Int) -> Unit) {
    Spacer(modifier = Modifier.height(10.dp))
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {onStudenSelected(index)}
            .background(if (index == selectedIndex) Color(0xFF75EEFF) else Color.White)
    ) {

        Text(text = students[index],
            fontSize = 22.sp,
            modifier = Modifier.padding(start = 15.dp)
        )

        Box(
            contentAlignment = Alignment.CenterEnd,
        ) {
            IconButton(
                modifier = Modifier.padding(end = 10.dp),
                onClick = { students.removeAt(index) }
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = ""
                )
            }

        }

    }

}

@Composable
fun GetWindowState(
    windowWidth: Dp,
    windowHeight: Dp,
): WindowState {
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val screenWidth = screenSize.width
    val screenHeight = screenSize.height

    val positionX = (screenWidth / 2 - windowWidth.value.toInt() / 2)
    val positionY = (screenHeight / 2 - windowHeight.value.toInt() / 2)

    return rememberWindowState(
        size = DpSize(windowWidth, windowHeight),
        position = WindowPosition(positionX.dp, positionY.dp)
    )
}


fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        resizable = false,
        state = GetWindowState(800.dp, 800.dp)
        ) {
        MainScreen()
    }
}
