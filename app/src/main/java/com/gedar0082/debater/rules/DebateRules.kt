package com.gedar0082.debater.rules

class DebateRules(
    val ruleType: RulesTypes,
    val roundCount: Int,
    val thesisInRound: Int,
    val argumentsInThesis: List<Int>) {
    var round = 0
}