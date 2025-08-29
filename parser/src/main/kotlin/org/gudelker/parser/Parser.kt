import org.gudelker.result.ParserResult
import org.gudelker.tokenstream.TokenStream

interface Parser {
    fun parse(tokenStream: TokenStream): ParserResult
}
