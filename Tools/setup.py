import docker

# Initialize the Docker client
client = docker.from_env()

# Define the container name
container_name = 'cassandra'

def start_cassandra_container():
    try:
        # Pull the latest Cassandra image if not already present
        client.images.pull('cassandra:latest')

        # Create a network if it doesn't exist
        network_name = 'cassandra'
        try:
            client.networks.create(network_name)
        except docker.errors.APIError:
            # Network already exists
            pass

        # Start the Cassandra container
        client.containers.run(
            'cassandra:latest',
            name=container_name,
            hostname='cassandra',
            network=network_name,
            ports={'9042': '9042'},
            detach=True,
            remove=True
        )

        print(f'Cassandra container ({container_name}) started successfully.')

        print(f'Please wait for a minute or two for the Cassandra container to start and setup...')

    except Exception as e:
        print(f'Error starting Cassandra container: {e}')

def stop_cassandra_container():
    try:
        # Stop the Cassandra container
        container = client.containers.get(container_name)
        container.stop()
        print(f'Cassandra container ({container_name}) stopped successfully.')
    except docker.errors.NotFound:
        print(f'Cassandra container ({container_name}) not found.')

def create_keyspace():
    try:
        # Execute CQL commands in the Cassandra container
        command = [
            'cqlsh',
            '-e',
            "CREATE KEYSPACE IF NOT EXISTS testspace "
            "WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};"
        ]
        container = client.containers.get(container_name)
        exit_code, output = container.exec_run(command)

        # Check if the keyspace creation was successful
        if exit_code == 0:
            print('Keyspace "testspace" created successfully.')
        else:
            print(f'Error creating keyspace: {output.decode()}')

    except Exception as e:
        print(f'Error creating keyspace: {e}')

if __name__ == '__main__':

    function_to_run = input("0. Exit\n1. Start Cassandra Container\n2. Create Keyspace (\"testspace\")\n3. Stop Cassandra Container\nChoose (0-3)\n")

    if function_to_run == "0":
        exit()
    elif function_to_run == "1":
        start_cassandra_container()
    elif function_to_run == "2":
        create_keyspace()
    elif function_to_run == "3":
        stop_cassandra_container()
    else:
        print("Invalid choice")