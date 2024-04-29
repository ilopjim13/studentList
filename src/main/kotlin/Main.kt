import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.io.File

@Composable
fun MainScreen() {
    val fichero = GestorFichero()
    val ficheroStudiantes = File("students.txt")
    val students = remember { fichero.leer(ficheroStudiantes).toMutableList() }
    val focusRequester = remember { FocusRequester() }
    var texto by remember { mutableStateOf("") }
    Surface(
        color = Color.LightGray,
        modifier = Modifier.fillMaxSize()
    ) {
        StudentList(students, texto,focusRequester, {
            focusRequester.requestFocus()
            students.clear()
        }, {
            texto = it
        },{
            fichero.borrarStudents(ficheroStudiantes)
            students.forEach {
                fichero.escribir(ficheroStudiantes, "$it\n")
            }
        }) {
            focusRequester.requestFocus()
            if (texto != "") students.add(texto)
            texto = ""
        }
    }
}


@Composable
@Preview
fun StudentList(students: List<String>,texto:String ,focusRequester: FocusRequester, onClearAll: () -> Unit, onValueChange: (String) -> Unit, onSaveChange: () -> Unit,  onButtonClick: () -> Unit) {

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

                    OutlinedTextField(
                        value = texto,
                        onValueChange = onValueChange,
                        label = {Text("Usuario")},
                        placeholder = { Text("Introduce el usuario...")},
                        modifier = Modifier.focusRequester(focusRequester)
                    )

                    Button(onClick = onButtonClick) {
                        Text(text = "Add new student")
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Students ${students.size}")
                    LazyColumn(
                        userScrollEnabled = true,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxHeight(0.7f)
                            .width(220.dp)
                            .border(2.dp, Color.Black)
                            .background(Color.White)
                    ) {

                        items(students) { message ->
                            MessageRow(message)
                        }

                    }
                    Button(
                        onClick = onClearAll
                    ) {
                        Text(text = "Clear All")
                    }

                }
            }
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxHeight(1f)) {
                Button(onClick = onSaveChange) {
                    Text(text = "Guardar Cambios")
                }
            }

        }
    }
}



@Composable
fun StudentText(name: String) {
    Text(
        text = name,
        fontSize = 28.sp,
        modifier = Modifier.padding(10.dp)
    )
}

@Composable
fun MessageRow(message: String) {
    Text(text = message)
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MainScreen()
    }
}
