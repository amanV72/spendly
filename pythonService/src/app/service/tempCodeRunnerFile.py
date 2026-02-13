from dotenv import load_dotenv
from langchain_core.prompts import ChatPromptTemplate,MessagesPlaceholder
from pydantic import BaseModel
from langchain_openai import ChatOpenAI
from langchain_mistralai import ChatMistralAI

from langchain_core.utils.function_calling import convert_to_openai_tool
from typing import Optional
import os

from schema.expense import Expense

class LLmService:
  def __init__(self):
    load_dotenv()
    self.prompt = ChatPromptTemplate.from_messages(
      [
        (
          "system",
          "you are an expert extraction algoritms. "
          "Only extract relevant information from the text."
          "If you do not know the value of an attribute asked to extract,"
          "return null for the attribute's value."
        ),
        (
          "human","{text}"
        )
      ]  
    )
    self.apiKey= os.getenv('OPENAI_API_KEY')
    self.llm = ChatMistralAI(api_key=self.apiKey, model="mistral-large-latest")
    self.runnable = self.prompt | self.llm.with_structured_output(schema=Expense)
  def runLLM(self,message):
    return self.runnable.invoke({"text": message})
 

