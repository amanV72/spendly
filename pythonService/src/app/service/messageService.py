from app.schema.expense import Expense # Your Pydantic model
from app.service.llmService import LLmService
from app.utils.MessageUtil import MessageUtil # New import for MessageUtil
from typing import Optional

class MessageService:
    def __init__(self):
        self.messageUtil = MessageUtil() # Initialize MessageUtil
        self.llmService = LLmService() # Initialize LLmService

    # Updated method signature to accept image_path, matching Flask app's call
    def process_message(self, text_message: str, image_path: Optional[str] = None) -> Optional[Expense]:
        """
        Processes a message, potentially with an image, using the LLM service
        after checking if it's a bank SMS (for text-only inputs).

        Args:
            text_message (str): The text content of the message (e.g., bank SMS).
            image_path (Optional[str]): The file path to an uploaded image (e.g., receipt).

        Returns:
            Optional[Expense]: An Expense object if extraction is successful and relevant input is provided, otherwise None.
        """
        # If both text_message and image_path are None, there's nothing to process.
        if text_message is None and image_path is None:
            return None

        # Determine the text to pass to isBankSms. If text_message is None, use an empty string
        # so that isBankSms doesn't receive None.
        # The isBankSms check primarily applies to filtering text-based messages.
        # If an image is provided, we generally want to attempt extraction regardless of text content.
        is_bank_sms = False
        if text_message is not None:
            is_bank_sms = self.messageUtil.isBankSms(text_message)
        
        # If it's not a bank SMS AND no image is provided, then return None.
        # This means if an image IS provided, we proceed with LLM even if text isn't a bank SMS.
        if not is_bank_sms and image_path is None:
            return None

        # Prepare the text for the LLM call. If text_message is None, pass an empty string.
        # The LLM's multimodal input can handle an empty string for text if an image is present.
        text_for_llm = text_message if text_message is not None else ""

        try:
            # Call the LLmService's runLLM method, passing both text (possibly empty) and image path
            extracted_expense = self.llmService.runLLM(text_for_llm, image_path)
            return extracted_expense
        except Exception as e:
            # Log the error for debugging purposes
            print(f"Error in MessageService processing LLM: {e}")
            return None

