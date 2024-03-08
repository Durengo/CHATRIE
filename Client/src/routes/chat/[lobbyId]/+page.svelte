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

	let token = authToken;
	// let chatHistory = [];
	let lobbyId = currentLobbyId;
	let currentUser = loginUsername;
	let sendToUsername = currentSendToUsername;
	let chatHistory = currentChatHistory || [];
	let isLoading = true;

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

			console.log('lobbyId:', lobbyId);
			console.log('token:', token);
			console.log('currentUser:', currentUser);
			console.log('sendToUsername:', sendToUsername);

			setTimeout(async () => {
				await fetchChatHistory(lobbyId, token);
				isLoading = false;
			}, 100);
		} catch (error) {
			console.error('Error fetching chat history:', error);
		}
	});

	let newMessage = '';
</script>

<h1>Chat Room {lobbyId}</h1>

{#if isLoading}
	<p>Loading chat history...</p>
{:else if chatHistory !== undefined && chatHistory !== null}
	{#if chatHistory.length > 0}
		<ul>
			{#each chatHistory as message}
				<li>
					{formatTimestamp(message.lobbyChatKey.timestamp)} - {message.sentBy}: {message.message}
				</li>
			{/each}
		</ul>

		<input type="text" bind:value={newMessage} placeholder="Enter your message" />
		<button
			on:click={() => {
				sendMessage(lobbyId, currentUser, sendToUsername, newMessage, token);
				newMessage = '';
			}}>Send</button
		>
	{:else}
		<p>No chat history available.</p>
	{/if}
{:else}
	<p>Error loading chat history.</p>
{/if}
