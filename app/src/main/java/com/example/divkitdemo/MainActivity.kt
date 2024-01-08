package com.example.divkitdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.divkitdemo.databinding.ActivityMainBinding
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div2.DivData
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private fun createDivConfiguration(): DivConfiguration {
        return DivConfiguration.Builder(
            GlideDivImageLoader(this)
        )
            .supportHyphenation(true)
            .visualErrorsEnabled(true)
            .build()
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val divJson =
            JSONObject(resources.openRawResource(R.raw.alfa).bufferedReader().use { it.readText() })

        val templateJson = divJson.optJSONObject("templates")
        val cardJson = divJson.getJSONObject("card")

        val divContext = Div2Context(baseContext = this, configuration = createDivConfiguration())
        binding.root.addView(DivViewFactory(divContext, templateJson).createView(cardJson))
    }
}

class DivViewFactory(
    private val context: Div2Context,
    private val templatesJson: JSONObject? = null
) {

    private val environment = DivParsingEnvironment(ParsingErrorLogger.ASSERT).apply {
        if (templatesJson != null) parseTemplates(templatesJson)
    }

    fun createView(cardJson: JSONObject): Div2View {
        val divData = DivData(environment, cardJson)
        return Div2View(context).apply {
            setData(divData, DivDataTag(divData.logId))
        }
    }
}