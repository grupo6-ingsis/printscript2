package org.gudelker.result

import org.gudelker.token.Token

data class ValidToken(val tokens: List<Token>) : TokenResult
