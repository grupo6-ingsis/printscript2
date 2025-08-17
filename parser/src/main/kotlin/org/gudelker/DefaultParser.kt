package org.gudelker

import org.example.Statement

class DefaultParser(val list: List<Token>, val root : List<Statement>) {
    fun parse(): DefaultParser { // Hacer un result para después.

        for(token in list){
            while(token. )
        }
        var ast : List<Statement> = root.toMutableList()
        val newRoot : List<Statement> = ast.toMutableList().add(stat2)
        return DefaultParser(list[2..-1], root)
    }

    fun getRoot(): List<Statement> {
        return root
    }
}
