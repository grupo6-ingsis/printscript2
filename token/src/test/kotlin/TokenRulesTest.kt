import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import org.gudelker.rules.ColonTokenizer
import org.gudelker.rules.IdentifierTokenizer
import org.gudelker.rules.NewLineTokenizer
import org.gudelker.rules.SemicolonTokenizer
import org.gudelker.rules.WhitespaceTokenizer

class TokenRulesTest {
    val colonTokenizer: ColonTokenizer = ColonTokenizer()


    @BeforeEach
    fun setUp() {
        val colonTokenizer: ColonTokenizer = ColonTokenizer()
        val semiColon: SemicolonTokenizer = SemicolonTokenizer()
        val identifierTokenizer = IdentifierTokenizer()
        val whitespaceTokenizer = WhitespaceTokenizer()
        val newLineTokenizer = NewLineTokenizer()



    }
    @Test
    fun `test token rules`() {
        val actualWord = ""



    }
}