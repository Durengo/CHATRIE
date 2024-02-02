# CHATRIE

Basic chatting app for NOSQL course.
The backend is made with [Cassandra](https://cassandra.apache.org/).
The frontend - I'm still deciding...

## Requirements

1. [Node.js](https://nodejs.org/)
2. [Python>=3.0](https://www.python.org/)
3. [Docker](https://www.docker.com/)

## Packages used

These packages are automatically installed by following the [setup](#setup) section.

1. [Python Docker Package](https://pypi.org/project/docker/)
2. [NPM Cassandra Package](https://www.npmjs.com/package/cassandra/)

## Setup

1. Setup Python environment
    * $python -m venv venv
    * $./venv/Scripts/activate
    * pip install -r ./requirements.txt
2. Setup the Docker environment with the provided scripts
    * $./Tools/setup.py
    * 1 To setup and run the Cassandra container
    * 2 To setup the keyspace
    * 3 To stop the container
    * Deleting the container is currently done manually
3. Setup the frontend
    * $npm install
4. Launch the app
    * $node index.js
