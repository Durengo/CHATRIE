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
			// console.log('Chat history:', data);
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

export async function onlyReturnChatHistory(lobbyId, authToken) {
	try {
		const response = await fetch(`http://localhost:8090/chats/${lobbyId}`, {
			method: 'GET',
			headers: { 'Content-Type': 'application/json' },
			Authorization: `Bearer ${authToken}`
		});

		if (response.ok) {
			const data = await response.json();
			return data;
		} else if (response.status === 404) {
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
		// console.log('Response: ', data);
		return data;
	} else {
		throw new Error('Failed to fetch chatrooms');
	}
}

export async function sortLobbiesByLatestMessage(currentUser, lobbies, authToken) {
	/* fetchChatHistory returns:
		{
			lobbyChatKey: {
				lobbyId: string,
				timestamp: string
			},
			sentBy: string,
			sentTo: string,
			message: string
		}
		We will use the timestamp to sort the lobbies by latest message. Also we can display the latest message in the lobby list.
	*/

	const lobbiesWithLatestMessage = [];

	/* lobbiesWithLatestMessage should look like this:
		[
			{
				lobbyId: string,
				withUser: string,
				lastMessageFrom: string,
				message: string,
				timestamp: string,
				hasMessages: boolean
			},
			...
		]
	*/

	for (let i = 0; i < lobbies.length; i++) {
		const lobby = lobbies[i];

		try {
			console.log('Fetching chat history for lobby:', lobby.lobbyId);
			const chatHistory = await onlyReturnChatHistory(lobby.lobbyId, authToken);

			console.log('Chat history:', chatHistory);

			// Need to handle the usecase where the user is the sender or receiver. We need to display the other user's nickname.
			let withUserReal = '';
			if (lobby.user1Nickname === currentUser) {
				withUserReal = lobby.user2Nickname;
			} else {
				withUserReal = lobby.user1Nickname;
			}

			if (chatHistory && chatHistory.length > 0) {
				const latestMessage = chatHistory[chatHistory.length - 1];

				lobbiesWithLatestMessage.push({
					lobbyId: lobby.lobbyId,

					// withUser:
					// 	latestMessage.sentBy === currentUser ? lobby.user2Nickname : lobby.user1Nickname,
					withUser: withUserReal,
					lastMessageFrom: latestMessage.sentBy,
					message: latestMessage.message,
					timestamp: latestMessage.lobbyChatKey.timestamp,
					// Add a flag to indicate that this lobby has messages. We will use this for sorting.
					hasMessages: true
				});
			}
			// Handle the case where the chat history is empty
			else {
				lobbiesWithLatestMessage.push({
					lobbyId: lobby.lobbyId,
					// withUser: lobby.user1Nickname === currentUser ? lobby.user2Nickname : lobby.user1Nickname,
					withUser: withUserReal,
					lastMessageFrom: 'None',
					message: 'No messages yet',
					timestamp: new Date().toISOString(),
					hasMessages: false
				});
			}
		} catch (error) {
			console.error('Error fetching chat history:', error);
		}
	}

	// Before sort
	console.log('Before sort:', lobbiesWithLatestMessage);

	// Sort the lobbies by latest message and put lobbies without messages at the bottom
	const sortedLobbies = lobbiesWithLatestMessage.sort((a, b) => {
		// Lobby with messages comes first
		if (a.hasMessages && !b.hasMessages) {
			return -1;
		}
		// Lobby without messages comes last
		else if (!a.hasMessages && b.hasMessages) {
			return 1;
		} else {
			return new Date(b.timestamp) - new Date(a.timestamp);
		}
	});

	// After sort
	console.log('After sort:', sortedLobbies);

	return sortedLobbies;
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
