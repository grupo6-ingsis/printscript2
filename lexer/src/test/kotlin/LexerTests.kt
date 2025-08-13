import org.gudelker.NormalLexer
import org.gudelker.rules.AssignationTokenizer
import org.gudelker.rules.ColonTokenizer
import org.gudelker.rules.DoubleTokenizer
import org.gudelker.rules.EqualComparativeTokenizer
import org.gudelker.rules.IdentifierTokenizer
import org.gudelker.rules.IntegerTokenizer
import org.gudelker.rules.LetTokenizer
import org.gudelker.rules.NewLineTokenizer
import org.gudelker.rules.NotLineAfterSemicolonTokenizer
import org.gudelker.rules.ProhibitedSymbolDoubleTokenizer
import org.gudelker.rules.SemicolonTokenizer
import org.gudelker.rules.WhitespaceTokenizer
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class LexerTests {
    lateinit var lexer: NormalLexer


    @BeforeEach
    fun setUp() {
        val colonTokenizer = ColonTokenizer()
        val semiColon = SemicolonTokenizer()
        val identifierTokenizer = IdentifierTokenizer()
        val whitespaceTokenizer = WhitespaceTokenizer()
        val newLineTokenizer = NewLineTokenizer()
        val letTokenizer = LetTokenizer()
        val eqTokenizer = EqualComparativeTokenizer()
        val assignationTokenizer = AssignationTokenizer()
        val integer = IntegerTokenizer()
        val double = DoubleTokenizer()
        val prohibitedSymbolDoubleTokenizer = ProhibitedSymbolDoubleTokenizer()
        val notLineAfterSemicolonTokenizer = NotLineAfterSemicolonTokenizer()
        val tokenizers = listOf(
            letTokenizer,
            colonTokenizer,
            notLineAfterSemicolonTokenizer,
            semiColon,
            whitespaceTokenizer,
            newLineTokenizer,
            eqTokenizer,
            assignationTokenizer,
            prohibitedSymbolDoubleTokenizer,
            identifierTokenizer,
            double,
            integer,
        )
        lexer = NormalLexer(tokenizers)



    }
    @Test
    fun `test token rules`() {
        val tokens = lexer.lex("/Users/pedrodelaguila/faculty/ingsis/printscript2/lexer/src/test/lexer.txt")
        for (token in tokens) {
            println(token)
        }
    }
}