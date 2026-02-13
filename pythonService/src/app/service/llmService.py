import os
import base64
from typing import Optional, Union, List, Dict
from PIL import Image # Pillow library for image processing (install with: pip install Pillow)
import io # Used for handling image bytes if needed, though not strictly required here

from dotenv import load_dotenv
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.messages import HumanMessage, SystemMessage # Import SystemMessage and HumanMessage
from pydantic import BaseModel # Assuming Expense is a Pydantic model
import google.generativeai as genai # Good to keep, though ChatGoogleGenerativeAI wraps it
from langchain_google_genai import ChatGoogleGenerativeAI

from app.schema.expense import Expense # Your Pydantic model with fields like amount, currency, merchant etc.

class LLmService:
    def __init__(self):
        load_dotenv()

        # Set API Key
        self.apiKey = os.getenv('GEMINI_API_KEY')
        if not self.apiKey:
            raise ValueError("GEMINI_API_KEY not found in .env file")

        # Initialize Gemini chat model (gemini-2.0-flash supports multimodal input)
        self.llm = ChatGoogleGenerativeAI(
            model="gemini-2.0-flash", # This model can handle both text and image inputs
            temperature=0,
            google_api_key=self.apiKey
        )

        # Define the system prompt content. This will be a fixed SystemMessage.
        self.system_message_content = (
            "You are an expert extraction algorithm. "
            "Only extract relevant information from the text and image provided. "
            "If you do not know the value of an attribute asked to extract, "
            "return null for the attribute's value."
        )

        # The runnable will now be the LLM with structured output directly.
        # We will construct the full list of messages (system + human) in the runLLM method.
        self.structured_llm = self.llm.with_structured_output(schema=Expense)

    def runLLM(self, text_message: str, image_path: Optional[str] = None) -> Expense:
        """
        Runs the LLM with a text message and an optional image.

        Args:
            text_message (str): The primary text input for the LLM.
            image_path (Optional[str]): The file path to the image to be included.
                                        If None, only text is sent.

        Returns:
            Expense: The extracted Expense object from the LLM's structured output.
        """
        # Prepare content for the HumanMessage. This will be a list of dictionaries,
        # where each dictionary represents a part of the content (text or image).
        human_message_content: List[Union[str, Dict]] = []

        # Add the text part to the human message content
        human_message_content.append({"type": "text", "text": text_message})

        # If an image path is provided, process the image and add it to the content
        if image_path:
            try:
                # Read the image file in binary mode
                with open(image_path, "rb") as image_file:
                    # Encode the image bytes to a base64 string
                    encoded_string = base64.b64encode(image_file.read()).decode("utf-8")

                # Determine the MIME type of the image using PIL (Pillow)
                # This is important for the LLM to correctly interpret the image data.
                img_format = None
                try:
                    # Open the image to get its format (e.g., 'JPEG', 'PNG')
                    img = Image.open(image_path)
                    img_format = img.format.lower() # Convert to lowercase (e.g., 'jpeg', 'png')
                except Exception as e:
                    # Fallback if PIL fails to determine format. JPEG is a common safe default.
                    print(f"Warning: Could not determine image format with PIL for {image_path}, defaulting to jpeg. Error: {e}")
                    img_format = "jpeg" # Default to jpeg if format detection fails

                mime_type = f"image/{img_format}" # Construct the full MIME type string

                # Add the image part to the human message content
                human_message_content.append({
                    "type": "image_url",
                    "image_url": {"url": f"data:{mime_type};base64,{encoded_string}"}
                })
            except FileNotFoundError:
                # Raise an error if the image file doesn't exist
                raise FileNotFoundError(f"Image file not found at: {image_path}")
            except Exception as e:
                # Catch any other general errors during image processing (e.g., corrupted file)
                raise ValueError(f"Error processing image at {image_path}: {e}")

        # Construct the full list of messages to send to the LLM.
        # This list includes the system message (for instructions) and the human message
        # (containing both text and image parts).
        messages = [
            SystemMessage(content=self.system_message_content), # The system instructions
            HumanMessage(content=human_message_content)         # The user's input (text + image)
        ]

        # Invoke the structured LLM with the prepared list of messages.
        # The .with_structured_output(schema=Expense) part handles the JSON parsing
        # into your Pydantic Expense model automatically.
        return self.structured_llm.invoke(messages)

