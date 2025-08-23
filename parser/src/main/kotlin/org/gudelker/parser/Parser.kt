import org.gudelker.result.Result
import org.gudelker.tokenstream.TokenStream

interface Parser {
    fun parse(tokenStream: TokenStream): Result
}
