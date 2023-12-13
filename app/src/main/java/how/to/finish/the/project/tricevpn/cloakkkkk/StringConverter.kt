package how.to.finish.the.project.tricevpn.cloakkkkk

import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.IOException

class StringConverter : Converter<ResponseBody, String> {
    @Throws(IOException::class)
    override fun convert(value: ResponseBody): String {
        return value.use { value ->
            value.string()
        }
    }
}