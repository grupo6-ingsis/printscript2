import org.example.org.gudelker.Statement
import org.gudelker.Token

interface Parser {
    fun parse(tokenList: List<Token>): List<Statement>
}
