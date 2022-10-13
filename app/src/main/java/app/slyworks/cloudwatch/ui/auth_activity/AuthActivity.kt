package app.slyworks.cloudwatch.ui.auth_activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.slyworks.cloudwatch.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {
    //region Vars
    private lateinit var binding:ActivityAuthBinding
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* TODO: add fullscreen code here from iGrades and extra from StackOverflow */
        /* TODO: add white/black gradient overlay asset, generated from adobeXD, inspiration pinterest*/
        /* TODO: add font and set up day and night mode */
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


}