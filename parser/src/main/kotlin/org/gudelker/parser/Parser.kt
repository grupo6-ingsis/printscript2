import org.gudelker.parser.result.ParserResult
import org.gudelker.parser.tokenstream.TokenStream

interface Parser {
    fun parse(tokenStream: TokenStream): ParserResult
}
