<!-- TODO: If we attemp to instantly go to a lobby chat all the vars will be null. If we refresh the page everything is good. -->

<script>
	import { onMount } from 'svelte';
	import {
		loginUsername,
		authToken,
		currentLobbyId,
		currentSendToUsername,
		currentChatHistory
	} from '$lib/stores.js';
	import { sendMessage, fetchChatHistory } from '$lib/scripts/lobbies.js';
	import { formatTimestamp } from '$lib/scripts/utils.js';
	import { goto } from '$app/navigation';

	let token = authToken;
	// let chatHistory = [];
	let lobbyId = currentLobbyId;
	let currentUser = loginUsername;
	let sendToUsername = currentSendToUsername;
	let chatHistory = currentChatHistory || [];
	let isLoading = true;
	let noChats = false;

	onMount(async () => {
		try {
			// Access route parameters using page.params
			// const { lobbyId: routeLobbyId, sendToUser: routeSendToUser } =

			// console.log('routeLobbyId:', routeLobbyId);
			// console.log('routeSendToUser:', routeSendToUser);

			// // Set lobbyId and sendToUsername in stores
			// currentLobbyId.set(routeLobbyId);
			// currentSendToUsername.set(routeSendToUser);

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

			console.log('lobbyId:', lobbyId);
			console.log('token:', token);
			console.log('currentUser:', currentUser);
			console.log('sendToUsername:', sendToUsername);

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
			sendMessage(lobbyId, currentUser, sendToUsername, newMessage, token);
			newMessage = '';
		}
	};
</script>

<h1>Chat Room {lobbyId}</h1>

{#if isLoading}
	<p>Loading chat history...</p>
{:else if chatHistory !== undefined && chatHistory !== null}
	{#if chatHistory.length > 0}
		<ul>
			{#each chatHistory as message}
				<li>
					{formatTimestamp(message.lobbyChatKey.timestamp)} - {message.sentBy}:
					<span style="white-space: pre-line;">{message.message}</span>
				</li>
			{/each}
		</ul>

		<textarea bind:value={newMessage} placeholder="Enter your message" on:keydown={handleKeyDown}
		></textarea>
		<button
			on:click={() => {
				sendMessage(lobbyId, currentUser, sendToUsername, newMessage, token);
				newMessage = '';
			}}>Send</button
		>
	{:else if noChats}
		<p>No chat history available.</p>
		<p>Begin a new chat now!</p>

		<textarea bind:value={newMessage} placeholder="Enter your message" on:keydown={handleKeyDown}
		></textarea>
		<button
			on:click={() => {
				sendMessage(lobbyId, currentUser, sendToUsername, newMessage, token);
				newMessage = '';
			}}>Send</button
		>
	{/if}
{:else}
	<p>Error loading chat history.</p>
{/if}
