export function formChatObject(lobbyId, sentBy, sentTo, message) {
	if (!lobbyId || !sentBy || !sentTo) {
		console.error('Invalid message data');
		return;
	}

	if (sentBy === sentTo) {
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
		sentBy: sentBy,
		sentTo: sentTo,
		message: message
	};

	return payload;
}

export async function sendChatMessage(socket, chatMessage) {
	socket.emit('send_chat', chatMessage);
}
