package com.gedar0082.debater.rules

object RulesManager {

//    enum class RulesTypes{
//        CLASSIC, BRITISH, FREE
//    }

    var RULE = RulesTypes.CLASSIC
    var ROUND_COUNT = 0
    var THESES_IN_ROUND = 0
    lateinit var ARGUMENTS_IN_ROUND : List<Int>


    fun initial(rule: RulesTypes){
        when(rule){
            RulesTypes.CLASSIC -> classicRulesSetter()
            RulesTypes.BRITISH -> britishRulesSetter()
            RulesTypes.FREE -> freeRulesSetter()
        }
    }

    private fun classicRulesSetter(){
        RULE = RulesTypes.CLASSIC
        ROUND_COUNT = 3
        THESES_IN_ROUND = 2
        ARGUMENTS_IN_ROUND = listOf(1, 2, 3)
    }

    private fun britishRulesSetter(){
        RULE = RulesTypes.BRITISH
    }

    private fun freeRulesSetter(){
        RULE = RulesTypes.FREE
    }
}