import { writable } from 'svelte/store';
import { currentChatHistory } from '$lib/stores.js';

export async function fetchChatHistory(lobbyId, authToken) {
	try {
		const response = await fetch(`http://localhost:8090/chats/${lobbyId}`, {
			method: 'GET',
			headers: { 'Content-Type': 'application/json' },
			Authorization: `Bearer ${authToken}`
		});
		const data = await response.json();

		if (response.ok) {
			console.log('Chat history:', data);
			sessionStorage.setItem('currentChatHistory', JSON.stringify(data));
			currentChatHistory.set(data);
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

export async function sendMessage(lobbyId, messageFrom, messageTo, message, authToken) {
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
