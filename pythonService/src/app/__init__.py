from flask import Flask, request, jsonify
from kafka import KafkaProducer
from app.service.messageService import MessageService
from pydantic import BaseModel # Assuming this is used by Expense or other models
import json
import os
import tempfile # For creating temporary files

app = Flask(__name__)
app.config.from_pyfile('config.py') # Load your Flask configuration

# Initialize MessageService
messageService = MessageService()

#Kafka producer setup (currently commented out in your provided code, keeping it that way)
kafka_host = os.getenv('KAFKA_HOST','localhost')
kafka_port = os.getenv('KAFKA_PORT','9092')
kafka_bootstrap_servers = f'{kafka_host}:{kafka_port}'
producer= KafkaProducer(bootstrap_servers=kafka_bootstrap_servers,
                        value_serializer=lambda v: json.dumps(v).encode('utf-8')
                        )

@app.route('/ds/v1/message', methods=['POST'])
def handle_message():
    # Initialize variables for cleanup and data handling
    image_path = None
    temp_file = None
    user_id = request.headers.get('user-id') 
    if not user_id:
        return jsonify({"error": "Bad Request", "details": "'user_id' is a required field"}), 401

    try:

        # --- MODIFIED: Extract message and user_id based on Content-Type ---
        if request.is_json:
            # If the request Content-Type is 'application/json'
            data = request.get_json()
            message = data.get('message')
        elif request.content_type and "multipart/form-data" in request.content_type:
            # If the request Content-Type is 'multipart/form-data'
            message = request.form.get('message')
            
        else:
            # If an unexpected Content-Type is received
            return jsonify({"error": "Unsupported Content-Type", "details": "Expected 'application/json' or 'multipart/form-data'"}), 415

        # Check for image file in the request (only for multipart/form-data)
        if 'image' in request.files:
            image_file = request.files['image']
            if image_file.filename != '':
                # Create a temporary file to store the uploaded image
                fd, temp_file_path = tempfile.mkstemp(suffix=os.path.splitext(image_file.filename)[1])
                os.close(fd)
                image_file.save(temp_file_path)
                image_path = temp_file_path
                temp_file = temp_file_path # Store for cleanup

        # Process the message using MessageService (user_id is NOT passed here)
        result = messageService.process_message(message, image_path)

        if result is None:
            return jsonify({"message": "Not a bank SMS or no relevant data extracted"}), 500
        
        # --- MODIFIED: Add user_id to the final response ---
        response_data = result.model_dump()
        if not response_data.get('amount'):
           return jsonify({
                  "error": "Extraction Failed",
                  "details": "No amount was found in the image or message."
                 }), 422 
        
        response_data['user_id'] = user_id # Add the user_id to the response dictionary

       # Kafka producer send (now sends data including the user_id)
        if producer:
            producer.send('expense_service', response_data)

        return jsonify(response_data), 200

    except Exception as e:
        # Log the error for debugging
        print(f"An error occurred: {e}")
        return jsonify({"error": "Internal Server Error", "details": str(e)}), 500
    finally:
        # Clean up the temporary image file if it was created
        if temp_file and os.path.exists(temp_file):
            os.remove(temp_file)
            print(f"Cleaned up temporary file: {temp_file}")


@app.route('/', methods=['GET'])
def handle_get():
    return 'hello world'

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=8010, debug=True)