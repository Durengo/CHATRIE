<!-- TODO: If we attemp to instantly go to a lobby chat all the vars will be null. If we refresh the page everything is good. -->

<script>
	import { onMount, afterUpdate } from 'svelte';
	import {
		loginUsername,
		authToken,
		currentLobbyId,
		currentSendToUsername,
		currentChatHistory
	} from '$lib/stores.js';
	import {
		sendMessageToBackendAndSocket,
		sendMessage,
		fetchChatHistory
	} from '$lib/scripts/lobbies.js';
	import { formatTimestamp } from '$lib/scripts/utils.js';
	import { formChatObject, sendChatMessage } from '$lib/scripts/broker.js';
	import { goto } from '$app/navigation';
	import { onDestroy } from 'svelte';
	import { io } from 'socket.io-client';

	let token = authToken;
	// let chatHistory = [];
	let lobbyId = currentLobbyId;
	let currentUser = loginUsername;
	let sendToUsername = currentSendToUsername;
	let chatHistory = currentChatHistory || [];
	let isLoading = true;
	let noChats = false;

	let socket;
	let chatHistoryContainer;
	let prevScrollTop = 0;

	// const send_message = async () => {
	// 	const chatMessage = formChatObject(
	// 		lobbyId,
	// 		currentUser,
	// 		sendToUsername,
	// 		'test'
	// 	);

	// 	console.log('chatMessage:', chatMessage);

	// 	socket.emit('send_chat', chatMessage);
	// };

	// Define a debounced version of the handleWindowResize function
	// const debouncedHandleWindowResize = debounce(handleWindowResize, 200);

	const redirectToLogin = async () => {
		setTimeout(() => {
			goto('/login', { replaceState: true });
		}, 500);
	};

	onMount(async () => {
		try {
			const unsubscribeToken = authToken.subscribe((value) => {
				token = value;
			});

			const unsubscribeLobbyId = currentLobbyId.subscribe((value) => {
				lobbyId = value;
			});

			const unsubscribeCurrentUser = loginUsername.subscribe((value) => {
				currentUser = value;
			});

			const unsubscribeSendToUsername = currentSendToUsername.subscribe((value) => {
				sendToUsername = value;
			});

			const unsubscribeChatHistory = currentChatHistory.subscribe((value) => {
				chatHistory = value;
			});

			lobbyId = sessionStorage.getItem('currentLobbyId');
			sendToUsername = sessionStorage.getItem('currentSendToUsername');

			if (lobbyId === null || lobbyId === undefined || lobbyId === '' || lobbyId === 'null') {
				isLoading = false;
				return;
			}

			// console.log('lobbyId:', lobbyId);
			// console.log('token:', token);
			// console.log('currentUser:', currentUser);
			// console.log('sendToUsername:', sendToUsername);

			{
				if (lobbyId && currentUser && sendToUsername) {
					socket = io('http://localhost:8089', {
						query: { room: lobbyId, from: currentUser, to: sendToUsername }
					});

					socket.on('connect', () => {
						// console.log('Connected to WebSocket server');
					});

					socket.on('disconnect', () => {
						// console.log('Disconnected from WebSocket server');
					});

					const this_queue = `chat-message-${lobbyId}-${currentUser}`;

					// console.log(`This user queue is: ${this_queue}`);

					socket.on(`${this_queue}`, (data) => {
						// receivedMessage = data;
						// console.log('Received chat message:', data);
						fetchChatHistory(lobbyId, authToken);
					});

					socket.on('receive_chat', (data) => {
						// console.log('Received chat message:', data);
						// Update UI to display the incoming chat message
					});
				}
			}

			setTimeout(async () => {
				if (lobbyId !== null || lobbyId !== undefined || lobbyId !== '' || lobbyId !== 'null') {
					noChats = await fetchChatHistory(lobbyId, token);
					isLoading = false;
				}
			}, 100);
		} catch (error) {
			console.error('Error fetching chat history:', error);
		}
	});

	let newMessage = '';

	const handleKeyDown = (event) => {
		if (event.key === 'Enter' && !event.shiftKey) {
			event.preventDefault();
			const chatMessage = formChatObject(lobbyId, currentUser, sendToUsername, newMessage);
			// console.log('chatMessage:', chatMessage);
			sendMessageToBackendAndSocket(chatMessage, token, socket);
			newMessage = '';
		}
	};

	afterUpdate(() => {
		if (chatHistoryContainer) {
			// Scroll to the bottom of the chat history container after updating
			// chatHistoryContainer.scrollTop = chatHistoryContainer.scrollHeight;

			// Adjust scroll position to maintain relative position after update
			chatHistoryContainer.scrollTop =
				chatHistoryContainer.scrollHeight - chatHistoryContainer.clientHeight - prevScrollTop;
			prevScrollTop = 0; // Reset prevScrollTop after adjustment
		}
	});

	const handleWindowResize = () => {
		if (chatHistoryContainer) {
		}
	};

	// window.addEventListener('resize', handleWindowResize);
	// Check if window is defined before attaching the event listener
	// if (typeof window !== 'undefined') {
	// 	window.addEventListener('resize', () => {
	// 		// Throttle the execution of handleWindowResize using requestAnimationFrame
	// 		if (!resizeRequestId) {
	// 			resizeRequestId = requestAnimationFrame(() => {
	// 				handleWindowResize();
	// 				resizeRequestId = null;
	// 			});
	// 		}
	// 	});

	// 	// Cleanup function to cancel the requestAnimationFrame when the component is destroyed
	// 	onDestroy(() => {
	// 		if (resizeRequestId) {
	// 			cancelAnimationFrame(resizeRequestId);
	// 			resizeRequestId = null;
	// 		}
	// 	});
	// }

	onDestroy(() => {
		// window.removeEventListener('resize', handleWindowResize);
		if (socket) {
			socket.disconnect();
			// console.log('Socket disconnected');
		}
	});
</script>

{#if isLoading}
	<p>Loading chat history...</p>
{:else if chatHistory !== undefined && chatHistory !== null && currentUser !== null}
	<h1>Chatting with {sendToUsername}</h1>

	{#if chatHistory.length > 0}
		<div class="chat-container">
			<div class="chat-history-container" bind:this={chatHistoryContainer}>
				<ul>
					{#each chatHistory as message}
						<li class={message.sentBy === currentUser ? 'sent-by-me' : 'sent-by-other'}>
							<span>{formatTimestamp(message.lobbyChatKey.timestamp)} - {message.sentBy}:</span>
							<span style="white-space: pre-line;">{message.message}</span>
						</li>
					{/each}
				</ul>
			</div>

			<div class="input-container">
				<textarea
					bind:value={newMessage}
					placeholder="Enter your message"
					on:keydown={handleKeyDown}
				></textarea>
				<button
					on:click={() => {
						const chatMessage = formChatObject(lobbyId, currentUser, sendToUsername, newMessage);
						// console.log('chatMessage:', chatMessage);
						sendMessageToBackendAndSocket(chatMessage, token, socket);
						newMessage = '';
					}}>Send</button
				>
			</div>
		</div>
	{:else if noChats}
		<p>No chat history available.</p>
		<p>Begin a new chat now!</p>

		<div class="input-container">
			<textarea bind:value={newMessage} placeholder="Enter your message" on:keydown={handleKeyDown}
			></textarea>
			<button
				on:click={() => {
					const chatMessage = formChatObject(lobbyId, currentUser, sendToUsername, newMessage);
					// console.log('chatMessage:', chatMessage);
					sendMessageToBackendAndSocket(chatMessage, token, socket);
					newMessage = '';
				}}>Send</button
			>
		</div>
	{/if}
{:else if lobbyId === null || lobbyId === undefined || lobbyId === '' || lobbyId === 'null' || currentUser === null}
	<p>Please log in.</p>
	<!-- Call redirectToLogin -->
	{#await redirectToLogin()}
		<p>Redirecting to login...</p>
	{:then}
		<p>Redirecting to login...</p>
	{:catch error}
		<p>Error redirecting to login: {error.message}</p>
	{/await}
{:else}
	<p>Error loading chat history.</p>
{/if}

<style>
	.chat-container {
		display: flex;
		flex-direction: column;
	}

	.chat-history-container {
		overflow-y: auto; /* Enable vertical scrolling if needed */
		max-height: 60vh; /* Set maximum height for the chat history container */
		/* Other styles remain unchanged */
	}

	.input-container {
		display: flex;
		align-items: center;
		border: 1px solid #ccc; /* Optional: Add border for styling */
		border-radius: 5px; /* Optional: Add border radius for styling */
		padding: 10px; /* Optional: Add padding for styling */
	}

	.input-container textarea {
		flex: 1;
		min-height: 40px; /* Set minimum height for the textarea */
		resize: none; /* Prevent textarea from being resized by the user */
		margin-right: 10px; /* Optional: Add margin as needed */
	}

	.input-container button {
		min-width: 80px; /* Set minimum width for the button */
	}

	ul {
		list-style-type: none; /* Hide default bullet points */
		margin: 0;
		padding: 0;
	}

	.sent-by-me {
		text-align: right;
		padding-right: 20px; /* Add some space to the right of the message */
		position: relative;
	}

	.sent-by-other {
		text-align: left;
		padding-left: 20px; /* Add some space to the left of the message */
		position: relative;
	}

	.sent-by-me > span {
		display: block;
		text-align: left; /* Align timestamp and sender information to the left */
	}

	.sent-by-other > span {
		display: block;
		text-align: right; /* Align timestamp and sender information to the right */
	}

	.sent-by-me::before {
		content: ''; /* Empty content for bullet point */
		width: 10px; /* Adjust the width of the "bullet point" */
		height: 10px; /* Adjust the height of the "bullet point" */
		background-color: transparent; /* Make it invisible */
		position: absolute;
		right: 0; /* Position it to the right */
		top: 50%; /* Align it vertically */
		transform: translateY(-50%); /* Center it vertically */
	}

	.sent-by-other::before {
		content: ''; /* Empty content for bullet point */
		width: 10px; /* Adjust the width of the "bullet point" */
		height: 10px; /* Adjust the height of the "bullet point" */
		background-color: transparent; /* Make it invisible */
		position: absolute;
		left: 0; /* Position it to the left */
		top: 50%; /* Align it vertically */
		transform: translateY(-50%); /* Center it vertically */
	}
</style>
