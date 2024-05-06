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
fun MainScreen(viewModelStudent: IViewModelStudent) {
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val state = rememberLazyListState()

    Surface(
        color = Color.LightGray,
        modifier = Modifier.fillMaxSize()
    ) {
        StudentList(
            viewModelStudent = viewModelStudent,
            state = state,
            interactionSource =  interactionSource,
            focusRequester = focusRequester,
            onClearAll = {
                focusRequester.requestFocus()
                viewModelStudent.clearStundents()
            },
            onValueChange = {
                viewModelStudent.valueChange(it)
            },
            onSaveChange = {
                viewModelStudent.saveChanges()
            },
            onAddStudent = {
                focusRequester.requestFocus()
                viewModelStudent.addStudents()
            }
        )



        if (viewModelStudent.showDialog.value) {
            Toast("Changes Saved") {
                viewModelStudent.setShowDialog(true)
                focusRequester.requestFocus()
            }
        }

        LaunchedEffect(viewModelStudent.showDialog.value) {
            if (viewModelStudent.showDialog.value) {
                delay(2000)
                viewModelStudent.setShowDialog(false)
            }
        }
    }
}


@Composable
@Preview
fun StudentList(
    viewModelStudent: IViewModelStudent,
    state: LazyListState,
    interactionSource: MutableInteractionSource,
    focusRequester: FocusRequester,
    onClearAll: () -> Unit,
    onValueChange: (String) -> Unit,
    onSaveChange: () -> Unit,
    onAddStudent: () -> Unit
) {

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

                    NuevosEstuiandtes(viewModelStudent.newStudent.value, focusRequester,onValueChange, onAddStudent)

                    Boton("Add new student", interactionSource, onAddStudent) {viewModelStudent.setKeyPressedState(it)}

                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Students ${viewModelStudent.students.size}")
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth(0.5f).fillMaxHeight(0.7f)
                    ) {

                        ListaEstudiantes(
                            selectedIndex =  viewModelStudent.selectedIndex.value,
                            students =  viewModelStudent.students,
                            state = state,
                            removeStudents =  {viewModelStudent.removeStudents(it)},
                            setSelectedIndex =  {viewModelStudent.setSelectedIndex(it)}
                            )

                        VerticalScrollbar(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .fillMaxHeight(),
                            adapter = rememberScrollbarAdapter(scrollState = state)
                        )
                    }

                    Boton("Clear All", interactionSource, onClearAll) {viewModelStudent.setKeyPressedState(it)}

                }
            }
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxHeight(1f)) {
                Boton("Save Changes", interactionSource, onSaveChange) {viewModelStudent.setKeyPressedState(it)}
            }

        }
    }
}

@Composable
fun ListaEstudiantes(
    selectedIndex:Int,
    students:List<String>,
    state: LazyListState,
    removeStudents: (Int) -> Unit,
    setSelectedIndex: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .border(2.dp, Color.Black)
            .background(Color.White)
            .focusable()
            .onFocusChanged { focusState ->
                if (focusState.isFocused && selectedIndex >= 0) setSelectedIndex(selectedIndex)
            }
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyUp) {
                    when (event.key) {
                        Key.DirectionUp -> {
                            if (selectedIndex > 0) {
                                setSelectedIndex(selectedIndex - 1)
                                true
                            } else false
                        }
                        Key.DirectionDown -> {
                            if (selectedIndex < students.size - 1) {
                                setSelectedIndex(selectedIndex + 1)
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
            MessageRow(index,students, selectedIndex, setSelectedIndex, removeStudents)
        }
    }
}

@Composable
fun NuevosEstuiandtes(
    newStudent:String,
    focusRequester: FocusRequester,
    onValueChange: (String) -> Unit,
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
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit,
    setKeyPressedState:(Boolean) -> Unit
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
                            setKeyPressedState(true)
                        }

                        KeyEventType.KeyUp -> {
                            setKeyPressedState(false)
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
fun MessageRow(
    index: Int,
    students: List<String>,
    selectedIndex:Int,
    setSelectedIndex: (Int) -> Unit,
    removeStudents: (Int) -> Unit
) {
    Spacer(modifier = Modifier.height(10.dp))
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {setSelectedIndex(index)}
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
                onClick = { removeStudents(index) }
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

@Composable
fun ElegirTipoViewModel(ventanaElegir:Boolean,onScreen:() -> Unit, onViewDB:(Boolean) -> Unit , exitApplication: ()-> Unit) {
    var elegido by remember { mutableStateOf(false) }
    if (ventanaElegir) {
        Window(onCloseRequest = exitApplication ) {
            MaterialTheme {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column {
                        Row (
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    onViewDB(false)
                                    elegido = !elegido
                                }
                            ) {
                                Icon(
                                    imageVector = if (elegido) Icons.Default.CheckBoxOutlineBlank else Icons.Default.CheckBox,
                                    contentDescription = null
                                )
                            }
                            Text("Fichero")
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    onViewDB(true)
                                    elegido = !elegido
                                }
                            ) {
                                Icon(
                                    imageVector = if (!elegido) Icons.Default.CheckBoxOutlineBlank else Icons.Default.CheckBox,
                                    contentDescription = null
                                )
                            }
                            Text("Base de datos")
                        }


                    }

                    Button(
                        onClick = onScreen,
                    ) {
                        Text("Aceptar")
                    }

                }
            }
        }
    }
    }



fun main() = application {
    val fichero = GestorFichero()
    val fileStudiants = File("students.txt")
    var viewBD by remember { mutableStateOf(false) }
    var screenStudents by remember { mutableStateOf(false) }
    var ventanaElegir by remember { mutableStateOf(true) }

    ElegirTipoViewModel(ventanaElegir,
        onScreen = {
            screenStudents = true
            ventanaElegir = false },
        {viewBD = it})
    { exitApplication() }

    val viewModel = if (viewBD) StudentViewModelDB(fichero, fileStudiants)
        else ViewModelStudent(fichero, fileStudiants)

    if (screenStudents) {
        Window(
            onCloseRequest = ::exitApplication,
            resizable = false,
            state = GetWindowState(800.dp, 800.dp)
        ) {
            MainScreen(viewModel)
        }
    }

}
