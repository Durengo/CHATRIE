import { writable } from 'svelte/store';
import { currentChatHistory } from '$lib/stores.js';

export async function fetchChatHistory(lobbyId, authToken) {
	try {
		const response = await fetch(`http://localhost:8090/chats/${lobbyId}`, {
			method: 'GET',
			headers: { 'Content-Type': 'application/json' },
			Authorization: `Bearer ${authToken}`
		});

		if (response.ok) {
			const data = await response.json();
			console.log('Chat history:', data);
			sessionStorage.setItem('currentChatHistory', JSON.stringify(data));
			currentChatHistory.set(data);
		} else if (response.status === 404) {
			sessionStorage.setItem('currentChatHistory', JSON.stringify([]));
			currentChatHistory.set([]);
			return true;
		} else {
			throw new Error(`Error fetching chat history: ${data.message}`);
		}
	} catch (error) {
		console.error('Error fetching chat history:', error);
		throw error;
	}
}

export async function fetchLobbies(username, authToken) {
	// console.log(`Request: http://localhost:8090/lobbies/user/${username}}`);

	const response = await fetch(`http://localhost:8090/lobbies/user/${username}`, {
		method: 'GET',
		headers: { 'Content-Type': 'application/json' },
		// body: JSON.stringify({ username, password }),
		Authorization: `Bearer ${authToken}`
	});

	if (response.ok) {
		var data = await response.json();
		console.log('Response: ', data);
		return data;
	} else {
		throw new Error('Failed to fetch chatrooms');
	}
}

export async function createLobby(user1, user2, authToken) {
	try {
		// Make sure that the lobby does not exist. As long as the username is the sender or receiver, no new lobby will be created.
		const lobbies = await fetchLobbies(user1, authToken);

		for (let i = 0; i < lobbies.length; i++) {
			if (lobbies[i].user1 === user2 || lobbies[i].user2 === user2) {
				console.log('Lobby already exists');
				return lobbies[i];
			}
		}

		// If the lobby does not exist, create a new one
		// We need to generate a UUID for the lobby. We'll get this from the server.
		const lobbyId = await fetch('http://localhost:8090/lobbies/uuid', {
			method: 'GET',
			headers: { 'Content-Type': 'application/json' },
			Authorization: `Bearer ${authToken}`
		});

		if (lobbyId.ok) {
			const uuid = await lobbyId.json();
			console.log('Lobby ID:', uuid);

			const response = await fetch('http://localhost:8090/lobbies', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json',
					Authorization: `Bearer ${authToken}`
				},
				body: JSON.stringify({
					lobbyId: uuid,
					user1Nickname: user1,
					user2Nickname: user2
				})
			});

			if (response.ok) {
				const data = await response.json();
				console.log('Lobby created:', data);
				return data;
			} else {
				throw new Error(`Error creating lobby: ${response.statusText}`);
			}
		} else {
			throw new Error('Failed to generate lobby ID');
		}
	} catch (error) {
		console.error('Error creating lobby:', error);
		throw error;
	}
}

export async function sendMessage(lobbyId, messageFrom, messageTo, message, authToken) {
	if (!lobbyId || !messageFrom || !messageTo || !authToken) {
		console.error('Invalid message data');
		return;
	}

	if (messageFrom === messageTo) {
		console.error('Cannot send message to self');
		return;
	}

	if (!message.trim()) {
		console.error('Message is empty or contains only whitespace');
		return;
	}

	const currentTimestamp = new Date().toISOString();

	const payload = {
		lobbyChatKey: {
			lobbyId: lobbyId,
			timestamp: currentTimestamp
		},
		sentBy: messageFrom,
		sentTo: messageTo,
		message: message
	};

	console.log('Payload:', JSON.stringify(payload));

	try {
		const response = await fetch('http://localhost:8090/chats', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify(payload),
			Authorization: `Bearer ${authToken}`
		});

		if (response.ok) {
			console.log('Message sent!');
			fetchChatHistory(lobbyId, authToken);
			// Update chat history
		} else {
			console.error('Error sending message:', response.statusText);
		}
	} catch (error) {
		console.error('Error sending message:', error);
	}
}

export async function fetchUsers(authToken) {
	const response = await fetch('http://localhost:8090/users/all', {
		method: 'GET',
		headers: { 'Content-Type': 'application/json' },
		Authorization: `Bearer ${authToken}`
	});

	if (response.ok) {
		const data = await response.json();
		console.log('Users:', data);
		return data;
	} else {
		throw new Error('Failed to fetch users');
	}
}
