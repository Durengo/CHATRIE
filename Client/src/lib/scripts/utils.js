export function formatTimestamp(timestampString) {
	const date = new Date(timestampString);
	const hours = date.getHours().toString().padStart(2, '0');
	const minutes = date.getMinutes().toString().padStart(2, '0');
	const seconds = date.getSeconds().toString().padStart(2, '0');

	return `${hours}:${minutes}:${seconds}`;
}

export function checkSendToWho(senderUsername, nickname1, nickname2) {
	if (senderUsername === nickname1) {
		return nickname2;
	} else {
		return nickname1;
	}
}
