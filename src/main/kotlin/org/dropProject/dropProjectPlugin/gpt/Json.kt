/*Response{protocol=h2, code=200, message=, url=https://api.openai.com/v1/completions}
{
  "id": "cmpl-89FHRNQSVlaudqgYeb9KFvr2L1O1j",
  "object": "text_completion",
  "created": 1697214381,
  "model": "gpt-3.5-turbo-instruct",
  "choices": [
    {
      "text": "prompt",
      "index": 0,
      "logprobs": null,
      "finish_reason": "stop"
    }
  ],
  "usage": {
    "prompt_tokens": 10,
    "completion_tokens": 76,
    "total_tokens": 86
  }
}*/
/*
package org.dropProject.dropProjectPlugin.gpt

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GPTResponse(
    val choices: Array<Choice>,
)

@JsonClass(generateAdapter = true)
data class Choice(
    val text: String,
)
*/



