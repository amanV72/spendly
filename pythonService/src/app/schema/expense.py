from pydantic import BaseModel, Field
from typing import Optional

class Expense(BaseModel):
    amount : Optional[float] = Field(title='expense',description='Expense made in transaction')
    merchant : Optional[str] = Field(title='merchant',description='merchant name whom the transaction has been made')
    currency : Optional[str] = Field(title='currency',description='currency of transaction') 

    def serialize(self):
        return {
            "amount":self.amount,
            "merchant": self.merchant,
            "currency": self.currency,
        }
