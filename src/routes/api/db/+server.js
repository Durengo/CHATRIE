// src/routes/api/connectToCassandra.js

import cassandra from 'cassandra-driver';

export async function GET(req, res) {
	try {
		const client = new cassandra.Client({
			contactPoints: ['127.0.0.1'],
			localDataCenter: 'datacenter1',
			keyspace: 'testspace'
		});

		await client.connect();
		return new Response('Connection to Cassandra successful!', {
			status: 200
		});
	} catch (error) {
		console.error('Connection failed:', error);
		return new Response('Failed to connect to Cassandra.', {
			status: 500
		});
	}
}
