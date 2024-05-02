import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.delay
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

    Surface(
        color = Color.LightGray,
        modifier = Modifier.fillMaxSize()
    ) {
        StudentList(students = students, showDialog = showDialog, keyPressedState, interactionSource, stateVertical, newStudent,focusRequester,
            onClearAll = {
             focusRequester.requestFocus()
             students.clear()
        }, onValueChange = {
            newStudent = it
        }, onSaveChange = {
            fichero.borrarStudents(ficheroStudiantes)
            students.forEach { fichero.escribir(ficheroStudiantes, "$it\n") }
            showDialog = true
        }, onShowDialog = {
            showDialog = false })
        {
            focusRequester.requestFocus()
            if (newStudent.isNotBlank()) students.add(newStudent)
            newStudent = ""
        }
    }
}


@Composable
@Preview
fun StudentList(students: MutableList<String>, showDialog:Boolean, keyPressedState:  MutableState<Boolean>, interactionSource: MutableInteractionSource, stateVertical:ScrollState ,newStudent:String ,focusRequester: FocusRequester, onClearAll: () -> Unit, onValueChange: (String) -> Unit, onSaveChange: () -> Unit,onShowDialog: () -> Unit,  onButtonClick: () -> Unit) {
    val state = rememberLazyListState()
    var selectedIndex by remember { mutableStateOf(0)}
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
                Column(verticalArrangement = Arrangement.spacedBy(15.dp, alignment = Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally) {

                    NuevosEstuiandtes(newStudent, onValueChange, focusRequester, onButtonClick)

                    Boton("Add new student", onButtonClick, keyPressedState, interactionSource)

                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Students ${students.size}")
                    Box (
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.width(220.dp).fillMaxHeight(0.7f)
                    ) {

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(2.dp, Color.Black)
                                .background(Color.White)
                                .onKeyEvent { event ->
                                    if (event.type == KeyEventType.KeyUp) {
                                        when (event.key) {
                                            Key.DirectionUp -> {
                                                if (selectedIndex > 0) {
                                                    selectedIndex--
                                                    true
                                                } else false
                                            }
                                            Key.DirectionDown -> {
                                                if (selectedIndex < students.size - 1) {
                                                    selectedIndex++
                                                    true
                                                } else false
                                            }
                                            else -> false
                                        }
                                    } else {
                                        false//Solo manejar cuando la tecla se haya levantado de la presión
                                    }
                                },
                            state
                        ) {

                            items(students) { message ->
                                MessageRow(message, students)
                            }


                        }
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

            if (showDialog) Toast("Changes Saved", onShowDialog)

        }
    }
}


@Composable
fun NuevosEstuiandtes(newStudent: String, onValueChange: (String) -> Unit, focusRequester: FocusRequester, onButtonClick: () -> Unit) {
    OutlinedTextField(
        value = newStudent,
        onValueChange = onValueChange,
        label = {Text("New student")},
        singleLine = true,
        placeholder = { Text("Enter the new student...")},
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
fun Boton(texto: String, onClick: () -> Unit, keyPressedState:  MutableState<Boolean>, interactionSource: MutableInteractionSource) {

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
        //icon = painterResource("info_icon.png"),
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
    // Cierra el Toast después de 2 segundos
    LaunchedEffect(Unit) {
        delay(1000)
        onDismiss()
    }
}

@Composable
fun MessageRow(message: String, students: MutableList<String>) {

    Spacer(modifier = Modifier.height(10.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween ) {
        Text(text = message, fontSize = 22.sp, modifier = Modifier.padding(start = 15.dp))
        Box(
            contentAlignment = Alignment.CenterEnd,
        ) {
            IconButton(
                modifier = Modifier.padding(end = 10.dp),
                onClick = { students.remove(message) }
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = ""
                )
            }

        }

    }

}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MainScreen()
    }
}
