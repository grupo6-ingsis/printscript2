package org.gudelker.result

import org.gudelker.Token

data class ValidToken(val tokens: List<Token>) : TokenResult
