package org.gudelker.smtposition

data class Positioned<T>(val value: T, val position: StatementPosition)
