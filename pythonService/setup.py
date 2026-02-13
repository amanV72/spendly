from  setuptools import setup,find_packages

install_requires=[
    
    # Core application packages
    "Flask~=3.0",
    "kafka-python",
    "Pillow~=11.3",
    "python-dotenv~=1.0", # Corrected version

    # Specify only the high-level AI packages you need.
    # Pip will automatically pull in the correct, compatible versions of
    # langchain-core, pydantic, protobuf, google-generativeai, etc.
    "langchain-google-genai",
    "langchain-mistralai",
    "langchain-openai",

     # The base SDK your code explicitly needs
    "google-generativeai"
]

setup(
    name='ds-service',
    version='1.0',
    packages=find_packages(where='src'),
    package_dir={'':'src'},
    install_requires=install_requires ,
    include_package_data=True

)
