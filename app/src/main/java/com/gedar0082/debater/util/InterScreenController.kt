package com.gedar0082.debater.util


import com.gedar0082.debater.model.net.pojo.ArgumentJson
import com.gedar0082.debater.model.net.pojo.ThesisJson


/**
 * @property argumentPressed - current pressed argument, for which we are answering, when we are
 * answering for thesis
 * @property thesisPressed - thesis, which are we current answer for
 * chooseAnswerArg - has 4 states. 0 - not using. 1 - thesis is answered. 2 - argument is choosing.
 * 3 - returning to ThesisMap fragment, but data is not empty. When cycle of answer is over, set to 0
 */
object InterScreenController {

    var chooseAnswerArg = 0
    var argumentPressed: ArgumentJson? = null
    var thesisPressed: ThesisJson? = null

}