import org.example.org.gudelker.ExpressionStatement
import org.example.org.gudelker.Grouping
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.ParseResult
import org.gudelker.result.SyntaxError
import org.gudelker.result.ValidStatementResult
import org.gudelker.rule.SyntaxRule
import org.gudelker.tokenstream.TokenStream

class GroupingRule(private val expressionRule: SyntaxRule) : SyntaxRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        return tokenStream.current()?.getType() == TokenType.OPEN_PARENTHESIS
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        val (openParen, afterOpen) = tokenStream.consume(TokenType.OPEN_PARENTHESIS)
        if (openParen == null) {
            return ParseResult(SyntaxError("Expected '('"), tokenStream)
        }

        val exprResult = expressionRule.parse(afterOpen)
        if (exprResult.result !is ValidStatementResult) {
            return ParseResult(SyntaxError("Invalid expression in grouping"), exprResult.tokenStream)
        }
        val expr = exprResult.result.getStatement() as ExpressionStatement

        val (closeParen, afterClose) = exprResult.tokenStream.consume(TokenType.CLOSE_PARENTHESIS)
        if (closeParen == null) {
            return ParseResult(SyntaxError("Expected ')'"), exprResult.tokenStream)
        }

        val grouping = Grouping(openParen.getValue(), expr, closeParen.getValue())
        return ParseResult(ValidStatementResult(grouping), afterClose)
    }
}
