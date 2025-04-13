package com.example.buildnote

import android.app.Application
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.beust.klaxon.Klaxon
import com.example.buildnote.ui.theme.ExampleAppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import org.json.JSONObject

import com.google.accompanist.permissions.rememberPermissionState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExampleAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    /*Greeting(
                       modifier = Modifier.padding(innerPadding)
                    )
                     */
                    // ViewModelExample(modifier = Modifier.padding(innerPadding))

                    // Navigation
                    val navController = rememberNavController()
                    AppNavigation(navController = navController, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier,
             navController: NavController) {
    var name = remember { mutableStateOf("Timo") }

    Column (modifier = modifier
        .fillMaxSize()
        .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {

        Row(modifier = modifier
            .background(Color.Cyan)
            .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically) {

            Text(
                text = "Hello ${name.value}",
                modifier = modifier
            )

            Spacer(modifier = Modifier.width(16.dp))

            Button(onClick = {
                name.value = "New Timo"
                Log.i("Button","Button clicked")
            }) {
                Icon(painterResource(id = com.example.buildnote.R.drawable.baseline_home_24), contentDescription = "Home Icon")
                Text("Change name")
            }
        }

        Row {
            Text("Links")
            Spacer(modifier = Modifier.weight(1.0f)) // eat all space
            Text("Rechts")
        }

        Image(painterResource(id= R.drawable.altin),
            contentDescription = "Altin",
        )

        Button(onClick = {navController.navigate("viewmodelExample")}) {
            Text("Navigate IN")
        }

        Spacer(modifier = Modifier.weight(1.0f))

        Button(onClick = { navController.navigate("someView") }) {
            Text("Navigate Some View")
        }

    }
}


/**
 * Use ViewModel in Composable
 */
@Composable
fun ViewModelExample(modifier: Modifier = Modifier,
                     viewModel: AppViewModel = viewModel(),
                     onNavigateBack: () -> Unit) {

    Column (modifier = modifier
        .fillMaxSize()
        .padding(15.dp)){
        Row{
            TextField(value = viewModel.firstName.value,
                onValueChange = { viewModel.changeFirstName(it) },
                label = {Text("Firstname")},
                textStyle = TextStyle(color = Color.Red),)
        }
        Text("Input: ${viewModel.firstName.value}")

        Button(onClick = {
            viewModel.doServerCall()
            Log.i("Button","Button serverrequ clicked")
        }) {
            Text("Do Servercall")
        }

        Spacer(modifier = Modifier.weight(1.0f))

        Button(onClick = onNavigateBack) {
            Text("Navigate Back")
        }
    }
}

/**
 * ViewModel, remember / rememberSaveable is not necessary
 * Lifecycle of viewmodel is until app is destroyed
 *
 *Put in own file
 *
 * import androidx.lifecycle.viewmodel.compose.viewModel needed
 * Dependecy: implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1") needed
 */
class AppViewModel(application: Application): AndroidViewModel(application) {
    // own var
    var firstName = mutableStateOf("")
    fun changeFirstName(input: String) {
        firstName.value = input
    }

    // servercall example
    private val context = getApplication<Application>().applicationContext
    fun doServerCall(){
        val latitude = 52.52
        val longitude = 13.405
        val endpoint = "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&daily=temperature_2m_max,temperature_2m_min&current_weather=true&timezone=Europe%2FBerlin"

        val requestQueue = Volley.newRequestQueue(context)
        val request = StringRequest(
            Request.Method.GET, endpoint, { response ->
                jsonParser(response)
                Log.i("Reponse:", response)
            },
            Response.ErrorListener {
                Log.e("Error", "Error loading data is $it")
            }
        )
        requestQueue.add(request)
    }

    // depedency: implementation ("com.beust:klaxon:5.5")
    fun jsonParser(jsonString: String) {
        try {
            // manual parsing
            val jsonObject = JSONObject(jsonString)
            //firstName.value = jsonObject.getString("timezone");

            // autoparsing with Klaxon
            val backingObject = Klaxon().parse<BackingObject>(jsonString)
            firstName.value = backingObject?.timezone.toString()
        } catch (e:Exception) {
            Log.e("Error", "Error parsing json is $e")
        }
    }

}

// For Klaxon-Matching (define intresseted objects
class BackingObject(val timezone: String){}


/**
 * Worker example

class TestWorker(context: Context,
                 workerparams: WorkerParameters): Worker(context, workerparams) {

    override fun doWork(): Result {
        Log.i("Worker", "Worker started")

        val outputData: Data = Data.Builder()
            .putString("Message", "This ist the calc value")
            .build()

        return Result.success(outputData)
    }
}

@Composable
fun Worker_Example(modifier: Modifier = Modifier) {
    val workerRequest = OneTimeWorkRequestBuilder<TestWorker>().build()
    //Connect an observer to the workRequest to retrive the result
    WorkManager.getInstance(context).getWorkInfosByIdLiveData(workerRequest.id)
        .observeForever(Observer {
            if(it.state == WorkInfo.State.SUCCEEDED) {
                Log.i("Service", "${it.outputData.getString("Message")}")
            }
        })

} */


/**
 * Permissions example
 * Defined in Mainfest <uses-permission android:name="android.permission.READ_CONTACTS"/>
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsExample(modifier: Modifier = Modifier, contentResolver: ContentResolver) {
    var contacts = remember { mutableListOf<String>() }

    val contactPermissionState = rememberPermissionState(
        android.Manifest.permission.READ_CONTACTS
    ) {
        if (it){
            val uri: Uri = ContactsContract.Contacts.CONTENT_URI

            val cursor: Cursor? = contentResolver.query(
                uri, arrayOf(
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER
                ), null, null, null
            )

            if (cursor != null) {
                var res: Boolean = cursor.moveToFirst()
                while (res) {
                    val name: String = cursor.getString(1)
                    contacts.add(name)
                    res = cursor.moveToNext()
                }
            }
        }
    }
}
