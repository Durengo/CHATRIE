<!-- TODO: If we attemp to instantly go to a lobby chat all the vars will be null. If we refresh the page everything is good. -->

<script>
	import { onMount } from 'svelte';
	import { loginUsername, authToken, currentLobbyId, currentSendToUsername } from '$lib/stores.js';
	import { fetchChatHistory } from '$lib/scripts/lobbies.js';
	import { formatTimestamp } from '$lib/scripts/utils.js';

	let token = authToken;
	let chatHistory = [];
	let lobbyId = currentLobbyId;
	let currentUser = loginUsername;
	let sendToUsername = currentSendToUsername;

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

			console.log('lobbyId:', lobbyId);
			console.log('token:', token);
			console.log('currentUser:', currentUser);
			console.log('sendToUsername:', sendToUsername);

			chatHistory = await fetchChatHistory(lobbyId, token);
		} catch (error) {
			console.error('Error fetching chat history:', error);
			// Handle the error appropriately
		}
	});
</script>

<h1>Chat Room {lobbyId}</h1>

{#if chatHistory}
	<ul>
		{#each chatHistory as message}
			<li>
				{formatTimestamp(message.lobbyChatKey.timestamp)} - {message.sentBy}: {message.message}
			</li>
		{/each}
	</ul>
{:else}
	<p>Loading chat history...</p>
{/if}
