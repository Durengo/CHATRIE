// src/routes/api/connectToSocket.js

export async function GET(req, res) {
	try {
		const response = await fetch('http://localhost:8090/users');
		if (response.ok) {
			const responseData = await response.json();
			console.log('Data received from socket:', responseData);

			return new Response('Connected to Socket.', {
				status: 200
			});
		}
	} catch (error) {
		console.error('Connection failed:', error);
		return new Response('Failed to connect to Socket.', {
			status: 500
		});
	}
}
